package com.albionradar.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log
import com.albionradar.AlbionRadarApp
import com.albionradar.R
import com.albionradar.data.EventLogManager
import com.albionradar.data.RadarState
import com.albionradar.protocol.ParsedEvent
import com.albionradar.protocol.ProtocolParser
import com.albionradar.util.Constants
import com.albionradar.util.PreferenceKeys
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.atomic.AtomicBoolean

/**
 * VPN Service for packet capture without root
 */
class RadarVpnService : VpnService() {

    companion object {
        private const val TAG = "RadarVpnService"
        private const val VPN_ADDRESS = "10.0.0.2"
        private const val VPN_ROUTE = "0.0.0.0"
        
        private val _isRunning = MutableStateFlow(false)
        val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()
        
        const val ACTION_START = "com.albionradar.ACTION_START"
        const val ACTION_STOP = "com.albionradar.ACTION_STOP"
    }

    private var vpnInterface: ParcelFileDescriptor? = null
    private var inputChannel: FileInputStream? = null
    private var outputChannel: FileOutputStream? = null
    
    private val isStopping = AtomicBoolean(false)
    private val parser = ProtocolParser()
    private val radarState = RadarState()
    private val logManager = EventLogManager.getInstance()
    
    private var captureJob: Job? = null
    private var processJob: Job? = null
    
    private val packetBuffer = ByteBuffer.allocateDirect(Constants.BUFFER_SIZE)
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startRadar()
            ACTION_STOP -> stopRadar()
        }
        return START_STICKY
    }

    /**
     * Start the VPN and packet capture
     */
    private fun startRadar() {
        if (_isRunning.value) {
            Log.d(TAG, "Radar already running")
            return
        }
        
        Log.d(TAG, "Starting radar...")
        
        // Start foreground service
        startForeground(com.albionradar.util.Constants.NotificationId.SERVICE, createNotification())
        
        // Setup VPN interface
        if (!setupVpn()) {
            Log.e(TAG, "Failed to setup VPN")
            stopSelf()
            return
        }
        
        _isRunning.value = true
        isStopping.set(false)
        
        logManager.logServiceStarted()
        
        // Start packet capture coroutine
        captureJob = CoroutineScope(Dispatchers.IO).launch {
            capturePackets()
        }
        
        // Start event processing coroutine
        processJob = CoroutineScope(Dispatchers.IO).launch {
            processEvents()
        }
        
        Log.d(TAG, "Radar started successfully")
    }

    /**
     * Stop the VPN and packet capture
     */
    private fun stopRadar() {
        if (!_isRunning.value) return
        
        Log.d(TAG, "Stopping radar...")
        
        isStopping.set(true)
        
        // Cancel jobs
        captureJob?.cancel()
        processJob?.cancel()
        
        // Close VPN interface
        try {
            inputChannel?.close()
            outputChannel?.close()
            vpnInterface?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error closing VPN interface", e)
        }
        
        vpnInterface = null
        inputChannel = null
        outputChannel = null
        
        // Clear state
        radarState.clearAll()
        parser.clearFragments()
        
        _isRunning.value = false
        logManager.logServiceStopped()
        
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        
        Log.d(TAG, "Radar stopped")
    }

    /**
     * Setup VPN interface
     */
    private fun setupVpn(): Boolean {
        return try {
            val builder = Builder()
                .setSession(getString(R.string.app_name))
                .addAddress(VPN_ADDRESS, 24)
                .addRoute(VPN_ROUTE, 0)
                .setMtu(1500)
            
            // Allow all apps to be routed through VPN
            // We'll filter only Albion packets
            
            vpnInterface = builder.establish()
            
            if (vpnInterface == null) {
                Log.e(TAG, "VPN interface is null")
                return false
            }
            
            inputChannel = FileInputStream(vpnInterface!!.fileDescriptor)
            outputChannel = FileOutputStream(vpnInterface!!.fileDescriptor)
            
            Log.d(TAG, "VPN interface established")
            true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to setup VPN", e)
            false
        }
    }

    /**
     * Main packet capture loop
     */
    private suspend fun capturePackets() {
        Log.d(TAG, "Starting packet capture loop")
        
        val buffer = ByteArray(Constants.BUFFER_SIZE)
        
        while (!isStopping.get() && vpnInterface != null) {
            try {
                // Read packet from VPN interface
                val length = inputChannel?.read(buffer) ?: -1
                
                if (length <= 0) {
                    delay(10)
                    continue
                }
                
                // Process the packet
                processPacket(buffer, length)
                
            } catch (e: Exception) {
                if (!isStopping.get()) {
                    Log.e(TAG, "Error reading packet", e)
                }
                delay(100)
            }
        }
        
        Log.d(TAG, "Packet capture loop ended")
    }

    /**
     * Process a single packet
     */
    private fun processPacket(buffer: ByteArray, length: Int) {
        try {
            // Parse IP header
            if (length < 20) return
            
            val version = (buffer[0].toInt() shr 4) and 0x0F
            if (version != 4) return // Only IPv4 for now
            
            // Extract source and destination ports
            val ipHeaderLength = (buffer[0].toInt() and 0x0F) * 4
            if (length < ipHeaderLength + 20) return
            
            val srcPort = ((buffer[ipHeaderLength].toInt() and 0xFF) shl 8) or 
                          (buffer[ipHeaderLength + 1].toInt() and 0xFF)
            val dstPort = ((buffer[ipHeaderLength + 2].toInt() and 0xFF) shl 8) or 
                          (buffer[ipHeaderLength + 3].toInt() and 0xFF)
            
            // Check if this is Albion traffic (port 5056)
            if (srcPort != Constants.ALBION_PORT && dstPort != Constants.ALBION_PORT) {
                // Not Albion traffic, forward it
                forwardPacket(buffer, length)
                return
            }
            
            // This is Albion traffic - parse it
            val payloadOffset = ipHeaderLength + 20 // IP header + TCP header (simplified)
            
            if (length > payloadOffset) {
                val payloadSize = length - payloadOffset
                
                if (payloadSize > 2) {
                    // Parse the game protocol
                    val events = parser.parsePacket(
                        buffer.copyOfRange(payloadOffset, length),
                        payloadSize
                    )
                    
                    // Handle parsed events
                    events.forEach { event ->
                        handleParsedEvent(event)
                    }
                }
            }
            
            // Still forward the packet
            forwardPacket(buffer, length)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error processing packet", e)
        }
    }

    /**
     * Forward packet to actual network
     */
    private fun forwardPacket(buffer: ByteArray, length: Int) {
        try {
            // Protect socket from VPN to avoid loop
            // The packet will be sent through the real network interface
            outputChannel?.write(buffer, 0, length)
        } catch (e: Exception) {
            // Ignore write errors
        }
    }

    /**
     * Handle parsed event from protocol
     */
    private fun handleParsedEvent(event: ParsedEvent) {
        when (event) {
            is ParsedEvent.NewCharacter -> {
                val player = event.player
                radarState.addPlayer(player)
                
                // Log and alert for hostile players
                if (player.isHostile()) {
                    logManager.logPlayerDetected(
                        player.name, 
                        player.flag, 
                        player.posX, 
                        player.posY
                    )
                    // Trigger alert
                    AlertManager.triggerHostileAlert(this, player)
                }
            }
            
            is ParsedEvent.Move -> {
                radarState.updatePlayerPosition(event.id, event.posX, event.posY)
            }
            
            is ParsedEvent.Leave -> {
                radarState.removePlayer(event.id)
            }
            
            is ParsedEvent.NewResource -> {
                val resource = event.resource
                radarState.addResource(resource)
                logManager.logResourceDetected(
                    resource.getDisplayName(),
                    resource.tier,
                    resource.enchantLevel,
                    resource.posX,
                    resource.posY
                )
                // Check if should alert
                if (shouldAlertResource(resource)) {
                    AlertManager.triggerResourceAlert(this, resource)
                }
            }
            
            is ParsedEvent.ResourceStateChange -> {
                radarState.updateResourceState(event.id, event.state)
            }
            
            is ParsedEvent.ResourceHarvested -> {
                radarState.removeResource(event.id)
            }
            
            is ParsedEvent.NewMob -> {
                val mob = event.mob
                radarState.addMob(mob)
                logManager.logMobDetected(mob.getDisplayName(), mob.tier, mob.posX, mob.posY)
            }
            
            is ParsedEvent.NewChest -> {
                val chest = event.chest
                radarState.addChest(chest)
                logManager.logChestDetected(chest.getDisplayName(), chest.tier, chest.posX, chest.posY)
            }
            
            is ParsedEvent.NewFishing -> {
                val fishing = event.fishing
                radarState.addFishingNode(fishing)
                logManager.logFishingDetected(fishing.getDisplayName(), fishing.tier, fishing.posX, fishing.posY)
            }
            
            is ParsedEvent.NewMist -> {
                val mist = event.mist
                radarState.addMist(mist)
                logManager.logMistDetected(mist.getDisplayName(), mist.tier, mist.posX, mist.posY)
            }
            
            is ParsedEvent.NewDungeon -> {
                val dungeon = event.dungeon
                radarState.addDungeon(dungeon)
                logManager.logDungeonDetected(dungeon.getDisplayName(), dungeon.tier, dungeon.posX, dungeon.posY)
            }
            
            is ParsedEvent.HealthUpdate -> {
                // Handle health update
            }
            
            else -> {
                // Handle other events
            }
        }
    }

    /**
     * Process events coroutine
     */
    private suspend fun processEvents() {
        // This coroutine can be used for batched processing
        while (!isStopping.get()) {
            delay(100)
        }
    }

    /**
     * Check if resource should trigger alert
     */
    private fun shouldAlertResource(resource: com.albionradar.data.model.Resource): Boolean {
        // Check user preferences for this resource tier/enchant
        // For now, alert for T6+ resources
        return resource.tier >= 6
    }

    /**
     * Create notification for foreground service
     */
    private fun createNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            packageManager.getLaunchIntentForPackage(packageName),
            PendingIntent.FLAG_IMMUTABLE
        )
        
        return androidx.core.app.NotificationCompat.Builder(this, AlbionRadarApp.CHANNEL_SERVICE)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_content))
            .setSmallIcon(R.drawable.ic_radar)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    override fun onDestroy() {
        Log.d(TAG, "Service destroyed")
        stopRadar()
        super.onDestroy()
    }

    override fun onRevoke() {
        Log.d(TAG, "VPN permission revoked")
        stopRadar()
        super.onRevoke()
    }
}

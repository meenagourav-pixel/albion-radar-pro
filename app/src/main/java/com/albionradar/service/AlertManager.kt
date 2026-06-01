package com.albionradar.service

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import com.albionradar.AlbionRadarApp
import com.albionradar.R
import com.albionradar.data.model.Mob
import com.albionradar.data.model.Player
import com.albionradar.data.model.Resource
import com.albionradar.util.PreferenceKeys

/**
 * Manages alerts for hostile players, resources, and other entities
 */
object AlertManager {
    
    private var soundPool: SoundPool? = null
    private var alertSoundId: Int = 0
    private var isLoaded = false
    
    // Cooldown tracking to prevent spam
    private var lastHostileAlertTime = 0L
    private var lastResourceAlertTime = 0L
    private val ALERT_COOLDOWN_MS = 3000L // 3 second cooldown
    
    /**
     * Initialize sound pool
     */
    fun initialize(context: Context) {
        if (soundPool != null) return
        
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        
        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttributes)
            .build()
        
        soundPool?.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0 && sampleId == alertSoundId) {
                isLoaded = true
            }
        }
        
        // Load alert sound (use default system sound if custom not available)
        try {
            alertSoundId = soundPool?.load(context, R.raw.alert_hostile, 1) ?: 0
        } catch (e: Exception) {
            // Use default if resource not found
            alertSoundId = 0
            isLoaded = true
        }
    }
    
    /**
     * Trigger alert for hostile player
     */
    fun triggerHostileAlert(context: Context, player: Player) {
        val currentTime = System.currentTimeMillis()
        
        // Check cooldown
        if (currentTime - lastHostileAlertTime < ALERT_COOLDOWN_MS) {
            return
        }
        lastHostileAlertTime = currentTime
        
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        
        // Check if hostile alerts are enabled
        if (!prefs.getBoolean(PreferenceKeys.ALERT_HOSTILE, true)) {
            return
        }
        
        // Play sound if enabled
        if (prefs.getBoolean(PreferenceKeys.SOUND_ALERTS, true)) {
            playAlertSound(context)
        }
        
        // Vibrate if enabled
        if (prefs.getBoolean(PreferenceKeys.VIBRATION_ALERTS, true)) {
            vibrate(context, VibrationPattern.HOSTILE)
        }
        
        // Show notification
        showHostileNotification(context, player)
    }
    
    /**
     * Trigger alert for resource
     */
    fun triggerResourceAlert(context: Context, resource: Resource) {
        val currentTime = System.currentTimeMillis()
        
        // Check cooldown
        if (currentTime - lastResourceAlertTime < ALERT_COOLDOWN_MS) {
            return
        }
        lastResourceAlertTime = currentTime
        
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        
        // Check if resource alerts are enabled
        if (!prefs.getBoolean(PreferenceKeys.ALERT_RESOURCES, false)) {
            return
        }
        
        // Check specific resource filter
        if (!isResourceAlertEnabled(prefs, resource)) {
            return
        }
        
        // Vibrate if enabled (no sound for resources by default)
        if (prefs.getBoolean(PreferenceKeys.VIBRATION_ALERTS, true)) {
            vibrate(context, VibrationPattern.RESOURCE)
        }
    }
    
    /**
     * Trigger alert for mob
     */
    fun triggerMobAlert(context: Context, mob: Mob) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        
        if (!prefs.getBoolean(PreferenceKeys.ALERT_MOBS, false)) {
            return
        }
        
        // Only alert for boss-type mobs
        if (mob.isBossType()) {
            if (prefs.getBoolean(PreferenceKeys.SOUND_ALERTS, true)) {
                playAlertSound(context)
            }
            if (prefs.getBoolean(PreferenceKeys.VIBRATION_ALERTS, true)) {
                vibrate(context, VibrationPattern.BOSS)
            }
        }
    }
    
    /**
     * Check if resource alert is enabled for this specific resource
     */
    private fun isResourceAlertEnabled(prefs: SharedPreferences, resource: Resource): Boolean {
        val key = "${PreferenceKeys.RESOURCE_FILTER_PREFIX}${resource.resourceType}_${resource.tier}_${resource.enchantLevel}"
        return prefs.getBoolean(key, false)
    }
    
    /**
     * Play alert sound
     */
    private fun playAlertSound(context: Context) {
        if (!isLoaded) {
            initialize(context)
        }
        
        soundPool?.play(alertSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
    }
    
    /**
     * Vibrate device
     */
    private fun vibrate(context: Context, pattern: VibrationPattern) {
        val vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        
        if (!vibrator.hasVibrator()) return
        
        val timing = when (pattern) {
            VibrationPattern.HOSTILE -> longArrayOf(0, 500, 200, 500, 200, 500)
            VibrationPattern.RESOURCE -> longArrayOf(0, 200, 100, 200)
            VibrationPattern.BOSS -> longArrayOf(0, 300, 100, 300, 100, 300, 100, 300)
        }
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createWaveform(timing, -1)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(timing, -1)
        }
    }
    
    /**
     * Show hostile player notification
     */
    private fun showHostileNotification(context: Context, player: Player) {
        val notification = NotificationCompat.Builder(context, AlbionRadarApp.CHANNEL_ALERTS)
            .setSmallIcon(R.drawable.ic_warning)
            .setContentTitle(context.getString(R.string.notification_hostile_detected))
            .setContentText("Hostile player: ${player.name}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .build()
        
        try {
            NotificationManagerCompat.from(context).notify(
                com.albionradar.util.Constants.NotificationId.ALERT_HOSTILE,
                notification
            )
        } catch (e: SecurityException) {
            // Notification permission not granted
        }
    }
    
    /**
     * Release resources
     */
    fun release() {
        soundPool?.release()
        soundPool = null
        isLoaded = false
    }
}

/**
 * Vibration patterns
 */
enum class VibrationPattern {
    HOSTILE,    // Long repeated vibration for hostile players
    RESOURCE,   // Short vibration for resources
    BOSS        // Medium repeated vibration for bosses
}

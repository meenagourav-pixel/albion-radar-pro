package com.albionradar.ui

import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.albionradar.R
import com.albionradar.databinding.ActivityMainBinding
import com.albionradar.service.RadarOverlayService
import com.albionradar.service.RadarVpnService
import com.albionradar.util.PreferenceKeys
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isRadarRunning = false
    private var hasOverlayPermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        checkPermissions()
        observeState()
    }

    private fun setupUI() {
        // Start/Stop button
        binding.btnStartStop.setOnClickListener {
            if (isRadarRunning) {
                stopRadar()
            } else {
                startRadar()
            }
        }
        
        // Settings button
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        
        // Resource filter button
        binding.btnResourceFilter.setOnClickListener {
            startActivity(Intent(this, ResourceFilterActivity::class.java))
        }
        
        // Mob filter button
        binding.btnMobFilter.setOnClickListener {
            startActivity(Intent(this, MobFilterActivity::class.java))
        }
        
        // Alert config button
        binding.btnAlertConfig.setOnClickListener {
            startActivity(Intent(this, AlertConfigActivity::class.java))
        }
        
        // Log viewer button
        binding.btnLogs.setOnClickListener {
            startActivity(Intent(this, LogViewerActivity::class.java))
        }
        
        // Grant overlay permission button
        binding.btnGrantOverlay.setOnClickListener {
            requestOverlayPermission()
        }
    }

    private fun checkPermissions() {
        hasOverlayPermission = Settings.canDrawOverlays(this)
        updateOverlayPermissionUI()
    }

    private fun updateOverlayPermissionUI() {
        if (hasOverlayPermission) {
            binding.overlayPermissionCard.isVisible = false
        } else {
            binding.overlayPermissionCard.isVisible = true
            binding.btnGrantOverlay.setOnClickListener {
                requestOverlayPermission()
            }
        }
    }

    private fun requestOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            android.net.Uri.parse("package:$packageName")
        )
        startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION)
    }

    private fun startRadar() {
        if (!hasOverlayPermission) {
            Toast.makeText(this, R.string.overlay_permission_required, Toast.LENGTH_SHORT).show()
            requestOverlayPermission()
            return
        }
        
        // Request VPN permission
        val vpnIntent = VpnService.prepare(this)
        if (vpnIntent != null) {
            startActivityForResult(vpnIntent, REQUEST_VPN_PERMISSION)
        } else {
            // VPN permission already granted
            startVpnService()
        }
    }

    private fun startVpnService() {
        // Start VPN service
        val intent = Intent(this, RadarVpnService::class.java).apply {
            action = RadarVpnService.ACTION_START
        }
        startService(intent)
        
        // Start overlay service if enabled
        val showOverlay = getSharedPreferences("radar_prefs", MODE_PRIVATE)
            .getBoolean(PreferenceKeys.SHOW_OVERLAY, true)
        
        if (showOverlay) {
            val overlayIntent = Intent(this, RadarOverlayService::class.java).apply {
                action = RadarOverlayService.ACTION_SHOW
            }
            startService(overlayIntent)
        }
        
        isRadarRunning = true
        updateUI()
        
        Toast.makeText(this, R.string.service_started, Toast.LENGTH_SHORT).show()
    }

    private fun stopRadar() {
        // Stop VPN service
        val vpnIntent = Intent(this, RadarVpnService::class.java).apply {
            action = RadarVpnService.ACTION_STOP
        }
        startService(vpnIntent)
        
        // Stop overlay service
        val overlayIntent = Intent(this, RadarOverlayService::class.java).apply {
            action = RadarOverlayService.ACTION_HIDE
        }
        startService(overlayIntent)
        
        isRadarRunning = false
        updateUI()
        
        Toast.makeText(this, R.string.service_stopped, Toast.LENGTH_SHORT).show()
    }

    private fun updateUI() {
        if (isRadarRunning) {
            binding.btnStartStop.text = getString(R.string.stop_radar)
            binding.btnStartStop.setBackgroundColor(getColor(R.color.error))
            binding.statusIndicator.setImageResource(R.drawable.ic_status_active)
            binding.statusText.text = getString(R.string.radar_status_active)
        } else {
            binding.btnStartStop.text = getString(R.string.start_radar)
            binding.btnStartStop.setBackgroundColor(getColor(R.color.accent))
            binding.statusIndicator.setImageResource(R.drawable.ic_status_inactive)
            binding.statusText.text = getString(R.string.radar_status_inactive)
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            RadarVpnService.isRunning.collectLatest { running ->
                isRadarRunning = running
                updateUI()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        when (requestCode) {
            REQUEST_VPN_PERMISSION -> {
                if (resultCode == RESULT_OK) {
                    startVpnService()
                } else {
                    Toast.makeText(this, R.string.vpn_permission_required, Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_OVERLAY_PERMISSION -> {
                checkPermissions()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkPermissions()
    }

    companion object {
        private const val REQUEST_VPN_PERMISSION = 1001
        private const val REQUEST_OVERLAY_PERMISSION = 1002
    }
}

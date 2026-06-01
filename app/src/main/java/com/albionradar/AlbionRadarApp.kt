package com.albionradar

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.preference.PreferenceManager
import com.albionradar.util.PreferenceKeys

class AlbionRadarApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Initialize preferences with defaults
        initPreferences()
        
        // Create notification channels
        createNotificationChannels()
    }

    private fun initPreferences() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        
        // Set default values if not already set
        if (!prefs.contains(PreferenceKeys.SCANNER_RANGE)) {
            prefs.edit().putInt(PreferenceKeys.SCANNER_RANGE, 50).apply()
        }
        if (!prefs.contains(PreferenceKeys.OVERLAY_OPACITY)) {
            prefs.edit().putInt(PreferenceKeys.OVERLAY_OPACITY, 80).apply()
        }
        if (!prefs.contains(PreferenceKeys.OVERLAY_SIZE)) {
            prefs.edit().putInt(PreferenceKeys.OVERLAY_SIZE, 300).apply()
        }
        if (!prefs.contains(PreferenceKeys.SOUND_ALERTS)) {
            prefs.edit().putBoolean(PreferenceKeys.SOUND_ALERTS, true).apply()
        }
        if (!prefs.contains(PreferenceKeys.VIBRATION_ALERTS)) {
            prefs.edit().putBoolean(PreferenceKeys.VIBRATION_ALERTS, true).apply()
        }
        if (!prefs.contains(PreferenceKeys.SHOW_OVERLAY)) {
            prefs.edit().putBoolean(PreferenceKeys.SHOW_OVERLAY, true).apply()
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            
            // Radar Service Channel
            val serviceChannel = NotificationChannel(
                CHANNEL_SERVICE,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_desc)
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(serviceChannel)
            
            // Alert Channel
            val alertChannel = NotificationChannel(
                CHANNEL_ALERTS,
                "Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Radar alert notifications"
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(alertChannel)
        }
    }

    companion object {
        const val CHANNEL_SERVICE = "radar_service"
        const val CHANNEL_ALERTS = "radar_alerts"
        
        @Volatile
        private var instance: AlbionRadarApp? = null
        
        fun getInstance(): AlbionRadarApp {
            return instance ?: throw IllegalStateException("Application not initialized")
        }
    }
}

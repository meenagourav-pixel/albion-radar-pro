package com.albionradar.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.albionradar.service.RadarVpnService

/**
 * Broadcast receiver for notification actions
 */
class NotificationReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "NotificationReceiver"
        
        const val ACTION_STOP_RADAR = "com.albionradar.ACTION_STOP_RADAR"
        const val ACTION_PAUSE_ALERTS = "com.albionradar.ACTION_PAUSE_ALERTS"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Received action: ${intent.action}")
        
        when (intent.action) {
            ACTION_STOP_RADAR -> {
                val stopIntent = Intent(context, RadarVpnService::class.java).apply {
                    action = RadarVpnService.ACTION_STOP
                }
                context.startService(stopIntent)
            }
            
            ACTION_PAUSE_ALERTS -> {
                // Pause alerts for 5 minutes
                val prefs = context.getSharedPreferences("radar_prefs", Context.MODE_PRIVATE)
                prefs.edit()
                    .putLong("alerts_paused_until", System.currentTimeMillis() + 300000)
                    .apply()
            }
        }
    }
}

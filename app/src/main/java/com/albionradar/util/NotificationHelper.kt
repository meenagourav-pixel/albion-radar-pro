package com.albionradar.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.albionradar.R
import com.albionradar.receiver.NotificationReceiver

/**
 * Helper class for creating and managing notifications
 */
object NotificationHelper {
    
    /**
     * Create service notification
     */
    fun createServiceNotification(
        context: Context,
        title: String,
        content: String
    ): Notification {
        val stopIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_STOP_RADAR
        }
        
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(context, "radar_service")
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_radar)
            .setOngoing(true)
            .setSilent(true)
            .addAction(R.drawable.ic_close, "Stop", stopPendingIntent)
            .build()
    }
    
    /**
     * Create hostile player alert notification
     */
    fun createHostileAlertNotification(
        context: Context,
        playerName: String
    ): Notification {
        val pauseIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_PAUSE_ALERTS
        }
        
        val pausePendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            pauseIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(context, "radar_alerts")
            .setContentTitle(context.getString(R.string.notification_hostile_detected))
            .setContentText("Hostile player: $playerName")
            .setSmallIcon(R.drawable.ic_warning)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_alert, "Pause Alerts 5m", pausePendingIntent)
            .build()
    }
    
    /**
     * Show notification
     */
    fun showNotification(context: Context, id: Int, notification: Notification) {
        try {
            NotificationManagerCompat.from(context).notify(id, notification)
        } catch (e: SecurityException) {
            // Notification permission not granted
        }
    }
    
    /**
     * Cancel notification
     */
    fun cancelNotification(context: Context, id: Int) {
        NotificationManagerCompat.from(context).cancel(id)
    }
    
    /**
     * Cancel all notifications
     */
    fun cancelAllNotifications(context: Context) {
        NotificationManagerCompat.from(context).cancelAll()
    }
}

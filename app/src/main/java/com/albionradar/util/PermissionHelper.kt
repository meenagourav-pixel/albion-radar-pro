package com.albionradar.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build

/**
 * Helper class for handling permissions
 */
object PermissionHelper {
    
    /**
     * Check if overlay permission is granted
     */
    fun hasOverlayPermission(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }
    
    /**
     * Request overlay permission
     */
    fun requestOverlayPermission(activity: Activity, requestCode: Int) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            android.net.Uri.parse("package:${activity.packageName}")
        )
        activity.startActivityForResult(intent, requestCode)
    }
    
    /**
     * Check if VPN permission is granted
     */
    fun hasVpnPermission(context: Context): Boolean {
        return VpnService.prepare(context) == null
    }
    
    /**
     * Request VPN permission
     */
    fun requestVpnPermission(activity: Activity, requestCode: Int) {
        val intent = VpnService.prepare(activity)
        if (intent != null) {
            activity.startActivityForResult(intent, requestCode)
        }
    }
    
    /**
     * Check if notification permission is granted (Android 13+)
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Permission not required before Android 13
        }
    }
    
    /**
     * Request notification permission (Android 13+)
     */
    fun requestNotificationPermission(activity: Activity, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                requestCode
            )
        }
    }
    
    /**
     * Check all required permissions
     */
    fun hasAllPermissions(context: Context): Boolean {
        return hasOverlayPermission(context) && hasNotificationPermission(context)
    }
    
    /**
     * Get list of missing permissions
     */
    fun getMissingPermissions(context: Context): List<String> {
        val missing = mutableListOf<String>()
        
        if (!hasOverlayPermission(context)) {
            missing.add("Overlay")
        }
        
        if (!hasNotificationPermission(context)) {
            missing.add("Notification")
        }
        
        return missing
    }
    
    /**
     * Get permission status description
     */
    fun getPermissionStatus(context: Context): String {
        val status = mutableListOf<String>()
        
        status.add("Overlay: ${if (hasOverlayPermission(context)) "✓" else "✗"}")
        status.add("VPN: ${if (hasVpnPermission(context)) "✓" else "✗ (requested on start)"}")
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            status.add("Notifications: ${if (hasNotificationPermission(context)) "✓" else "✗"}")
        }
        
        return status.joinToString("\n")
    }
}

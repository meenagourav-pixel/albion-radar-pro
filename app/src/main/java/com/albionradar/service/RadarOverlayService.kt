package com.albionradar.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import com.albionradar.R
import com.albionradar.data.RadarState
import com.albionradar.ui.view.RadarView
import com.albionradar.util.PreferenceKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Overlay service for displaying radar on top of other apps
 */
class RadarOverlayService : Service() {

    companion object {
        private const val TAG = "RadarOverlayService"
        
        const val ACTION_SHOW = "com.albionradar.overlay.SHOW"
        const val ACTION_HIDE = "com.albionradar.overlay.HIDE"
        const val ACTION_UPDATE = "com.albionradar.overlay.UPDATE"
        
        private var _isShowing = false
        val isShowing: Boolean get() = _isShowing
    }

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View
    private lateinit var radarView: RadarView
    
    private var updateJob: Job? = null
    private val radarState = RadarState()
    
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    override fun onCreate() {
        super.onCreate()
        
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        
        createOverlayView()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_SHOW -> showOverlay()
            ACTION_HIDE -> hideOverlay()
            ACTION_UPDATE -> updateOverlay()
        }
        
        return START_STICKY
    }

    /**
     * Create the overlay view
     */
    @SuppressLint("ClickableViewAccessibility", "InflateParams")
    private fun createOverlayView() {
        // Get overlay size from preferences
        val prefs = getSharedPreferences("radar_prefs", Context.MODE_PRIVATE)
        val size = prefs.getInt(PreferenceKeys.OVERLAY_SIZE, 300)
        val opacity = prefs.getInt(PreferenceKeys.OVERLAY_OPACITY, 80)
        
        // Create layout params
        val params = WindowManager.LayoutParams(
            size,
            size,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = prefs.getInt(PreferenceKeys.OVERLAY_POSITION_X, 100)
            y = prefs.getInt(PreferenceKeys.OVERLAY_POSITION_Y, 100)
            dimAmount = 0f
        }
        
        // Inflate overlay layout
        overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_radar, null)
        
        // Get radar view
        radarView = overlayView.findViewById(R.id.radar_view)
        radarView.setOpacity(opacity)
        
        // Add close button handler
        overlayView.findViewById<View>(R.id.btn_close)?.setOnClickListener {
            hideOverlay()
        }
        
        // Setup touch listener for dragging
        overlayView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager.updateViewLayout(overlayView, params)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    // Save position
                    prefs.edit()
                        .putInt(PreferenceKeys.OVERLAY_POSITION_X, params.x)
                        .putInt(PreferenceKeys.OVERLAY_POSITION_Y, params.y)
                        .apply()
                    true
                }
                else -> false
            }
        }
        
        // Store params for later use
        overlayView.tag = params
        
        // Add to window
        windowManager.addView(overlayView, params)
        
        _isShowing = true
        
        // Start update loop
        startUpdateLoop()
    }

    /**
     * Show the overlay
     */
    private fun showOverlay() {
        if (overlayView.parent == null) {
            val params = overlayView.tag as WindowManager.LayoutParams
            windowManager.addView(overlayView, params)
        }
        overlayView.visibility = View.VISIBLE
        _isShowing = true
        startUpdateLoop()
    }

    /**
     * Hide the overlay
     */
    private fun hideOverlay() {
        overlayView.visibility = View.GONE
        _isShowing = false
        stopUpdateLoop()
    }

    /**
     * Update overlay data
     */
    private fun updateOverlay() {
        radarView.invalidate()
    }

    /**
     * Start the update loop
     */
    private fun startUpdateLoop() {
        if (updateJob?.isActive == true) return
        
        updateJob = CoroutineScope(Dispatchers.Main).launch {
            RadarState.getInstance().updateFlow.collectLatest {
                radarView.invalidate()
            }
        }
    }

    /**
     * Stop the update loop
     */
    private fun stopUpdateLoop() {
        updateJob?.cancel()
        updateJob = null
    }

    /**
     * Update overlay size
     */
    fun updateSize(newSize: Int) {
        val params = overlayView.tag as WindowManager.LayoutParams
        params.width = newSize
        params.height = newSize
        windowManager.updateViewLayout(overlayView, params)
    }

    /**
     * Update overlay opacity
     */
    fun updateOpacity(opacity: Int) {
        radarView.setOpacity(opacity)
    }

    override fun onDestroy() {
        super.onDestroy()
        
        stopUpdateLoop()
        
        if (overlayView.parent != null) {
            windowManager.removeView(overlayView)
        }
        
        _isShowing = false
    }
}

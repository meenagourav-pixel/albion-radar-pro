package com.albionradar.ui.view

import android.content.Context
import android.content.SharedPreferences
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.albionradar.R
import com.albionradar.data.RadarState
import com.albionradar.data.model.*
import com.albionradar.util.PreferenceKeys
import kotlin.math.*

/**
 * Custom view for drawing the radar overlay
 */
class RadarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val radarState = RadarState.getInstance()
    private lateinit var prefs: SharedPreferences
    
    // Paint objects
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rangePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val centerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val playerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val resourcePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mobPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val chestPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    
    // Settings
    private var scannerRange: Float = 50f
    private var opacity: Int = 80
    private var showPlayers: Boolean = true
    private var showResources: Boolean = true
    private var showMobs: Boolean = true
    private var showChests: Boolean = true
    private var showFishing: Boolean = true
    private var showMists: Boolean = true
    private var showDungeons: Boolean = true
    
    // Calculated values
    private var centerX: Float = 0f
    private var centerY: Float = 0f
    private var scale: Float = 1f
    
    init {
        initPaints()
    }
    
    private fun initPaints() {
        // Background
        backgroundPaint.color = Color.parseColor("#CC000000")
        backgroundPaint.style = Paint.Style.FILL
        
        // Grid
        gridPaint.color = Color.parseColor("#33FFFFFF")
        gridPaint.style = Paint.Style.STROKE
        gridPaint.strokeWidth = 1f
        
        // Range circle
        rangePaint.color = Color.parseColor("#44FFFFFF")
        rangePaint.style = Paint.Style.STROKE
        rangePaint.strokeWidth = 2f
        
        // Center point
        centerPaint.color = Color.WHITE
        centerPaint.style = Paint.Style.FILL
        
        // Player dots
        playerPaint.style = Paint.Style.FILL
        
        // Resource dots
        resourcePaint.style = Paint.Style.FILL
        
        // Mob dots
        mobPaint.style = Paint.Style.FILL
        
        // Chest dots
        chestPaint.style = Paint.Style.FILL
        
        // Text
        textPaint.color = Color.WHITE
        textPaint.textSize = 10f
        textPaint.textAlign = Paint.Align.CENTER
    }
    
    fun setOpacity(value: Int) {
        opacity = value
        val alpha = (opacity * 255 / 100).coerceIn(0, 255)
        backgroundPaint.alpha = alpha
        invalidate()
    }
    
    fun setScannerRange(range: Float) {
        scannerRange = range
        invalidate()
    }
    
    fun updatePreferences(preferences: SharedPreferences) {
        prefs = preferences
        scannerRange = prefs.getInt(PreferenceKeys.SCANNER_RANGE, 50).toFloat()
        showPlayers = prefs.getBoolean(PreferenceKeys.SHOW_PLAYERS, true)
        showResources = prefs.getBoolean(PreferenceKeys.SHOW_RESOURCES, true)
        showMobs = prefs.getBoolean(PreferenceKeys.SHOW_MOBS, true)
        showChests = prefs.getBoolean(PreferenceKeys.SHOW_CHESTS, true)
        showFishing = prefs.getBoolean(PreferenceKeys.SHOW_FISHING, true)
        showMists = prefs.getBoolean(PreferenceKeys.SHOW_MISTS, true)
        showDungeons = prefs.getBoolean(PreferenceKeys.SHOW_DUNGEONS, true)
        invalidate()
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        scale = min(w, h) / (scannerRange * 2.5f)
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val radius = min(width, height) / 2f
        
        // Draw background circle
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)
        
        // Draw grid circles
        val gridCount = 4
        for (i in 1..gridCount) {
            val r = radius * i / gridCount
            canvas.drawCircle(centerX, centerY, r, gridPaint)
        }
        
        // Draw grid lines
        canvas.drawLine(centerX - radius, centerY, centerX + radius, centerY, gridPaint)
        canvas.drawLine(centerX, centerY - radius, centerX, centerY + radius, gridPaint)
        
        // Draw range circle
        val rangeRadius = scannerRange * scale
        if (rangeRadius < radius) {
            canvas.drawCircle(centerX, centerY, rangeRadius, rangePaint)
        }
        
        // Draw center point (player)
        canvas.drawCircle(centerX, centerY, 6f, centerPaint)
        
        // Draw entities
        drawEntities(canvas, radius)
    }
    
    private fun drawEntities(canvas: Canvas, maxRadius: Float) {
        // Draw resources
        if (showResources) {
            radarState.resources.values.forEach { resource ->
                drawEntity(canvas, resource, maxRadius, resourcePaint, 4f)
            }
        }
        
        // Draw mobs
        if (showMobs) {
            radarState.mobs.values.forEach { mob ->
                val size = 4f * mob.getSizeMultiplier()
                drawEntity(canvas, mob, maxRadius, mobPaint, size)
            }
        }
        
        // Draw chests
        if (showChests) {
            radarState.chests.values.forEach { chest ->
                drawEntity(canvas, chest, maxRadius, chestPaint, 5f)
            }
        }
        
        // Draw fishing nodes
        if (showFishing) {
            radarState.fishingNodes.values.forEach { fishing ->
                drawEntity(canvas, fishing, maxRadius, resourcePaint, 4f)
            }
        }
        
        // Draw mists
        if (showMists) {
            radarState.mists.values.forEach { mist ->
                val size = 5f * mist.getSizeMultiplier()
                drawEntity(canvas, mist, maxRadius, mobPaint, size)
            }
        }
        
        // Draw dungeons
        if (showDungeons) {
            radarState.dungeons.values.forEach { dungeon ->
                val size = 5f * dungeon.getSizeMultiplier()
                drawEntity(canvas, dungeon, maxRadius, chestPaint, size)
            }
        }
        
        // Draw players (last, on top)
        if (showPlayers) {
            radarState.players.values.forEach { player ->
                drawPlayer(canvas, player, maxRadius)
            }
        }
    }
    
    private fun drawEntity(
        canvas: Canvas, 
        entity: Entity, 
        maxRadius: Float,
        paint: Paint,
        size: Float
    ) {
        val distance = entity.distanceFromPlayer()
        if (distance > scannerRange) return
        
        val x = centerX + entity.posX * scale
        val y = centerY + entity.posY * scale
        
        // Check if within radar bounds
        val distFromCenter = sqrt((x - centerX).pow(2) + (y - centerY).pow(2))
        if (distFromCenter > maxRadius) return
        
        // Set color based on entity type
        paint.color = when (entity) {
            is Resource -> entity.getColor()
            is Mob -> entity.getColor()
            is Chest -> entity.getColor()
            is FishingNode -> entity.getColor()
            is Mist -> entity.getColor()
            is Dungeon -> entity.getColor()
            else -> Color.WHITE
        }
        
        // Draw dot
        canvas.drawCircle(x, y, size, paint)
    }
    
    private fun drawPlayer(canvas: Canvas, player: Player, maxRadius: Float) {
        val distance = player.distanceFromPlayer()
        if (distance > scannerRange) return
        
        val x = centerX + player.posX * scale
        val y = centerY + player.posY * scale
        
        // Check if within radar bounds
        val distFromCenter = sqrt((x - centerX).pow(2) + (y - centerY).pow(2))
        if (distFromCenter > maxRadius) return
        
        // Set color based on player status
        playerPaint.color = player.getColor()
        
        // Draw dot (larger for hostile)
        val size = if (player.isHostile()) 6f else 4f
        canvas.drawCircle(x, y, size, playerPaint)
        
        // Draw hostile indicator ring
        if (player.isHostile()) {
            val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.RED
                style = Paint.Style.STROKE
                strokeWidth = 2f
            }
            canvas.drawCircle(x, y, 10f, ringPaint)
        }
        
        // Draw name for hostile players
        if (player.isHostile()) {
            textPaint.color = Color.RED
            canvas.drawText(player.name, x, y - 15f, textPaint)
        }
    }
}

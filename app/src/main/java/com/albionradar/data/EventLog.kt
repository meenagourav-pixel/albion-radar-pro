package com.albionradar.data

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Represents a single radar event log entry
 */
data class EventLog(
    val timestamp: Long = System.currentTimeMillis(),
    val eventType: EventType,
    val entityName: String,
    val details: String,
    val posX: Float? = null,
    val posY: Float? = null,
    val tier: Int? = null
) {
    /**
     * Format timestamp for display
     */
    fun getFormattedTime(): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    /**
     * Format full timestamp
     */
    fun getFullTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    /**
     * Convert to JSON string
     */
    fun toJson(): String {
        return """{"timestamp":$timestamp,"eventType":"${eventType.name}","entityName":"$entityName","details":"$details","posX":${posX ?: "null"},"posY":${posY ?: "null"},"tier":${tier ?: "null"}}"""
    }
}

/**
 * Event types for logging
 */
enum class EventType {
    @SerializedName("player_detected")
    PLAYER_DETECTED,
    
    @SerializedName("hostile_detected")
    HOSTILE_DETECTED,
    
    @SerializedName("resource_detected")
    RESOURCE_DETECTED,
    
    @SerializedName("mob_detected")
    MOB_DETECTED,
    
    @SerializedName("chest_detected")
    CHEST_DETECTED,
    
    @SerializedName("fishing_detected")
    FISHING_DETECTED,
    
    @SerializedName("mist_detected")
    MIST_DETECTED,
    
    @SerializedName("dungeon_detected")
    DUNGEON_DETECTED,
    
    @SerializedName("player_left")
    PLAYER_LEFT,
    
    @SerializedName("resource_harvested")
    RESOURCE_HARVESTED,
    
    @SerializedName("service_started")
    SERVICE_STARTED,
    
    @SerializedName("service_stopped")
    SERVICE_STOPPED
}

/**
 * Event log manager
 */
class EventLogManager {
    
    private val _logs = CopyOnWriteArrayList<EventLog>()
    val logs: List<EventLog> get() = _logs.toList()
    
    private var _maxEntries = 1000
    var maxEntries: Int
        get() = _maxEntries
        set(value) {
            _maxEntries = value
            trimLogs()
        }
    
    /**
     * Add a log entry
     */
    fun addLog(log: EventLog) {
        _logs.add(log)
        trimLogs()
    }
    
    /**
     * Add a player detection log
     */
    fun logPlayerDetected(name: String, flag: Int, posX: Float, posY: Float) {
        val eventType = if (flag == 255) EventType.HOSTILE_DETECTED else EventType.PLAYER_DETECTED
        val details = if (flag == 255) "Hostile player detected!" else "Player detected (flag: $flag)"
        addLog(EventLog(
            eventType = eventType,
            entityName = name,
            details = details,
            posX = posX,
            posY = posY
        ))
    }
    
    /**
     * Add a resource detection log
     */
    fun logResourceDetected(name: String, tier: Int, enchantLevel: Int, posX: Float, posY: Float) {
        val enchantSuffix = if (enchantLevel > 0) ".$enchantLevel" else ""
        addLog(EventLog(
            eventType = EventType.RESOURCE_DETECTED,
            entityName = "T$tier$enchantSuffix $name",
            details = "Resource available",
            posX = posX,
            posY = posY,
            tier = tier
        ))
    }
    
    /**
     * Add a mob detection log
     */
    fun logMobDetected(name: String, tier: Int, posX: Float, posY: Float) {
        addLog(EventLog(
            eventType = EventType.MOB_DETECTED,
            entityName = name,
            details = "Mob spawned",
            posX = posX,
            posY = posY,
            tier = tier
        ))
    }
    
    /**
     * Add a chest detection log
     */
    fun logChestDetected(name: String, tier: Int, posX: Float, posY: Float) {
        addLog(EventLog(
            eventType = EventType.CHEST_DETECTED,
            entityName = name,
            details = "Chest available",
            posX = posX,
            posY = posY,
            tier = tier
        ))
    }
    
    /**
     * Add a fishing node detection log
     */
    fun logFishingDetected(name: String, tier: Int, posX: Float, posY: Float) {
        addLog(EventLog(
            eventType = EventType.FISHING_DETECTED,
            entityName = name,
            details = "Fishing spot available",
            posX = posX,
            posY = posY,
            tier = tier
        ))
    }
    
    /**
     * Add a mist detection log
     */
    fun logMistDetected(name: String, tier: Int, posX: Float, posY: Float) {
        addLog(EventLog(
            eventType = EventType.MIST_DETECTED,
            entityName = name,
            details = "Mist portal available",
            posX = posX,
            posY = posY,
            tier = tier
        ))
    }
    
    /**
     * Add a dungeon detection log
     */
    fun logDungeonDetected(name: String, tier: Int, posX: Float, posY: Float) {
        addLog(EventLog(
            eventType = EventType.DUNGEON_DETECTED,
            entityName = name,
            details = "Dungeon entrance available",
            posX = posX,
            posY = posY,
            tier = tier
        ))
    }
    
    /**
     * Log service start
     */
    fun logServiceStarted() {
        addLog(EventLog(
            eventType = EventType.SERVICE_STARTED,
            entityName = "Radar Service",
            details = "Service started"
        ))
    }
    
    /**
     * Log service stop
     */
    fun logServiceStopped() {
        addLog(EventLog(
            eventType = EventType.SERVICE_STOPPED,
            entityName = "Radar Service",
            details = "Service stopped"
        ))
    }
    
    /**
     * Clear all logs
     */
    fun clearLogs() {
        _logs.clear()
    }
    
    /**
     * Trim logs to max entries
     */
    private fun trimLogs() {
        while (_logs.size > _maxEntries) {
            _logs.removeAt(0)
        }
    }
    
    /**
     * Export logs as JSON
     */
    fun exportAsJson(): String {
        val sb = StringBuilder()
        sb.append("[\n")
        _logs.forEachIndexed { index, log ->
            sb.append("  ")
            sb.append(log.toJson())
            if (index < _logs.size - 1) {
                sb.append(",")
            }
            sb.append("\n")
        }
        sb.append("]")
        return sb.toString()
    }
    
    /**
     * Export logs as CSV
     */
    fun exportAsCsv(): String {
        val sb = StringBuilder()
        sb.append("Timestamp,Event Type,Entity Name,Details,Position X,Position Y,Tier\n")
        _logs.forEach { log ->
            sb.append("${log.getFullTimestamp()},")
            sb.append("${log.eventType.name},")
            sb.append("\"${log.entityName}\",")
            sb.append("\"${log.details}\",")
            sb.append("${log.posX ?: ""},")
            sb.append("${log.posY ?: ""},")
            sb.append("${log.tier ?: ""}\n")
        }
        return sb.toString()
    }
    
    /**
     * Get logs filtered by event type
     */
    fun getLogsByType(eventType: EventType): List<EventLog> {
        return _logs.filter { it.eventType == eventType }
    }
    
    /**
     * Get logs within time range
     */
    fun getLogsInRange(startTime: Long, endTime: Long): List<EventLog> {
        return _logs.filter { it.timestamp in startTime..endTime }
    }
    
    companion object {
        @Volatile
        private var instance: EventLogManager? = null
        
        fun getInstance(): EventLogManager {
            return instance ?: synchronized(this) {
                instance ?: EventLogManager().also { instance = it }
            }
        }
    }
}

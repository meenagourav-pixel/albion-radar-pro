package com.albionradar.data.model

/**
 * Represents a Mist portal on the radar
 */
data class Mist(
    override val id: Long,
    override var posX: Float,
    override var posY: Float,
    val mistType: MistType,
    val tier: Int,
    val expireTime: Long = 0
) : Entity(id, posX, posY, "Mist Portal") {
    
    override val entityType: EntityType = EntityType.MIST
    
    /**
     * Get display name
     */
    fun getDisplayName(): String {
        return "T$tier ${mistType.displayName}"
    }
    
    /**
     * Get color based on mist type
     */
    fun getColor(): Int {
        return when (mistType) {
            MistType.SOLO -> 0xFF9400D3.toInt()   // Purple
            MistType.DUO -> 0xFF8A2BE2.toInt()     // Blue Violet
            MistType.SMALL_GROUP -> 0xFF9932CC.toInt() // Dark Orchid
            MistType.FULL_GROUP -> 0xFF4B0082.toInt()  // Indigo
            MistType.CORRUPTED -> 0xFFFF0000.toInt()   // Red
            MistType.AVALONIAN -> 0xFFFFD700.toInt()   // Gold
            MistType.UNKNOWN -> 0xFF9370DB.toInt()     // Medium Purple
        }
    }
    
    /**
     * Get size multiplier based on mist type
     */
    fun getSizeMultiplier(): Float {
        return when (mistType) {
            MistType.AVALONIAN -> 1.5f
            MistType.CORRUPTED -> 1.3f
            MistType.FULL_GROUP -> 1.2f
            else -> 1.0f
        }
    }
    
    /**
     * Check if mist is expiring soon
     */
    fun isExpiringSoon(currentTime: Long, thresholdMs: Long = 60000): Boolean {
        if (expireTime == 0L) return false
        return (expireTime - currentTime) < thresholdMs
    }
    
    companion object {
        /**
         * Parse mist type from string
         */
        fun parseMistType(typeString: String): MistType {
            return when {
                typeString.contains("SOLO", ignoreCase = true) -> MistType.SOLO
                typeString.contains("DUO", ignoreCase = true) -> MistType.DUO
                typeString.contains("SMALL_GROUP", ignoreCase = true) || 
                    typeString.contains("SMALLGROUP", ignoreCase = true) -> MistType.SMALL_GROUP
                typeString.contains("FULL_GROUP", ignoreCase = true) || 
                    typeString.contains("FULLGROUP", ignoreCase = true) -> MistType.FULL_GROUP
                typeString.contains("CORRUPTED", ignoreCase = true) -> MistType.CORRUPTED
                typeString.contains("AVALONIAN", ignoreCase = true) -> MistType.AVALONIAN
                else -> MistType.UNKNOWN
            }
        }
    }
}

/**
 * Mist portal types
 */
enum class MistType(val displayName: String) {
    SOLO("Solo Mist"),
    DUO("Duo Mist"),
    SMALL_GROUP("Small Group Mist"),
    FULL_GROUP("Full Group Mist"),
    CORRUPTED("Corrupted Dungeon"),
    AVALONIAN("Avalonian Raid"),
    UNKNOWN("Unknown Mist")
}

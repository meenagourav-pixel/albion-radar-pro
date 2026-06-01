package com.albionradar.data.model

/**
 * Represents a dungeon entrance on the radar
 */
data class Dungeon(
    override val id: Long,
    override var posX: Float,
    override var posY: Float,
    val dungeonType: DungeonType,
    val tier: Int,
    val isExpired: Boolean = false
) : Entity(id, posX, posY, dungeonType.displayName) {
    
    override val entityType: EntityType = EntityType.DUNGEON
    
    /**
     * Get display name
     */
    fun getDisplayName(): String {
        return "T$tier ${dungeonType.displayName}"
    }
    
    /**
     * Get color based on dungeon type
     */
    fun getColor(): Int {
        return when (dungeonType) {
            DungeonType.SOLO_YELLOW -> 0xFFFFFF00.toInt() // Yellow
            DungeonType.SOLO_BLUE -> 0xFF00BFFF.toInt()   // Blue
            DungeonType.SOLO_GREEN -> 0xFF00FF00.toInt()  // Green
            DungeonType.SOLO_RED -> 0xFFFF0000.toInt()    // Red
            DungeonType.SOLO_BLACK -> 0xFF000000.toInt()  // Black
            DungeonType.GROUP_YELLOW -> 0xFFFFFF00.toInt()
            DungeonType.GROUP_BLUE -> 0xFF00BFFF.toInt()
            DungeonType.GROUP_GREEN -> 0xFF00FF00.toInt()
            DungeonType.GROUP_RED -> 0xFFFF0000.toInt()
            DungeonType.GROUP_BLACK -> 0xFF000000.toInt()
            DungeonType.AVALONIAN -> 0xFFFFD700.toInt()   // Gold
            DungeonType.HELLGATE -> 0xFFFF1493.toInt()    // Deep Pink
            DungeonType.EXPEDITION -> 0xFFFFA500.toInt()  // Orange
            DungeonType.CORRUPTED -> 0xFF9400D3.toInt()   // Purple
            DungeonType.UNKNOWN -> 0xFFFF69B4.toInt()     // Hot Pink
        }
    }
    
    /**
     * Get size multiplier
     */
    fun getSizeMultiplier(): Float {
        return when (dungeonType) {
            DungeonType.AVALONIAN -> 1.5f
            DungeonType.HELLGATE -> 1.3f
            DungeonType.CORRUPTED -> 1.2f
            DungeonType.GROUP_BLACK, DungeonType.GROUP_RED -> 1.1f
            else -> 1.0f
        }
    }
    
    /**
     * Check if is group dungeon
     */
    fun isGroupDungeon(): Boolean {
        return dungeonType.name.startsWith("GROUP_")
    }
    
    /**
     * Check if is solo dungeon
     */
    fun isSoloDungeon(): Boolean {
        return dungeonType.name.startsWith("SOLO_")
    }
    
    companion object {
        /**
         * Parse dungeon type from string
         */
        fun parseDungeonType(typeString: String): DungeonType {
            return when {
                typeString.contains("HELLGATE", ignoreCase = true) -> DungeonType.HELLGATE
                typeString.contains("AVALONIAN", ignoreCase = true) -> DungeonType.AVALONIAN
                typeString.contains("CORRUPTED", ignoreCase = true) -> DungeonType.CORRUPTED
                typeString.contains("EXPEDITION", ignoreCase = true) -> DungeonType.EXPEDITION
                
                typeString.contains("GROUP", ignoreCase = true) -> {
                    when {
                        typeString.contains("BLACK", ignoreCase = true) -> DungeonType.GROUP_BLACK
                        typeString.contains("RED", ignoreCase = true) -> DungeonType.GROUP_RED
                        typeString.contains("GREEN", ignoreCase = true) -> DungeonType.GROUP_GREEN
                        typeString.contains("BLUE", ignoreCase = true) -> DungeonType.GROUP_BLUE
                        typeString.contains("YELLOW", ignoreCase = true) -> DungeonType.GROUP_YELLOW
                        else -> DungeonType.UNKNOWN
                    }
                }
                
                typeString.contains("SOLO", ignoreCase = true) || 
                    typeString.contains("EXPEDITION", ignoreCase = true) -> {
                    when {
                        typeString.contains("BLACK", ignoreCase = true) -> DungeonType.SOLO_BLACK
                        typeString.contains("RED", ignoreCase = true) -> DungeonType.SOLO_RED
                        typeString.contains("GREEN", ignoreCase = true) -> DungeonType.SOLO_GREEN
                        typeString.contains("BLUE", ignoreCase = true) -> DungeonType.SOLO_BLUE
                        typeString.contains("YELLOW", ignoreCase = true) -> DungeonType.SOLO_YELLOW
                        else -> DungeonType.UNKNOWN
                    }
                }
                
                else -> DungeonType.UNKNOWN
            }
        }
    }
}

/**
 * Dungeon types
 */
enum class DungeonType(val displayName: String) {
    SOLO_YELLOW("Solo Yellow Dungeon"),
    SOLO_BLUE("Solo Blue Dungeon"),
    SOLO_GREEN("Solo Green Dungeon"),
    SOLO_RED("Solo Red Dungeon"),
    SOLO_BLACK("Solo Black Dungeon"),
    GROUP_YELLOW("Group Yellow Dungeon"),
    GROUP_BLUE("Group Blue Dungeon"),
    GROUP_GREEN("Group Green Dungeon"),
    GROUP_RED("Group Red Dungeon"),
    GROUP_BLACK("Group Black Dungeon"),
    AVALONIAN("Avalonian Dungeon"),
    HELLGATE("Hellgate"),
    EXPEDITION("Expedition"),
    CORRUPTED("Corrupted Dungeon"),
    UNKNOWN("Unknown Dungeon")
}

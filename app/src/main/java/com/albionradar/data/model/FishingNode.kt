package com.albionradar.data.model

/**
 * Represents a fishing node on the radar
 */
data class FishingNode(
    override val id: Long,
    override var posX: Float,
    override var posY: Float,
    val fishType: FishType,
    val tier: Int,
    val enchantLevel: Int = 0
) : Entity(id, posX, posY, "Fishing Spot") {
    
    override val entityType: EntityType = EntityType.FISHING
    
    /**
     * Get display name
     */
    fun getDisplayName(): String {
        val enchantSuffix = if (enchantLevel > 0) ".${enchantLevel}" else ""
        return "T$tier$enchantSuffix ${fishType.displayName}"
    }
    
    /**
     * Get color based on tier
     */
    fun getColor(): Int {
        return when (tier) {
            1 -> 0xFF87CEEB.toInt() // Sky Blue
            2 -> 0xFF00BFFF.toInt() // Deep Sky Blue
            3 -> 0xFF1E90FF.toInt() // Dodger Blue
            4 -> 0xFF4169E1.toInt() // Royal Blue
            5 -> 0xFF0000CD.toInt() // Medium Blue
            6 -> 0xFF00008B.toInt() // Dark Blue
            7 -> 0xFF191970.toInt() // Midnight Blue
            8 -> 0xFF00CED1.toInt() // Dark Turquoise (rare)
            else -> 0xFF00BFFF.toInt()
        }
    }
    
    /**
     * Get filter key
     */
    fun getFilterKey(): String {
        return "fishing_${tier}_${enchantLevel}"
    }
    
    companion object {
        /**
         * Parse fish type from string
         */
        fun parseFishType(typeString: String): FishType {
            return when {
                typeString.contains("COASTAL", ignoreCase = true) -> FishType.COASTAL
                typeString.contains("DEEP", ignoreCase = true) -> FishType.DEEP
                typeString.contains("SHALLOW", ignoreCase = true) -> FishType.SHALLOW
                typeString.contains("RIVER", ignoreCase = true) -> FishType.RIVER
                else -> FishType.UNKNOWN
            }
        }
    }
}

/**
 * Fish types
 */
enum class FishType(val displayName: String) {
    SHALLOW("Shallow Water"),
    COASTAL("Coastal Water"),
    DEEP("Deep Water"),
    RIVER("River"),
    UNKNOWN("Unknown Water")
}

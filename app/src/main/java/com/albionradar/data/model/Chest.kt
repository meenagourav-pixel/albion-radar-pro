package com.albionradar.data.model

/**
 * Represents a chest on the radar
 */
data class Chest(
    override val id: Long,
    override var posX: Float,
    override var posY: Float,
    val chestType: ChestType,
    val tier: Int,
    val isOpen: Boolean = false
) : Entity(id, posX, posY, chestType.displayName) {
    
    override val entityType: EntityType = EntityType.CHEST
    
    /**
     * Get display name
     */
    fun getDisplayName(): String {
        return "T$tier ${chestType.displayName}"
    }
    
    /**
     * Get color based on chest type
     */
    fun getColor(): Int {
        return when (chestType) {
            ChestType.STANDARD -> 0xFFFFD700.toInt()   // Gold
            ChestType.SILVER -> 0xFFC0C0C0.toInt()      // Silver
            ChestType.GOLDEN -> 0xFFFFD700.toInt()      // Bright Gold
            ChestType.ETC -> 0xFFCD853F.toInt()         // Peru
            ChestType.CHEST_MINI -> 0xFFDAA520.toInt()  // Goldenrod
        }
    }
    
    /**
     * Get size multiplier
     */
    fun getSizeMultiplier(): Float {
        return when (chestType) {
            ChestType.GOLDEN -> 1.5f
            ChestType.SILVER -> 1.25f
            else -> 1.0f
        }
    }
    
    companion object {
        /**
         * Determine chest type from type string
         */
        fun parseChestType(typeString: String): ChestType {
            return when {
                typeString.contains("GOLDEN", ignoreCase = true) -> ChestType.GOLDEN
                typeString.contains("SILVER", ignoreCase = true) -> ChestType.SILVER
                typeString.contains("MINI", ignoreCase = true) -> ChestType.CHEST_MINI
                typeString.contains("ETC", ignoreCase = true) -> ChestType.ETC
                else -> ChestType.STANDARD
            }
        }
    }
}

/**
 * Chest types
 */
enum class ChestType(val displayName: String) {
    STANDARD("Chest"),
    SILVER("Silver Chest"),
    GOLDEN("Golden Chest"),
    ETC("ETC Chest"),
    CHEST_MINI("Mini Chest")
}

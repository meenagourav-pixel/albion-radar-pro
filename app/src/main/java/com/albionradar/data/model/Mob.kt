package com.albionradar.data.model

import com.albionradar.util.Constants

/**
 * Represents a mob/monster on the radar
 */
data class Mob(
    override val id: Long,
    override var posX: Float,
    override var posY: Float,
    val mobType: Int,
    val typeName: String,
    val tier: Int,
    val enchantLevel: Int,
    val health: Int = 100,
    val isAggressive: Boolean = false
) : Entity(id, posX, posY, typeName) {
    
    override val entityType: EntityType = EntityType.MOB
    
    /**
     * Get filter key for this mob type
     */
    fun getFilterKey(): String {
        return "mob_$mobType"
    }
    
    /**
     * Get mob category name
     */
    fun getCategoryName(): String {
        return when (mobType) {
            Constants.MobType.LIVING_HARVESTABLE -> "Living Harvestable"
            Constants.MobType.LIVING_SKINNABLE -> "Living Skinnable"
            Constants.MobType.ENEMY -> "Enemy"
            Constants.MobType.ENCHANTED_ENEMY -> "Enchanted Enemy"
            Constants.MobType.MINI_BOSS -> "Mini Boss"
            Constants.MobType.BOSS -> "Boss"
            Constants.MobType.DRONE -> "Drone"
            Constants.MobType.MIST_BOSS -> "Mist Boss"
            Constants.MobType.EVENTS -> "Events"
            else -> "Unknown"
        }
    }
    
    /**
     * Get display name with tier
     */
    fun getDisplayName(): String {
        val enchantSuffix = if (enchantLevel > 0) ".${enchantLevel}" else ""
        return "T$tier$enchantSuffix $typeName"
    }
    
    /**
     * Get color based on mob type
     */
    fun getColor(): Int {
        return when (mobType) {
            Constants.MobType.BOSS -> 0xFFFFD700.toInt()      // Gold
            Constants.MobType.MINI_BOSS -> 0xFFFF4500.toInt() // Orange-Red
            Constants.MobType.MIST_BOSS -> 0xFF9400D3.toInt() // Purple
            Constants.MobType.ENCHANTED_ENEMY -> 0xFFFF00FF.toInt() // Magenta
            Constants.MobType.DRONE -> 0xFF00FFFF.toInt()     // Cyan
            Constants.MobType.EVENTS -> 0xFFFFFF00.toInt()    // Yellow
            else -> 0xFFFF6B6B.toInt() // Light red for normal enemies
        }
    }
    
    /**
     * Get icon size multiplier based on importance
     */
    fun getSizeMultiplier(): Float {
        return when (mobType) {
            Constants.MobType.BOSS -> 2.0f
            Constants.MobType.MINI_BOSS -> 1.5f
            Constants.MobType.MIST_BOSS -> 1.5f
            Constants.MobType.ENCHANTED_ENEMY -> 1.25f
            else -> 1.0f
        }
    }
    
    /**
     * Check if this is a boss-type mob
     */
    fun isBossType(): Boolean {
        return mobType in listOf(
            Constants.MobType.BOSS,
            Constants.MobType.MINI_BOSS,
            Constants.MobType.MIST_BOSS
        )
    }
    
    /**
     * Check if should trigger alert
     */
    fun shouldAlert(): Boolean {
        return isBossType() || mobType == Constants.MobType.ENCHANTED_ENEMY
    }
    
    companion object {
        /**
         * Determine mob type from mob index/type
         */
        fun determineMobType(mobIndex: Int, typeName: String): Int {
            return when {
                typeName.contains("Boss", ignoreCase = true) && 
                    typeName.contains("Mist", ignoreCase = true) -> Constants.MobType.MIST_BOSS
                
                typeName.contains("Boss", ignoreCase = true) && 
                    typeName.contains("Mini", ignoreCase = true) -> Constants.MobType.MINI_BOSS
                
                typeName.contains("Boss", ignoreCase = true) -> Constants.MobType.BOSS
                
                typeName.contains("Drone", ignoreCase = true) -> Constants.MobType.DRONE
                
                typeName.contains("Enchanted", ignoreCase = true) -> Constants.MobType.ENCHANTED_ENEMY
                
                typeName.contains("Young", ignoreCase = true) ||
                    typeName.contains("Aged", ignoreCase = true) ||
                    typeName.contains("Elder", ignoreCase = true) ||
                    typeName.contains("Ancient", ignoreCase = true) -> Constants.MobType.LIVING_SKINNABLE
                
                typeName.contains("Gatherer", ignoreCase = true) ||
                    typeName.contains("Heret", ignoreCase = true) -> Constants.MobType.LIVING_HARVESTABLE
                
                mobIndex >= Constants.MOB_TIER_OFFSET -> Constants.MobType.ENEMY
                
                else -> Constants.MobType.ENEMY
            }
        }
    }
}

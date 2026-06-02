package com.albionradar.data.model

import com.albionradar.util.Constants

/**
 * Represents a harvestable resource on the radar
 */
data class Resource(
    override val id: Long,
    override var posX: Float,
    override var posY: Float,
    val resourceType: Int,
    val tier: Int,
    val enchantLevel: Int,
    val isLiving: Boolean,
    val typeId: Int,
    val state: ResourceState = ResourceState.AVAILABLE
) : Entity(id, posX, posY, getResourceTypeName(resourceType, isLiving)) {
    
    override val entityType: EntityType = EntityType.RESOURCE
    
    /**
     * Get unique filter key for this resource
     * Format: "resourceType_tier_enchant" (e.g., "0_4_2" for T4.2 Fiber)
     */
    fun getFilterKey(): String {
        return "${resourceType}_${tier}_${enchantLevel}"
    }
    
    /**
     * Get display name
     */
    fun getDisplayName(): String {
        val baseName = when (resourceType) {
            Constants.ResourceType.FIBER -> "Fiber"
            Constants.ResourceType.HIDE -> "Hide"
            Constants.ResourceType.WOOD -> "Wood"
            Constants.ResourceType.ORE -> "Ore"
            Constants.ResourceType.ROCK -> "Rock"
            else -> "Unknown"
        }
        val category = if (isLiving) "Living" else "Static"
        val enchantSuffix = if (enchantLevel > 0) ".${enchantLevel}" else ""
        return "$category T$tier$enchantSuffix $baseName"
    }
    
    /**
     * Get color based on tier
     */
    fun getColor(): Int {
        return when (tier) {
            1 -> 0xFFFFFFFF.toInt()
            2 -> 0xFF4CAF50.toInt()
            3 -> 0xFF2196F3.toInt()
            4 -> 0xFF9C27B0.toInt()
            5 -> 0xFFFF9800.toInt()
            6 -> 0xFFE91E63.toInt()
            7 -> 0xFFF44336.toInt()
            8 -> 0xFFFFD700.toInt()
            else -> 0xFFFFFFFF.toInt()
        }
    }
    
    /**
     * Get enchantment color
     */
    fun getEnchantColor(): Int {
        return when (enchantLevel) {
            1 -> 0xFF00FF00.toInt()
            2 -> 0xFF00BFFF.toInt()
            3 -> 0xFF9400D3.toInt()
            4 -> 0xFFFFD700.toInt()
            else -> 0xFFFFFFFF.toInt()
        }
    }
    
    /**
     * Check if resource is harvestable
     */
    fun isHarvestable(): Boolean {
        return state == ResourceState.AVAILABLE
    }
    
    companion object {
        fun getResourceTypeName(type: Int, isLiving: Boolean): String {
            val category = if (isLiving) "Living" else "Static"
            val typeName = when (type) {
                Constants.ResourceType.FIBER -> "Fiber"
                Constants.ResourceType.HIDE -> "Hide"
                Constants.ResourceType.WOOD -> "Wood"
                Constants.ResourceType.ORE -> "Ore"
                Constants.ResourceType.ROCK -> "Rock"
                else -> "Unknown"
            }
            return "$category $typeName"
        }
    }
}

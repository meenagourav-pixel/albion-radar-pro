package com.albionradar.data.model

import kotlin.math.sqrt

/**
 * Base class for all radar entities
 */
abstract class Entity(
    val id: Long,
    var posX: Float,
    var posY: Float,
    val typeName: String
) {
    abstract val entityType: EntityType
    
    /**
     * Calculate distance from player (center at 0,0)
     */
    fun distanceFromPlayer(): Float {
        return sqrt(posX * posX + posY * posY)
    }
    
    /**
     * Check if entity is within range
     */
    fun isInRange(range: Float): Boolean {
        return distanceFromPlayer() <= range
    }
}

/**
 * Entity types enumeration
 */
enum class EntityType {
    PLAYER,
    RESOURCE,
    MOB,
    CHEST,
    FISHING,
    MIST,
    DUNGEON
}

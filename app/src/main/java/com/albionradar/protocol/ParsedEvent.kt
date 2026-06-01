package com.albionradar.protocol

import com.albionradar.data.model.*

/**
 * Sealed class representing parsed events from protocol
 */
sealed class ParsedEvent {
    
    /**
     * New character/player detected
     */
    data class NewCharacter(val player: Player) : ParsedEvent()
    
    /**
     * Player movement
     */
    data class Move(val id: Long, val posX: Float, val posY: Float) : ParsedEvent()
    
    /**
     * Player/entity left
     */
    data class Leave(val id: Long) : ParsedEvent()
    
    /**
     * New resource detected
     */
    data class NewResource(val resource: Resource) : ParsedEvent()
    
    /**
     * Resource state changed
     */
    data class ResourceStateChange(val id: Long, val state: ResourceState) : ParsedEvent()
    
    /**
     * Resource harvested/removed
     */
    data class ResourceHarvested(val id: Long) : ParsedEvent()
    
    /**
     * New mob detected
     */
    data class NewMob(val mob: Mob) : ParsedEvent()
    
    /**
     * Mob movement
     */
    data class MobMove(val id: Long, val posX: Float, val posY: Float) : ParsedEvent()
    
    /**
     * Mob removed
     */
    data class MobRemoved(val id: Long) : ParsedEvent()
    
    /**
     * New chest detected
     */
    data class NewChest(val chest: Chest) : ParsedEvent()
    
    /**
     * Chest opened/removed
     */
    data class ChestRemoved(val id: Long) : ParsedEvent()
    
    /**
     * New fishing node detected
     */
    data class NewFishing(val fishing: FishingNode) : ParsedEvent()
    
    /**
     * Fishing node removed
     */
    data class FishingRemoved(val id: Long) : ParsedEvent()
    
    /**
     * New mist portal detected
     */
    data class NewMist(val mist: Mist) : ParsedEvent()
    
    /**
     * Mist portal removed
     */
    data class MistRemoved(val id: Long) : ParsedEvent()
    
    /**
     * New dungeon detected
     */
    data class NewDungeon(val dungeon: Dungeon) : ParsedEvent()
    
    /**
     * Dungeon removed
     */
    data class DungeonRemoved(val id: Long) : ParsedEvent()
    
    /**
     * Health update for entity
     */
    data class HealthUpdate(val id: Long, val health: Int) : ParsedEvent()
    
    /**
     * Local player info update
     */
    data class LocalPlayerUpdate(
        val id: Long,
        val name: String,
        val guildName: String?,
        val allianceName: String?
    ) : ParsedEvent()
    
    /**
     * Party member update
     */
    data class PartyMemberUpdate(val members: List<String>) : ParsedEvent()
}

package com.albionradar.data.model

/**
 * Represents a player entity on the radar
 */
data class Player(
    override val id: Long,
    override var posX: Float,
    override var posY: Float,
    val name: String,
    val guildName: String?,
    val allianceName: String?,
    val flag: Int,
    val isPartyMember: Boolean = false,
    val isGuildMember: Boolean = false,
    val isAllianceMember: Boolean = false,
    val health: Int = 100,
    val itemId: Int = 0
) : Entity(id, posX, posY, "Player") {
    
    override val entityType: EntityType = EntityType.PLAYER
    
    /**
     * Get player status based on flag
     */
    fun getStatus(): PlayerStatus {
        return when (flag) {
            Constants.PlayerFlags.PASSIVE -> PlayerStatus.PASSIVE
            in Constants.PlayerFlags.FACTION_START..Constants.PlayerFlags.FACTION_END -> PlayerStatus.FACTION
            Constants.PlayerFlags.HOSTILE -> PlayerStatus.HOSTILE
            else -> PlayerStatus.UNKNOWN
        }
    }
    
    /**
     * Check if player is hostile (should trigger alert)
     */
    fun isHostile(): Boolean {
        return flag == Constants.PlayerFlags.HOSTILE && 
               !isPartyMember && 
               !isGuildMember && 
               !isAllianceMember
    }
    
    /**
     * Get display color based on status
     */
    fun getColor(): Int {
        return when {
            isPartyMember -> Constants.PlayerColors.PARTY
            isGuildMember -> Constants.PlayerColors.GUILD
            isAllianceMember -> Constants.PlayerColors.ALLIANCE
            getStatus() == PlayerStatus.HOSTILE -> Constants.PlayerColors.HOSTILE
            getStatus() == PlayerStatus.FACTION -> Constants.PlayerColors.FACTION
            else -> Constants.PlayerColors.PASSIVE
        }
    }
    
    companion object {
        object Constants {
            val PlayerColors = com.albionradar.util.Constants.PlayerColors
            val PlayerFlags = com.albionradar.util.Constants.PlayerFlags
        }
    }
}

/**
 * Player status enumeration
 */
enum class PlayerStatus {
    PASSIVE,    // Green - Non-hostile
    FACTION,    // Orange - In a faction
    HOSTILE,    // Red - Hostile/Red player
    UNKNOWN     // Unknown status
}

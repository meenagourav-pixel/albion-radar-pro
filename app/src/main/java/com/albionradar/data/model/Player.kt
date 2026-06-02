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
            PlayerConstants.PlayerFlags.PASSIVE -> PlayerStatus.PASSIVE
            in PlayerConstants.PlayerFlags.FACTION_START..PlayerConstants.PlayerFlags.FACTION_END -> PlayerStatus.FACTION
            PlayerConstants.PlayerFlags.HOSTILE -> PlayerStatus.HOSTILE
            else -> PlayerStatus.UNKNOWN
        }
    }
    
    /**
     * Check if player is hostile (should trigger alert)
     */
    fun isHostile(): Boolean {
        return flag == PlayerConstants.PlayerFlags.HOSTILE && 
               !isPartyMember && 
               !isGuildMember && 
               !isAllianceMember
    }
    
    /**
     * Get display color based on status
     */
    fun getColor(): Int {
        return when {
            isPartyMember -> PlayerConstants.PlayerColors.PARTY
            isGuildMember -> PlayerConstants.PlayerColors.GUILD
            isAllianceMember -> PlayerConstants.PlayerColors.ALLIANCE
            getStatus() == PlayerStatus.HOSTILE -> PlayerConstants.PlayerColors.HOSTILE
            getStatus() == PlayerStatus.FACTION -> PlayerConstants.PlayerColors.FACTION
            else -> PlayerConstants.PlayerColors.PASSIVE
        }
    }
}

/**
 * Player constants
 */
object PlayerConstants {
    object PlayerFlags {
        const val PASSIVE = 0
        const val FACTION_START = 1
        const val FACTION_END = 6
        const val HOSTILE = 255
    }
    
    object PlayerColors {
        const val PASSIVE = 0x00FF88
        const val HOSTILE = 0xFF0000
        const val FACTION = 0xFFA500
        const val GUILD = 0x0088FF
        const val PARTY = 0x88FF00
        const val ALLIANCE = 0x00FFFF
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

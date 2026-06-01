package com.albionradar.util

/**
 * Game constants for Albion Online
 */
object Constants {
    // Network
    const val ALBION_PORT = 5056
    const val BUFFER_SIZE = 65536
    const val PACKET_TIMEOUT_MS = 5000L
    
    // Protocol
    const val MAGIC_BYTE_UNENCRYPTED = 0x00
    const val MAGIC_BYTE_ENCRYPTED = 0x01
    const val PROTOCOL_TYPE_GPBINARY_V18 = 1
    const val OPERATION_EVENT = 2
    const val OPERATION_RESPONSE = 3
    const val OPERATION_REQUEST = 4
    
    // Event Codes (from OpenRadar)
    object Events {
        const val LEAVE = 1
        const val MOVE = 3
        const val HEALTH_UPDATE = 6
        const val NEW_CHARACTER = 29
        const val NEW_SIMPLE_HARVESTABLE_LIST = 38
        const val NEW_HARVESTABLE_OBJECT = 40
        const val HARVESTABLE_CHANGE_STATE = 46
        const val NEW_HARVESTABLE_OBJECT_BATCH = 59
        const val HARVEST_FINISHED = 61
        const val NEW_MOB = 123
        const val NEW_CAGED_OBJECT = 530
    }
    
    // Player Flags
    object PlayerFlags {
        const val PASSIVE = 0
        const val FACTION_START = 1
        const val FACTION_END = 6
        const val HOSTILE = 255
    }
    
    // Player Colors (hex without alpha)
    object PlayerColors {
        const val PASSIVE = 0x00FF88
        const val HOSTILE = 0xFF0000
        const val FACTION = 0xFFA500
        const val GUILD = 0x0088FF
        const val PARTY = 0x88FF00
        const val ALLIANCE = 0x00FFFF
    }
    
    // Resource Types
    object ResourceType {
        const val FIBER = 0
        const val HIDE = 1
        const val WOOD = 2
        const val ORE = 3
        const val ROCK = 4
    }
    
    // Resource Type Names (for parsing)
    val RESOURCE_NAMES = mapOf(
        "FIBER" to ResourceType.FIBER,
        "HIDE" to ResourceType.HIDE,
        "WOOD" to ResourceType.WOOD,
        "ORE" to ResourceType.ORE,
        "ROCK" to ResourceType.ROCK
    )
    
    // Resource Category
    object ResourceCategory {
        const val STATIC = 0
        const val LIVING = 1
    }
    
    // Tiers
    object Tier {
        const val MIN = 1
        const val MAX = 8
    }
    
    // Enchantments
    object Enchant {
        const val MIN = 0
        const val MAX = 4
    }
    
    // Mob Types (from OpenRadar categories)
    object MobType {
        const val LIVING_HARVESTABLE = 0
        const val LIVING_SKINNABLE = 1
        const val ENEMY = 2
        const val ENCHANTED_ENEMY = 3
        const val MINI_BOSS = 4
        const val BOSS = 5
        const val DRONE = 6
        const val MIST_BOSS = 7
        const val EVENTS = 8
    }
    
    // Mob Type Names for filtering
    val MOB_TYPE_NAMES = arrayOf(
        "LivingHarvestable",
        "LivingSkinnable", 
        "Enemy",
        "EnchantedEnemy",
        "MiniBoss",
        "Boss",
        "Drone",
        "MistBoss",
        "Events"
    )
    
    // Mob Tier Offset (from OpenRadar)
    const val MOB_TIER_OFFSET = 16
    
    // Position XOR keys for movement encryption
    val POSITION_XOR_KEYS = intArrayOf(
        0x00000000, 0x00000000, 0x00000000, 0x00000000
    )
    
    // Alert Types
    object AlertType {
        const val HOSTILE_PLAYER = 0
        const val RESOURCE = 1
        const val MOB = 2
        const val CHEST = 3
    }
    
    // Notification IDs
    object NotificationId {
        const val SERVICE = 1001
        const val ALERT_HOSTILE = 2001
        const val ALERT_RESOURCE = 2002
        const val ALERT_MOB = 2003
    }
}

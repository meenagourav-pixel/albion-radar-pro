package com.albionradar.util

import com.albionradar.data.model.Mob

/**
 * Maps mob type strings from the game protocol to internal types
 */
object MobTypeMapper {
    
    // Boss identifiers
    private val bossNames = setOf(
        "BOSS", "OVERLORD", "COMMANDER", "LORD", "KING", "QUEEN",
        "GATEKEEPER", "GUARDIAN", "TITAN", "ANCIENT"
    )
    
    // Mini boss identifiers
    private val miniBossNames = setOf(
        "MINIBOSS", "MINI_BOSS", "ELITE", "CHAMPION", "VETERAN",
        "VETERANGUARD", "AUGMENTED"
    )
    
    // Mist boss identifiers
    private val mistBossNames = setOf(
        "MISTBOSS", "MIST_BOSS", "FAIRY", "PIXIE", "WISP"
    )
    
    // Drone identifiers
    private val droneNames = setOf(
        "DRONE", "SENTINEL", "SCOUT", "OBSERVER"
    )
    
    // Enchanted enemy identifiers
    private val enchantedNames = setOf(
        "ENCHANTED", "AUGMENTED", "CORRUPTED", "EMPOWERED"
    )
    
    // Living harvestable identifiers (mob versions of resources)
    private val livingHarvestableNames = setOf(
        "GATHERER", "HERET", "MISCRAFTER", "COLLECTOR"
    )
    
    // Living skinnable identifiers
    private val livingSkinnableNames = setOf(
        "YOUNG", "ELDER", "MATURE", "ANCIENT", "BEAST", "BEARA",
        "MOABIRD", "DIREBEAR", "DIREBOAR", "DIREWOLF"
    )
    
    // Event mob identifiers
    private val eventNames = setOf(
        "EVENT", "INVASION", "RAID", "HOLIDAY", "SPECIAL"
    )
    
    /**
     * Determine mob type from name and index
     */
    fun determineMobType(mobName: String, mobIndex: Int): Int {
        val upper = mobName.uppercase()
        
        // Check for specific types in order of priority
        return when {
            // Mist boss
            mistBossNames.any { upper.contains(it) } -> Constants.MobType.MIST_BOSS
            
            // Boss
            bossNames.any { upper.contains(it) } -> Constants.MobType.BOSS
            
            // Mini boss
            miniBossNames.any { upper.contains(it) } -> Constants.MobType.MINI_BOSS
            
            // Drone
            droneNames.any { upper.contains(it) } -> Constants.MobType.DRONE
            
            // Enchanted
            enchantedNames.any { upper.contains(it) } -> Constants.MobType.ENCHANTED_ENEMY
            
            // Living harvestable (resource mobs)
            livingHarvestableNames.any { upper.contains(it) } -> Constants.MobType.LIVING_HARVESTABLE
            
            // Living skinnable
            livingSkinnableNames.any { upper.contains(it) } -> Constants.MobType.LIVING_SKINNABLE
            
            // Event mobs
            eventNames.any { upper.contains(it) } -> Constants.MobType.EVENTS
            
            // Default to regular enemy
            else -> Constants.MobType.ENEMY
        }
    }
    
    /**
     * Calculate tier from mob index
     */
    fun calculateTier(mobIndex: Int): Int {
        // Mob tier is encoded in the index
        // T1 = 0-15, T2 = 16-31, etc. (each tier has 16 variants)
        return (mobIndex / Constants.MOB_TIER_OFFSET) + 1
    }
    
    /**
     * Calculate enchant level from mob index
     */
    fun calculateEnchantLevel(mobIndex: Int): Int {
        // Enchant level is in the lower bits of the mob index
        // Within each tier (16 mobs), there are 4 base types × 4 enchant levels
        return (mobIndex % 4)
    }
    
    /**
     * Get mob info from index and name
     */
    fun getMobInfo(mobName: String, mobIndex: Int): MobInfo {
        return MobInfo(
            mobType = determineMobType(mobName, mobIndex),
            tier = calculateTier(mobIndex).coerceIn(1, 8),
            enchantLevel = calculateEnchantLevel(mobIndex)
        )
    }
    
    /**
     * Mob info data class
     */
    data class MobInfo(
        val mobType: Int,
        val tier: Int,
        val enchantLevel: Int
    )
}

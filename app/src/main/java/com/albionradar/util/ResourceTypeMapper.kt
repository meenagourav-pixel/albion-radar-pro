package com.albionradar.util

/**
 * Maps resource type strings from the game protocol to internal types
 */
object ResourceTypeMapper {
    
    // Resource type mapping from game strings
    private val fiberTypes = setOf(
        "FIBER", "FIBERS", "COTTON", "FLAX", "HEMP", "NETTLE", "SKYFLOWER",
        "TEPLANT", "MARTLOCK", "BRIDGEHELM", "STAYFIBER"
    )
    
    private val hideTypes = setOf(
        "HIDE", "HIDES", "SKIN", "LEATHER", "FUR", "ANIMALHIDE",
        "CREATUREHIDE", "ToughHIDE", "RUGGEDHIDE", "ThickHIDE"
    )
    
    private val woodTypes = setOf(
        "WOOD", "TIMBER", "LOG", "PLANK", "TREE", "OAK", "BIRCH",
        "CEDAR", "PINE", "ASH", "ELM", "MOHOGANY", "REDWOOD", "ANCIENTWOOD"
    )
    
    private val oreTypes = setOf(
        "ORE", "METAL", "IRON", "COPPER", "TIN", "SILVER", "GOLD",
        "TITANIUM", "PLATINUM", "MITHRIL", "RUNITE", "GALVANITE", "TINORE"
    )
    
    private val rockTypes = setOf(
        "ROCK", "STONE", "SLATE", "GRANITE", "LIMESTONE", "SANDSTONE",
        "TRAVERTINE", "MARBLE", "BASALT", "OBSIDIAN", "CHANTEDROCK"
    )
    
    // Living resource indicators
    private val livingIndicators = setOf(
        "LIVING", "GIFTCOIN", "YOUNG", "ELDER", "ANCIENT", "MATURE"
    )
    
    /**
     * Get resource type from string
     */
    fun getResourceType(typeString: String): Int {
        val upper = typeString.uppercase()
        
        return when {
            fiberTypes.any { upper.contains(it) } -> Constants.ResourceType.FIBER
            hideTypes.any { upper.contains(it) } -> Constants.ResourceType.HIDE
            woodTypes.any { upper.contains(it) } -> Constants.ResourceType.WOOD
            oreTypes.any { upper.contains(it) } -> Constants.ResourceType.ORE
            rockTypes.any { upper.contains(it) } -> Constants.ResourceType.ROCK
            else -> -1 // Unknown
        }
    }
    
    /**
     * Check if resource is living type
     */
    fun isLivingResource(typeString: String): Boolean {
        val upper = typeString.uppercase()
        return livingIndicators.any { upper.contains(it) }
    }
    
    /**
     * Extract tier from type string
     */
    fun extractTier(typeString: String): Int {
        // Look for tier patterns: T1-T8, TIER1-TIER8, @1-@8
        val tierPatterns = listOf(
            Regex("(?i)T([1-8])"),
            Regex("(?i)TIER([1-8])"),
            Regex("@([1-8])")
        )
        
        for (pattern in tierPatterns) {
            val match = pattern.find(typeString)
            if (match != null && match.groupValues.size > 1) {
                return match.groupValues[1].toIntOrNull() ?: 1
            }
        }
        
        return 1
    }
    
    /**
     * Extract enchant level from type string
     */
    fun extractEnchantLevel(typeString: String): Int {
        // Look for enchant patterns: .1-.4, LEVEL1-LEVEL4, E1-E4
        val enchantPatterns = listOf(
            Regex("\\.([0-4])"),
            Regex("(?i)LEVEL([0-4])"),
            Regex("(?i)E([0-4])"),
            Regex("@([0-4])(?!\\d)") // @1-@4 for enchant in some formats
        )
        
        for (pattern in enchantPatterns) {
            val match = pattern.find(typeString)
            if (match != null && match.groupValues.size > 1) {
                return match.groupValues[1].toIntOrNull() ?: 0
            }
        }
        
        return 0
    }
    
    /**
     * Parse resource info from type string
     */
    fun parseResourceInfo(typeString: String, typeId: Int): ResourceInfo {
        return ResourceInfo(
            type = getResourceType(typeString).let { if (it == -1) typeId % 5 else it },
            tier = extractTier(typeString),
            enchantLevel = extractEnchantLevel(typeString),
            isLiving = isLivingResource(typeString)
        )
    }
    
    /**
     * Resource info data class
     */
    data class ResourceInfo(
        val type: Int,
        val tier: Int,
        val enchantLevel: Int,
        val isLiving: Boolean
    )
}

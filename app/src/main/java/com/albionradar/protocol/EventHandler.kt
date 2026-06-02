package com.albionradar.protocol

import com.albionradar.data.model.*
import com.albionradar.util.Constants
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Handles parsing of specific event types
 */
class EventHandler {
    
    /**
     * Parse NewCharacter event (code 29)
     */
    fun parseNewCharacter(buffer: ByteBuffer, paramCount: Int): ParsedEvent? {
        try {
            val parameters = parseParameters(buffer, paramCount)
            
            val id = parameters[0]?.asLong() ?: return null
            val name = parameters[1]?.asString() ?: "Unknown"
            val guildName = parameters.getOrNull(2)?.asString()
            val allianceName = parameters.getOrNull(3)?.asString()
            val flag = parameters.getOrNull(4)?.asInt() ?: 0
            val itemId = parameters.getOrNull(5)?.asInt() ?: 0
            
            val posX = parameters.getOrNull(10)?.asFloat() ?: 0f
            val posY = parameters.getOrNull(11)?.asFloat() ?: 0f
            
            val player = Player(
                id = id,
                posX = posX,
                posY = posY,
                name = name,
                guildName = guildName,
                allianceName = allianceName,
                flag = flag,
                itemId = itemId
            )
            
            return ParsedEvent.NewCharacter(player)
            
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    
    /**
     * Parse Move event (code 3)
     */
    fun parseMove(buffer: ByteBuffer, paramCount: Int): ParsedEvent? {
        try {
            val parameters = parseParameters(buffer, paramCount)
            
            val id = parameters[0]?.asLong() ?: return null
            val posBytes = parameters[1]?.asByteArray() ?: return null
            
            val posX = decodePosition(posBytes, 0)
            val posY = decodePosition(posBytes, 8)
            
            return ParsedEvent.Move(id, posX, posY)
            
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    
    /**
     * Parse Leave event (code 1)
     */
    fun parseLeave(buffer: ByteBuffer, paramCount: Int): ParsedEvent? {
        try {
            val parameters = parseParameters(buffer, paramCount)
            val id = parameters[0]?.asLong() ?: return null
            return ParsedEvent.Leave(id)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    
    /**
     * Parse NewHarvestableObject event (code 40)
     */
    fun parseNewHarvestableObject(buffer: ByteBuffer, paramCount: Int): ParsedEvent? {
        try {
            val parameters = parseParameters(buffer, paramCount)
            
            val id = parameters[0]?.asLong() ?: return null
            val typeId = parameters[1]?.asInt() ?: 0
            val typeString = parameters.getOrNull(2)?.asString() ?: ""
            
            val resourceInfo = parseResourceType(typeString, typeId)
            
            val posX = parameters.getOrNull(3)?.asFloat() ?: 0f
            val posY = parameters.getOrNull(4)?.asFloat() ?: 0f
            
            val resource = Resource(
                id = id,
                posX = posX,
                posY = posY,
                resourceType = resourceInfo.type,
                tier = resourceInfo.tier,
                enchantLevel = resourceInfo.enchantLevel,
                isLiving = resourceInfo.isLiving,
                typeId = typeId
            )
            
            return ParsedEvent.NewResource(resource)
            
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    
    /**
     * Parse NewSimpleHarvestableList event (code 38)
     */
    fun parseNewSimpleHarvestableList(buffer: ByteBuffer, paramCount: Int): List<ParsedEvent> {
        val events = mutableListOf<ParsedEvent>()
        
        try {
            val parameters = parseParameters(buffer, paramCount)
            
            for (i in 0 until paramCount step 5) {
                val id = parameters.getOrNull(i)?.asLong() ?: continue
                val typeId = parameters.getOrNull(i + 1)?.asInt() ?: 0
                val typeString = parameters.getOrNull(i + 2)?.asString() ?: ""
                val posX = parameters.getOrNull(i + 3)?.asFloat() ?: 0f
                val posY = parameters.getOrNull(i + 4)?.asFloat() ?: 0f
                
                val resourceInfo = parseResourceType(typeString, typeId)
                
                val resource = Resource(
                    id = id,
                    posX = posX,
                    posY = posY,
                    resourceType = resourceInfo.type,
                    tier = resourceInfo.tier,
                    enchantLevel = resourceInfo.enchantLevel,
                    isLiving = resourceInfo.isLiving,
                    typeId = typeId
                )
                
                events.add(ParsedEvent.NewResource(resource))
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return events
    }
    
    /**
     * Parse NewHarvestableObjectBatch event (code 59)
     */
    fun parseNewHarvestableObjectBatch(buffer: ByteBuffer, paramCount: Int): List<ParsedEvent> {
        val events = mutableListOf<ParsedEvent>()
        
        try {
            val parameters = parseParameters(buffer, paramCount)
            val count = parameters.getOrNull(0)?.asInt() ?: 0
            
            for (i in 0 until count) {
                val baseIndex = 1 + i * 5
                
                val id = parameters.getOrNull(baseIndex)?.asLong() ?: continue
                val typeId = parameters.getOrNull(baseIndex + 1)?.asInt() ?: 0
                val typeString = parameters.getOrNull(baseIndex + 2)?.asString() ?: ""
                val posX = parameters.getOrNull(baseIndex + 3)?.asFloat() ?: 0f
                val posY = parameters.getOrNull(baseIndex + 4)?.asFloat() ?: 0f
                
                val resourceInfo = parseResourceType(typeString, typeId)
                
                val resource = Resource(
                    id = id,
                    posX = posX,
                    posY = posY,
                    resourceType = resourceInfo.type,
                    tier = resourceInfo.tier,
                    enchantLevel = resourceInfo.enchantLevel,
                    isLiving = resourceInfo.isLiving,
                    typeId = typeId
                )
                
                events.add(ParsedEvent.NewResource(resource))
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return events
    }
    
    /**
     * Parse HarvestableChangeState event (code 46)
     */
    fun parseHarvestableChangeState(buffer: ByteBuffer, paramCount: Int): ParsedEvent? {
        try {
            val parameters = parseParameters(buffer, paramCount)
            
            val id = parameters[0]?.asLong() ?: return null
            val state = parameters.getOrNull(1)?.asInt() ?: 0
            
            val resourceState = when (state) {
                0 -> ResourceState.AVAILABLE
                1 -> ResourceState.IN_PROGRESS
                2 -> ResourceState.DEPLETED
                else -> ResourceState.AVAILABLE
            }
            
            return ParsedEvent.ResourceStateChange(id, resourceState)
            
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    
    /**
     * Parse HarvestFinished event (code 61)
     */
    fun parseHarvestFinished(buffer: ByteBuffer, paramCount: Int): ParsedEvent? {
        try {
            val parameters = parseParameters(buffer, paramCount)
            val id = parameters[0]?.asLong() ?: return null
            return ParsedEvent.ResourceHarvested(id)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    
    /**
     * Parse NewMob event (code 123)
     */
    fun parseNewMob(buffer: ByteBuffer, paramCount: Int): ParsedEvent? {
        try {
            val parameters = parseParameters(buffer, paramCount)
            
            val id = parameters[0]?.asLong() ?: return null
            val mobIndex = parameters[1]?.asInt() ?: 0
            val typeName = parameters.getOrNull(2)?.asString() ?: "Unknown"
            
            val posX = parameters.getOrNull(3)?.asFloat() ?: 0f
            val posY = parameters.getOrNull(4)?.asFloat() ?: 0f
            
            val mobType = Mob.determineMobType(mobIndex, typeName)
            val tier = if (mobIndex >= Constants.MOB_TIER_OFFSET) {
                (mobIndex / Constants.MOB_TIER_OFFSET) + 1
            } else {
                (mobIndex / 16) + 1
            }
            
            val enchantLevel = (mobIndex % 16) / 4
            
            val mob = Mob(
                id = id,
                posX = posX,
                posY = posY,
                mobType = mobType,
                typeName = typeName,
                tier = tier,
                enchantLevel = enchantLevel
            )
            
            return ParsedEvent.NewMob(mob)
            
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    
    /**
     * Parse NewCagedObject event (code 530) - Chests, Mists, Dungeons
     */
    fun parseNewCagedObject(buffer: ByteBuffer, paramCount: Int): ParsedEvent? {
        try {
            val parameters = parseParameters(buffer, paramCount)
            
            val id = parameters[0]?.asLong() ?: return null
            val typeId = parameters[1]?.asInt() ?: 0
            val typeString = parameters.getOrNull(2)?.asString() ?: ""
            
            val posX = parameters.getOrNull(3)?.asFloat() ?: 0f
            val posY = parameters.getOrNull(4)?.asFloat() ?: 0f
            
            when {
                typeString.contains("CHEST", ignoreCase = true) -> {
                    val chestType = Chest.parseChestType(typeString)
                    val tier = extractTierFromTypeString(typeString)
                    
                    val chest = Chest(
                        id = id,
                        posX = posX,
                        posY = posY,
                        chestType = chestType,
                        tier = tier
                    )
                    return ParsedEvent.NewChest(chest)
                }
                
                typeString.contains("MIST", ignoreCase = true) -> {
                    val mistType = Mist.parseMistType(typeString)
                    val tier = extractTierFromTypeString(typeString)
                    
                    val mist = Mist(
                        id = id,
                        posX = posX,
                        posY = posY,
                        mistType = mistType,
                        tier = tier
                    )
                    return ParsedEvent.NewMist(mist)
                }
                
                typeString.contains("DUNGEON", ignoreCase = true) ||
                typeString.contains("HIDEOUT", ignoreCase = true) ||
                typeString.contains("HELLGATE", ignoreCase = true) -> {
                    val dungeonType = Dungeon.parseDungeonType(typeString)
                    val tier = extractTierFromTypeString(typeString)
                    
                    val dungeon = Dungeon(
                        id = id,
                        posX = posX,
                        posY = posY,
                        dungeonType = dungeonType,
                        tier = tier
                    )
                    return ParsedEvent.NewDungeon(dungeon)
                }
                
                typeString.contains("FISH", ignoreCase = true) -> {
                    val fishType = FishingNode.parseFishType(typeString)
                    val tier = extractTierFromTypeString(typeString)
                    val enchantLevel = extractEnchantFromTypeString(typeString)
                    
                    val fishing = FishingNode(
                        id = id,
                        posX = posX,
                        posY = posY,
                        fishType = fishType,
                        tier = tier,
                        enchantLevel = enchantLevel
                    )
                    return ParsedEvent.NewFishing(fishing)
                }
            }
            
            return null
            
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    
    /**
     * Parse HealthUpdate event (code 6)
     */
    fun parseHealthUpdate(buffer: ByteBuffer, paramCount: Int): ParsedEvent? {
        try {
            val parameters = parseParameters(buffer, paramCount)
            
            val id = parameters[0]?.asLong() ?: return null
            val health = parameters.getOrNull(1)?.asInt() ?: 100
            
            return ParsedEvent.HealthUpdate(id, health)
            
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    
    /**
     * Parse protocol parameters from buffer
     */
    private fun parseParameters(buffer: ByteBuffer, count: Int): List<ParameterValue?> {
        val parameters = mutableListOf<ParameterValue?>()
        
        for (i in 0 until count) {
            try {
                val param = parseParameter(buffer)
                parameters.add(param)
            } catch (e: Exception) {
                parameters.add(null)
            }
        }
        
        return parameters
    }
    
    /**
     * Parse a single parameter
     */
    private fun parseParameter(buffer: ByteBuffer): ParameterValue? {
        if (buffer.remaining() < 1) return null
        
        val type = buffer.get().toInt() and 0xFF
        
        return when (type) {
            0 -> ParameterValue(null)
            1 -> ParameterValue(buffer.get().toInt())
            2 -> {
                if (buffer.remaining() >= 2) ParameterValue(buffer.short.toInt()) else null
            }
            3 -> {
                if (buffer.remaining() >= 4) ParameterValue(buffer.int) else null
            }
            4 -> {
                if (buffer.remaining() >= 8) ParameterValue(buffer.long) else null
            }
            5 -> {
                if (buffer.remaining() >= 4) ParameterValue(buffer.float) else null
            }
            6 -> {
                if (buffer.remaining() >= 8) ParameterValue(buffer.double) else null
            }
            7 -> ParameterValue(buffer.get().toInt() != 0)
            8 -> {
                if (buffer.remaining() >= 2) {
                    val length = buffer.short.toInt() and 0xFFFF
                    if (buffer.remaining() >= length) {
                        val bytes = ByteArray(length)
                        buffer.get(bytes)
                        ParameterValue(String(bytes, Charsets.UTF_8))
                    } else null
                } else null
            }
            9 -> {
                if (buffer.remaining() >= 4) {
                    val length = buffer.int
                    if (buffer.remaining() >= length && length > 0 && length < 65536) {
                        val bytes = ByteArray(length)
                        buffer.get(bytes)
                        ParameterValue(bytes)
                    } else null
                } else null
            }
            10 -> {
                if (buffer.remaining() >= 4) {
                    val arrayLength = buffer.int
                    val array = mutableListOf<ParameterValue?>()
                    for (j in 0 until arrayLength) {
                        array.add(parseParameter(buffer))
                    }
                    ParameterValue(array.toTypedArray())
                } else null
            }
            11 -> {
                if (buffer.remaining() >= 4) {
                    val dictLength = buffer.int
                    val dict = mutableMapOf<String, ParameterValue?>()
                    for (j in 0 until dictLength) {
                        val keyLen = buffer.short.toInt() and 0xFFFF
                        val keyBytes = ByteArray(keyLen)
                        buffer.get(keyBytes)
                        val key = String(keyBytes, Charsets.UTF_8)
                        dict[key] = parseParameter(buffer)
                    }
                    ParameterValue(dict)
                } else null
            }
            else -> null
        }
    }
    
    /**
     * Decode position from bytes (XOR encoded)
     */
    private fun decodePosition(bytes: ByteArray, offset: Int): Float {
        if (bytes.size < offset + 8) return 0f
        
        val buffer = ByteBuffer.wrap(bytes, offset, 8).order(ByteOrder.LITTLE_ENDIAN)
        val rawValue = buffer.long
        val decoded = rawValue
        
        return java.lang.Float.intBitsToFloat((decoded and 0xFFFFFFFF).toInt())
    }
    
    /**
     * Parse resource type from type string
     */
    private fun parseResourceType(typeString: String, typeId: Int): ResourceInfo {
        val resourceType = when {
            typeString.contains("FIBER", ignoreCase = true) -> Constants.ResourceType.FIBER
            typeString.contains("HIDE", ignoreCase = true) -> Constants.ResourceType.HIDE
            typeString.contains("WOOD", ignoreCase = true) -> Constants.ResourceType.WOOD
            typeString.contains("ORE", ignoreCase = true) -> Constants.ResourceType.ORE
            typeString.contains("ROCK", ignoreCase = true) -> Constants.ResourceType.ROCK
            else -> typeId % 5
        }
        
        val tier = extractTierFromTypeString(typeString)
        val enchantLevel = extractEnchantFromTypeString(typeString)
        val isLiving = typeString.contains("LIVING", ignoreCase = true)
        
        return ResourceInfo(resourceType, tier, enchantLevel, isLiving)
    }
    
    /**
     * Extract tier from type string
     */
    private fun extractTierFromTypeString(typeString: String): Int {
        val tierMatch = Regex("(?i)TIER([1-8])|T([1-8])|@([1-8])").find(typeString)
        return tierMatch?.groupValues?.filter { it.isNotEmpty() }?.last()?.toIntOrNull() ?: 1
    }
    
    /**
     * Extract enchant level from type string
     */
    private fun extractEnchantFromTypeString(typeString: String): Int {
        val enchantMatch = Regex("(?i)LEVEL([0-4])|@([0-4])|\\.([0-4])").find(typeString)
        return enchantMatch?.groupValues?.filter { it.isNotEmpty() }?.last()?.toIntOrNull() ?: 0
    }
    
    private data class ResourceInfo(
        val type: Int,
        val tier: Int,
        val enchantLevel: Int,
        val isLiving: Boolean
    )
}

class ParameterValue(private val value: Any?) {
    fun asInt(): Int = when (value) {
        is Int -> value
        is Short -> value.toInt()
        is Byte -> value.toInt()
        is Long -> value.toInt()
        is Number -> value.toInt()
        else -> 0
    }
    
    fun asLong(): Long = when (value) {
        is Long -> value
        is Int -> value.toLong()
        is Number -> value.toLong()
        else -> 0L
    }
    
    fun asFloat(): Float = when (value) {
        is Float -> value
        is Double -> value.toFloat()
        is Number -> value.toFloat()
        else -> 0f
    }
    
    fun asString(): String = value?.toString() ?: ""
    
    fun asByteArray(): ByteArray = when (value) {
        is ByteArray -> value
        else -> ByteArray(0)
    }
    
    fun isNull(): Boolean = value == null
}

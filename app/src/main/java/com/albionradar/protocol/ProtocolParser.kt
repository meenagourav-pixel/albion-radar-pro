package com.albionradar.protocol

import com.albionradar.util.Constants
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Main protocol parser for Albion Online packets
 * Handles GpBinaryV18 protocol parsing
 */
class ProtocolParser {
    
    private val eventHandler = EventHandler()
    
    // Fragment buffer for reassembling split packets
    private val fragmentBuffer = mutableMapOf<Int, ByteArray>()
    
    /**
     * Parse a packet and return parsed events
     */
    fun parsePacket(data: ByteArray, length: Int): List<ParsedEvent> {
        if (length < 2) return emptyList()
        
        val events = mutableListOf<ParsedEvent>()
        
        try {
            val buffer = ByteBuffer.wrap(data, 0, length).order(ByteOrder.LITTLE_ENDIAN)
            
            // Check magic byte for encryption
            val magicByte = buffer.get().toInt() and 0xFF
            
            // Skip encrypted packets (we need decryption key)
            if (magicByte == Constants.MAGIC_BYTE_ENCRYPTED) {
                // Encrypted packets cannot be parsed without the key
                return emptyList()
            }
            
            // Parse operation type
            val operationType = buffer.get().toInt() and 0xFF
            
            when (operationType) {
                Constants.OPERATION_EVENT -> {
                    events.addAll(parseEvent(buffer))
                }
                Constants.OPERATION_RESPONSE -> {
                    // Handle response packets if needed
                }
                Constants.OPERATION_REQUEST -> {
                    // Handle request packets if needed  
                }
            }
            
        } catch (e: Exception) {
            // Log parsing error but don't crash
            e.printStackTrace()
        }
        
        return events
    }
    
    /**
     * Parse an event packet
     */
    private fun parseEvent(buffer: ByteBuffer): List<ParsedEvent> {
        val events = mutableListOf<ParsedEvent>()
        
        try {
            // Read event code
            val eventCode = buffer.short.toInt() and 0xFFFF
            
            // Read parameter count
            val paramCount = buffer.get().toInt() and 0xFF
            
            // Parse based on event code
            when (eventCode) {
                Constants.Events.NEW_CHARACTER -> {
                    val event = eventHandler.parseNewCharacter(buffer, paramCount)
                    if (event != null) events.add(event)
                }
                Constants.Events.MOVE -> {
                    val event = eventHandler.parseMove(buffer, paramCount)
                    if (event != null) events.add(event)
                }
                Constants.Events.LEAVE -> {
                    val event = eventHandler.parseLeave(buffer, paramCount)
                    if (event != null) events.add(event)
                }
                Constants.Events.NEW_HARVESTABLE_OBJECT -> {
                    val event = eventHandler.parseNewHarvestableObject(buffer, paramCount)
                    if (event != null) events.add(event)
                }
                Constants.Events.NEW_SIMPLE_HARVESTABLE_LIST -> {
                    events.addAll(eventHandler.parseNewSimpleHarvestableList(buffer, paramCount))
                }
                Constants.Events.NEW_HARVESTABLE_OBJECT_BATCH -> {
                    events.addAll(eventHandler.parseNewHarvestableObjectBatch(buffer, paramCount))
                }
                Constants.Events.HARVESTABLE_CHANGE_STATE -> {
                    val event = eventHandler.parseHarvestableChangeState(buffer, paramCount)
                    if (event != null) events.add(event)
                }
                Constants.Events.HARVEST_FINISHED -> {
                    val event = eventHandler.parseHarvestFinished(buffer, paramCount)
                    if (event != null) events.add(event)
                }
                Constants.Events.NEW_MOB -> {
                    val event = eventHandler.parseNewMob(buffer, paramCount)
                    if (event != null) events.add(event)
                }
                Constants.Events.NEW_CAGED_OBJECT -> {
                    val event = eventHandler.parseNewCagedObject(buffer, paramCount)
                    if (event != null) events.add(event)
                }
                Constants.Events.HEALTH_UPDATE -> {
                    val event = eventHandler.parseHealthUpdate(buffer, paramCount)
                    if (event != null) events.add(event)
                }
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return events
    }
    
    /**
     * Clear fragment buffer
     */
    fun clearFragments() {
        fragmentBuffer.clear()
    }
}

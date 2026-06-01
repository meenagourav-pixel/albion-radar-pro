package com.albionradar.service

import com.albionradar.data.model.*
import com.albionradar.protocol.EventHandler
import com.albionradar.protocol.ParsedEvent
import com.albionradar.protocol.ProtocolParser
import com.albionradar.util.Constants
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Processes raw network packets and extracts game events
 */
class PacketProcessor {

    private val parser = ProtocolParser()
    private val eventHandler = EventHandler()
    
    // Callback for processed events
    var onEventProcessed: ((ParsedEvent) -> Unit)? = null
    
    /**
     * Process a raw packet
     */
    fun processPacket(data: ByteArray, length: Int) {
        if (length < 20) return // Minimum packet size
        
        try {
            val buffer = ByteBuffer.wrap(data, 0, length).order(ByteOrder.LITTLE_ENDIAN)
            
            // Parse IP header
            val version = (buffer.get().toInt() shr 4) and 0x0F
            if (version != 4) return // Only IPv4
            
            val ipHeaderLength = (data[0].toInt() and 0x0F) * 4
            if (length < ipHeaderLength + 20) return
            
            // Parse TCP header
            val srcPort = ((data[ipHeaderLength].toInt() and 0xFF) shl 8) or 
                          (data[ipHeaderLength + 1].toInt() and 0xFF)
            val dstPort = ((data[ipHeaderLength + 2].toInt() and 0xFF) shl 8) or 
                          (data[ipHeaderLength + 3].toInt() and 0xFF)
            
            // Check if this is Albion traffic
            if (srcPort != Constants.ALBION_PORT && dstPort != Constants.ALBION_PORT) {
                return
            }
            
            // Parse TCP header length
            val tcpHeaderLength = ((data[ipHeaderLength + 12].toInt() shr 4) and 0x0F) * 4
            
            // Get payload offset
            val payloadOffset = ipHeaderLength + tcpHeaderLength
            
            if (length > payloadOffset + 2) {
                val payloadLength = length - payloadOffset
                
                // Parse game protocol
                val events = parser.parsePacket(
                    data.copyOfRange(payloadOffset, length),
                    payloadLength
                )
                
                // Emit events
                events.forEach { event ->
                    onEventProcessed?.invoke(event)
                }
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Process multiple packets
     */
    fun processPackets(packets: List<Pair<ByteArray, Int>>) {
        packets.forEach { (data, length) ->
            processPacket(data, length)
        }
    }
    
    /**
     * Reset processor state
     */
    fun reset() {
        parser.clearFragments()
    }
}

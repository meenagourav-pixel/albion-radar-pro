package com.albionradar.data

import com.albionradar.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap

/**
 * Central state manager for all radar entities
 */
object RadarStateManager {
    
    private val _instance = RadarState()
    
    fun getInstance(): RadarState = _instance
}

/**
 * Central state manager for all radar entities
 */
class RadarState {
    
    // Players
    private val _players = ConcurrentHashMap<Long, Player>()
    val players: Map<Long, Player> get() = _players.toMap()
    
    // Resources
    private val _resources = ConcurrentHashMap<Long, Resource>()
    val resources: Map<Long, Resource> get() = _resources.toMap()
    
    // Mobs
    private val _mobs = ConcurrentHashMap<Long, Mob>()
    val mobs: Map<Long, Mob> get() = _mobs.toMap()
    
    // Chests
    private val _chests = ConcurrentHashMap<Long, Chest>()
    val chests: Map<Long, Chest> get() = _chests.toMap()
    
    // Fishing nodes
    private val _fishingNodes = ConcurrentHashMap<Long, FishingNode>()
    val fishingNodes: Map<Long, FishingNode> get() = _fishingNodes.toMap()
    
    // Mists
    private val _mists = ConcurrentHashMap<Long, Mist>()
    val mists: Map<Long, Mist> get() = _mists.toMap()
    
    // Dungeons
    private val _dungeons = ConcurrentHashMap<Long, Dungeon>()
    val dungeons: Map<Long, Dungeon> get() = _dungeons.toMap()
    
    // Player info
    private var _localPlayerId: Long = 0
    val localPlayerId: Long get() = _localPlayerId
    
    private var _localPlayerName: String = ""
    val localPlayerName: String get() = _localPlayerName
    
    private var _localPlayerGuild: String? = null
    val localPlayerGuild: String? get() = _localPlayerGuild
    
    private var _localPlayerAlliance: String? = null
    val localPlayerAlliance: String? get() = _localPlayerAlliance
    
    // Party members
    private val _partyMembers = mutableSetOf<String>()
    val partyMembers: Set<String> get() = _partyMembers.toSet()
    
    // Guild members
    private val _guildMembers = mutableSetOf<String>()
    val guildMembers: Set<String> get() = _guildMembers.toSet()
    
    // Alliance members
    private val _allianceMembers = mutableSetOf<String>()
    val allianceMembers: Set<String> get() = _allianceMembers.toSet()
    
    // State flows for UI updates
    private val _updateFlow = MutableStateFlow(0L)
    val updateFlow: StateFlow<Long> = _updateFlow.asStateFlow()
    
    // Entity counts
    private val _playerCount = MutableStateFlow(0)
    val playerCount: StateFlow<Int> = _playerCount.asStateFlow()
    
    private val _resourceCount = MutableStateFlow(0)
    val resourceCount: StateFlow<Int> = _resourceCount.asStateFlow()
    
    private val _hostileCount = MutableStateFlow(0)
    val hostileCount: StateFlow<Int> = _hostileCount.asStateFlow()
    
    /**
     * Set local player info
     */
    fun setLocalPlayer(id: Long, name: String, guild: String?, alliance: String?) {
        _localPlayerId = id
        _localPlayerName = name
        _localPlayerGuild = guild
        _localPlayerAlliance = alliance
        notifyUpdate()
    }
    
    /**
     * Add/update a player
     */
    fun addPlayer(player: Player) {
        _players[player.id] = player
        updateCounts()
        notifyUpdate()
    }
    
    /**
     * Remove a player
     */
    fun removePlayer(id: Long) {
        _players.remove(id)
        updateCounts()
        notifyUpdate()
    }
    
    /**
     * Update player position
     */
    fun updatePlayerPosition(id: Long, posX: Float, posY: Float) {
        _players[id]?.let { player ->
            _players[id] = player.copy(posX = posX, posY = posY)
            notifyUpdate()
        }
    }
    
    /**
     * Get player by ID
     */
    fun getPlayer(id: Long): Player? = _players[id]
    
    /**
     * Add/update a resource
     */
    fun addResource(resource: Resource) {
        _resources[resource.id] = resource
        updateCounts()
        notifyUpdate()
    }
    
    /**
     * Remove a resource
     */
    fun removeResource(id: Long) {
        _resources.remove(id)
        updateCounts()
        notifyUpdate()
    }
    
    /**
     * Update resource state
     */
    fun updateResourceState(id: Long, state: ResourceState) {
        _resources[id]?.let { resource ->
            _resources[id] = resource.copy(state = state)
            notifyUpdate()
        }
    }
    
    /**
     * Get resource by ID
     */
    fun getResource(id: Long): Resource? = _resources[id]
    
    /**
     * Add/update a mob
     */
    fun addMob(mob: Mob) {
        _mobs[mob.id] = mob
        notifyUpdate()
    }
    
    /**
     * Remove a mob
     */
    fun removeMob(id: Long) {
        _mobs.remove(id)
        notifyUpdate()
    }
    
    /**
     * Update mob position
     */
    fun updateMobPosition(id: Long, posX: Float, posY: Float) {
        _mobs[id]?.let { mob ->
            _mobs[id] = mob.copy(posX = posX, posY = posY)
            notifyUpdate()
        }
    }
    
    /**
     * Add/update a chest
     */
    fun addChest(chest: Chest) {
        _chests[chest.id] = chest
        notifyUpdate()
    }
    
    /**
     * Remove a chest
     */
    fun removeChest(id: Long) {
        _chests.remove(id)
        notifyUpdate()
    }
    
    /**
     * Add/update a fishing node
     */
    fun addFishingNode(node: FishingNode) {
        _fishingNodes[node.id] = node
        notifyUpdate()
    }
    
    /**
     * Remove a fishing node
     */
    fun removeFishingNode(id: Long) {
        _fishingNodes.remove(id)
        notifyUpdate()
    }
    
    /**
     * Add/update a mist
     */
    fun addMist(mist: Mist) {
        _mists[mist.id] = mist
        notifyUpdate()
    }
    
    /**
     * Remove a mist
     */
    fun removeMist(id: Long) {
        _mists.remove(id)
        notifyUpdate()
    }
    
    /**
     * Add/update a dungeon
     */
    fun addDungeon(dungeon: Dungeon) {
        _dungeons[dungeon.id] = dungeon
        notifyUpdate()
    }
    
    /**
     * Remove a dungeon
     */
    fun removeDungeon(id: Long) {
        _dungeons.remove(id)
        notifyUpdate()
    }
    
    /**
     * Add party member
     */
    fun addPartyMember(name: String) {
        _partyMembers.add(name)
    }
    
    /**
     * Remove party member
     */
    fun removePartyMember(name: String) {
        _partyMembers.remove(name)
    }
    
    /**
     * Clear party members
     */
    fun clearPartyMembers() {
        _partyMembers.clear()
    }
    
    /**
     * Check if player name is in party
     */
    fun isPartyMember(name: String): Boolean {
        return _partyMembers.contains(name)
    }
    
    /**
     * Add guild member
     */
    fun addGuildMember(name: String) {
        _guildMembers.add(name)
    }
    
    /**
     * Check if player name is in guild
     */
    fun isGuildMember(name: String): Boolean {
        return _guildMembers.contains(name) || name == _localPlayerGuild
    }
    
    /**
     * Add alliance member
     */
    fun addAllianceMember(name: String) {
        _allianceMembers.add(name)
    }
    
    /**
     * Check if player name is in alliance
     */
    fun isAllianceMember(name: String): Boolean {
        return _allianceMembers.contains(name) || name == _localPlayerAlliance
    }
    
    /**
     * Clear all entities
     */
    fun clearAll() {
        _players.clear()
        _resources.clear()
        _mobs.clear()
        _chests.clear()
        _fishingNodes.clear()
        _mists.clear()
        _dungeons.clear()
        _partyMembers.clear()
        _guildMembers.clear()
        _allianceMembers.clear()
        updateCounts()
        notifyUpdate()
    }
    
    /**
     * Get all entities within range
     */
    fun getEntitiesInRange(range: Float): List<Entity> {
        val result = mutableListOf<Entity>()
        
        _players.values.forEach { if (it.isInRange(range)) result.add(it) }
        _resources.values.forEach { if (it.isInRange(range)) result.add(it) }
        _mobs.values.forEach { if (it.isInRange(range)) result.add(it) }
        _chests.values.forEach { if (it.isInRange(range)) result.add(it) }
        _fishingNodes.values.forEach { if (it.isInRange(range)) result.add(it) }
        _mists.values.forEach { if (it.isInRange(range)) result.add(it) }
        _dungeons.values.forEach { if (it.isInRange(range)) result.add(it) }
        
        return result
    }
    
    /**
     * Get hostile players
     */
    fun getHostilePlayers(): List<Player> {
        return _players.values.filter { 
            it.isHostile() && it.id != _localPlayerId 
        }
    }
    
    /**
     * Update counts
     */
    private fun updateCounts() {
        _playerCount.value = _players.size
        _resourceCount.value = _resources.size
        _hostileCount.value = getHostilePlayers().size
    }
    
    /**
     * Notify observers of update
     */
    private fun notifyUpdate() {
        _updateFlow.value = System.currentTimeMillis()
    }
    
    companion object {
        fun getInstance(): RadarState = RadarStateManager.getInstance()
    }
}

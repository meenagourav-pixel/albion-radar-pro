package com.albionradar.service

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.albionradar.data.model.*
import com.albionradar.util.PreferenceKeys

/**
 * Manages filtering logic for entities displayed on radar
 */
class FilterManager(private val context: Context) {
    
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val resourcePrefs = context.getSharedPreferences("resource_filters", Context.MODE_PRIVATE)
    private val mobPrefs = context.getSharedPreferences("mob_filters", Context.MODE_PRIVATE)
    
    /**
     * Check if player should be shown on radar
     */
    fun shouldShowPlayer(player: Player): Boolean {
        // Check if showing players at all
        if (!prefs.getBoolean(PreferenceKeys.SHOW_PLAYERS, true)) {
            return false
        }
        
        // Check based on player status
        when (player.getStatus()) {
            PlayerStatus.PASSIVE -> {
                return prefs.getBoolean(PreferenceKeys.SHOW_PASSIVE_PLAYERS, true)
            }
            PlayerStatus.HOSTILE -> {
                return prefs.getBoolean(PreferenceKeys.SHOW_HOSTILE_PLAYERS, true)
            }
            PlayerStatus.FACTION -> {
                return prefs.getBoolean(PreferenceKeys.SHOW_FACTION_PLAYERS, true)
            }
            PlayerStatus.UNKNOWN -> {
                return true
            }
        }
    }
    
    /**
     * Check if player should trigger hostile alert
     */
    fun shouldAlertHostile(player: Player): Boolean {
        if (!player.isHostile()) return false
        if (!prefs.getBoolean(PreferenceKeys.ALERT_HOSTILE, true)) return false
        
        // Check exclusions
        if (prefs.getBoolean(PreferenceKeys.EXCLUDE_PARTY, true) && player.isPartyMember) {
            return false
        }
        if (prefs.getBoolean(PreferenceKeys.EXCLUDE_GUILD, true) && player.isGuildMember) {
            return false
        }
        if (prefs.getBoolean(PreferenceKeys.EXCLUDE_ALLIANCE, true) && player.isAllianceMember) {
            return false
        }
        
        return true
    }
    
    /**
     * Check if resource should be shown on radar
     */
    fun shouldShowResource(resource: Resource): Boolean {
        if (!prefs.getBoolean(PreferenceKeys.SHOW_RESOURCES, true)) {
            return false
        }
        
        // Check specific resource filter
        val key = "${PreferenceKeys.RESOURCE_FILTER_PREFIX}${resource.resourceType}_${resource.tier}_${resource.enchantLevel}"
        return resourcePrefs.getBoolean(key, resource.tier >= 4)
    }
    
    /**
     * Check if resource should trigger alert
     */
    fun shouldAlertResource(resource: Resource): Boolean {
        if (!prefs.getBoolean(PreferenceKeys.ALERT_RESOURCES, false)) {
            return false
        }
        
        // Check minimum tier
        val minTier = prefs.getInt("alert_min_tier", 6)
        if (resource.tier < minTier) {
            return false
        }
        
        // Check minimum enchant
        val minEnchant = prefs.getInt("alert_min_enchant", 0)
        if (resource.enchantLevel < minEnchant) {
            return false
        }
        
        // Check resource type
        return when (resource.resourceType) {
            0 -> prefs.getBoolean("alert_fiber", true)
            1 -> prefs.getBoolean("alert_hide", true)
            2 -> prefs.getBoolean("alert_wood", true)
            3 -> prefs.getBoolean("alert_ore", true)
            4 -> prefs.getBoolean("alert_rock", true)
            else -> true
        }
    }
    
    /**
     * Check if mob should be shown on radar
     */
    fun shouldShowMob(mob: Mob): Boolean {
        if (!prefs.getBoolean(PreferenceKeys.SHOW_MOBS, true)) {
            return false
        }
        
        // Check specific mob type filter
        val key = "${PreferenceKeys.MOB_FILTER_PREFIX}${mob.mobType}"
        return mobPrefs.getBoolean(key, mob.isBossType())
    }
    
    /**
     * Check if mob should trigger alert
     */
    fun shouldAlertMob(mob: Mob): Boolean {
        if (!prefs.getBoolean(PreferenceKeys.ALERT_MOBS, false)) {
            return false
        }
        
        // Only alert for boss types
        return mob.isBossType()
    }
    
    /**
     * Check if chest should be shown
     */
    fun shouldShowChest(chest: Chest): Boolean {
        return prefs.getBoolean(PreferenceKeys.SHOW_CHESTS, true)
    }
    
    /**
     * Check if chest should trigger alert
     */
    fun shouldAlertChest(chest: Chest): Boolean {
        return prefs.getBoolean(PreferenceKeys.ALERT_CHESTS, false) && chest.tier >= 4
    }
    
    /**
     * Check if fishing node should be shown
     */
    fun shouldShowFishing(node: FishingNode): Boolean {
        return prefs.getBoolean(PreferenceKeys.SHOW_FISHING, true)
    }
    
    /**
     * Check if mist should be shown
     */
    fun shouldShowMist(mist: Mist): Boolean {
        return prefs.getBoolean(PreferenceKeys.SHOW_MISTS, true)
    }
    
    /**
     * Check if dungeon should be shown
     */
    fun shouldShowDungeon(dungeon: Dungeon): Boolean {
        return prefs.getBoolean(PreferenceKeys.SHOW_DUNGEONS, true)
    }
    
    /**
     * Get scanner range
     */
    fun getScannerRange(): Float {
        return prefs.getInt(PreferenceKeys.SCANNER_RANGE, 50).toFloat()
    }
    
    /**
     * Check if overlay is enabled
     */
    fun isOverlayEnabled(): Boolean {
        return prefs.getBoolean(PreferenceKeys.SHOW_OVERLAY, true)
    }
    
    /**
     * Get overlay opacity
     */
    fun getOverlayOpacity(): Int {
        return prefs.getInt(PreferenceKeys.OVERLAY_OPACITY, 80)
    }
    
    /**
     * Get overlay size
     */
    fun getOverlaySize(): Int {
        return prefs.getInt(PreferenceKeys.OVERLAY_SIZE, 300)
    }
    
    /**
     * Check if sound alerts are enabled
     */
    fun isSoundEnabled(): Boolean {
        return prefs.getBoolean(PreferenceKeys.SOUND_ALERTS, true)
    }
    
    /**
     * Check if vibration alerts are enabled
     */
    fun isVibrationEnabled(): Boolean {
        return prefs.getBoolean(PreferenceKeys.VIBRATION_ALERTS, true)
    }
}

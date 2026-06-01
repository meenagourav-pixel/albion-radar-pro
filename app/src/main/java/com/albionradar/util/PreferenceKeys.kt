package com.albionradar.util

/**
 * Constants for SharedPreferences keys
 */
object PreferenceKeys {
    // General Settings
    const val SCANNER_RANGE = "scanner_range"
    const val PLAYER_NAME = "player_name"
    
    // Overlay Settings
    const val SHOW_OVERLAY = "show_overlay"
    const val OVERLAY_OPACITY = "overlay_opacity"
    const val OVERLAY_SIZE = "overlay_size"
    const val OVERLAY_POSITION_X = "overlay_position_x"
    const val OVERLAY_POSITION_Y = "overlay_position_y"
    
    // Alert Settings
    const val SOUND_ALERTS = "sound_alerts"
    const val VIBRATION_ALERTS = "vibration_alerts"
    const val ALERT_HOSTILE = "alert_hostile"
    const val ALERT_RESOURCES = "alert_resources"
    const val ALERT_MOBS = "alert_mobs"
    const val ALERT_CHESTS = "alert_chests"
    
    // Resource Filter Keys (format: resource_type_tier_enchant)
    // Example: "fiber_4_0", "hide_8_4"
    const val RESOURCE_FILTER_PREFIX = "resource_filter_"
    
    // Mob Filter Keys
    const val MOB_FILTER_PREFIX = "mob_filter_"
    
    // Filter Keys for Mob Types
    const val FILTER_LIVING_HARVESTABLE = "filter_living_harvestable"
    const val FILTER_LIVING_SKINNABLE = "filter_living_skinnable"
    const val FILTER_ENEMY = "filter_enemy"
    const val FILTER_ENCHANTED_ENEMY = "filter_enchanted_enemy"
    const val FILTER_MINI_BOSS = "filter_mini_boss"
    const val FILTER_BOSS = "filter_boss"
    const val FILTER_DRONE = "filter_drone"
    const val FILTER_MIST_BOSS = "filter_mist_boss"
    const val FILTER_EVENTS = "filter_events"
    
    // Entity Display Keys
    const val SHOW_PLAYERS = "show_players"
    const val SHOW_RESOURCES = "show_resources"
    const val SHOW_MOBS = "show_mobs"
    const val SHOW_CHESTS = "show_chests"
    const val SHOW_FISHING = "show_fishing"
    const val SHOW_MISTS = "show_mists"
    const val SHOW_DUNGEONS = "show_dungeons"
    
    // Player Filter
    const val SHOW_PASSIVE_PLAYERS = "show_passive_players"
    const val SHOW_HOSTILE_PLAYERS = "show_hostile_players"
    const val SHOW_FACTION_PLAYERS = "show_faction_players"
    const val EXCLUDE_PARTY = "exclude_party"
    const val EXCLUDE_GUILD = "exclude_guild"
    const val EXCLUDE_ALLIANCE = "exclude_alliance"
    
    // Log Settings
    const val LOG_ENABLED = "log_enabled"
    const val MAX_LOG_ENTRIES = "max_log_entries"
    
    // Service State
    const val SERVICE_RUNNING = "service_running"
    const val LAST_SERVER_IP = "last_server_ip"
}

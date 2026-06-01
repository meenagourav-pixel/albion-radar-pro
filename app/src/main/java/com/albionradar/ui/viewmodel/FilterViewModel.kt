package com.albionradar.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.albionradar.util.PreferenceKeys
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for filter screens
 */
class FilterViewModel(application: Application) : AndroidViewModel(application) {

    // Resource filters
    private val _resourceFilters = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val resourceFilters: StateFlow<Map<String, Boolean>> = _resourceFilters.asStateFlow()
    
    // Mob filters
    private val _mobFilters = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val mobFilters: StateFlow<Map<Int, Boolean>> = _mobFilters.asStateFlow()
    
    private val resourcePrefs = application.getSharedPreferences("resource_filters", Context.MODE_PRIVATE)
    private val mobPrefs = application.getSharedPreferences("mob_filters", Context.MODE_PRIVATE)
    
    init {
        loadFilters()
    }
    
    private fun loadFilters() {
        viewModelScope.launch {
            // Load resource filters
            val resourceMap = mutableMapOf<String, Boolean>()
            for (type in 0..4) {
                for (tier in 1..8) {
                    for (enchant in 0..4) {
                        val key = "${PreferenceKeys.RESOURCE_FILTER_PREFIX}${type}_${tier}_$enchant"
                        resourceMap[key] = resourcePrefs.getBoolean(key, tier >= 4)
                    }
                }
            }
            _resourceFilters.value = resourceMap
            
            // Load mob filters
            val mobMap = mutableMapOf<Int, Boolean>()
            for (mobType in 0..8) {
                val key = "${PreferenceKeys.MOB_FILTER_PREFIX}$mobType"
                mobMap[mobType] = mobPrefs.getBoolean(key, mobType in listOf(4, 5, 7)) // Bosses
            }
            _mobFilters.value = mobMap
        }
    }
    
    /**
     * Set resource filter
     */
    fun setResourceFilter(type: Int, tier: Int, enchant: Int, enabled: Boolean) {
        val key = "${PreferenceKeys.RESOURCE_FILTER_PREFIX}${type}_${tier}_$enchant"
        resourcePrefs.edit().putBoolean(key, enabled).apply()
        
        val current = _resourceFilters.value.toMutableMap()
        current[key] = enabled
        _resourceFilters.value = current
    }
    
    /**
     * Set mob filter
     */
    fun setMobFilter(mobType: Int, enabled: Boolean) {
        val key = "${PreferenceKeys.MOB_FILTER_PREFIX}$mobType"
        mobPrefs.edit().putBoolean(key, enabled).apply()
        
        val current = _mobFilters.value.toMutableMap()
        current[mobType] = enabled
        _mobFilters.value = current
    }
    
    /**
     * Select all resource filters
     */
    fun selectAllResources() {
        val editor = resourcePrefs.edit()
        val current = _resourceFilters.value.toMutableMap()
        
        for (type in 0..4) {
            for (tier in 1..8) {
                for (enchant in 0..4) {
                    val key = "${PreferenceKeys.RESOURCE_FILTER_PREFIX}${type}_${tier}_$enchant"
                    editor.putBoolean(key, true)
                    current[key] = true
                }
            }
        }
        
        editor.apply()
        _resourceFilters.value = current
    }
    
    /**
     * Deselect all resource filters
     */
    fun deselectAllResources() {
        val editor = resourcePrefs.edit()
        val current = _resourceFilters.value.toMutableMap()
        
        for (type in 0..4) {
            for (tier in 1..8) {
                for (enchant in 0..4) {
                    val key = "${PreferenceKeys.RESOURCE_FILTER_PREFIX}${type}_${tier}_$enchant"
                    editor.putBoolean(key, false)
                    current[key] = false
                }
            }
        }
        
        editor.apply()
        _resourceFilters.value = current
    }
    
    /**
     * Select all mob filters
     */
    fun selectAllMobs() {
        val editor = mobPrefs.edit()
        val current = _mobFilters.value.toMutableMap()
        
        for (mobType in 0..8) {
            val key = "${PreferenceKeys.MOB_FILTER_PREFIX}$mobType"
            editor.putBoolean(key, true)
            current[mobType] = true
        }
        
        editor.apply()
        _mobFilters.value = current
    }
    
    /**
     * Deselect all mob filters
     */
    fun deselectAllMobs() {
        val editor = mobPrefs.edit()
        val current = _mobFilters.value.toMutableMap()
        
        for (mobType in 0..8) {
            val key = "${PreferenceKeys.MOB_FILTER_PREFIX}$mobType"
            editor.putBoolean(key, false)
            current[mobType] = false
        }
        
        editor.apply()
        _mobFilters.value = current
    }
}

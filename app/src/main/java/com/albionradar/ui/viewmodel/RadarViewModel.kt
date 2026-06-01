package com.albionradar.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.albionradar.data.EventLogManager
import com.albionradar.data.RadarState
import com.albionradar.service.RadarVpnService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * ViewModel for the radar main screen
 */
class RadarViewModel(application: Application) : AndroidViewModel(application) {

    private val radarState = RadarState.getInstance()
    private val logManager = EventLogManager.getInstance()
    
    // UI State
    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()
    
    private val _playerCount = MutableStateFlow(0)
    val playerCount: StateFlow<Int> = _playerCount.asStateFlow()
    
    private val _resourceCount = MutableStateFlow(0)
    val resourceCount: StateFlow<Int> = _resourceCount.asStateFlow()
    
    private val _hostileCount = MutableStateFlow(0)
    val hostileCount: StateFlow<Int> = _hostileCount.asStateFlow()
    
    private val _hasOverlayPermission = MutableStateFlow(false)
    val hasOverlayPermission: StateFlow<Boolean> = _hasOverlayPermission.asStateFlow()
    
    private val _hasVpnPermission = MutableStateFlow(false)
    val hasVpnPermission: StateFlow<Boolean> = _hasVpnPermission.asStateFlow()
    
    init {
        observeState()
    }
    
    private fun observeState() {
        // Observe VPN service state
        viewModelScope.launch {
            RadarVpnService.isRunning.collectLatest { running ->
                _isRunning.value = running
            }
        }
        
        // Observe entity counts
        viewModelScope.launch {
            radarState.playerCount.collectLatest { count ->
                _playerCount.value = count
            }
        }
        
        viewModelScope.launch {
            radarState.resourceCount.collectLatest { count ->
                _resourceCount.value = count
            }
        }
        
        viewModelScope.launch {
            radarState.hostileCount.collectLatest { count ->
                _hostileCount.value = count
            }
        }
    }
    
    /**
     * Update permission states
     */
    fun updatePermissions(hasOverlay: Boolean, hasVpn: Boolean) {
        _hasOverlayPermission.value = hasOverlay
        _hasVpnPermission.value = hasVpn
    }
    
    /**
     * Get recent logs
     */
    fun getRecentLogs(count: Int = 50): List<com.albionradar.data.EventLog> {
        return logManager.logs.takeLast(count)
    }
    
    /**
     * Clear all entity data
     */
    fun clearData() {
        radarState.clearAll()
    }
    
    override fun onCleared() {
        super.onCleared()
    }
}

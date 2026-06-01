package com.albionradar.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.albionradar.R
import com.albionradar.databinding.ActivityAlertConfigBinding
import com.albionradar.util.PreferenceKeys

class AlertConfigActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlertConfigBinding
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlertConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        supportActionBar?.apply {
            setTitle(R.string.alert_config_title)
            setDisplayHomeAsUpEnabled(true)
        }
        
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        
        setupAlertSettings()
        setupResourceAlerts()
    }

    private fun setupAlertSettings() {
        // Sound alerts switch
        binding.switchSoundAlerts.isChecked = prefs.getBoolean(PreferenceKeys.SOUND_ALERTS, true)
        binding.switchSoundAlerts.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(PreferenceKeys.SOUND_ALERTS, isChecked).apply()
        }
        
        // Vibration alerts switch
        binding.switchVibrationAlerts.isChecked = prefs.getBoolean(PreferenceKeys.VIBRATION_ALERTS, true)
        binding.switchVibrationAlerts.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(PreferenceKeys.VIBRATION_ALERTS, isChecked).apply()
        }
        
        // Hostile player alerts
        binding.switchAlertHostile.isChecked = prefs.getBoolean(PreferenceKeys.ALERT_HOSTILE, true)
        binding.switchAlertHostile.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(PreferenceKeys.ALERT_HOSTILE, isChecked).apply()
        }
        
        // Resource alerts
        binding.switchAlertResources.isChecked = prefs.getBoolean(PreferenceKeys.ALERT_RESOURCES, false)
        binding.switchAlertResources.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(PreferenceKeys.ALERT_RESOURCES, isChecked).apply()
        }
        
        // Mob alerts
        binding.switchAlertMobs.isChecked = prefs.getBoolean(PreferenceKeys.ALERT_MOBS, false)
        binding.switchAlertMobs.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(PreferenceKeys.ALERT_MOBS, isChecked).apply()
        }
        
        // Chest alerts
        binding.switchAlertChests.isChecked = prefs.getBoolean(PreferenceKeys.ALERT_CHESTS, false)
        binding.switchAlertChests.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(PreferenceKeys.ALERT_CHESTS, isChecked).apply()
        }
    }

    private fun setupResourceAlerts() {
        // Minimum tier for resource alerts
        val minTier = prefs.getInt("alert_min_tier", 6)
        binding.seekbarMinTier.progress = minTier - 1 // 0-indexed
        binding.textMinTierValue.text = "T$minTier"
        
        binding.seekbarMinTier.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val tier = progress + 1
                binding.textMinTierValue.text = "T$tier"
                prefs.edit().putInt("alert_min_tier", tier).apply()
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        // Minimum enchant level for resource alerts
        val minEnchant = prefs.getInt("alert_min_enchant", 0)
        binding.seekbarMinEnchant.progress = minEnchant
        binding.textMinEnchantValue.text = if (minEnchant == 0) "Any" else "E$minEnchant"
        
        binding.seekbarMinEnchant.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.textMinEnchantValue.text = if (progress == 0) "Any" else "E$progress"
                prefs.edit().putInt("alert_min_enchant", progress).apply()
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        // Resource types to alert
        setupResourceTypeToggles()
    }

    private fun setupResourceTypeToggles() {
        // Fiber
        binding.checkboxFiber.isChecked = prefs.getBoolean("alert_fiber", true)
        binding.checkboxFiber.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("alert_fiber", isChecked).apply()
        }
        
        // Hide
        binding.checkboxHide.isChecked = prefs.getBoolean("alert_hide", true)
        binding.checkboxHide.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("alert_hide", isChecked).apply()
        }
        
        // Wood
        binding.checkboxWood.isChecked = prefs.getBoolean("alert_wood", true)
        binding.checkboxWood.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("alert_wood", isChecked).apply()
        }
        
        // Ore
        binding.checkboxOre.isChecked = prefs.getBoolean("alert_ore", true)
        binding.checkboxOre.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("alert_ore", isChecked).apply()
        }
        
        // Rock
        binding.checkboxRock.isChecked = prefs.getBoolean("alert_rock", true)
        binding.checkboxRock.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("alert_rock", isChecked).apply()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}

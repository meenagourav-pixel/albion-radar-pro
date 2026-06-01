package com.albionradar.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreferenceCompat
import com.albionradar.R
import com.albionradar.util.PreferenceKeys

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        supportActionBar?.apply {
            setTitle(R.string.settings_title)
            setDisplayHomeAsUpEnabled(true)
        }
        
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings_container, SettingsFragment())
                .commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    class SettingsFragment : androidx.preference.PreferenceFragmentCompat() {
        
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
            
            setupPreferences()
        }
        
        private fun setupPreferences() {
            // Scanner Range
            findPreference<SeekBarPreference>(PreferenceKeys.SCANNER_RANGE)?.apply {
                setOnPreferenceChangeListener { _, newValue ->
                    true
                }
            }
            
            // Overlay Settings
            findPreference<SwitchPreferenceCompat>(PreferenceKeys.SHOW_OVERLAY)?.apply {
                setOnPreferenceChangeListener { _, newValue ->
                    true
                }
            }
            
            // Overlay Opacity
            findPreference<SeekBarPreference>(PreferenceKeys.OVERLAY_OPACITY)?.apply {
                setOnPreferenceChangeListener { _, newValue ->
                    true
                }
            }
            
            // Overlay Size
            findPreference<SeekBarPreference>(PreferenceKeys.OVERLAY_SIZE)?.apply {
                setOnPreferenceChangeListener { _, newValue ->
                    true
                }
            }
            
            // Sound Alerts
            findPreference<SwitchPreferenceCompat>(PreferenceKeys.SOUND_ALERTS)?.apply {
                setOnPreferenceChangeListener { _, newValue ->
                    true
                }
            }
            
            // Vibration Alerts
            findPreference<SwitchPreferenceCompat>(PreferenceKeys.VIBRATION_ALERTS)?.apply {
                setOnPreferenceChangeListener { _, newValue ->
                    true
                }
            }
            
            // Alert for Hostile
            findPreference<SwitchPreferenceCompat>(PreferenceKeys.ALERT_HOSTILE)?.apply {
                setOnPreferenceChangeListener { _, newValue ->
                    true
                }
            }
            
            // Exclude Party
            findPreference<SwitchPreferenceCompat>(PreferenceKeys.EXCLUDE_PARTY)?.apply {
                setOnPreferenceChangeListener { _, newValue ->
                    true
                }
            }
            
            // Exclude Guild
            findPreference<SwitchPreferenceCompat>(PreferenceKeys.EXCLUDE_GUILD)?.apply {
                setOnPreferenceChangeListener { _, newValue ->
                    true
                }
            }
            
            // Exclude Alliance
            findPreference<SwitchPreferenceCompat>(PreferenceKeys.EXCLUDE_ALLIANCE)?.apply {
                setOnPreferenceChangeListener { _, newValue ->
                    true
                }
            }
        }
    }
}

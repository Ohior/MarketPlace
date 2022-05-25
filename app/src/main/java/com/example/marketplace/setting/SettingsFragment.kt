package com.example.marketplace.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import androidx.preference.SwitchPreferenceCompat
import com.example.marketplace.R
import com.example.marketplace.tool.Tool

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)
        val prefman = preferenceManager.findPreference<SwitchPreferenceCompat>("key_apptheme_setting")
        prefman?.setOnPreferenceClickListener {
            if (prefman.isChecked){
                loadSettings()
            }else{
                loadSettings()
            }
            true
        }
    }
    private fun loadSettings() {

        val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val switchpref = sp.getBoolean("key_apptheme_setting", false)
        if (switchpref){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
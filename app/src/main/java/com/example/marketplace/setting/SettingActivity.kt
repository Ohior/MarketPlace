package com.example.marketplace.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import com.example.marketplace.MainActivity
import com.example.marketplace.R
import com.example.marketplace.tool.Tool

class SettingActivity : AppCompatActivity() {
    private val DARK_THEME = "dark"
    private val BRIGHT_THEME = "bright"
    private val CUSTOM_THEME = "customTheme"
    private var custom_theme: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        supportFragmentManager.beginTransaction()
            .replace(R.id.id_fl_fragment_container, SettingsFragment())
            .commit()
    }
}
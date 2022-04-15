package com.example.marketplace.setting

import android.os.Bundle
import android.preference.PreferenceFragment
import com.example.marketplace.R

class SettingFragment: PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference)

    }
}
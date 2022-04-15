package com.example.marketplace.setting

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatDelegate
import com.example.marketplace.R
import com.example.marketplace.tool.Constant
import com.example.marketplace.tool.Tool

class SettingActivity : AppCompatActivity() {
    private  var SETTING_THEME = "settingtheme"
    private  var SETTING_THEME_COLOR = "settingthemecolor"
    private  var SETTING_APP_COLOR = "settingappcolor"

    private lateinit var id_btn_set_theme:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        id_btn_set_theme = findViewById(R.id.id_btn_set_theme)

        id_btn_set_theme.setOnClickListener{
            val themetype = Constant.getString(applicationContext, SETTING_THEME)
            if (themetype == null || themetype == "light") {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Constant.setString(applicationContext, SETTING_THEME, "dark")
                Tool.showShortToast(applicationContext, "DARK MODE ENABLE")
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Constant.setString(applicationContext, SETTING_THEME, "light")
                Tool.showShortToast(applicationContext, "LIGHT MODE ENABLE")
            }
        }

//        if (findViewById<FrameLayout>(R.id.id_setting_fragment) != null){
//            if (savedInstanceState != null)return
//            fragmentManager.beginTransaction().add(R.id.id_setting_fragment, SettingFragment()).commit()
//
//        }
    }
}
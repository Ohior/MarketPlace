package com.example.marketplace

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.marketplace.setting.SettingActivity
import com.example.marketplace.tool.Constant
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception
import java.lang.RuntimeException
import java.lang.reflect.Method


class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private var press_time: Long = 0

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.market_menu, menu)
//        return super.onCreateOptionsMenu(menu)
        return true
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        if (menu.javaClass.simpleName == "MenuBuilder") {
            try {
                val m: Method = menu.javaClass.getDeclaredMethod(
                    "setOptionalIconsVisible", java.lang.Boolean.TYPE)
                m.isAccessible = true
                m.invoke(menu, true)
            } catch (e: NoSuchMethodException) {
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
        return super.onMenuOpened(featureId, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.id_menu_setting ->{
                startActivity(Intent(this, SettingActivity::class.java))
                true
            }
            R.id.id_menu_create_customer ->{
                true
            }
            R.id.id_menu_create_vendor ->{
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firebaseAuth = FirebaseAuth.getInstance()
        checkFirstRun()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Constant.setString(applicationContext, Constant.USERNAME, null)
        Constant.setString(applicationContext, Constant.PASSWORD, null)
        Constant.setString(applicationContext, Constant.USER_TYPE, null)
        firebaseAuth.signOut()
    }

    private fun checkFirstRun() {
        val PREFS_NAME = "MyPrefsFile"
        val PREF_VERSION_CODE_KEY = "version_code"
        val DOESNOT_EXIST = -1

        // Get current version code
        val currentVersionCode: Int = BuildConfig.VERSION_CODE

        // Get saved version code
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNOT_EXIST)

        // Check for first run or upgrade
        when {
            currentVersionCode == savedVersionCode -> {

                // This is just a normal run
                return
            }
            savedVersionCode == DOESNOT_EXIST -> {
                Constant.setString(applicationContext, Constant.USERNAME, null)
                Constant.setString(applicationContext, Constant.PASSWORD, null)
                Constant.setString(applicationContext, Constant.USER_TYPE, null)
                Constant.setString(applicationContext, Constant.CLICK_USER, null)
                Constant.setString(applicationContext, Constant.CLICK_PASSWORD, null)
                // TODO This is a new install (or the user cleared the shared preferences)
            }
            currentVersionCode > savedVersionCode -> {
                Constant.setString(applicationContext, Constant.USERNAME, null)
                Constant.setString(applicationContext, Constant.PASSWORD, null)
                Constant.setString(applicationContext, Constant.USER_TYPE, null)
                Constant.setString(applicationContext, Constant.CLICK_USER, null)
                Constant.setString(applicationContext, Constant.CLICK_PASSWORD, null)
                // TODO This is an upgrade
            }
        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply()
    }

}
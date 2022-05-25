package com.example.marketplace.profile

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.marketplace.MainActivity
import com.example.marketplace.R
import com.example.marketplace.setting.SettingActivity
import com.example.marketplace.tool.Tool
import com.google.firebase.auth.FirebaseAuth
import java.lang.reflect.Method


class ProfileActivity : AppCompatActivity() {

    private lateinit var firebase_auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        firebase_auth = FirebaseAuth.getInstance()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.shop_menu, menu)
//        return super.onCreateOptionsMenu(menu)
        menu?.findItem(R.id.id_menu_location)?.isVisible = false
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
            R.id.id_menu_logout ->{
                firebase_auth.signOut()
                this.finish()
                startActivity(Intent(applicationContext, MainActivity::class.java))
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val currentFragment: Fragment = this.fragmentManager.findFragmentById(R.id.profileFragment)
        if (currentFragment == ProfileFragment::class.java){
            finish()
            startActivity(Intent(applicationContext, MainActivity::class.java))
            Tool.showShortToast(applicationContext, "exiting your profile")
        }

    }
}
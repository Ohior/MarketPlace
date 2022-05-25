package com.example.marketplace.shop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.marketplace.MainActivity
import com.example.marketplace.R
import com.example.marketplace.setting.SettingActivity
import com.example.marketplace.tool.Constant
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception
import java.lang.RuntimeException
import java.lang.reflect.Method

class ShopActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    private var user_name: String? = null
    private var user_password: String? = null
    private var user_type: String? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.shop_menu, menu)
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
            R.id.id_menu_profile -> {
                finish()
                startActivity(Intent(this, MainActivity::class.java))
                true
            }
            R.id.id_menu_setting ->{
                startActivity(Intent(this, SettingActivity::class.java))
                true
            }
            R.id.id_menu_logout ->{
                firebaseAuth.signOut()
                this.finish()
                startActivity(Intent(this, MainActivity::class.java))
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)
        firebaseAuth = FirebaseAuth.getInstance()

        // get default data from user
        user_name = Constant.getString(applicationContext, Constant.USERNAME).toString()
        user_type = Constant.getString(applicationContext, Constant.USER_TYPE).toString()
        user_password = Constant.getString(applicationContext, Constant.PASSWORD).toString()
    }
}
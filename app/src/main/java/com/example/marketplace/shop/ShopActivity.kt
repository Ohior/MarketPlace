package com.example.marketplace.shop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.navigation.ui.onNavDestinationSelected
import com.example.marketplace.R

class ShopActivity : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.shop_menu, menu)
//        return super.onCreateOptionsMenu(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.id_menu_delete -> {
                Toast.makeText(applicationContext, "delete", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.id_menu_setting ->{
                Toast.makeText(applicationContext, "settings", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.id_menu_logout ->{
                finish()
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
    }
}
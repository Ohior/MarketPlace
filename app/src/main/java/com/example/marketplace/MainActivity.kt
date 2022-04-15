package com.example.marketplace

import android.hardware.SensorEvent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.marketplace.tool.Constant
import com.google.firebase.auth.FirebaseAuth
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private var press_time: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Constant.setString(applicationContext, Constant.USERNAME, null)
        Constant.setString(applicationContext, Constant.PASSWORD, null)
        Constant.setString(applicationContext, Constant.USER_TYPE, null)
        firebaseAuth.signOut()
    }
}
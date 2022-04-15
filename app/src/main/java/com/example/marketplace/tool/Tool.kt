package com.example.marketplace.tool

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.example.marketplace.R
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timerTask

object Tool {

    fun showShortToast(context: Context, message:String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun rotateAnimation(imageview: ImageView, angle: Float, duration: Long=500) {
        imageview.animate().rotation(angle).setDuration(duration).start()
    }

    fun spinAnimation(imageview: ImageView, speed: Long=500): RotateAnimation {
        val rotate = RotateAnimation(0f, 355f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f)
        rotate.duration = speed
        rotate.repeatCount = Animation.INFINITE
        imageview.startAnimation(rotate)
        return rotate
    }

    fun debugMessage(mess: String, tag:String="DEBUG") {
        Log.e(tag, mess )
    }

    fun showLongToast(context:Context, message:String){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun delayAction(millis:Long, function: () -> Unit){
        Timer().schedule(timerTask {
            function()
        }, millis)
    }
    fun delayActionNewThread(millis:Long, function: () -> Unit){
        Executors.newSingleThreadScheduledExecutor()
            .schedule({
                      function()
            }, millis, TimeUnit.MILLISECONDS)
    }

    fun loadingProgressBar(context: Context, message: String="Loading.... Please wait", function:(ProgressDialog)->Unit){
        val progressbar = ProgressDialog(context)
        progressbar.setTitle(message)
        function(progressbar)
        progressbar.show()
    }

    fun popUpMessage(context:Context, message:String,function: (AlertDialog.Builder) -> Unit){
        val popup = AlertDialog.Builder(context)
        popup.setMessage(message)
        function(popup)
        popup.show()
    }
    fun popUpTitle(context:Context, title:String,function: (AlertDialog.Builder) -> Unit){
        val popup = AlertDialog.Builder(context)
        popup.setTitle(title)
        function(popup)
        popup.show()
    }

    fun popUpDisplay(context:Context, title: String, message:String,function: (AlertDialog.Builder) -> Unit){
        val popup = AlertDialog.Builder(context)
        popup.setTitle(title)
        popup.setMessage(message)
        function(popup)
        popup.show()
    }
    fun popUpDialog(context:Context,function: (AlertDialog.Builder) -> Unit){
        val popup = AlertDialog.Builder(context)
        function(popup)
    }

    fun loadingIconProgressBar(
        context: Context,
        message: String = "Loading.... Please wait",
        function: (ProgressDialog) -> Unit,
    ){
        val progressbar = ProgressDialog(context)
        progressbar.setTitle(message)
        progressbar.setIcon(R.drawable.applogo)
        progressbar.show()

        function(progressbar)
    }


    fun receiveDataFromFragment(fragment: Fragment, key: String): Any? {
        val args = fragment.arguments
        return args?.get(key)
    }

    fun sendDataToFragment(fragment: Fragment, fragmentmanager: FragmentManager?, key:String, data:String) {
        val bundle = Bundle()
        bundle.putString(key, data)
        fragment.arguments = bundle
        fragmentmanager?.beginTransaction()?.replace(R.id.profile_nav, fragment)?.commit()
    }

    fun getBearing(
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double,
    ): Double {
        val latitude1 = Math.toRadians(startLat)
        val latitude2 = Math.toRadians(endLat)
        val longDiff = Math.toRadians(endLng - startLng)
        val y = Math.sin(longDiff) * Math.cos(latitude2)
        val x =
            Math.cos(latitude1) * Math.sin(latitude2) - Math.sin(latitude1) * Math.cos(latitude2) * Math.cos(
                longDiff)
        return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360
    }
}


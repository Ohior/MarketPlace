package com.example.marketplace.tool

import android.content.Context
import android.util.Log
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.Toast

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
}


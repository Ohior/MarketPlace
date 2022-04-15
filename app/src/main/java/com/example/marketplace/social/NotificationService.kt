package com.example.marketplace.social

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.marketplace.R
import com.example.marketplace.tool.Constant
import com.example.marketplace.tool.Tool
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices

class NotificationService: Service() {


    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    fun startNotificationService(){
        val channelId = "location_notification_service"
        val notificationmanager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val resultintent = Intent()
        val pendingintent = PendingIntent.getActivity(
            applicationContext,
            0,
            resultintent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificationbuilder =  NotificationCompat.Builder(
            applicationContext,
            channelId
        )
        notificationbuilder.setSmallIcon(R.drawable.applogo)
        notificationbuilder.setContentTitle("Location service")
        notificationbuilder.setDefaults(NotificationCompat.DEFAULT_ALL)
        notificationbuilder.setContentText("Running")
        notificationbuilder.setContentIntent(pendingintent)
        notificationbuilder.setAutoCancel(false)
        notificationbuilder.setPriority(NotificationCompat.PRIORITY_MAX)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            if(notificationmanager.getNotificationChannel(channelId) == null){
                val notificationchannel = NotificationChannel(
                    channelId,
                    "Location Service",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationchannel.description = "This notification is use be Market Qube"
                notificationmanager.createNotificationChannel(notificationchannel)
            }
        }
        startForeground(Constant.LOACTION_SERVICE_ID, notificationbuilder.build())
        Tool.showShortToast(this@NotificationService, "Notification Service Started")
    }

    private fun stopNotificationService(){
        Tool.showShortToast(this@NotificationService, "Notification Service Stop")
        stopForeground(true)
        stopSelf()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null){
            val action = intent.action
            if (action != null){
                if (action == Constant.ACTION_START_LOCATION_SERVICE){
                    startNotificationService()
                }else if (action == Constant.ACTION_STOP_LOCATION_SERVICE){
                    stopNotificationService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}
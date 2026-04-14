package com.bakht.appcloner.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class CloneService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onCreate() { super.onCreate(); createNotificationChannel() }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("App Cloner").setContentText("Clones active")
            .setSmallIcon(android.R.drawable.ic_menu_manage)
            .setPriority(NotificationCompat.PRIORITY_LOW).setOngoing(true).build()
        startForeground(NOTIFICATION_ID, notification)
        return START_STICKY
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "App Cloner Service", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
    companion object { const val CHANNEL_ID = "app_cloner_service"; const val NOTIFICATION_ID = 1001 }
}

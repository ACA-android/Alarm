package com.dm.alarm

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat

class AlarmService : Service() {

    private lateinit var player: MediaPlayer

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("Service", "onStartCommand")
        if (intent?.action == "stop") {
            player.stop()
            stopSelf()
        } else {
            player = MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI).apply {
                isLooping = true
                start()
            }

            val stopIntent = Intent(this, AlarmService::class.java).apply {
                action = "stop"
            }
            val pendingIntent =
                PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            val notification = NotificationCompat.Builder(this, CHANNEL_DEFAULT_IMPORTANCE)
                .setContentTitle("Wake up")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(0, "stop", pendingIntent)
                .build()

            startForeground(123, notification)
        }
        return START_STICKY
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null

    }

    override fun onDestroy() {
        super.onDestroy()
        player.stop()
    }
}
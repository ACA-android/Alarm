package com.dm.alarm

import android.app.*
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


const val CHANNEL_DEFAULT_IMPORTANCE = "service channel"

class MainActivity : AppCompatActivity() {

    private var alarmTime = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()


        pickBtn.setOnClickListener {
            pickTime()
        }

        val intent = Intent(this, AlarmReceiver::class.java).apply {
            addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
        }

        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    alarmTime,
                    pendingIntent
                )
                Log.d("ALARM", "current time - ${System.currentTimeMillis()}")
                Log.d("ALARM", "alarm time - $alarmTime")
            }
        }
    }

    private fun pickTime() {
        val cldr: Calendar = Calendar.getInstance()
        val hour: Int = cldr.get(Calendar.HOUR_OF_DAY)
        val minutes: Int = cldr.get(Calendar.MINUTE)
        // time picker dialog
        val picker = TimePickerDialog(
            this@MainActivity,
            OnTimeSetListener { tp, sHour, sMinute ->
                timeTextView.text = "Time - $sHour:$sMinute"
                cldr.set(
                    cldr.get(Calendar.YEAR),
                    cldr.get(Calendar.MONTH),
                    cldr.get(Calendar.DATE),
                    sHour,
                    sMinute
                )
                Log.d("TIME", cldr.toString())
                alarmTime = cldr.timeInMillis
            },
            hour,
            minutes,
            true
        )
        picker.show()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    CHANNEL_DEFAULT_IMPORTANCE,
                    "Service channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for foreground services"
            }
            val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
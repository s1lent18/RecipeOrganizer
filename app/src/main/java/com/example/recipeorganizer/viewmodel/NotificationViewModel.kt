package com.example.recipeorganizer.viewmodel

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleNotification(timeInMillis: Long, title: String, message: String) {
        val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !canScheduleExactAlarmsCompat(alarmManager)) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            application.startActivity(intent)
            return
        }

        val intent = Intent(application, NotificationReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            application,
            timeInMillis.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )
    }

    private fun canScheduleExactAlarmsCompat(alarmManager: AlarmManager): Boolean {
        return try {
            val method = AlarmManager::class.java.getMethod("canScheduleExactAlarms")
            method.invoke(alarmManager) as Boolean
        } catch (e: Exception) {
            false // If the method does not exist, assume permission is needed
        }
    }
}

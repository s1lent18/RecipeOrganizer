package com.example.recipeorganizer.viewmodel

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.recipeorganizer.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val title = intent.getStringExtra("title") ?: "Default Title"
            val message = intent.getStringExtra("message") ?: "Default Message"

            val notificationManager = NotificationManagerCompat.from(context)
            val notification = NotificationCompat.Builder(context, "Main_Channel_ID")
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.foodapp)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setPublicVersion(
                    NotificationCompat.Builder(context, "Main_Channel_ID")
                        .setContentTitle("Hidden Notification")
                        .setContentText("Unlock to view the message.")
                        .build()
                )
                .build()

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notificationManager.notify(1, notification)
            }
        }
    }
}
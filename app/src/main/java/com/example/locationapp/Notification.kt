package com.example.locationapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

fun showLocationNotification(context: Context, location: LocationData, address: String) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channelId = "location_notifications"
        val channel = NotificationChannel(
            channelId,
            "Location Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for location updates"
        }
        notificationManager.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(context, "location_notifications")
        .setSmallIcon(android.R.drawable.ic_menu_mylocation)
        .setContentTitle("Location Information")
        .setContentText("Lat: ${location.latitude}, Lon: ${location.longitude}, Address: $address")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(1, notification)
}
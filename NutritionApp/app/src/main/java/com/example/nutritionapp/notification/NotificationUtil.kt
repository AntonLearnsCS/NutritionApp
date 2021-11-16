package com.example.nutritionapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.example.nutritionapp.BuildConfig
import com.example.nutritionapp.R
import com.example.nutritionapp.maps.RecipeNotificationClassDomain

private const val NOTIFICATION_CHANNEL_ID = BuildConfig.APPLICATION_ID + ".channel"

fun sendNotification(context: Context, recipeNotificationDomain: RecipeNotificationClassDomain) {
    Log.i("test","sendNotification called")
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // We need to create a NotificationChannel associated with our CHANNEL_ID before sending a notification.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        && notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null
    ) {
        val name = context.getString(R.string.app_name)
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            name,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    val intent = Intent(context,NotificationDescriptionActivity::class.java)//ReminderDescriptionActivity.newIntent(context, reminderDataItem)
    intent.putExtra("EXTRA_recipeNotification",recipeNotificationDomain)
    //create a pending intent that opens NotificationDescriptionActivity when the user clicks on the notification
    val stackBuilder = TaskStackBuilder.create(context)
        .addParentStack(NotificationDescriptionActivity::class.java)
        .addNextIntent(intent)

    val notificationPendingIntent = stackBuilder
        .getPendingIntent(getUniqueId(), PendingIntent.FLAG_UPDATE_CURRENT)

//    build the notification object with the data to be shown
    val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(recipeNotificationDomain.recipeName)
        .setContentText(recipeNotificationDomain.missingIngredients)
        .setContentIntent(notificationPendingIntent)
        .setAutoCancel(true)
        .build()

    println("Extra Item: " + recipeNotificationDomain.recipeName)

    notificationManager.notify(getUniqueId(), notification)
}

fun getUniqueId() = ((System.currentTimeMillis() % 10000).toInt())
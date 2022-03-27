package com.ichota.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ichota.activities.MainActivity
import com.ichota.R
import com.ichota.preferences.PrefKeys
import com.ichota.preferences.PreferenceHelper


private const val TAG = "FirebaseMessageService"
private const val CHANNEL_ID = "com.ichota.fcm.channel.id"
private const val NOTIFICATION_ID = 111

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private var helper: PreferenceHelper? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        helper = PreferenceHelper.getPreferences(this)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "onNewToken: $token")
        helper?.saveStringValue(PrefKeys.KEY_DEVICE_TOKEN, token)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        Log.d(TAG, "onMessageReceived: ${p0.data}")

        addNotification()
        super.onMessageReceived(p0)
    }

    private fun addNotification() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Notifications Example")
            .setContentText("This is a test notification")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setWhen(System.currentTimeMillis())

        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val contentIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, 0)
        builder.setContentIntent(contentIntent)


        with(NotificationManagerCompat.from(this)){
            notify(NOTIFICATION_ID,builder.build())
        }


    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notificationChannelName)
            val descriptionText = getString(R.string.notificationChannelDescription)
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
package com.ub.finanstics.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.messaging
import com.ub.finanstics.R
import com.ub.finanstics.api.ApiRepository
import com.ub.finanstics.presentation.MainActivity
import com.ub.finanstics.presentation.preferencesManagers.EncryptedPreferencesManager

class FinansticsFMS : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            if (isLongRunningJob()) {
                scheduleJob()
            } else {
                handleNow()
            }
        }

        remoteMessage.notification?.let {
            it.body?.let { body -> sendNotification(body) }
        }
    }

    @Suppress("FunctionOnlyReturningConstant")
    private fun isLongRunningJob() = true

    @Suppress("EmptyFunctionBlock")
    override fun onNewToken(token: String) {
    }

    private fun scheduleJob() {
        val work = OneTimeWorkRequest.Builder(FinansticsWorker::class.java).build()
        WorkManager.getInstance(this).beginWith(work).enqueue()
    }

    @Suppress("EmptyFunctionBlock")
    private fun handleNow() {
    }

    private fun sendNotification(messageBody: String) {
        val requestCode = 0
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Finanstics",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "FinansticsFMS"
    }
}

fun logFirebaseToken(context: Context) {
    val encryptedPref = EncryptedPreferencesManager(context)
    val fcmToken = encryptedPref.getString("fcm_token", "")
    if (fcmToken.isEmpty()) {
        Firebase.messaging.token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }

            val token = task.result
            encryptedPref.saveData("fcm_token", token)
        }
    }
}

@Suppress("TooGenericExceptionCaught")
suspend fun regFirebaseToken(context: Context) {
    val encryptedPref = EncryptedPreferencesManager(context)
    val fcmToken = encryptedPref.getString("fcm_token", "")

    encryptedPref.saveData("fcm_token", fcmToken)
    val token = encryptedPref.getString("token", "")

    if (token.isNotEmpty() && fcmToken.isNotEmpty()) {
        val apiRep = ApiRepository()
        try {
            val response = apiRep.registerFCMToken(token, fcmToken)
        } catch (_: Exception) {
        }
    }
}

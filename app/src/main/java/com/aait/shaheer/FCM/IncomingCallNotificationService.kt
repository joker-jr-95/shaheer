package com.aait.shaheer.FCM

import android.R
import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.aait.shaheer.FCM.FCM_ACTIONS.RECEIVE_CALL
import com.aait.shaheer.FCM.FCM_ACTIONS.REJECT_CALL
import com.aait.shaheer.ui.activity.main.ui.inbox.chat.AudioActivity


class IncomingCallNotificationService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action=intent?.action
        val apiKey=intent?.getStringExtra(AudioActivity.key)
        val session=intent?.getStringExtra(AudioActivity.session)
        val token=intent?.getStringExtra(AudioActivity.token)
        val name=intent?.getStringExtra("name")
        val avatar= intent?.getStringExtra("avatar")
        val notification_id=intent?.getIntExtra("notification_id",System.currentTimeMillis().toInt())

        try {
            if (intent?.action==RECEIVE_CALL){

            }
            else if (intent?.action==RECEIVE_CALL){
                val activeCallIntent = Intent(this, AudioActivity::class.java)
                activeCallIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                activeCallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                activeCallIntent.action = RECEIVE_CALL
                startActivity(activeCallIntent)

            }
            else if (intent?.action== REJECT_CALL){
                stopForeground(true)
            }
            else{

            }



            val receiveCallAction =
                Intent(applicationContext, HeadsUpNotificationActionReceiver::class.java)
            receiveCallAction.action = "RECEIVE_CALL"
            val cancelCallAction =
                Intent(applicationContext, HeadsUpNotificationActionReceiver::class.java)

            cancelCallAction.action = "CANCEL_CALL"
            val receiveCallPendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                1200,
                receiveCallAction,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val cancelCallPendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                1201,
                cancelCallAction,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            createChannel()
            var notificationBuilder: NotificationCompat.Builder? = null
            if (R.attr.data != null) {
                val defaultUri =
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

                notificationBuilder = NotificationCompat.Builder(this, "2222")
                    .setContentText("kareeem")
                    .setContentTitle("Incoming Voice Call")
                    .setSmallIcon(com.aait.shaheer.R.drawable.telephone_gray)

                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .addAction(com.aait.shaheer.R.drawable.call_user, "Receive Call", receiveCallPendingIntent)
                    .addAction(com.aait.shaheer.R.drawable.end_call, "Cancel call", cancelCallPendingIntent)
                    .setAutoCancel(true)
                    .setSound(defaultUri)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setFullScreenIntent(receiveCallPendingIntent, true)
            }
            var incomingCallNotification: Notification? = null
            if (notificationBuilder != null) {
                incomingCallNotification = notificationBuilder.build()
            }
            startForeground(120, incomingCallNotification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return START_NOT_STICKY

    }


    fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "2222",
                "voip",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Call Notifications"
            channel.lockscreenVisibility = Notification.FLAG_FOREGROUND_SERVICE
            channel.importance = NotificationManager.IMPORTANCE_HIGH
            channel.enableLights(true)
            channel.shouldShowLights()
            //channel.vibrationPattern = vibrate
            channel.enableVibration(true)

            val defaultUri =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

            channel.setSound(defaultUri, AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setLegacyStreamType(AudioManager.STREAM_RING)
                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION).build())

            /*Objects.requireNonNull(applicationContext.getSystemService(NotificationManager::class.java))
                .createNotificationChannel(channel)*/
            val manager =
                (applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            manager.createNotificationChannel(channel)
        }
    }

}
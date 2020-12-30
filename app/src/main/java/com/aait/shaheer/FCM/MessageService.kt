package com.aait.shaheer.FCM

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.aait.shaheer.FCM.FCM_ACTIONS.CALL_RESPONSE_ACTION_KEY
import com.aait.shaheer.FCM.FCM_ACTIONS.CANCEL_CALL
import com.aait.shaheer.FCM.FCM_ACTIONS.RECEIVE_CALL
import com.aait.shaheer.R
import com.aait.shaheer.domain.repository.auth_repository.authRepository
import com.aait.shaheer.domain.repository.base_repository.baseRepository
import com.aait.shaheer.ui.activity.details.ProductDetailsActivity
import com.aait.shaheer.ui.activity.main.MainActivity
import com.aait.shaheer.ui.activity.main.ui.inbox.QustAnswActivity
import com.aait.shaheer.ui.activity.main.ui.inbox.chat.AudioActivity
import com.aait.shaheer.ui.activity.main.ui.inbox.chat.ChatActivity
import com.aait.shaheer.ui.activity.main.ui.inbox.chat.DialActivity
import com.aait.shaheer.ui.activity.main.ui.menu.TicketDetailsActivity
import com.aait.shaheer.ui.activity.main.ui.other_profile.OtherProfileActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kareem.domain.core.Util
import com.kareem.domain.core.subscribeWithLoading
import kotlin.random.Random


class MessageService: FirebaseMessagingService() {

    private var msg: String?=""
    private var reciverId: String?=""
    private var senderId: String? = ""
    private var type: String?=""
    private var conversationId: String? = ""
    private var conversationName: String? = ""
    private var image: String? = ""

    override fun onNewToken(deviceId: String) {
        super.onNewToken(deviceId)
        sendRequestFcm(baseRepository.deviceId,deviceId)

    }

    private fun sendRequestFcm(oldId: String, newId: String) {
        authRepository.updateFcm(oldId,newId).subscribeWithLoading({},{baseRepository.deviceId=newId},{},{})
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.let {
            Log.e("fcm",it.data.toString())
            val message_ar = it.data["message_ar"]
            val message_en = it.data["message_en"]
            msg = if (baseRepository.lang=="ar"){
                message_ar
            } else{
                message_en
            }
            val title=it.data["title"]
            when(remoteMessage.data["key"]){
                "answerContact"->{
                    val help_id=it.data["help_id"]
                    val intent = Intent(this, QustAnswActivity::class.java)
                    intent.putExtra("help_id", help_id)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    sendNormalNotification(msg,title,intent)
                }
                "answerTicket"->{
                    val ticket_id=it.data["ticket_id"]
                    val intent = Intent(this, TicketDetailsActivity::class.java)
                    intent.putExtra("ticket_id", ticket_id)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    sendNormalNotification(msg,title,intent)
                }
                "newCall"->{
                    val api_key=it.data["OPENTOK_API_KEY"]
                    val session=it.data["session_token"]
                    val token=it.data["opentok_token"]
                    val userId=it.data["user_id"]
                    val name=it.data["name"]
                    val avatar=it.data["avatar"]

                    //handleVoipNotification(api_key,session,token,userId,name,avatar)
                    startActivity(Intent(applicationContext,DialActivity::class.java).apply {
                        flags = FLAG_ACTIVITY_NEW_TASK
                        putExtra("avatar",avatar)
                        putExtra("name",name)
                        putExtra(CALL_RESPONSE_ACTION_KEY, RECEIVE_CALL)
                        putExtra(AudioActivity.key, api_key)
                        putExtra(AudioActivity.session, session)
                        putExtra(AudioActivity.token, token)

                    })

                }
                "new_message"->{
                    val conversation_id=it.data["conversation_id"]
                    val intent = Intent(this, ChatActivity::class.java)
                    intent.putExtra("id", conversation_id!!)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    sendNormalNotification(msg,title,intent)
                }
                "follow"->{
                    val profile_id=it.data["user_id"]
                    val intent = Intent(this, OtherProfileActivity::class.java)
                    intent.putExtra("profile", profile_id!!.toInt())
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    sendNormalNotification(msg,title,intent)
                }

                else->{
                    if (remoteMessage.data["key"]=="likePost"||remoteMessage.data["key"]=="Repost"||remoteMessage.data["key"]=="commentPost"
                        ||remoteMessage.data["key"]=="replyComment"||remoteMessage.data["key"]=="likeComment"
                        ||remoteMessage.data["key"]=="newPost"
                    ){

                        val post_id=it.data["post_id"]
                        val intent = Intent(this, ProductDetailsActivity::class.java)
                        intent.putExtra("post_id", post_id)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        sendNormalNotification(msg,title,intent)
                    }
                    else if (remoteMessage.data["key"]!!.contains("Order",true)){

                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("to_service", true)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        sendNormalNotification(msg,title,intent)
                    }
                    else{
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("to_notifications", true)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        sendNormalNotification(msg,title,intent)
                    }
                }
            }
        }


    }

    private fun handleIncomeService(
        apiKey: String?,
        session: String?,
        token: String?,
        userId: String?,
        name: String?,
        avatar: String?
    ) {
        val receiveCallAction = Intent(this, IncomingCallNotificationService::class.java)
        receiveCallAction.action = RECEIVE_CALL
        receiveCallAction.apply {
            putExtra(AudioActivity.key, apiKey)
            putExtra(AudioActivity.session, session)
            putExtra(AudioActivity.token, token)
            putExtra("name", name)
            putExtra("notification_id",System.currentTimeMillis().toInt())
            putExtra("avatar", avatar)
        }
        startService(receiveCallAction)

    }


    private fun handleVoipNotification(
        apiKey: String?,
        session: String?,
        token: String?,
        userId: String?,
        name: String?,
        avatar: String?
    ) {
        try {
            val receiveCallAction =
                Intent(applicationContext, HeadsUpNotificationActionReceiver::class.java).apply {
                    putExtra(CALL_RESPONSE_ACTION_KEY, RECEIVE_CALL)
                    putExtra(AudioActivity.key, apiKey)
                    putExtra(AudioActivity.session, session)
                    putExtra(AudioActivity.token, token)
                }




            receiveCallAction.action = "RECEIVE_CALL"
            val cancelCallAction =
                Intent(applicationContext, HeadsUpNotificationActionReceiver::class.java)
            receiveCallAction.putExtra(
                CALL_RESPONSE_ACTION_KEY,
                CANCEL_CALL
            )
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
            if (android.R.attr.data != null) {
                val defaultUri =
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

                notificationBuilder = NotificationCompat.Builder(this, "2222")
                    .setContentText(name)
                    .setContentTitle("Incoming Voice Call")
                    .setSmallIcon(R.drawable.telephone_gray)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .addAction(R.drawable.ic_baseline_call_24, "Receive Call", receiveCallPendingIntent)
                    .addAction(R.drawable.ic_baseline_call_missed_24, "Cancel call", cancelCallPendingIntent)
                    .setAutoCancel(true)
                    .setSound(defaultUri)
                    .setLargeIcon(Util.getBitmapFromURL(avatar!!))
                    .setTimeoutAfter(30000)
                    .setFullScreenIntent(receiveCallPendingIntent, true)
            }
            var incomingCallNotification: Notification? = null
            if (notificationBuilder != null) {
                incomingCallNotification = notificationBuilder.build()
            }
            val nManager = NotificationManagerCompat.from(this)
            nManager.notify(2, incomingCallNotification!!)


        } catch (e: Exception) {
            e.printStackTrace()
        }


    }
    fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "2222",
                "voip",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Call Notifications"
            val defaultUri =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            channel.lightColor = Color.RED
            channel.importance=NotificationManager.IMPORTANCE_HIGH
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            channel.setSound(defaultUri, AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setLegacyStreamType(AudioManager.STREAM_RING)
                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION).build())

            val manager =
                (applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            manager.createNotificationChannel(channel)

        }
    }
    private fun sendNotification(msg: String?,
                                 title: String?,
                                 activity: Class<*>) {
        val intent = Intent(applicationContext, activity)

        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT/*Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK*/)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(applicationContext)
        builder.setSound(defaultUri)
            .setContentTitle(title)
            .setContentText(msg)
            .setAutoCancel(true)
            .setSmallIcon(R.mipmap.logo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //for android 8 +26 api notification
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val NOTIFICATION_CHANNEL_ID = "2000"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance)
            notificationChannel.enableLights(true)
            notificationChannel.setShowBadge(true)
            notificationChannel.setSound(defaultUri, AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            )
            notificationChannel.lightColor = Color.RED

            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            builder.setChannelId(NOTIFICATION_CHANNEL_ID)
            manager.createNotificationChannel(notificationChannel)
        }
        manager.notify(Random.nextInt(), builder.build())
    }
    private fun sendNormalNotification(
        message: String?,
        title: String?,
        intent: Intent
    ) {
        /*val intent = Intent(this, act)
        intent.putExtra("other_id", senderId)
        intent.putExtra("name", conversationName)
        intent.putExtra("img", image)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP*/
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val defaultUri =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder =
            NotificationCompat.Builder(applicationContext)
        builder.setSound(defaultUri)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSmallIcon(R.mipmap.logo)
            .setContentIntent(pendingIntent)
        val manager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //for android 8 +26 api notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val NOTIFICATION_CHANNEL_ID = "10003"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "NOTIFICATION_CHANNEL_NAME",
                importance
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.setShowBadge(true)
            notificationChannel.vibrationPattern = longArrayOf(
                100,
                200,
                300,
                400,
                500,
                400,
                300,
                200,
                400
            )

            builder.setChannelId(NOTIFICATION_CHANNEL_ID)
            manager.createNotificationChannel(notificationChannel)
        }
        manager.notify(Random.nextInt(), builder.build())
    }

}
package com.aait.shaheer.FCM

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.aait.shaheer.FCM.FCM_ACTIONS.CALL_RESPONSE_ACTION_KEY
import com.aait.shaheer.FCM.FCM_ACTIONS.RECEIVE_CALL
import com.aait.shaheer.ui.activity.main.ui.inbox.chat.AudioActivity


class HeadsUpNotificationActionReceiver  : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            Toast.makeText(context, "asd", Toast.LENGTH_SHORT).show()
            val action = intent.getStringExtra(CALL_RESPONSE_ACTION_KEY)
            val session=intent.getStringExtra(AudioActivity.session)
            val token=intent.getStringExtra(AudioActivity.token)
            val key=intent.getStringExtra(AudioActivity.key)
            action?.let { performClickAction(context, it, key,session,token) }
            // Close the notification after the click action is performed.
            val it = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            context!!.sendBroadcast(it)
            //context.stopService(Intent(context, HeadsUpNotificationService::class.java))
        }
    }

    private fun performClickAction(
        context: Context?,
        action: String,
        apiKey: String?,
        session: String?,
        token: String?
    ) {
       if (action== RECEIVE_CALL){
           val openIntent =  Intent(context, AudioActivity::class.java).apply {
               putExtra(AudioActivity.key, apiKey)
               putExtra(AudioActivity.session, session)
               putExtra(AudioActivity.token, token)
               Toast.makeText(context, "recieved", Toast.LENGTH_SHORT).show()
           }
           openIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
           context?.startActivity(openIntent)
       }
        else{
           val it = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
           context!!.sendBroadcast(it)
       }
    }



}
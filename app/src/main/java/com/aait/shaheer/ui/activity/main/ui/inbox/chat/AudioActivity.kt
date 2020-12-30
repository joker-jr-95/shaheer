package com.aait.shaheer.ui.activity.main.ui.inbox.chat

import android.Manifest
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.opengl.GLSurfaceView
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.view.WindowManager
import com.aait.shaheer.R
import com.aait.shaheer.domain.repository.chat_repository.inboxRepository
import com.google.gson.Gson
import com.kareem.domain.core.toasty
import com.kareem.presetntation.base.ParentActivity
import com.kareem.presetntation.helper.SocketConnection
import com.opentok.android.*
import com.squareup.okhttp.internal.http.SocketConnector
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.activity_audio.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.json.JSONObject
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


class AudioActivity : ParentActivity(), Session.SessionListener, PublisherKit.PublisherListener {

    private var player: MediaPlayer? = null
    private var name: String? = null
    private var userAvatar: String? = null
    private var mSubscriber: Subscriber?=null
    private lateinit var mPublisher: Publisher
    private  var mSession: Session?=null
    private var API_KEY = "46770024"
    private var SESSION_ID = "1_MX40Njc3MDAyNH5-MTU5MDkxODE5NzQ4Mn5ONUZkWUtYZmkwWmdsSE1rM2pDS0s1SlJ-fg"
    private var TOKEN ="T1==cGFydG5lcl9pZD00Njc3MDAyNCZzaWc9MDI5NGFmODM2NDY3YTNmMTRmMzRhYzhkNzUzMDJhZGJhZGZhMDM4MjpzZXNzaW9uX2lkPTFfTVg0ME5qYzNNREF5Tkg1LU1UVTVNRGt4T0RFNU56UTRNbjVPTlVaa1dVdFlabWt3V21kc1NFMXJNMnBEUzBzMVNsSi1mZyZjcmVhdGVfdGltZT0xNTkwOTM2MTIyJnJvbGU9c3Vic2NyaWJlciZub25jZT0xNTkwOTM2MTIyLjM2MzEzNDk5MDQ4Mjc=" /*T1==cGFydG5lcl9pZD00Njc3MDAyNCZzaWc9YmRlYzcwMGNjMTdlN2Y2Y2NiY2I5NjJmOTEwYTgwNTYwNmM4Y2EzMTpzZXNzaW9uX2lkPTFfTVg0ME5qYzNNREF5Tkg1LU1UVTVNRGt4T0RFNU56UTRNbjVPTlVaa1dVdFlabWt3V21kc1NFMXJNMnBEUzBzMVNsSi1mZyZjcmVhdGVfdGltZT0xNTkwOTE4MzMwJm5vbmNlPTAuMjE1NTA2NjQ1MTA2NzkzNTImcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTU5MTUyMzEzMCZpbml0aWFsX2xheW91dF9jbGFzc19saXN0PQ==*/
    private val LOG_TAG = AudioActivity::class.java.simpleName
    private var isFirstTime=true

    override val layoutResource: Int
        get() = R.layout.activity_audio

    override fun init() {
        name=intent.getStringExtra("name")
        userAvatar=intent.getStringExtra("avatar")
       // setSocketCall()
        setBg()
        requestPermissions()
        setActions()
    }
        private fun setSocketCall() {
        //conversation_id , receiver_id, sender_id , name ,avatar
        var convId=intent.getStringExtra("conv_id")
        var recv_id=intent.getStringExtra("recv_id")
        Log.e("ids", "$convId,$recv_id")
        val jsonObject = JSONObject()
        jsonObject.put("conversation_id", Gson().toJson(convId.toInt()))
        jsonObject.put("sender_id", Gson().toJson(inboxRepository.loadUser()?.id))
        jsonObject.put("name", Gson().toJson(inboxRepository.loadUser()?.name))
        jsonObject.put("avatar", Gson().toJson(inboxRepository.loadUser()?.avatar))
        jsonObject.put("receiver_id", Gson().toJson(recv_id.toInt()))
        SocketConnection.addEvent("newCall",jsonObject)
    }

    private fun setActions() {
        end_call_iv.onClick {
            mSession?.disconnect()
            mSession?.unsubscribe(mSubscriber)
            mSession?.unpublish(mPublisher)
            mSession?.deleteObservers()
            finish()
        }
    }

    private fun setBg() {
        Picasso.get().load(userAvatar).into(object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {

            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                Blurry.with(applicationContext).radius(25).sampling(3).from(bitmap).into(bg_avatar)
            }
        })


        Picasso.get().load(userAvatar).into(avatar)
        tv_name.text=name

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private fun requestPermissions() {
        val perms = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
        if (EasyPermissions.hasPermissions(
                this,
                *perms
            )
        ) { // initialize view objects from your layout
// initialize and connect to the session
            if (intent.hasExtra(Tag)) {
                intent.getStringExtra(token)
                SESSION_ID=intent.getStringExtra(session)!!
                TOKEN=intent.getStringExtra(token)!!
                API_KEY=intent.getStringExtra(key)!!
                createSession()
            }
        }
        else {
            EasyPermissions.requestPermissions(
                this,
                "This app needs access to your camera and mic to make video calls",
                RC_VIDEO_APP_PERM,
                *perms
            )
        }
    }

    private fun createSession() {
        mSession = Session.Builder(this, API_KEY, SESSION_ID).build()
        mSession?.setSessionListener(this)
        mSession?.connect(TOKEN)

        if (intent.getStringExtra(Tag)=="publisher") {
            ring()
        }



    }
    private var savedVolumeControlStream: Int = 1

    private fun ring() {
        savedVolumeControlStream = volumeControlStream
        volumeControlStream = AudioManager.STREAM_RING
        player = MediaPlayer.create(applicationContext,R.raw.hang)
        player?.isLooping=true
        player?.start()
    }

    override fun onStreamDropped(p0: Session?, p1: Stream?) {
        Log.e(LOG_TAG, "Stream Dropped")
        SoundPoolManager.getInstance(this).playDisconnect()
        stopFlag()
        mSubscriber = null
        tv_connecting.text=getString(R.string.lost_connection)
        finish()

    }
    private fun stopFlag() {
        savedVolumeControlStream = volumeControlStream
        volumeControlStream = AudioManager.STREAM_VOICE_CALL
        var player = MediaPlayer.create(applicationContext,R.raw.stop)
        player?.isLooping=false
        player?.start()
        player?.setOnCompletionListener {
            player?.release()
            player=null
        }
    }
fun removeSound(){
    if (player!=null) {
        if (player?.isPlaying == true) {
        player?.stop()
        }
    }
}
    override fun onStreamReceived(p0: Session?, stream: Stream?) {

        Log.e("conn_id",mSession?.connection?.connectionId+","+stream?.connection?.connectionId)
        //toasty("recieved")
        if (mSubscriber==null ) {
            //new client
            if (intent.getStringExtra(Tag)=="publisher") {
                if (mSession?.connection?.connectionId != stream?.connection?.connectionId) {
                    removeSound()
                    tv_connecting.text = getString(R.string.connected)
                }
            }
            else{
                tv_connecting.text = getString(R.string.connected)
            }


            mSubscriber = Subscriber.Builder(this, stream).build()
            mSession?.subscribe(mSubscriber)

        }

    }


    override fun onConnected(session: Session?) {
        Log.e(LOG_TAG,"session connected")
        //toasty("connect")
        Log.e(LOG_TAG,"conn_id:"+session?.connection?.connectionId)
        mPublisher = Publisher.Builder(this).build()
        mPublisher.setPublisherListener(this)
        mPublisher.publishAudio=true
        mPublisher.publishVideo=false

        //publisher_container.addView(mPublisher.view)
        if (mPublisher.view is GLSurfaceView) {
            (mPublisher.view as GLSurfaceView).setZOrderOnTop(true)
        }

        mSession?.publish(mPublisher)

       // ring()

    }

    override fun onDisconnected(p0: Session?) {
        Log.e(LOG_TAG,"session disconnect")
        //toasty("disconnect")
       // tv_connecting.text=getString(R.string.lost_connection)
        removeSound()
    }

    override fun onError(p0: Session?, p1: OpentokError?) {
        //toasty("error")
       // removeSound()
    }

    override fun onStreamCreated(p0: PublisherKit?, p1: Stream?) {
            //stream created
        //toasty("created")
        Log.e(LOG_TAG,"session created")
    }

    override fun onStreamDestroyed(p0: PublisherKit?, p1: Stream?) {
        Log.e(LOG_TAG,"session destroyed")
        //toasty("destroyed")

        mSession?.disconnect()
        mSession?.unsubscribe(mSubscriber)
        mSession?.unpublish(mPublisher)
        mSession?.deleteObservers()
        player?.release()
        player=null
        removeSound()



    }

    override fun onError(p0: PublisherKit?, p1: OpentokError?) {
        Log.e("error",p1?.message)
    }

    companion object {
        private const val  RC_VIDEO_APP_PERM = 124
        val Tag:String="reciever"
        val session:String="Session"
        val token:String="Token"
        val key:String="Key"
    }

    override fun onPause() {
        Log.d(LOG_TAG, "onPause")
        super.onPause()
        if (mSession != null) {
            mSession?.onPause()

        }
    }

    override fun onResume() {
        Log.d(LOG_TAG, "onResume")
        super.onResume()
        if (mSession != null) {
            mSession?.onResume()
        }
    }
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val window: Window = window
        window.addFlags(
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        )

    }

    override fun onDestroy() {
        Log.e("des","1")
        mSession?.disconnect()
        mSession?.unsubscribe(mSubscriber)
        mSession?.unpublish(mPublisher)
        mSession?.deleteObservers()
        player?.release()
        player=null
        super.onDestroy()

    }

}
package com.aait.shaheer.ui.activity.main.ui.inbox.chat

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import com.aait.shaheer.R
import com.aait.shaheer.domain.repository.home_repository.homeRepository
import com.kareem.presetntation.base.ParentActivity
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.activity_dial.*
import org.jetbrains.anko.sdk27.coroutines.onClick



class DialActivity : ParentActivity() {
    private var savedVolumeControlStream: Int = 0
    override val layoutResource: Int
        get() = R.layout.activity_dial
    private var name: String? = null
    private var apiKey: String? = null
    private var token: String? = null
    private var session: String? = null
    private var userAvatar: String? = null



    override fun init() {
        name=intent.getStringExtra("name")
        userAvatar=intent.getStringExtra("avatar")
         apiKey=intent?.getStringExtra(AudioActivity.key)
         session=intent?.getStringExtra(AudioActivity.session)
         token=intent?.getStringExtra(AudioActivity.token)

        setBg()
        ring()

        setActions()
    }

    private fun ring() {
        savedVolumeControlStream = volumeControlStream
        volumeControlStream = AudioManager.STREAM_RING

        SoundPoolManager.getInstance(this).playRinging()
        Handler().postDelayed({
            SoundPoolManager.getInstance(this).stopRinging()
            finish()
        },30000)

    }

    private fun setActions() {

        iv_ans.onClick {
            SoundPoolManager.getInstance(applicationContext).stopRinging()
            val openIntent =  Intent(applicationContext, AudioActivity::class.java).apply {
                putExtra(AudioActivity.key, apiKey)
                putExtra(AudioActivity.session, session)
                putExtra(AudioActivity.token, token)
                putExtra("avatar", userAvatar)
                putExtra("name", name)
                putExtra(AudioActivity.Tag,"tag")
            }
            startActivity(openIntent)
            finish()
                    }
        end_call_iv.onClick {
            SoundPoolManager.getInstance(applicationContext).stopRinging()
            finish()
        }

    }

    override fun onDestroy() {
        SoundPoolManager.getInstance(applicationContext).release()
        volumeControlStream = savedVolumeControlStream
        SoundPoolManager.getInstance(this).release()

        super.onDestroy()
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
}
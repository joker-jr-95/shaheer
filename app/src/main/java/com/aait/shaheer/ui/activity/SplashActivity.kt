package com.aait.shaheer.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import com.aait.shaheer.BuildConfig
import com.aait.shaheer.R
import com.aait.shaheer.domain.repository.auth_repository.authRepository
import com.aait.shaheer.domain.repository.base_repository.baseRepository
import com.aait.shaheer.domain.repository.chat_repository.inboxRepository
import com.aait.shaheer.domain.repository.home_repository.homeRepository
import com.aait.shaheer.ui.activity.connect.SuggestActivity
import com.aait.shaheer.ui.activity.intro.IntroActivity
import com.aait.shaheer.ui.activity.main.MainActivity
import com.google.firebase.iid.FirebaseInstanceId
import com.kareem.presetntation.base.ParentActivity
import com.kareem.presetntation.helper.SocketConnection
import org.json.JSONObject


class SplashActivity : ParentActivity() {
    override val layoutResource: Int
        get() = R.layout.activity_splash

    @SuppressLint("NewApi")
    override fun init() {
    //Log.e("token",FirebaseInstanceId.getInstance().token)
        
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            if (BuildConfig.DEBUG)  Log.e("token", it.token)

            baseRepository.deviceId=it.token
        }
        Handler().postDelayed({

            if (authRepository.isLogin()) {
                if (homeRepository.hasFriends().blockingGet() || homeRepository.loadUser()?.you_following!!){
                    startActivity(Intent(this, MainActivity::class.java))
                }
                else {
                    startActivity(Intent(this, SuggestActivity::class.java))
                }
                finish()
            }
            else{
                startActivity(Intent(this, IntroActivity::class.java))
                finish()
            }
        },3000)

    }



}
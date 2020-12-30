package com.aait.shaheer.ui.activity.intro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aait.shaheer.R
import com.aait.shaheer.ui.activity.auth.LoginActivity
import com.aait.shaheer.ui.activity.auth.RegisterActivity
import com.kareem.presetntation.base.ParentActivity
import kotlinx.android.synthetic.main.activity_getting_start.*
import kotlinx.android.synthetic.main.activity_getting_start.register_btn
import kotlinx.android.synthetic.main.activity_intro.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class GettingStartActivity : ParentActivity() {
    override val layoutResource: Int
        get() = R.layout.activity_getting_start

    override fun init() {
        login_lay.onClick {
            startActivity(Intent(applicationContext,LoginActivity::class.java))
        }
        register_btn.onClick {
            startActivity(Intent(applicationContext,RegisterActivity::class.java))
        }
    }

}
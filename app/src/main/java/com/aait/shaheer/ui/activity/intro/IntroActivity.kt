package com.aait.shaheer.ui.activity.intro

import android.content.Intent
import android.util.Log
import android.view.animation.ScaleAnimation
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.intro_model.Data
import com.aait.shaheer.ui.activity.auth.LoginActivity
import com.aait.shaheer.ui.activity.auth.RegisterActivity
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty

import com.kareem.presetntation.base.ParentActivity
import com.kareem.presetntation.features.on_boarding.IntroSliderAdapter
import introGateway
import kotlinx.android.synthetic.main.activity_intro.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class IntroActivity : ParentActivity(), ViewPager.OnPageChangeListener {
    override val layoutResource: Int
        get() = R.layout.activity_intro




    override fun init() {
        tab_layout.setupWithViewPager(pager, true);
        sendRequestData()
        setActions()



    }

    private fun setActions() {
        register_btn.onClick {
            startActivity(Intent(applicationContext, RegisterActivity::class.java))
        }
        login_btn.onClick {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
        }
        skip.onClick {
            startActivity(Intent(applicationContext, GettingStartActivity::class.java))
        }
    }

    private fun sendRequestData() {
        introGateway.getSliders().subscribeWithLoading({showProgressDialog()},{handleData(it.data)},{handleError(it)},{})
    }

    private fun handleData(data: List<Data>?) {
        hideProgressDialog()
        setAdapter(data?.map { it.image } as ArrayList<String>, data.map { it.name } as ArrayList<String>,
            data.map { it.description } as ArrayList<String>)
    }

    private fun handleError(error: Throwable) {
        hideProgressDialog()
        toasty(error.message!!,2)
        Log.e("error",error.message)
    }

    override fun onPageScrollStateChanged(p0: Int) {

    }
private fun setAdapter( imgs:ArrayList<String>,
                         titles:ArrayList<String>,
                         descs:ArrayList<String>){
    val sliderAdapter = IntroSliderAdapter(this,imgs,titles,descs)
    pager.adapter=sliderAdapter

    pager.offscreenPageLimit=3
    pager.addOnPageChangeListener(this)

}
    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
    }

    override fun onPageSelected(position: Int) {
        //addDots(position)
    }




    private lateinit var list: Array<TextView?>

    override fun onBackPressed() {
        startActivity(Intent(applicationContext,GettingStartActivity::class.java))
        finish()

    }
    }



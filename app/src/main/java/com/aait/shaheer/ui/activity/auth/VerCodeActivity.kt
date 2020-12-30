package com.aait.shaheer.ui.activity.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.connections_model.SuggestedUsers
import com.aait.shaheer.data_layer.model.register_model.RegisterModel
import com.aait.shaheer.domain.repository.auth_repository.authRepository
import com.aait.shaheer.domain.repository.base_repository.baseRepository
import com.aait.shaheer.ui.activity.connect.SuggestActivity
import com.aait.shaheer.ui.activity.main.MainActivity
import com.chaos.view.PinView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.kareem.presetntation.base.ParentActivity
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_ver_code.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.concurrent.TimeUnit


class VerCodeActivity : ParentActivity() {
    private lateinit var mTimer: CountDownTimer
    override val layoutResource: Int
        get() = R.layout.activity_ver_code



    override fun init() {
        setValidation()
        startTimer()
        setActions()
    }

    private fun setActions() {
        resend_lay.onClick {
            sendRequestResend()
        }

        verify_btn.onClick {
            sendRequest()
        }
    }

    private fun sendRequestResend() {
        authRepository.resendCode().subscribeWithLoading({showProgressDialog()},{handleReset(it)},{handleError(it)},{})
    }

    private fun handleReset(data: RegisterModel) {
        toasty(getString(R.string.sent_successfully))
        mTimer.cancel()
        startTimer()
        hideProgressDialog()
        authRepository.saveToken(data)
        authRepository.saveUser(data)
    }

    private fun sendRequest() {
        authRepository.activeAccount(firstPinView.text.toString()).
            subscribeWithLoading({showProgressDialog()},{handleData(it)},{handleError(it)},{})
    }

    private fun handleData(data: RegisterModel) {
        mTimer.cancel()
        hideProgressDialog()
        authRepository.saveToken(data)
        authRepository.saveUser(data)
        goHome()
    }

    private fun goHome() {
        authRepository.saveLogin(true)

        if (authRepository.loadUser()?.following_users==false){
            startActivity(Intent(applicationContext, SuggestActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
        else {
            startActivity(Intent(applicationContext, MainActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
        finish()
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)
    }

    private fun handleError(error: Throwable) {
        mTimer.cancel()
        hideProgressDialog()
        toasty(error.message!!,2)
        Log.e("error",error.message!!)
    }

    @SuppressLint("CheckResult")
    private fun setValidation() {
        checkPattern(firstPinView)?.subscribe {
            verify_btn.isEnabled=it
            if (it){
                verify_btn.alpha=1f
            }
            else{
                verify_btn.alpha=0.5f

            }
        }
    }

    private fun startTimer() {
        mTimer = object : CountDownTimer(30000L, 1000L) {
            override fun onFinish() {
                timer_txt.visibility= View.INVISIBLE
            }

            override fun onTick(sec: Long) {
                timer_txt.visibility= View.VISIBLE
                timer_txt.text="(${TimeUnit.MILLISECONDS.toSeconds(sec)})"

            }

        }
        mTimer.start()
    }

    fun checkPattern(pattern: PinView): Observable<Boolean>?{
        return RxTextView.textChanges(pattern)
            .map { inputText -> inputText.trim().isNotBlank() && inputText.trim().length > 3}
            .distinctUntilChanged()
    }

}
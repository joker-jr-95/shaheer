package com.aait.shaheer.ui.activity.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.connections_model.SuggestedUsers
import com.aait.shaheer.data_layer.model.register_model.RegisterModel
import com.aait.shaheer.domain.repository.auth_repository.authRepository
import com.aait.shaheer.domain.repository.base_repository.baseRepository
import com.aait.shaheer.ui.activity.connect.SuggestActivity
import com.aait.shaheer.ui.activity.main.MainActivity
import com.google.firebase.iid.FirebaseInstanceId
import com.kareem.domain.core.*
import com.kareem.presetntation.base.ParentActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.sdk27.coroutines.onClick


class LoginActivity : ParentActivity() {
    private  var token: String?=null
    override val layoutResource: Int
        get() = R.layout.activity_login

    @SuppressLint("CheckResult")
    override fun init() {
        val validatePhone = etPhone.validatePhone(input_lay)
        val validatePass = etPass.validatePass(etPassLay)
            setFcm()
        login_btn.checkAuthValidation(validatePhone , validatePass)?.subscribe {
            login_btn.isEnabled=it
            if (it){
                login_btn.alpha=1f
            }
            else{
                login_btn.alpha=0.5f
            }
        }
        setActions()
    }

    private fun setFcm() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            token=it.token
            baseRepository.deviceId=it.token

        }
    }

    private fun setActions() {

        login_btn.onClick {
            /*authRepository.takeIf { etPhone.text.toString().isNotBlank() && etPass.text.toString().length > 5
            }?*/authRepository.login(etPhone.text.toString(),etPass.text.toString(),token)
            .subscribeWithLoading({showProgressDialog()},{handleData(it)},{LogError(it)},{hideProgressDialog()})
        }
        forgot_pass.onClick {
            startActivity(Intent(applicationContext,ForgetPassActivity::class.java))
        }
    }

    @SuppressLint("CheckResult")
    private fun handleData(it: RegisterModel) {
        authRepository.saveUser(it)
        if (it.value == "2") {
            it.msg?.let { it1 -> toasty(it1,2) }
            goActivationCode(it)
        } else {
            authRepository.saveToken(it)
            if (it.data?.active=="pending"){
                goActivationCode(it)
            }

            else if (it.data?.active=="active"){
                goHome()
            }
        }
    }

    private fun goActivationCode(response: RegisterModel) {

        startActivity(Intent(applicationContext, VerCodeActivity::class.java).also {

        }
        )
    }

    private fun LogError(it: Throwable) {
        hideProgressDialog()
        toasty(it.message!!,2)
        Log.e("error12",it.message!!)
    }

    private fun goHome() {
        hideProgressDialog()
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

}
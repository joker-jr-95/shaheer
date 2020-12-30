package com.aait.shaheer.ui.activity.auth

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.register_model.RegisterModel
import com.aait.shaheer.domain.repository.auth_repository.authRepository
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding2.widget.RxTextView
import com.kareem.domain.core.*
import com.kareem.presetntation.base.ParentActivity
import com.kareem.presetntation.helper.disable
import com.kareem.presetntation.helper.enable
import io.reactivex.Observable
import io.reactivex.functions.Function3
import io.reactivex.functions.Function4
import kotlinx.android.synthetic.main.activity_forget_pass_comp.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class ForgetPassCompActivity : ParentActivity() {
    override val layoutResource: Int
        get() = R.layout.activity_forget_pass_comp

    @SuppressLint("CheckResult")
    override fun init() {
        checkConfPass(etCode,etNewPass,etConfPass)?.subscribe {
            if (it){ send_btn.enable() }
            else { send_btn.disable() }
        }

        setActions()
    }

    private fun setActions() {
        send_btn.onClick {
            sendRequest()
        }
    }

    private fun sendRequest() {
        authRepository.resetPassword(etCode.text.toString()
        ,etNewPass.text.toString()
        ).subscribeWithLoading({showProgressDialog()},{
            handleData(it)
        },{handleError(it)},{})
    }

    private fun handleError(error: Throwable) {
        hideProgressDialog()
        Log.e("error",error.message!!)
        toasty(error.message!!,2)

    }

    private fun handleData(data: RegisterModel) {
        hideProgressDialog()
        authRepository.saveToken(data)
        authRepository.saveUser(data)
        goLogin()

    }

    private fun goLogin() {
        startActivity(Intent(applicationContext,LoginActivity::class.java))
    }

    //check pass
    fun checkPass(editText: EditText): Observable<Boolean>? {
        return RxTextView.textChanges(editText)
            .skipInitialValue()
            .map { inputText -> inputText.trim().isNotBlank() && inputText.trim().length > 5}
            .filter {
                if (it){
                    etNewPassLay.helperText=""
                }
                else{
                    etNewPassLay.helperText=getString(R.string.helper_pass)
                }
                it
            }
            .distinctUntilChanged()
    }
    //check ver
    fun checkVer(editText: EditText): Observable<Boolean>? {
        return RxTextView.textChanges(editText)
            .skipInitialValue()
            .map { inputText -> inputText.trim().isNotBlank() && inputText.trim().length > 3}
            .filter {
                if (it){
                    code_lay.helperText=""
                }
                else{
                    code_lay.helperText=getString(R.string.helper_ver)
                }
                it
            }
            .distinctUntilChanged()
    }


    fun checkMatchObsservalbe(etPass:TextInputEditText,etPassConf:TextInputEditText):Observable<Boolean>?{
        return RxTextView.textChanges(etPassConf)
            .skipInitialValue()
            .map { inputText -> inputText.trim().isNotBlank() && inputText.toString() == etPass.text.toString() }
            .filter {
                if (it){
                    etConfPassLay.helperText=""

                }
                else{
                    etConfPassLay.helperText=getString(R.string.error_match)
                }
                it
            }
            .distinctUntilChanged()
    }

    fun checkConfPass(verCode:TextInputEditText,newPass: TextInputEditText,confPass:TextInputEditText): Observable<Boolean>? {
        return Observable.combineLatest(
            checkVer(verCode),
            checkPass(newPass),
            checkMatchObsservalbe(newPass,confPass),
            Function3 { t1:Boolean, t2:Boolean,t4:Boolean ->
                val isCorrect = when{
                    // all accepted
                    t1 && t2  && t4-> true

                    else->false
                }
                isCorrect
            }
        )
            .distinctUntilChanged()

    }



}
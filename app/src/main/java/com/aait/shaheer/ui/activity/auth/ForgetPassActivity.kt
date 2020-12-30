package com.aait.shaheer.ui.activity.auth

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.register_model.RegisterModel
import com.aait.shaheer.domain.repository.auth_repository.authRepository
import com.aait.shaheer.helper.TextDrawable
import com.chaos.view.PinView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.kareem.domain.core.checkVer
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.kareem.domain.core.validatePhone
import com.kareem.presetntation.base.ParentActivity
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_forget_pass.*
import kotlinx.android.synthetic.main.activity_ver_code.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class ForgetPassActivity : ParentActivity() {
    override val layoutResource: Int
        get() = R.layout.activity_forget_pass


    private var countryIso: String?=""

    @SuppressLint("CheckResult")
    override fun init() {
        countryIso=country_code.selectedCountryNameCode
        //setViews()
        etPhone.validatePhone(input_lay).subscribe {

            if (it){
                Log.e("enabled",it.toString())
                forget_btn.isEnabled=true
                forget_btn.alpha=1f
            }
            else{
                Log.e("enabled2",it.toString())
                forget_btn.isEnabled=false
                forget_btn.alpha=0.5f
            }
        }
        forget_btn.onClick {
            sendRequest()
        }

    }

    private fun sendRequest() {
        authRepository.forgetPass(etPhone.text.toString()).subscribeWithLoading({
            showProgressDialog()
        },{handleData(it)},{handleError(it)},{})
    }

    private fun handleData(it: RegisterModel) {
        hideProgressDialog()
        authRepository.saveToken(it)
        authRepository.saveUser(it)
        goComplete()
    }

    private fun goComplete() {
        startActivity(Intent(applicationContext,ForgetPassCompActivity::class.java))
    }

    private fun handleError(error: Throwable) {
        hideProgressDialog()
        toasty(error.message!!,2)
        Log.e("error",error.message!!)
    }


    private fun setViews() {
        etPhone.setCompoundDrawables(
            TextDrawable(
                etPhone,
                country_code.selectedCountryCodeWithPlus + ""
            ), null, null, null
        )

        country_code.setOnCountryChangeListener {
            countryIso=country_code.selectedCountryNameCode
            etPhone.setCompoundDrawables(
                TextDrawable(etPhone, country_code.selectedCountryCodeWithPlus + ""),
                null,
                null,
                null)
        }
    }
}
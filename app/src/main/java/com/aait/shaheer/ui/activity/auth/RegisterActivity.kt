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
import com.aait.shaheer.domain.repository.base_repository.baseRepository
import com.aait.shaheer.helper.TextDrawable
import com.aait.shaheer.ui.activity.main.ui.menu.TermsActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.iid.FirebaseInstanceId
import com.jakewharton.rxbinding2.widget.RxTextView
import com.kareem.domain.core.Util
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.kareem.presetntation.base.ParentActivity
import com.kareem.presetntation.helper.disable
import com.kareem.presetntation.helper.enable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function4
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class RegisterActivity : ParentActivity() {

    private  var token: String?=null
    private var countryIso: String?=""

    override val layoutResource: Int
        get() = R.layout.activity_register

    @SuppressLint("CheckResult")
    override fun init() {
        countryIso=country_code.selectedCountryNameCode
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            token=it.token
            baseRepository.deviceId=it.token
            Log.e("TOKEN",it.token)
        }
        setViews()
        checkAuth(etName,etEmail,etPhone,etPass,etConfPass)?.subscribe {
            if (it){
                Log.e("chec","1")
                reg_btn.enable()
            }
            else{
                Log.e("chec","11")
                reg_btn.disable()
            }
        }
        setActions()

    }

    private fun setActions() {
        terms_lay.onClick {
            startActivity(Intent(applicationContext,TermsActivity::class.java))
        }
        reg_btn.onClick {
            authRepository.takeIf { etPass.text.toString() == etConfPass.text.toString()}
                ?.register(etName.text.toString(),etPhone.text.toString(),etPass.text.toString(),
                    etEmail.text.toString(),countryIso,token
                    )?.subscribeWithLoading({showProgressDialog()},{handleData(it)},{handleError(it)},{})
        }
    }

    private fun handleError(error: Throwable) {
        Log.e("error",error.message!!)
        hideProgressDialog()
        error.message?.let { toasty(it,2) }
    }

    private fun handleData(data: RegisterModel) {
        hideProgressDialog()
        authRepository.apply {
            saveUser(data)
            saveToken(data)
        }
        goVerif()

    }

    private fun goVerif() {
        startActivity(Intent(applicationContext,VerCodeActivity::class.java))
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

    //check pass
    fun checkPass(editText: EditText): Observable<Boolean>? {
        return RxTextView.textChanges(editText)
            .skipInitialValue()
            .map { inputText -> inputText.trim().isNotBlank() && inputText.trim().length > 5}
            .filter {
                if (etConfPass.text.toString().isBlank()){
                if (it){
                    etPassLay.helperText=""
                }
                else{
                    etPassLay.helperText=getString(R.string.helper_pass)
                    reg_btn.disable()
                }
                return@filter it
                }
                else{
                    if (it){
                        if (etPass.text.toString()!=etConfPass.text.toString()){
                            etConfPassLay.helperText=getString(R.string.error_match)
                            reg_btn.disable()
                        }
                        else{
                            etConfPassLay.helperText=""
                        }
                            etPassLay.helperText=""
                        }

                    else{
                        if (etPass.text.toString()!=etConfPass.text.toString()){
                            etConfPassLay.helperText=getString(R.string.error_match)
                            reg_btn.disable()
                        }
                        else{
                            etConfPassLay.helperText=getString(R.string.error_match)
                            reg_btn.disable()
                        }

                        etPassLay.helperText=getString(R.string.helper_pass)
                    }
                        return@filter it && etPass.text.toString()==etConfPass.text.toString()
                }
            }
    }
    //check pass
    fun checkConfPass(editText: EditText): Observable<Boolean>? {
        return RxTextView.textChanges(editText)
            .skipInitialValue()
            .map { inputText -> inputText.trim().isNotBlank() && etPass.text.toString()==inputText.toString()}
            .filter {
                if (it){
                    etConfPassLay.helperText=""
                }
                else{
                    etConfPassLay.helperText=getString(R.string.error_match)
                    reg_btn.disable()
                }
                it
            }
    }
    //check name
    fun checkName(editText: EditText): Observable<Boolean>? {
        return RxTextView.textChanges(editText)
            .skipInitialValue()
            .map { inputText -> inputText.trim().isNotBlank() && inputText.trim().length > 3}
            .filter {
                if (it){
                    name_lay.helperText=""
                }
                else{
                    name_lay.helperText=getString(R.string.helper_name)
                    reg_btn.disable()
                }
                it
            }
    }
  //check mail
    fun checkMail(editText: EditText): Observable<Boolean>? {
        return RxTextView.textChanges(editText)
            .skipInitialValue()
            .map { inputText -> Util.isEmailValid(inputText.toString())}
            .filter {
                if (it){
                    email_lay.helperText=""
                }
                else{
                    email_lay.helperText=getString(R.string.helper_mail)
                    reg_btn.disable()
                }
                it
            }
    }
  //check phone
    fun checkPhone(editText: EditText): Observable<Boolean>? {
        return RxTextView.textChanges(editText)
            .skipInitialValue()
            .map { inputText -> Util.isPhone(inputText.toString())}
            .filter {
                if (it){
                    phone_lay.helperText=""
                }
                else{
                    phone_lay.helperText=getString(R.string.helper_phone)
                    reg_btn.disable()
                }
                it
            }
    }


    fun checkMatchObsservalbe(etPass: TextInputEditText, etPassConf: TextInputEditText): Observable<Boolean>?{
        return RxTextView.textChanges(etPassConf)
            .skipInitialValue()
            .map { inputText -> inputText.trim().isNotBlank() && inputText.toString() == etPass.text.toString() }
            .filter {
                if (it){
                    etConfPassLay.helperText=""

                }
                else{
                    etConfPassLay.helperText=getString(R.string.error_match)
                    reg_btn.disable()
                }
                it
            }
    }
    fun combineObsPass(/*etPassLay: TextInputLayout, etPassConfLay: TextInputLayout*/): Observable<Boolean>?{
        return Observable.combineLatest(
            checkPass(etPass),
            checkConfPass(etConfPass),
            BiFunction { t1, t2 ->
                Log.e("chec2",(t1&&t2).toString())
                return@BiFunction t1&&t2

            }
        )
    }

    fun checkAuth(name: TextInputEditText,mail:TextInputEditText,phone:TextInputEditText, newPass: TextInputEditText, confPass: TextInputEditText): Observable<Boolean>? {
        return Observable.combineLatest(
            checkName(name),
            checkMail(mail),
            checkPhone(phone),
            combineObsPass(),
            Function4 { t1, t2, t3, t4 ->
                Log.e("chec3",(t4).toString())
                val isCorrect = when{
                    // all accepted


                            t1 && t2 && t3 && t4-> true

                    else->false
                }
                isCorrect
            }

        )


    }

}
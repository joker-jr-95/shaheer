package com.aait.shaheer.ui.activity.main.ui.menu

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.common.BaseResponse
import com.aait.shaheer.domain.repository.menu_repository.menuRepository
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding2.widget.RxTextView
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.kareem.presetntation.base.ParentActivity
import io.reactivex.Observable
import io.reactivex.functions.Function3
import io.reactivex.functions.Function4
import kotlinx.android.synthetic.main.activity_change_pass.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class ChangePassActivity : ParentActivity() {
    override val layoutResource: Int
        get() = R.layout.activity_change_pass

    override fun init() {
        setTitleToolbar(getString(R.string.change_password))
        setACtions()
    }

    private fun setACtions() {
        subscribeBtn()
        confirm_btn.onClick {
            menuRepository.changePassword(etOldPass.text.toString(),
                etPass.text.toString()
                ).subscribeWithLoading({showProgressDialog()},{handleData(it)},{handleError(it.message)},{})
        }
    }

    private fun handleData(it: BaseResponse) {
        hideProgressDialog()
        it.msg?.let { it1 -> toasty(it1) }
        onBackPressed()
    }

    private fun handleError(message: String?) {
        hideProgressDialog()
        message?.let { toasty(it,2) }
    }

    @SuppressLint("CheckResult")
    private fun subscribeBtn() {
        checkConfPass(etOldPass,etPass,etConfPass)!!.subscribe {
            confirm_btn.isEnabled=it
            if (it){
                confirm_btn.alpha=1f
            }
            else{
                confirm_btn.alpha=0.5f
            }
        }
    }

    //check pass
    fun checkPass(editText: EditText,textInputLay: TextInputLayout): Observable<Boolean>? {
        return RxTextView.textChanges(editText)
            .skipInitialValue()
            .map { inputText -> inputText.trim().isNotBlank() && inputText.trim().length > 5}
            .doOnNext {
                if (it){
                    textInputLay.helperText=""
                }
                else{
                    textInputLay.helperText=getString(R.string.error_password)
                }
            }
            .skip(1)
            .distinctUntilChanged()
    }


    fun checkMatchObsservalbe(etPass: TextInputEditText, etPassConf: TextInputEditText,etconfLay: TextInputLayout): Observable<Boolean>?{
        return RxTextView.textChanges(etPassConf)
            .skipInitialValue()
            .map { inputText -> inputText.trim().isNotBlank() && inputText.toString() == etPass.text.toString() }
            .doOnNext {
                if (it){
                    etconfLay.helperText=""
                }
                else{
                    etconfLay.helperText=getString(R.string.error_match)
                }
            }
            .skip(1)
            .distinctUntilChanged()
    }

    fun checkConfPass(currentPass: TextInputEditText, newPass: TextInputEditText, confirmNew: TextInputEditText): Observable<Boolean>? {
        return Observable.combineLatest(
            checkPass(currentPass,etOldPassLay),
            checkPass(newPass,etNewPassLay),
            checkPass(confirmNew,etconfLay),
            checkMatchObsservalbe(newPass,confirmNew,etconfLay),
            Function4 { t1:Boolean, t2:Boolean, t3:Boolean, t4:Boolean ->
                val isCorrect = when{
                    // all accepted
                    t1 && t2 && t3&&t4-> true

                    else->false
                }
                isCorrect
            }
        )
            .skip(1)
            .distinctUntilChanged()

    }



}
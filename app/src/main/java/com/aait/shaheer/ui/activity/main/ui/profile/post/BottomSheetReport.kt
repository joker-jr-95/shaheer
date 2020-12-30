package com.aait.shaheer.ui.activity.main.ui.profile.post

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.connections_model.Friend
import com.aait.shaheer.domain.repository.home_repository.homeRepository
import com.aait.shaheer.helper.KeyBoardHelper
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import kotlinx.android.synthetic.main.bottom_sheet_report.*
import kotlinx.android.synthetic.main.bottom_sheet_report.view.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onFocusChange

class BottomSheetReport(val user:Friend):BottomSheetDialogFragment() {
    private var progressDialog: ACProgressFlower? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.MyBottomSheetDialogTheme)

    }
    override fun setupDialog(dialog: Dialog, style: Int) {
        val view = View.inflate(context, R.layout.bottom_sheet_report, null)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(view)
        KeyBoardHelper(activity!!,view.rootView)
        Glide.with(context!!)
            .load(user.avatar)
            .into(view.iv_user)
        view.tv_name.text=user.name

        view.et_reason.onFocusChange { v, hasFocus ->
            if (hasFocus){
                Handler().postDelayed({
                    val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as (InputMethodManager)
                    imm.showSoftInput(view.save_btn, InputMethodManager.SHOW_IMPLICIT)


                },300)
            }
        }

        view.save_btn.onClick {
            if (view.et_reason.text.isNotBlank()){
                //request
                sendRequest(view)
            }
            else{
                view.et_reason.error=getString(R.string.empty_field)
            }
        }
    }
    fun showProgressDialog(message: String?=null) {
        progressDialog = ACProgressFlower.Builder(activity)
            .direction(ACProgressConstant.DIRECT_CLOCKWISE)
            .themeColor(Color.GRAY)
            .fadeColor(Color.WHITE)
            .build()
        progressDialog!!.show()
    }
    fun hideProgressDialog(){
        progressDialog?.hide()
        progressDialog?.dismiss()
    }

    private fun sendRequest(view: View) {
        homeRepository.reportUser(user.id.toString(),view.et_reason.text.toString())
            .subscribeWithLoading({showProgressDialog()},{handleData(it.msg)},{handleError(it.message)},{})
    }

    private fun handleError(message: String?) {
        hideProgressDialog()
        activity?.toasty(message!!,2)
        Log.e("error",message)
    }

    private fun handleData(msg: String?) {
        hideProgressDialog()
        activity?.toasty(msg!!)
        dismiss()
    }

}
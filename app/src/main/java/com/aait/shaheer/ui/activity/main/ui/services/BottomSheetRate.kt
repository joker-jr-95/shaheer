package com.aait.shaheer.ui.activity.main.ui.services


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
import com.aait.shaheer.data_layer.model.service_model.InprogressOrder
import com.aait.shaheer.domain.repository.home_repository.homeRepository
import com.aait.shaheer.domain.repository.order_repository.orderRepository
import com.aait.shaheer.helper.KeyBoardHelper
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import kotlinx.android.synthetic.main.bottom_sheet_rate.*
import kotlinx.android.synthetic.main.bottom_sheet_rate.view.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onFocusChange

class BottomSheetRate(val item:InprogressOrder, val isSold:Boolean):BottomSheetDialogFragment() {
    private var userId: Int? = 0
    private var progressDialog: ACProgressFlower? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.MyBottomSheetDialogTheme)

    }
    override fun setupDialog(dialog: Dialog, style: Int) {
        val view = View.inflate(context, R.layout.bottom_sheet_rate, null)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(view)
        KeyBoardHelper(activity!!,view.rootView)

        if (isSold) {
            userId=item.client?.id
            Glide.with(context!!)
                .load(item.client?.avatar)
                .into(view.iv_user)
            view.tv_name.text = item.client?.name
        }
        else{
            userId=item.provider?.id
            Glide.with(context!!)
                .load(item.provider?.avatar)
                .into(view.iv_user)
            view.tv_name.text = item.provider?.name
        }
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
        with(view) {

                orderRepository.rateOrder(
                    item.post_id.toString()
                    , userId.toString(), this.rate_.rating.toString(), et_reason.text.toString()
                )
                    .subscribeWithLoading({ showProgressDialog() },
                        { handleRate(it.msg) },
                        { handleError(it.message) },
                        {})

        }
    }

    private fun handleRate(msg: String?) {
        hideProgressDialog()
        msg?.let { activity!!.toasty(it) }
        dismiss()
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
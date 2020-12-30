package com.aait.shaheer.ui.activity.main.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.aait.shaheer.R
import kotlinx.android.synthetic.main.normal_layout.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class MyDialogFragment(val title:String?=null,val actionBtn:String?=null,val cancelBtn:String?=null
                       ,val dialogCallBack:(type:String)->Unit):DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


          val  view = inflater.inflate(R.layout.normal_layout, container, false)
            // Set transparent background and no title
            if (dialog != null && dialog!!.window != null) {
                dialog!!.window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
            }

            return view

    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            dialog.window!!.setLayout(
                600,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )


        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        /*if (action_btn.text==context?.getString(R.string.sign_out)){
            tv_head.visibility=View.VISIBLE
        }
        else{
            tv_head.visibility=View.GONE
        }*/

        if (!title.isNullOrBlank()){
            tv_title.text=title
        }
        if (!actionBtn.isNullOrBlank()){
            action_btn.text=actionBtn
        }
        if (!cancelBtn.isNullOrBlank()){
            cancel_btn.text=cancelBtn
        }
        action_btn.onClick {
            dialogCallBack(ACTION)
            dismiss()
        }
        cancel_btn.onClick {
            dialogCallBack(CANCEL)
            dismiss()
        }
        if (actionBtn!!.contains(getString(R.string.delete))){
            action_btn.setTextColor(ContextCompat.getColor(context!!,android.R.color.holo_red_dark))
        }
    }
    companion object DialogAction{
            val ACTION = "action"
            val CANCEL = "cancel"

        }
    }


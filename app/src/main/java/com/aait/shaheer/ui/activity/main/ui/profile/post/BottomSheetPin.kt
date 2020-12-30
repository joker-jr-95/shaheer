package com.aait.shaheer.ui.activity.main.ui.profile.post

import android.app.Dialog
import android.view.View
import com.aait.shaheer.R
import com.aait.shaheer.ui.activity.main.ui.dialog.MyDialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.botto_sheet_pin.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class BottomSheetPin(val isPin:Boolean,val callBackPin:(isPin:Boolean)->Unit
,val callBackDelete:(isDelete:Boolean)->Unit
): BottomSheetDialogFragment() {


    private lateinit var pintTitle: String

    override fun setupDialog(dialog: Dialog, style: Int) {
        val view = View.inflate(context, R.layout.botto_sheet_pin, null)
        dialog.setContentView(view)
        pintTitle=getString(R.string.confirm_pin)
        if (isPin) {
            view.pin_tv.text = getString(R.string.unpin)
        }
        else{
            view.pin_tv.text=getString(R.string.pin_post)
        }

        view.pin_lay.onClick {

            if (view.pin_tv.text == getString(R.string.pin_post)){
                pintTitle=getString(R.string.confirm_pin)
                val myDialog = MyDialogFragment(
                    pintTitle, getString(R.string.yes)
                    , getString(R.string.cancel)
                ) {

                    if (MyDialogFragment.ACTION == it) {
                        view.pin_tv.text = getString(R.string.unpin)
                        callBackPin(true)
                        this@BottomSheetPin.dismiss()
                    }

                }
                myDialog.show(fragmentManager!!, "")
            }
            else {
                pintTitle=getString(R.string.confirm_unpin)
                val myDialog = MyDialogFragment(
                    pintTitle, getString(R.string.yes)
                    , getString(R.string.cancel)
                ) {

                    if (MyDialogFragment.ACTION == it) {
                        view.pin_tv.text = getString(R.string.pin_post)
                        callBackPin(false)
                        this@BottomSheetPin.dismiss()
                    }

                }
                myDialog.show(fragmentManager!!, "")
            }
        }

        view.delete_lay.onClick {
            val myDialog=  MyDialogFragment(getString(R.string.confirm_delete),
                getString(R.string.delete_post)
                ,getString(R.string.cancel)
            ){
                if (MyDialogFragment.ACTION==it){
                        callBackDelete(true)
                        this@BottomSheetPin.dismiss()
                }
            }
            myDialog.show(fragmentManager!!,"")

        }


    }
}
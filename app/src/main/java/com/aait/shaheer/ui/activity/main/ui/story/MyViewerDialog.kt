package com.aait.shaheer.ui.activity.main.ui.story

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.recyclerview.widget.LinearLayoutManager
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.stories_model.Viewer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_my_viewer.view.*

class MyViewerDialog(val viewers: List<Viewer>,val onHide:()->Unit) : BottomSheetDialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.MyBottomSheetDialogTheme)
    }
val mAdapter=ViewerAdapter()
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val view = View.inflate(context, R.layout.bottom_sheet_my_viewer, null)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(view)
        view.rec_viewer.layoutManager=LinearLayoutManager(activity)
        view.rec_viewer.adapter=mAdapter
        mAdapter.swapData(viewers)
        if (viewers.isEmpty()){
            view.empty_tv.visibility=View.VISIBLE
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onHide()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onHide
    }
}
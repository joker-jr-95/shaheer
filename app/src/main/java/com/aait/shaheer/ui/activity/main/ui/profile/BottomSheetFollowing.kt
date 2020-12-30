package com.aait.shaheer.ui.activity.main.ui.profile

import android.app.Dialog
import android.util.Log
import android.view.View
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.connections_model.Friend
import com.aait.shaheer.helper.Tweet
import com.aait.shaheer.ui.activity.main.ui.dialog.MyDialogFragment
import com.aait.shaheer.ui.activity.main.ui.profile.post.BottomSheetReport
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.botto_sheet_following.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class BottomSheetFollowing(val user:Friend,val callBackFollow:(user:Friend,Tweet)->Unit) : BottomSheetDialogFragment() {

    private lateinit var blockTitle: String
    private lateinit var muteTitle: String

    companion object{
        var isMute=false
        var isBlocked=false
        var firstTime=true
        var userId=0

    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        val view = View.inflate(context, R.layout.botto_sheet_following, null)
        dialog.setContentView(view)
         muteTitle=getString(R.string.mute_title)
         blockTitle=getString(R.string.confirm_block)

        if (firstTime){
            isMute= user.muted!!
            isBlocked= user.blocked!!
            firstTime=false

        }


        Log.e("mute_first", firstTime.toString())
        Log.e("mute",user.muted.toString())

        if (user.following_him!!) {
            view.follow_tv.text = getString(R.string.unfollow_user)
        }
        else{
            view.follow_tv.text=getString(R.string.follow_user)
        }
        if (user.muted==true){
            view.mute_tv.text=getString(R.string.unmute_user)
        }
        else{
            view.mute_tv.text=getString(R.string.mute_user)
        }
        if (user.blocked==true){
            view.block_tv.text=getString(R.string.unblock_user)
        }
        else{
            view.block_tv.text=getString(R.string.block_user)
        }

        view.follow_lay.onClick {
            val b = !user.following_him
            callBackFollow(user,Tweet.FOLLOW)
            if (view.follow_tv.text==getString(R.string.unfollow_user)) {
                view.follow_tv.text = getString(R.string.follow_user)
            }
            else{
                view.follow_tv.text=getString(R.string.unfollow_user)
            }
        }

        view.mute_lay.onClick {

            if (view.mute_tv.text==getString(R.string.mute_user)) {
                val myDialog = MyDialogFragment(
                    muteTitle,
                    getString(R.string.confirm)
                    , getString(R.string.cancel)
                ) {
                    if (MyDialogFragment.ACTION == it) {
                        view.mute_tv.text=getString(R.string.unmute_user)
                        isMute=true
                        user.muted= isMute
                        callBackFollow(user, Tweet.MUTE)

                        this@BottomSheetFollowing.dismiss()
                    }
                }
                myDialog.show(fragmentManager!!, "")
            }
            else{
                isMute=false
                user.muted= isMute
                view.mute_tv.text=getString(R.string.mute_user)
                callBackFollow(user, Tweet.MUTE)
            }


        }
        view.block_lay.onClick {

            if (view.block_tv.text==getString(R.string.block_user)) {

                val myDialog=  MyDialogFragment(blockTitle,
                    getString(R.string.confirm)
                    ,getString(R.string.cancel)
                ){
                    if (MyDialogFragment.ACTION==it){
                        view.block_tv.text=getString(R.string.unblock_user)
                        isBlocked=true
                        user.blocked= isBlocked
                        callBackFollow(user,Tweet.BLOCK)
                        this@BottomSheetFollowing.dismiss()
                    }
                }
                myDialog.show(fragmentManager!!,"")
            }
            else{
                isBlocked=false
                user.blocked= isBlocked
                view.block_tv.text=getString(R.string.block_user)
                callBackFollow(user,Tweet.BLOCK)
            }


        }
        view.report_lay.onClick {
            val myDialog=  BottomSheetReport(user)
            myDialog.show(fragmentManager!!,"report")


        }


    }
}
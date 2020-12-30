package com.aait.shaheer.ui.activity.connect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.connections_model.Data
import com.aait.shaheer.data_layer.model.connections_model.Friend
import com.aait.shaheer.data_layer.remote.HomeApi
import com.aait.shaheer.domain.repository.home_repository.homeRepository
import com.aait.shaheer.ui.activity.main.MainActivity
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.kareem.presetntation.base.ParentActivity
import com.kareem.presetntation.helper.setRecycler
import kotlinx.android.synthetic.main.activity_suggest.*

class SuggestActivity : ParentActivity(), OnFollowListener {
    override val layoutResource: Int
        get() = R.layout.activity_suggest

    val mAdapter=SuggestedAdapter(this)

    override fun init() {
        setTitleToolbar(getString(R.string.connect))
        setRec()
        if (intent.getBooleanExtra("from_more",false)){
            sendRequest()
        }
        else {
            sendRequestConnect()
        }
    }

    private fun setRec() {
        rec_suggested.setRecycler(applicationContext,adapter = mAdapter)
    }
    private fun sendRequest() {
        intent.getBooleanExtra("from_more",false)
        homeRepository.getSuggestdUsers().subscribeWithLoading({showShimmer()},{ it.data?.let { it1 ->
            handleData(
                it1
            )
        } },{ handleError(it) },{})
    }

    private fun sendRequestConnect() {
        homeRepository.getSuggestdUsers().subscribeWithLoading({
            showShimmer()
        },{handleData(it.data)},{handleError(it)},{})
    }

    private fun handleData(data: Data?) {
        shimmer_load.visibility= View.GONE
        mAdapter.swapData(data?.suggestedUsers!!)
        if (data.friends!!.isNotEmpty()){
            homeRepository.hasFriends(true)
        }
    }

    private fun handleError(error: Throwable) {
        toasty(error.message!!,2)
        Log.e("error",error.message!!)
    }

    private fun showShimmer() {
        shimmer_load.visibility= View.VISIBLE
    }

    override fun onBackPressed() {
        if (intent.getBooleanExtra("from_more",false)) {
            super.onBackPressed()
        } else {
            goHome()
        }
    }

    private fun goHome() {
        startActivity(Intent(applicationContext,MainActivity::class.java))
        finish()
            // finish()
    }

    override fun onFollow(item: Friend, isFav: Boolean) {
        homeRepository.follow(item.id.toString()).subscribeWithLoading({},{ if (item.following_him!!){
            homeRepository.hasFriends(item.following_him)
        }},{handleError(it)},{})
    }

}
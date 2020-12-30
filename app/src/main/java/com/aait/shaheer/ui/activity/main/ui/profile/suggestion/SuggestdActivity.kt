package com.aait.shaheer.ui.activity.main.ui.profile.suggestion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.connections_model.Data
import com.aait.shaheer.data_layer.model.connections_model.Friend
import com.aait.shaheer.domain.repository.home_repository.homeRepository
import com.aait.shaheer.domain.repository.post_repository.postRepository
import com.aait.shaheer.ui.activity.connect.OnFollowListener
import com.aait.shaheer.ui.activity.connect.SuggestActivity
import com.aait.shaheer.ui.activity.connect.SuggestedAdapter
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.kareem.presetntation.base.ParentActivity
import kotlinx.android.synthetic.main.activity_suggestd.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class SuggestdActivity : ParentActivity(), OnFollowListener {
    private lateinit var mAdapter: SuggestedAdapter
    private lateinit var mFriendsAdapter: FriendHorizAdapter
    override val layoutResource: Int
        get() = R.layout.activity_suggestd

    override fun init() {
        initRecs()
        sendRequest()
        setActions()
    }

    private fun setActions() {
        tv_more.onClick {
            startActivity(Intent(applicationContext,SuggestActivity::class.java).apply {
                putExtra("from_more",true)
            })
        }
    }

    private fun initRecs() {
        mFriendsAdapter=FriendHorizAdapter()
        mAdapter=SuggestedAdapter(this)
        rec_friends.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        rec_friends.adapter=mFriendsAdapter

        rec_suggested.layoutManager=LinearLayoutManager(this)
        rec_suggested.adapter=mAdapter

    }

    private fun sendRequest() {
        homeRepository.getSuggestdUsers().subscribeWithLoading({showShimmer()},{ it.data?.let { it1 ->
            handleData(
                it1
            )
        } },{ it.message?.let { it1 ->
            handleError(it1)
        } },{})
    }

    private fun handleData(data: Data) {
        hideShimmer()
        if (data.friends.isNullOrEmpty()){
            suggest_lay.visibility=View.GONE
        }
        else{
            mFriendsAdapter.swapData(data.friends)
            if (data.friends.size>1) {
                friends_name.text = data.friends[0].name+" "+getString(R.string.and_other)
            }
            else{
                friends_name.text = data.friends[0].name
            }
        }
        if (data.suggestedUsers.isNullOrEmpty()){card_suggest.visibility=View.GONE}
        mAdapter.swapData(data.suggestedUsers!!)

    }

    private fun hideShimmer() {
        shimmer_load.visibility= View.GONE
    }

    private fun handleError(message: String) {
        Log.e("error",message)
        toasty(message,2)
    }

    private fun showShimmer() {
        shimmer_load.visibility= View.VISIBLE
    }

    override fun onFollow(item: Friend, isFav: Boolean) {
        homeRepository.follow(item.id.toString()).subscribeWithLoading({},{ if (item.following_him!!){homeRepository.hasFriends(item.following_him)}},{handleError(it.message!!)},{})
    }

}
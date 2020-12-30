package com.aait.shaheer.ui.activity.main.ui.other_profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.connections_model.Friend
import com.aait.shaheer.data_layer.model.profile_model.Data
import com.aait.shaheer.data_layer.model.profile_model.Profile
import com.aait.shaheer.domain.repository.chat_repository.inboxRepository
import com.aait.shaheer.domain.repository.home_repository.homeRepository
import com.aait.shaheer.domain.repository.profile_repository.profileRepository
import com.aait.shaheer.helper.Tweet
import com.aait.shaheer.ui.activity.main.ui.dialog.MyDialogFragment
import com.aait.shaheer.ui.activity.main.ui.inbox.chat.ChatActivity
import com.aait.shaheer.ui.activity.main.ui.profile.BottomSheetFollowing
import com.aait.shaheer.ui.activity.main.ui.profile.FollowersFragment
import com.aait.shaheer.ui.activity.main.ui.profile.FollowingFragment
import com.aait.shaheer.ui.activity.main.ui.profile.post.PostsFragment
import com.aait.shaheer.ui.activity.main.ui.profile.post.PostsFragment.Companion.other_profile
import com.google.android.material.tabs.TabLayout
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.kareem.presetntation.base.ParentActivity
import com.kareem.presetntation.helper.setImg
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.profile_header_other.*
import kotlinx.android.synthetic.main.toolbar_normal.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.textColor

class OtherProfileActivity : ParentActivity() {
    private lateinit var dialog: MyDialogFragment
    private  var user: Friend?=null
    private var isFollowing: Boolean? = false
    private var profile_id: Int = 0
    private  var mProfile: Profile?=null
    override val layoutResource: Int
        get() = R.layout.activity_other_profile

    private lateinit var following_tab: TabLayout.Tab
    private lateinit var followers_tab: TabLayout.Tab
    private lateinit var posts_tab: TabLayout.Tab


    override fun init() {
        iv_toolbar_menu.visibility= View.VISIBLE
        setActions()
        profile_id=intent.getIntExtra("profile",-1)
        sendRequest()
    }

    private fun setActions() {

        iv_toolbar_menu.onClick {
            BottomSheetFollowing(user!!) {friend_, tweet ->
                user=friend_
                when (tweet.name) {
                    Tweet.FOLLOW.name -> {
                        sendRequestFollow(user!!)
                    }
                    Tweet.MUTE.name->{
                        sendRequestMute(user!!)
                    }
                    Tweet.BLOCK.name -> {
                        sendRequestBlock(user!!)
                    }
                }

            }.show(supportFragmentManager,"")
        }
        follow_btn.onClick {
            isFollowing=!isFollowing!!
            if (isFollowing==false) {
                follow_btn.setBackgroundDrawable(getDrawable(R.drawable.follow_btn)!!)
                follow_btn.textColor = ContextCompat.getColor(applicationContext, android.R.color.white)
                follow_btn.text = getString(R.string.follow)

            }
            else{
                follow_btn.setBackgroundDrawable(getDrawable(R.drawable.unfollow_btn)!!)
                follow_btn.textColor= ContextCompat.getColor(applicationContext,R.color.colorAccent)
                follow_btn.text=getString(R.string.unfollow)

            }
            sendRequestFollow(user!!)
        }

        notif_iv.onClick {
           dialog = MyDialogFragment(getString(R.string.see_first)
            ,getString(R.string.ok)
            ,getString(R.string.cancel)
            ) {
                    if (MyDialogFragment.ACTION==it){
                        //sendRequestSeeFirst()
                    }
                     else   {
                        dialog.dismiss()
                        }
            }
        }

        msg_iv.onClick {
            sendRequestMsg()
        }

    }

    private fun sendRequestMsg() {
        inboxRepository.openChat(profile_id.toString())
            .subscribeWithLoading({
                showProgressDialog()
            },{
                handleData(it.data)
            },{
                handleError(it.message)
            },{

            })
    }

    private fun handleData(data: com.aait.shaheer.data_layer.model.Data?) {
        hideProgressDialog()
        startActivity(Intent(applicationContext,ChatActivity::class.java).apply {
            putExtra("id",data?.conversation_id.toString())
        })
    }


    private fun sendRequestFollow(item: Friend) {
        homeRepository.follow(item.id.toString())
            .subscribeWithLoading({},{},{handleError(it.message)},{})
    }

    private fun sendRequestMute(item: Friend) {
        homeRepository.mute(item.id.toString())
            .subscribeWithLoading({},{},{handleError(it.message)},{})
    }

    private fun sendRequestBlock(item: Friend) {
        homeRepository.block(item.id.toString())
            .subscribeWithLoading({},{},{handleError(it.message)},{})
    }



    private fun setProfile(profile: Profile?) {
        mProfile=profile
        if (user==null) {
            user = Friend(
                mProfile?.avatar, mProfile?.bio, mProfile?.category,
                mProfile?.category_id, mProfile?.you_following, mProfile?.id, mProfile?.name,
                mProfile?.verified, mProfile?.muted, mProfile?.blocked
            )
        }
        mProfile?.avatar?.let { setImg(it,iv_profile) }
       // mProfile?.cover?.let { setImg(it,iv_profile) }
        mProfile?.name?.let {
            tv_name.text=it
            setTitleToolbar(it)
        }
        mProfile?.time_zone?.let { tv_loc.text=it}
        mProfile?.bio?.let { tv_desc.text=it}
        mProfile?.you_following.let {
            isFollowing=it
            if (it==false) {
                follow_btn.setBackgroundDrawable(getDrawable(R.drawable.follow_btn)!!)
                follow_btn.textColor = ContextCompat.getColor(this, android.R.color.white)
                follow_btn.text = getString(R.string.follow)
            }
            else{
                follow_btn.setBackgroundDrawable(getDrawable(R.drawable.unfollow_btn)!!)
                follow_btn.textColor= ContextCompat.getColor(this,R.color.colorAccent)
                follow_btn.text=getString(R.string.unfollow)

            }
        }

    }

    private fun sendRequest() {
        profileRepository.getProfile(profile_id.toString()).subscribeWithLoading(
            {showProgressDialog()},{handleProfile(it.data)},{handleError(it.message)},{})
    }

    private fun handleError(message: String?) {
        hideProgressDialog()
        Log.e("error",message)
        toasty(message!!,2)
    }

    private fun handleProfile(data: Data?) {
        hideProgressDialog()
        setProfile(data?.profile)
        with(PostsFragment) {
            other_profile = true
            profile_id = data?.profile?.id!!
        }
        if (homeRepository.loadUser()!!.id==data!!.profile!!.id){
            iv_toolbar_menu.visibility=View.GONE
            follow_btn.visibility=View.GONE
            msg_iv.visibility=View.GONE
        }
        else{
            iv_toolbar_menu.visibility=View.VISIBLE
            follow_btn.visibility=View.VISIBLE
            msg_iv.visibility=View.VISIBLE

        }
        setuptabs()
        setupTabLayout()

    }

    private fun setuptabs() {
        posts_tab = tab_profile.newTab().setText(resources.getString(R.string.posts))
        following_tab = tab_profile.newTab().setText(resources.getString(R.string.following))
        followers_tab = tab_profile.newTab().setText(resources.getString(R.string.followers))
    }


    private fun setupTabLayout() {
        tab_profile.addTab(posts_tab,true)
        tab_profile.addTab(following_tab)
        tab_profile.addTab(followers_tab)
        setupPostsFragment()
        tab_profile.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                Log.e("pos", tab?.position.toString())
                when (tab?.position) {
                    0 -> setupPostsFragment()
                    1 -> setupFollowingFragment()
                    2 -> setupFollowersFragment()
                }

            }

        })

    }

    private fun setupFollowingFragment() {

        with(FollowingFragment) {
            other_profile = true
            profile_id = mProfile?.id!!
        }

        val fragment =
            FollowingFragment()
        val bundle = Bundle()
        fragment.arguments = bundle
        replaceFragment(fragment)
    }

    private fun setupFollowersFragment() {
        with(FollowersFragment) {
            other_profile = true
            profile_id = mProfile?.id!!
        }
        val fragment =
            FollowersFragment()
        val bundle = Bundle()
        fragment.arguments = bundle
        replaceFragment(fragment)
    }

    private fun setupPostsFragment() {
        val fragment =
            PostsFragment()
        val bundle = Bundle()
        fragment.arguments = bundle
        replaceFragment(fragment)
    }
    fun replaceFragment(fragment: Fragment) {
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.replace(R.id.frame, fragment)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        ft.commit()
    }
}
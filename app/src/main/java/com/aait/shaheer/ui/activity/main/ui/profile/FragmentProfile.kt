package com.aait.shaheer.ui.activity.main.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.aait.shaheer.R
import com.aait.shaheer.domain.repository.home_repository.homeRepository
import com.aait.shaheer.ui.activity.main.MainActivity
import com.aait.shaheer.ui.activity.main.ui.profile.post.PostsFragment
import com.google.android.material.tabs.TabLayout
import com.aait.shaheer.base.BaseFragment
import com.aait.shaheer.domain.core.Constant
import com.kareem.domain.core.Util
import com.kareem.presetntation.helper.setImg
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.profile_header.*
import kotlinx.android.synthetic.main.profile_header.iv_cover
import org.jetbrains.anko.sdk27.coroutines.onClick

class FragmentProfile : BaseFragment() {
    private lateinit var following_tab: TabLayout.Tab
    private lateinit var followers_tab: TabLayout.Tab
    private lateinit var posts_tab: TabLayout.Tab
    override val viewId: Int
        get() = R.layout.fragment_profile

    override fun init(view: View) {

    }

    override fun onResume() {
        super.onResume()
        setProfile()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        MainActivity.current_state.value=MainActivity.PROFILE
        setuptabs()
        setupTabLayout()
        setActions()
    }

    private fun setActions() {
        val userId=homeRepository.loadUser()?.id
        wallet_btn.onClick {
            Util.openUrl(activity!!,Constant.APP_BASE+"wallet/${userId}")
        }
    }

    private fun setProfile() {
        //context?.setImg(homeRepository.loadUser()!!.cover!!,iv_cover)
        context?.setImg(homeRepository.loadUser()!!.avatar!!,iv_profile)
        homeRepository.loadUser()!!.cover?.let { Picasso.get().load(it).into(iv_cover) /*setImg(it,iv_cover) */}

        tv_name.text=homeRepository.loadUser()!!.name
        tv_loc.text=homeRepository.loadUser()!!.time_zone
        tv_desc.text= homeRepository.loadUser()!!.bio

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
        FollowingFragment.other_profile=false
        FollowingFragment.profile_id=0
        val fragment =
            FollowingFragment()
        val bundle = Bundle()
        fragment.arguments = bundle
        replaceFragment(fragment)
    }

    private fun setupFollowersFragment() {
        FollowersFragment.other_profile=false
        FollowersFragment.profile_id=0
        val fragment =
            FollowersFragment()
        val bundle = Bundle()
        fragment.arguments = bundle
        replaceFragment(fragment)
    }

    private fun setupPostsFragment() {
        PostsFragment.other_profile=false
        PostsFragment.profile_id=0
        val fragment =
            PostsFragment()
        val bundle = Bundle()
        fragment.arguments = bundle
        replaceFragment(fragment)
    }


    fun replaceFragment(fragment: Fragment) {
        val fm = activity?.supportFragmentManager
        val ft = fm!!.beginTransaction()
        ft.replace(R.id.frame, fragment)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        ft.commit()
    }

}
package com.aait.shaheer.ui.activity.main.ui.inbox

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.aait.shaheer.R
import com.aait.shaheer.ui.activity.main.MainActivity
import com.aait.shaheer.ui.activity.main.ui.inbox.chat.RoomsFragment
import com.google.android.material.tabs.TabLayout
import com.aait.shaheer.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_services.*

class IboxFragment: BaseFragment() {
    override val viewId: Int
        get() = R.layout.inbox_fragment

    private lateinit var messages_tab: TabLayout.Tab
    private lateinit var notification_tab: TabLayout.Tab
    private lateinit var help_tab: TabLayout.Tab

    override fun init(view: View) {
        MainActivity.current_state.value= MainActivity.INBOX

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupTabs()
        setupTabLayout()
        addFragment(RoomsFragment(),"rooms")
    }

    private fun setupTabs() {
        messages_tab=tab_content.newTab().setText(resources.getString(R.string.messages))
        notification_tab=tab_content.newTab().setText(resources.getString(R.string.notifications))
        help_tab=tab_content.newTab().setText(resources.getString(R.string.helps))
    }


    private fun setupTabLayout() {
        tab_content.addTab(messages_tab,true)
        tab_content.addTab(notification_tab)
        tab_content.addTab(help_tab)
        tab_content.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                Log.e("pos",tab?.position.toString())
                when(tab?.position){
                    0->addFragment(RoomsFragment(),"messages")
                    1->addFragment(NotificationFragment(),"notification")
                    2->addFragment(HelpsFragment(),"helps")
                }

            }

        })
    }
    private fun addFragment(fragment: Fragment, key:String) {
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.frame, fragment)
            .commit()

    }}
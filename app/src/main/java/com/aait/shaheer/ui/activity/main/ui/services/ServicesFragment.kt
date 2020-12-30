package com.aait.shaheer.ui.activity.main.ui.services

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.aait.shaheer.R
import com.aait.shaheer.ui.activity.main.MainActivity
import com.google.android.material.tabs.TabLayout
import com.aait.shaheer.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_services.*

class ServicesFragment : BaseFragment() {
    override val viewId: Int
        get() = R.layout.fragment_services

    private lateinit var sold_tab: TabLayout.Tab
    private lateinit var bought_tab: TabLayout.Tab
    private lateinit var collection_tab: TabLayout.Tab

    override fun init(view: View) {
        MainActivity.current_state.value= MainActivity.SERVICES

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupTabs()
        setupTabLayout()
        addFragment(SoldFragment(),"sold")
    }

    private fun setupTabs() {
        sold_tab=tab_content.newTab().setText(resources.getString(R.string.sold))
        bought_tab=tab_content.newTab().setText(resources.getString(R.string.bought))
        collection_tab=tab_content.newTab().setText(resources.getString(R.string.collection))
    }


    private fun setupTabLayout() {
        tab_content.addTab(sold_tab,true)
        tab_content.addTab(bought_tab)
        tab_content.addTab(collection_tab)
        tab_content.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                Log.e("pos",tab?.position.toString())
                when(tab?.position){
                    0->addFragment(SoldFragment(),"sold")
                    1->addFragment(BoughtFragment(),"bought")
                    2->addFragment(CollectionFragment(),"collection")
                }

            }

        })
    }
    private fun addFragment(fragment: Fragment, key:String) {
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.frame, fragment)
            .commit()

    }
}
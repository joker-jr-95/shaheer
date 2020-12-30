package com.aait.shaheer.ui.activity.main

import android.content.Intent
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.stories_model.Data
import com.aait.shaheer.data_layer.model.stories_model.MyStory
import com.aait.shaheer.domain.repository.menu_repository.menuRepository
import com.aait.shaheer.ui.activity.main.ui.add_post.AddPostActivity
import com.aait.shaheer.ui.activity.main.ui.cart.CartActivity
import com.aait.shaheer.ui.activity.main.ui.home.HomeFragment
import com.aait.shaheer.ui.activity.main.ui.inbox.IboxFragment
import com.aait.shaheer.ui.activity.main.ui.inbox.chat.SearchUsersActivity
import com.aait.shaheer.ui.activity.main.ui.menu.NavFragment
import com.aait.shaheer.ui.activity.main.ui.profile.FragmentProfile
import com.aait.shaheer.ui.activity.main.ui.profile.edit_profile.ProfileActivity
import com.aait.shaheer.ui.activity.main.ui.profile.suggestion.SuggestdActivity
import com.aait.shaheer.ui.activity.main.ui.search.SearchActivity
import com.aait.shaheer.ui.activity.main.ui.services.ServicesFragment
import com.aait.shaheer.ui.activity.main.ui.story.StoryAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kareem.domain.core.Util
import com.kareem.domain.core.toasty
import com.kareem.presetntation.base.ParentActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.messages_toolbar.*
import kotlinx.android.synthetic.main.services_toolbar.*
import kotlinx.android.synthetic.main.toolbar_main.*
import kotlinx.android.synthetic.main.toolbar_main.menu_toolbar
import kotlinx.android.synthetic.main.toolbar_profile.*
import okhttp3.MultipartBody
import org.jetbrains.anko.sdk27.coroutines.onClick

class MainActivity : ParentActivity() {
    private var mData: Data? = null
    private  var myStories: List<MyStory> = mutableListOf()
    private var haveStory: Boolean = false
    private lateinit var mAdapter: StoryAdapter
    private val feed: Boolean=false
    private val services: Boolean=false
    private val inbox: Boolean=false
    private val profile: Boolean=false
    var mImgs:MutableList<MultipartBody.Part> = mutableListOf()
    companion object {
        val HOME: String = "home"
        val SERVICES: String = "services"
        val INBOX: String = "inbox"
        val PROFILE: String = "profile"
        var current_state=MutableLiveData<String>()
    }


    override val layoutResource: Int
        get() = R.layout.activity_main

    override fun init() {

        setActions()
        configBottomNavigation()
        setDefaultFragment()
        setNav()
        closeNav()
        setDrawer()

        current_state.observe(this, Observer {
            when(it){
                HOME->{
                    main_toolbar.visibility= View.VISIBLE
                    toolbar_profile.visibility=View.GONE
                    inbox_toolbar.visibility=View.GONE
                    services_toolbar.visibility=View.GONE
                }
                SERVICES->{
                    main_toolbar.visibility= View.GONE
                    toolbar_profile.visibility=View.GONE
                    inbox_toolbar.visibility=View.GONE
                    services_toolbar.visibility=View.VISIBLE
                }
                INBOX->{
                    main_toolbar.visibility= View.GONE
                    toolbar_profile.visibility=View.GONE
                    inbox_toolbar.visibility=View.VISIBLE
                    services_toolbar.visibility=View.GONE
                }
                PROFILE->{
                    main_toolbar.visibility= View.GONE
                    toolbar_profile.visibility=View.VISIBLE
                    inbox_toolbar.visibility=View.GONE
                    services_toolbar.visibility=View.GONE
                }
            }
        })

        if (intent.getBooleanExtra("to_service",false)){
            nav.selectedItemId=R.id.services
        }
        if (intent.getBooleanExtra("to_notifications",false)){
            nav.selectedItemId=R.id.inbox
        }

    }

    private fun closeNav() {
        if (drawer_layout!=null){
            if (drawer_layout.isDrawerOpen(nav_view)){
                drawer_layout.closeDrawers()
            }
        }
    }
    private fun setNav() {

        if (drawer_layout != null) {
            if (drawer_layout.isDrawerOpen(Gravity.RIGHT)) {
                drawer_layout.closeDrawer(Gravity.RIGHT)
            } else if (drawer_layout.isDrawerOpen(Gravity.LEFT)) {
                drawer_layout.closeDrawer(Gravity.LEFT)
            }
            val fragmentManager = supportFragmentManager
            val fragment =
                NavFragment()
            fragmentManager.beginTransaction().replace(R.id.nav_view, fragment).commit()

        }
    }

    private fun setDefaultFragment() {
        openFragment(HomeFragment(), HOME)

    }


    private fun setDrawer() {
        val scaleFactor=6f
        val actionBarDrawerToggle = object : ActionBarDrawerToggle(this, drawer_layout, R.string.open, R.string.close) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)

                if (Util.language=="ar") {
                    val slideX = drawerView.width * slideOffset
                    content.translationX = (slideX * -1)
                    content.scaleX = 1f - (slideOffset / scaleFactor)
                    content.scaleY = 1f - (slideOffset / scaleFactor)
                }

                else{
                    val slideX = drawerView.width * slideOffset
                    content.translationX = slideX
                    content.scaleX = 1f - (slideOffset / scaleFactor)
                    content.scaleY = 1f - (slideOffset / scaleFactor)
                }
            }
        }

        drawer_layout.addDrawerListener(actionBarDrawerToggle)

    }

    private fun setActions() {
        plus_msg_iv.onClick {
            startActivity(Intent(applicationContext, SearchUsersActivity::class.java))
        }
        search_toolbar_msg.onClick {
            startActivity(Intent(applicationContext, SearchActivity::class.java))
        }

        search_iv.onClick {
            startActivity(Intent(applicationContext,SearchActivity::class.java))
        }

        service_search_toolbar.onClick {
            startActivity(Intent(applicationContext,SearchActivity::class.java))
        }

        menu_toolbar.onClick {
            if (drawer_layout != null) {

                if (menuRepository.lang=="ar") {
                    if (drawer_layout.isDrawerOpen(nav_view)) {
                        drawer_layout.closeDrawer(nav_view)
                    } else {
                        if (drawer_layout != null) {
                            drawer_layout.openDrawer(nav_view)

                        }
                    }
                }
                //left
                else {
                    if (drawer_layout.isDrawerOpen(nav_view)) {
                        drawer_layout.closeDrawer(nav_view)
                    } else {
                        drawer_layout.openDrawer(nav_view)
                    }
                }


            }
        }
        fab_iv.onClick {
            startActivity(Intent(applicationContext,AddPostActivity::class.java))
        }
        menu_toolbar_profile.onClick {
            startActivity(Intent(applicationContext,SuggestdActivity::class.java))
        }
        img_edit_profile.onClick {
            startActivity(Intent(applicationContext,
                ProfileActivity::class.java))

        }
        img_cart.onClick {
            startActivity(Intent(applicationContext,CartActivity::class.java))
        }
        img_cart_service.onClick {
            startActivity(Intent(applicationContext,CartActivity::class.java))
        }
    }

    private fun configBottomNavigation() {
        nav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        when {
            feed -> nav.selectedItemId = R.id.feed
            services -> nav.selectedItemId = R.id.services
            inbox-> nav.selectedItemId=R.id.inbox
            profile-> nav.selectedItemId=R.id.profile

        }
    }
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {
        when (it.itemId) {
            R.id.feed->{
                openFragment(HomeFragment(), HOME)
                return@OnNavigationItemSelectedListener true
            }
            R.id.services ->{
                val servicesFragment = ServicesFragment()
                 openFragment(servicesFragment, SERVICES)
                return@OnNavigationItemSelectedListener true
            }
            R.id.inbox ->{
                openFragment(IboxFragment(), INBOX)
                return@OnNavigationItemSelectedListener true
            }

            R.id.profile ->{
                openFragment(FragmentProfile(), PROFILE)
                return@OnNavigationItemSelectedListener true
            }

            else -> return@OnNavigationItemSelectedListener false
        }


    }
    private fun openFragment(fragment: Fragment, tag:String) {

        val transaction = supportFragmentManager.beginTransaction()
        val fragmentByTag = supportFragmentManager.findFragmentByTag(tag)
        if (savedInstanceState == null) {
            if (fragmentByTag != null && !fragmentByTag.isAdded) {
                transaction.replace(R.id.container, fragmentByTag)
                if (tag != HOME) {

                }
                transaction.commit()
            } else if (!fragment.isAdded) {
                transaction.replace(R.id.container, fragment)
                if (tag != HOME) {
                } else {
                    nav.menu.getItem(0).isChecked = true
                }
            }
        }
        else {
            transaction.replace(R.id.container, fragment, tag)
        }
        transaction.commitAllowingStateLoss()
        supportFragmentManager.executePendingTransactions()
    }




    private fun handleError(it: Throwable) {
       // hideProgressDialog()
        it.message?.let { it1 -> toasty(it1,2) }
    }

    private fun handleData(msg: String) {
       // hideProgressDialog()
        haveStory=true
        toasty(msg)
    }

    override fun onBackPressed() {

        if (drawer_layout!=null){
            if (drawer_layout.isDrawerOpen(nav_view)){
                drawer_layout.closeDrawers()
            }
            else{
                super.onBackPressed()
            }
        }
        else{
            super.onBackPressed()
        }
    }
}
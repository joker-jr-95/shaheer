package com.aait.shaheer.ui.activity.main.ui.search

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.search_history_model.History
import com.aait.shaheer.domain.repository.chat_repository.inboxRepository
import com.aait.shaheer.ui.activity.main.ui.services.BoughtFragment
import com.aait.shaheer.ui.activity.main.ui.services.CollectionFragment
import com.aait.shaheer.ui.activity.main.ui.services.SoldFragment
import com.google.android.material.tabs.TabLayout
import com.jakewharton.rxbinding2.widget.RxTextView
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.kareem.presetntation.base.ParentActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.empty_lay.*
import kotlinx.android.synthetic.main.fragment_services.*
import kotlinx.android.synthetic.main.my_search_dialog_compat_tab.*
import kotlinx.android.synthetic.main.toolbar_search.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.concurrent.TimeUnit

class SearchTabActivity : ParentActivity() {
    companion object{
          var type: SearchType=SearchType.USER
    }

    override val layoutResource: Int
        get() = R.layout.activity_search_tab
    private lateinit var user_tab: TabLayout.Tab
    private lateinit var cat_tab: TabLayout.Tab

    private val mAdapter=SearchAdapter()
    override fun init() {
        arr_filter.visibility=View.GONE
         type = SearchType.USER
        setupTabs()
        setupTabLayout()
    }
    private fun setupTabs() {
        user_tab=tab_content.newTab().setText(resources.getString(R.string.people))
        cat_tab=tab_content.newTab().setText(resources.getString(R.string.services))
        setRec()
        setActions()
    }

    private fun setRec() {
        rv_items.layoutManager=LinearLayoutManager(this)
        rv_items.adapter=mAdapter
    }

    @SuppressLint("CheckResult")
    private fun setActions() {
        RxTextView.textChanges(txt_search)
            .delay(300, TimeUnit.MILLISECONDS)
            .map { search -> search.toString().isNotBlank()}
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                if (it) {
                    sendRequestSearch(txt_search.text.toString())
                }
            }
        arr_filter.onClick {
            startActivity(Intent(this@SearchTabActivity,FilterActivity::class.java))
        }

    }

    private fun sendRequestSearch(word: String) {
            inboxRepository.createSearch(word).subscribeWithLoading({lay_empty.visibility=View.GONE
                rv_items.visibility=View.VISIBLE
            },{
                if (type==SearchType.USER){
                    val users=it.data.users as MutableList<History>
                    if (users.isEmpty()){
                        lay_empty.visibility= View.VISIBLE
                        rv_items.visibility=View.GONE
                        text.text=getString(R.string.no_search_result)
                    }
                    mAdapter.swapData(users,false)

                }
                else{
                    val services=it.data.categories as MutableList<History>
                    if (services.isEmpty()){
                        lay_empty.visibility= View.VISIBLE
                        rv_items.visibility=View.GONE
                        text.text=getString(R.string.no_search_result)
                    }

                    mAdapter.swapData(services,false)
                }
            },{},{})
    }

    private fun setupTabLayout() {
        tab_content.addTab(user_tab,true)
        tab_content.addTab(cat_tab)
        tab_content.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                Log.e("pos",tab?.position.toString())
                mAdapter.clear()
                txt_search.text?.clear()
                when(tab?.position){

                    0->{
                        arr_filter.visibility=View.GONE
                        type=SearchType.USER
                    }
                    1->{
                        arr_filter.visibility=View.VISIBLE
                        type=SearchType.CATEGORY

                    }

                }

            }

        })
    }
enum class SearchType{
    USER,CATEGORY
}
}

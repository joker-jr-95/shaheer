package com.aait.shaheer.ui.activity.main.ui.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.search_history_model.Data
import com.aait.shaheer.data_layer.model.search_history_model.History
import com.aait.shaheer.domain.repository.chat_repository.inboxRepository
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.kareem.presetntation.base.ParentActivity
import kotlinx.android.synthetic.main.my_search_dialog_compat.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class SearchActivity : ParentActivity() {
    override val layoutResource: Int
        get() = R.layout.activity_search

    companion object{
        var min="0"
        var max="0"
    }
    private val mSearchAdapter = SearchAdapter()

    override fun init() {
        setTitleToolbar(getString(R.string.search))
        setRec()
        sendRequestSearch()
        setActions()
    }

    private fun setActions() {
        txt_search.onClick {
                startActivity(Intent(applicationContext,SearchTabActivity::class.java))
        }
    }

    private fun setRec() {
        rv_items.layoutManager=LinearLayoutManager(this)
        rv_items.adapter=mSearchAdapter
    }

    private fun sendRequestSearch() {
        inboxRepository.searchHistory().
            subscribeWithLoading({showProgressDialog()},{handleData(it.data)},{handleError(it.message)},{})
    }

    private fun handleData(data: Data) {
        hideProgressDialog()
        if (data.history.isNullOrEmpty()){
            txt_title.visibility= View.GONE
        }
        mSearchAdapter.swapData(data.history as MutableList<History>,true)
        max= data.max_price.toString()
        min= data.min_price.toString()
    }

    private fun handleError(message: String?) {
        hideProgressDialog()
        message?.let { toasty(it,2) }
    }

}
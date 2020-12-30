package com.aait.shaheer.ui.activity.main.ui.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.collection_model.Post
import com.aait.shaheer.domain.repository.chat_repository.inboxRepository
import com.aait.shaheer.ui.activity.main.ui.services.ItemPostColldection
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.kareem.presetntation.base.ParentActivity
import kotlinx.android.synthetic.main.activity_search_result.*

class SearchResultActivity : ParentActivity() {
    override val layoutResource: Int
        get() = R.layout.activity_search_result

    val mAdapter=ItemPostColldection()

    override fun init() {
        setTitleToolbar(getString(R.string.search_result))
        setRec()
        if (intent.hasExtra("id")) {
            val cat = intent.getStringExtra("id")
            sendRequest(cat)
        }
        else{
            handleData(FilterActivity.mData)
        }
    }

    private fun sendRequest(cat: String?) {

        inboxRepository.filter(category_ids = mutableListOf(cat!!.toInt()))
                .subscribeWithLoading({showShimmer()},{handleData(it.data)},{handleError(it.message)},{})
    }

    private fun handleError(message: String?) {
        message?.let { toasty(it,2) }
    }

    private fun handleData(data: List<Post>) {
        shimmer_collection.visibility= View.GONE
        empty_lay.visibility= View.GONE
        rec_collections.visibility= View.VISIBLE
        if (data.isEmpty()){
            shimmer_collection.visibility= View.GONE
            empty_lay.visibility= View.VISIBLE
            rec_collections.visibility= View.GONE
            mAdapter.swapData(data,supportFragmentManager)
        }
        else{
            mAdapter.swapData(data,supportFragmentManager)
        }
    }

    private fun showShimmer() {
        shimmer_collection.visibility= View.VISIBLE
        empty_lay.visibility= View.GONE
        rec_collections.visibility= View.GONE
    }

    private fun setRec() {
        rec_collections.layoutManager=LinearLayoutManager(this)
        rec_collections.adapter=mAdapter
    }

}
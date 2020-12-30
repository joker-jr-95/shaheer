package com.aait.shaheer.ui.activity.main.ui.services

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.collection_model.CollectionModel
import com.aait.shaheer.domain.repository.order_repository.orderRepository
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.aait.shaheer.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_collection.*

class CollectionFragment: BaseFragment() {
    private lateinit var mAdapter: CollectionAdapter

    override val viewId: Int
        get() = R.layout.fragment_collection



    override fun init(view: View) {

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
         mAdapter= CollectionAdapter(activity!!.supportFragmentManager)
        rec_collections.layoutManager=LinearLayoutManager(context)
        rec_collections.adapter=mAdapter
        sendRequest()


    }

    private fun sendRequest() {
        addDisposable(orderRepository.collections().subscribeWithLoading({showShimmer()},{handleData(it)},{handleError(it.message)},{}))
    }

    private fun showShimmer() {
        shimmer_collection.visibility=View.VISIBLE
        rec_collections.visibility=View.GONE
    }

    private fun handleError(message: String?) {
        message?.let { activity?.toasty(it,2) }
    }

    private fun handleData(data: CollectionModel) {
        hideShimmer()
        if (!data.data.isNullOrEmpty()) {
            data.data.let { mAdapter.swapData(it) }
        }
        else{
            shimmer_collection.visibility=View.GONE
            rec_collections.visibility=View.GONE
            empty_lay.visibility=View.VISIBLE
        }
    }

    private fun hideShimmer() {
        shimmer_collection.visibility=View.GONE
        rec_collections.visibility=View.VISIBLE
    }
}
package com.aait.shaheer.ui.activity.main.ui.inbox

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.helps_model.Data
import com.aait.shaheer.domain.repository.chat_repository.inboxRepository
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.aait.shaheer.base.BaseFragment
import kotlinx.android.synthetic.main.helps_fragment.*

class HelpsFragment: BaseFragment() {
    override val viewId: Int
        get() = R.layout.helps_fragment
    private val mAdapter=HelpAdapter()
    override fun init(view: View) {

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setRec()
        sendRequest()
    }

    private fun sendRequest() {
        addDisposable(
        inboxRepository.getHelps().subscribeWithLoading({handleLoad()},{handleData(it.data)},{handleError(it)},{})
        )
    }

    private fun handleLoad() {
        shimmer_load.visibility=View.VISIBLE
        rec_helps.visibility=View.GONE
        empty_lay.visibility=View.GONE
    }

    private fun handleError(it: Throwable) {
        it.message?.let { it1 -> activity?.toasty(it1,2) }
    }

    private fun handleData(data: List<Data>?) {
        shimmer_load.visibility=View.GONE
        rec_helps.visibility=View.VISIBLE

        data?.let {
            if (it.isEmpty()){
                shimmer_load.visibility=View.GONE
                rec_helps.visibility=View.GONE
                empty_lay.visibility=View.VISIBLE
            }
            mAdapter.swapData(it)

        }
    }

    private fun setRec() {
        rec_helps.layoutManager=LinearLayoutManager(context)
        rec_helps.adapter=mAdapter
    }
}
package com.aait.shaheer.ui.activity.main.ui.menu

import androidx.recyclerview.widget.LinearLayoutManager
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.faqs_model.FaqsData
import com.aait.shaheer.domain.repository.menu_repository.menuRepository
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.kareem.presetntation.base.ParentActivity
import kotlinx.android.synthetic.main.activity_faqs.*

class FaqsActivity : ParentActivity() {
    private lateinit var mAdapter: FaqsAdapter
    override val layoutResource: Int
        get() = R.layout.activity_faqs

    override fun init() {
        setTitleToolbar(getString(R.string.faqs))
        rec_faqs.layoutManager=LinearLayoutManager(applicationContext)
        mAdapter=FaqsAdapter()
        rec_faqs.adapter=mAdapter
        sendRequestFaqs()
    }

    private fun sendRequestFaqs() {
        menuRepository.faqs().subscribeWithLoading({showProgressDialog()},{handleData(it.data)},{handleError(it.message)},{})
    }

    private fun handleError(message: String?) {
        hideProgressDialog()
        message?.let { toasty(it,2) }
    }

    private fun handleData(data:List<FaqsData>) {
        hideProgressDialog()
        mAdapter.swapData(data)
    }

}
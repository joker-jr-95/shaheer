package com.aait.shaheer.ui.activity.main.ui.menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.terms_model.Data
import com.aait.shaheer.domain.repository.menu_repository.menuRepository
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.kareem.presetntation.base.ParentActivity
import kotlinx.android.synthetic.main.activity_terms.*

class TermsActivity : ParentActivity() {
    override val layoutResource: Int
        get() = R.layout.activity_terms

    override fun init() {
        setTitleToolbar(getString(R.string.terms))
        sendRequestTerms()
    }

    private fun sendRequestTerms() {
        menuRepository.terms().subscribeWithLoading({showProgressDialog()},{handleData(it.data)},{handleError(it.message)},{})
    }

    private fun handleError(message: String?) {
        hideProgressDialog()
        message?.let { toasty(it,2) }
    }

    private fun handleData(data: Data) {
        hideProgressDialog()
        tv_terms.text=data.terms

    }
}
package com.aait.shaheer.ui.activity.main.ui.inbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.helps_model.Data
import com.aait.shaheer.domain.repository.chat_repository.inboxRepository
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.kareem.presetntation.base.ParentActivity
import kotlinx.android.synthetic.main.activity_qust_answ.*

class QustAnswActivity : ParentActivity() {
    override val layoutResource: Int
        get() = R.layout.activity_qust_answ

    override fun init() {
        setTitleToolbar(getString(R.string.quest_answer))
        if (intent.hasExtra("help")) {
            val helpModel = intent.getSerializableExtra("help") as Data
            tv_quest.text = helpModel.message
            tv_answer.text = helpModel.answer
        }
        else{
           val id= intent.getStringExtra("help_id")
            sendRequestHelp(id)
        }

    }

    private fun sendRequestHelp(id: String?) {
        inboxRepository.helpDetails(id!!).subscribeWithLoading({showProgressDialog()},{ it.data?.let { it1 ->
            handleData(
                it1
            )
        } },{
            hanldeError(it.message)
        },{})
    }

    private fun hanldeError(message: String?) {
        hideProgressDialog()
        message?.let { toasty(it,2) }
    }

    private fun handleData(helpModel: Data) {
        hideProgressDialog()
        tv_quest.text = helpModel.message
        tv_answer.text = helpModel.answer
    }

}
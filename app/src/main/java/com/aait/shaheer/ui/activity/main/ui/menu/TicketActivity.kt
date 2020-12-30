package com.aait.shaheer.ui.activity.main.ui.menu

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.ticket_model.Tickets
import com.aait.shaheer.domain.repository.menu_repository.menuRepository
import com.aait.shaheer.helper.SwipeHelper
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.kareem.presetntation.base.ParentActivity
import kotlinx.android.synthetic.main.activity_ticket.*

class TicketActivity : ParentActivity() {
    private  var data: List<Tickets> = mutableListOf()
    override val layoutResource: Int
        get() = R.layout.activity_ticket

    val mAdapter=TicketAdapter()
    override fun onResume() {
        super.onResume()
        sendRequest()
    }
    override fun init() {
        setTitleToolbar(getString(R.string.tickets))
        setRec()


       object :SwipeHelper(applicationContext,rec_ticket){
           override fun instantiateUnderlayButton(
               viewHolder: RecyclerView.ViewHolder?,
               underlayButtons: MutableList<UnderlayButton>?
           ) {
               underlayButtons!!.add(
                   UnderlayButton(applicationContext,getString(R.string.delete),
                       R.drawable.delete_order,
                       Color.parseColor("#FF3c30"),
                       object : UnderlayButtonClickListener {
                           override fun onClick(pos: Int) {
                               sendRequestRemove(data[pos].id)
                               mAdapter.removeItem(pos)
                           }
                       }

                   ))

           }

       }

    }

    private fun sendRequestRemove(id: Int?) {
        menuRepository.cancelTicket(id.toString())
            .subscribeWithLoading({},{},{handleError(it.message)},{})
    }

    private fun sendRequest() {
        menuRepository.tickets().subscribeWithLoading({showProgressDialog()}
            ,{handleData(it.data)},{handleError(it.message)},{})
    }

    private fun handleData(data: List<Tickets>) {
        hideProgressDialog()
        this.data=data
        if (data.isEmpty()){
            empty_lay.visibility= View.VISIBLE
        }
        else {
            empty_lay.visibility=View.GONE
        }
        mAdapter.swapData(data)
    }

    private fun handleError(message: String?) {
        hideProgressDialog()
        message?.let { toasty(it,2) }
    }

    private fun setRec() {
        rec_ticket.layoutManager=LinearLayoutManager(applicationContext)
        rec_ticket.adapter=mAdapter
    }

}
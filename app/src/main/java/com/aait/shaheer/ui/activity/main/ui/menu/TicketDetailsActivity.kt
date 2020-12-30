package com.aait.shaheer.ui.activity.main.ui.menu

import android.util.Log
import android.view.View
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.ticket_model.Tickets
import com.aait.shaheer.domain.repository.base_repository.baseRepository
import com.aait.shaheer.domain.repository.menu_repository.menuRepository
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.kareem.presetntation.base.ParentActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_ticket_details.*
import kotlinx.android.synthetic.main.item_ticket.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.textColor

class TicketDetailsActivity : ParentActivity() {
    private lateinit var ticket: Tickets
    override val layoutResource: Int
        get() = R.layout.activity_ticket_details

    override fun init() {
        setTitleToolbar(getString(R.string.ticket_details))
        if (intent.hasExtra("ticket")) {
            ticket = intent.getSerializableExtra("ticket") as Tickets
            Log.e("ticket",ticket.toString())
            setTicket()
        }
        else{
            val ticketId=intent.getStringExtra("ticket_id")
            sendRequestTicket(ticketId)
        }
    }

    private fun sendRequestTicket(ticketId: String?) {
        menuRepository.ticket(ticketId.toString()).subscribeWithLoading({showProgressDialog()}
            ,{ticketModel ->
                hideProgressDialog()
                ticket=ticketModel.data
            setTicket()
            },{handleError(it.message)},{})
    }

    private fun setTicket(){
        tv_name_ticket.text=ticket.username
        Picasso.get().load(ticket.avatar).into(iv_avatar_ticket)
        ticket_name_tv.text="#"+ticket.id.toString()
        ticket_date_tv.text=ticket.date
        ticket_status_tv.text=ticket.status
        subject_tv.text=ticket.subject
        details_tv.text=ticket.text
        ticket_status_tv.text=ticket.status
        if (baseRepository.lang=="ar"){
            when(ticket.status){
                "open"->{ticket_status_tv.text="الشكوي مفتوحة"}
                "inprogress"->{ticket_status_tv.text="الشكوي قيد الدراسة"}
                else->{ticket_status_tv.text="الشكوي منتهية"}
            } }


        if (ticket.status=="open"){
            ticket_status_tv.textColor=getColor(R.color.black)
        }
        else if (ticket.status=="inprogress"){
            ticket_status_tv.textColor=getColor(R.color.colorAccent)
        }
        else{
            ticket_status_tv.textColor=getColor(android.R.color.darker_gray)
        }

        if (ticket.status=="open"){
            answer_lay.visibility= View.GONE
        }
        else if(ticket.status=="inprogress"){
            answer_lay.visibility= View.GONE

            if (ticket.make_satisfied=="false"){
                card_lay.visibility=View.VISIBLE
            }
            else{
                card_lay.visibility=View.GONE
            }
        }
        else{
            answer_lay.visibility= View.VISIBLE
            answer_tv.text=ticket.answer
        }

        btn_yes.onClick {
            sendRequestStais("true")
        }
        btn_no.onClick {
            sendRequestStais("false")
        }
    }
    private fun sendRequestStais(satis: String) {
        menuRepository.satisfy(ticket.id.toString(),satis)
            .subscribeWithLoading({showProgressDialog()},{handleData(it.msg)}
                ,{handleError(it.message)},{})

    }

    private fun handleError(message: String?) {
        hideProgressDialog()
        message?.let { toasty(it,2) }
    }

    private fun handleData(msg: String?) {
        hideProgressDialog()
        msg?.let { toasty(it) }
        card_lay.visibility=View.GONE

    }

}
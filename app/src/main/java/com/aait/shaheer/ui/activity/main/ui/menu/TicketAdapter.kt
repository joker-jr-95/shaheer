package com.aait.shaheer.ui.activity.main.ui.menu

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.ticket_model.Tickets
import com.aait.shaheer.domain.repository.base_repository.baseRepository
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_ticket.view.*
import org.jetbrains.anko.textColor
import java.util.*

class TicketAdapter : RecyclerView.Adapter<TicketAdapter.TicketVH>() {

    private var data: MutableList<Tickets> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketVH {
        return TicketVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_ticket, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: TicketVH, position: Int) = holder.bind(data[position])

    fun swapData(data: List<Tickets>) {
        this.data = data as MutableList<Tickets>
        notifyDataSetChanged()
    }

    fun removeItem(pos: Int) {
        data.removeAt(pos)
        notifyItemRemoved(pos)
        notifyItemRangeChanged(pos,itemCount)


    }

    class TicketVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item:Tickets) = with(itemView) {
            Picasso.get().load(item.avatar).into(iv_avatar)
            tv_name.text=item.username
            ticket_name_tv.text=item.subject
            ticket_date_tv.text=item.date
            ticket_status_tv.text=item.status
            if (baseRepository.lang=="ar"){
            when(item.status){
                "open"->{ticket_status_tv.text="الشكوي مفتوحة"}
                "inprogress"->{ticket_status_tv.text="الشكوي قيد الدراسة"}
                else->{ticket_status_tv.text="الشكوي منتهية"}
            } }

            ticket_num_tv.text="#"+item.id.toString()
            if (item.status=="open"){
                ticket_status_tv.textColor=context.getColor(R.color.black)
            }
            else if (item.status=="inprogress"){
                ticket_status_tv.textColor=context.getColor(R.color.colorAccent)
            }
            else{
                ticket_status_tv.textColor=context.getColor(android.R.color.darker_gray)
            }

            setOnClickListener {
                context.startActivity(Intent(context,TicketDetailsActivity::class.java).apply {
                    putExtra("ticket",item)
                })
            }
        }
    }
}
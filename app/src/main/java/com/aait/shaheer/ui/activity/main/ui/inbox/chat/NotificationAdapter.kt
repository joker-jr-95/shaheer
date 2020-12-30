package com.aait.shaheer.ui.activity.main.ui.inbox.chat

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.notification_model.Data
import com.aait.shaheer.data_layer.model.notification_model.Notification
import com.aait.shaheer.ui.activity.details.ProductDetailsActivity
import com.aait.shaheer.ui.activity.main.MainActivity
import com.aait.shaheer.ui.activity.main.ui.inbox.QustAnswActivity
import com.aait.shaheer.ui.activity.main.ui.menu.TicketDetailsActivity
import com.aait.shaheer.ui.activity.main.ui.other_profile.OtherProfileActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_notification.view.*
import java.util.*

class NotificationAdapter : RecyclerView.Adapter<NotificationAdapter.NotificationVH>() {

    private var data: MutableList<Notification> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationVH {
        return NotificationVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_notification, parent, false)
        )
    }
    fun removeItem(pos: Int) {
        data.removeAt(pos)
        notifyItemRemoved(pos)
        notifyItemRangeChanged(pos,itemCount)

    }
    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: NotificationVH, position: Int) =
        holder.bind(data[position])

    fun swapData(data: MutableList<Notification>) {
        this.data = data
        notifyDataSetChanged()
    }

    class NotificationVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item:Notification) = with(itemView) {
            Picasso.get().load(item.image).into(iv_avatar)
            tv_action.text=item.text
            tv_time.text=item.date

            setOnClickListener {
                when(item.key){
                     "answerContact"->{
                        context.startActivity(Intent(context,QustAnswActivity::class.java).apply {
                            putExtra("help_id", item.data?.help_id.toString())
                        })
                    }
                    "answerTicket"->{
                        context.startActivity(Intent(context,TicketDetailsActivity::class.java).apply {
                            putExtra("ticket_id", item.data?.ticket_id.toString())
                        })
                    }

                    "follow"->{
                        context.startActivity(Intent(context,OtherProfileActivity::class.java).apply {
                            putExtra("profile", item.data?.user_id!!.toInt())
                        })
                    }

                    else->{
                        if (item.key=="likePost"||item.key=="Repost"||item.key=="commentPost"||item.key=="replyComment"||item.key=="likeComment"||item.key=="newPost"){
                            context.startActivity(Intent(context,ProductDetailsActivity::class.java).apply {
                                putExtra("post_id",item.data?.post_id.toString())
                            })
                        }
                        else if (item.key!!.contains("Order",true)){
                                context.startActivity(Intent(context,MainActivity::class.java).apply {
                                    putExtra("to_service",true)
                                })

                        }
                    }
                }
            }
        }
    }
}
package com.aait.shaheer.ui.activity.main.ui.inbox.chat

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.rooms_model.DataItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_room.view.*
import java.util.*

class RoomAdapter : RecyclerView.Adapter<RoomAdapter.RoomVH>() {

    private var data: MutableList<DataItem> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomVH {
        return RoomVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_room, parent, false)
        )
    }

    fun removeItem(pos: Int) {
        data.removeAt(pos)
        notifyItemRemoved(pos)
        notifyItemRangeChanged(pos,itemCount)


    }
    fun updateItem(pos: Int){
        notifyItemChanged(pos)
    }


    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: RoomVH, position: Int) = holder.bind(data[position])

    fun swapData(data: List<DataItem>) {
        this.data = data as MutableList<DataItem>
        notifyDataSetChanged()
    }


    class RoomVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item:DataItem) = with(itemView) {
            tv_name.text=item.seconduser.name
            Picasso.get().load(item.seconduser.avatar).into(iv_avatar)
            published_tv.text=item.date
            //seen status
            if (item.seen=="true" || item.sender=="you") {
                seen_msg.visibility=View.INVISIBLE
            }
            else{
                seen_msg.visibility=View.VISIBLE
            }
            if (item.type=="text"){
                tv_msg.text=item.content
            }
            else{
                tv_msg.text=context.getText(R.string.sent_attachment)
            }
            setOnClickListener {
                context.startActivity(Intent(context,ChatActivity::class.java).apply {
                    putExtra("id",item.id.toString())
                })
            }
        }
    }
}
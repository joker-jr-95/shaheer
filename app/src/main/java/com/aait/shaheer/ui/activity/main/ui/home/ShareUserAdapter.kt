package com.aait.shaheer.ui.activity.main.ui.home

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.search_model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_room.view.*
import java.util.*

class ShareUserAdapter(val onItemClicked:(item:User)->Unit) : RecyclerView.Adapter<ShareUserAdapter.ShareUserVH>() {

    private var data: MutableList<User> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShareUserVH {
        return ShareUserVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_share_user, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ShareUserVH, position: Int) = holder.bind(data[position],onItemClicked)

    fun swapData(data: MutableList<User>) {
        this.data = data
        notifyDataSetChanged()
    }

    fun removeItem(pos: Int) {
        data.removeAt(pos)
        notifyItemRemoved(pos)
        notifyItemRangeChanged(pos, itemCount)


    }

    class ShareUserVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            item: User,
            onItemClicked: (item: User) -> Unit
        ) = with(itemView) {
            tv_name.text=item.name
            Picasso.get().load(item.image).into(iv_avatar)

            setOnClickListener {
                onItemClicked(item)
            }
        }
    }
}
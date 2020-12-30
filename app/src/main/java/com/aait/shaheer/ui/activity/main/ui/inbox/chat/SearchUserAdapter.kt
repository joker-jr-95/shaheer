package com.aait.shaheer.ui.activity.main.ui.inbox.chat

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.search_model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_room.view.*
import java.util.*

class SearchUserAdapter(val onItemClicked:(item:User)->Unit) : RecyclerView.Adapter<SearchUserAdapter.SearchVH>() {

    private var data: MutableList<User> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchVH {
        return SearchVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_search_user, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: SearchVH, position: Int) = holder.bind(data[position],onItemClicked)

    fun swapData(data: MutableList<User>) {
        this.data = data
        notifyDataSetChanged()
    }

    fun removeItem(pos: Int) {
        data.removeAt(pos)
        notifyItemRemoved(pos)
        notifyItemRangeChanged(pos, itemCount)



    }

    class SearchVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            item: User,
            onItemClicked: (item:User) -> Unit
        ) = with(itemView) {
            tv_name.text=item.name


                Picasso.get().load(item.image).into(iv_avatar)


            setOnClickListener {
                    onItemClicked(item)
            }
        }
    }
}
package com.aait.shaheer.ui.activity.main.ui.profile.suggestion

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.connections_model.Friend
import com.aait.shaheer.ui.activity.main.ui.other_profile.OtherProfileActivity
import com.kareem.presetntation.helper.setImg
import kotlinx.android.synthetic.main.item_suggestion.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.*

class FriendHorizAdapter : RecyclerView.Adapter<FriendHorizAdapter.FriendHorizVH>() {

    private var data: List<Friend> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendHorizVH {
        return FriendHorizVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_suggestion, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: FriendHorizVH, position: Int) =
        holder.bind(data[position])

    fun swapData(data: List<Friend>) {
        this.data = data
        notifyDataSetChanged()
    }

    class FriendHorizVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item:Friend) = with(itemView) {
            context.setImg(item.avatar!!,friend_img)
            friend_img.onClick {
                context.startActivity(Intent(context, OtherProfileActivity::class.java).apply {
                    putExtra("profile",item.id)
                })
            }
            setOnClickListener {
                
            }
        }
    }
}
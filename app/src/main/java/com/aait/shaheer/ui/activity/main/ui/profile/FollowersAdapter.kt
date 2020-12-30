package com.aait.shaheer.ui.activity.main.ui.profile

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.connections_model.Friend
import com.aait.shaheer.domain.repository.home_repository.homeRepository
import com.aait.shaheer.ui.activity.main.ui.other_profile.OtherProfileActivity
import com.aait.shaheer.ui.activity.main.ui.profile.post.OnMenuClicked
import com.kareem.presetntation.helper.setImg
import kotlinx.android.synthetic.main.follow_item.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.*

class FollowersAdapter (val onMenuClick: OnMenuClicked) : RecyclerView.Adapter<FollowersAdapter.FollowersVH>() {


    private var user: Friend?=null
    private var data: List<Friend> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowersVH {
        return FollowersVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.follow_item, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: FollowersVH, position: Int) {
        if (position==data.lastIndex){
            holder.itemView.bottom_view.visibility=View.GONE
        }
        if (homeRepository.loadUser()?.id==data[position].id){
            holder.itemView.menu_iv.visibility=View.INVISIBLE
        }
        else{
            holder.itemView.menu_iv.visibility=View.VISIBLE
        }
        holder.bind(data[position],onMenuClick,user)
    }

    fun swapData(data: List<Friend>) {
        this.data = data
        notifyDataSetChanged()
    }
    fun updatedUser(user:Friend?){
        this.user=user
    }

    class FollowersVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            item: Friend,
            onMenuClick: OnMenuClicked,
            user: Friend?
        ) = with(itemView) {
            context.setImg(item.avatar!!,iv_avatar)
            tv_name.text=item.name
            tv_job.text=item.category
            tv_desc.text=item.bio
            menu_iv.onClick {
            if (user!=null){
                onMenuClick.onMenuClicked(user)
            }
            else {
                onMenuClick.onMenuClicked(item)
            }

            }
            iv_avatar.onClick {
                context.startActivity(Intent(context,OtherProfileActivity::class.java).apply {
                    putExtra("profile",item.id)
                })
            }
            setOnClickListener {

            }
        }
    }
}
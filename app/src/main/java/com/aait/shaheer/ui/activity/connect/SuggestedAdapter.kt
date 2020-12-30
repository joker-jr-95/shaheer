package com.aait.shaheer.ui.activity.connect

import android.content.res.ColorStateList
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.connections_model.Friend
import com.kareem.presetntation.helper.setImg
import kotlinx.android.synthetic.main.suggested_item.view.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.*

class SuggestedAdapter(val onFollowClicked:OnFollowListener) : RecyclerView.Adapter<SuggestedAdapter.SuggestedVH>() {

    private var data: List<Friend> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestedVH {
        return SuggestedVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.suggested_item, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: SuggestedVH, position: Int) {
     if (position==data.lastIndex){
         holder.itemView.view_item.visibility=View.GONE
     }
        holder.bind(data[position], onFollowClicked)
    }

    fun swapData(data: List<Friend>) {
        this.data = data
        notifyDataSetChanged()
    }

    class SuggestedVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            item: Friend,
            onFollowClicked: OnFollowListener
        ) = with(itemView) {
            context.setImg(item.avatar!!,iv_avatar)
            tv_name.text=item.name
            tv_job.text=item.category
            tv_desc.text=item.bio
            follow_btn.backgroundResource=R.drawable.follow_btn

            if (item.following_him==false) {
                follow_btn.setBackgroundDrawable(context.getDrawable(R.drawable.follow_btn)!!)
                follow_btn.textColor = ContextCompat.getColor(context, android.R.color.white)
                follow_btn.text = context.getString(R.string.follow)

            }
                else{
                    follow_btn.setBackgroundDrawable(context.getDrawable(R.drawable.unfollow_btn)!!)
                    follow_btn.textColor=ContextCompat.getColor(context,R.color.colorAccent)
                    follow_btn.text=context.getString(R.string.unfollow)

            }
            follow_btn.onClick {
                if (follow_btn.text.toString()==context.getString(R.string.unfollow)){
                    follow_btn.setBackgroundDrawable(context.getDrawable(R.drawable.follow_btn)!!)
                    follow_btn.textColor=ContextCompat.getColor(context,android.R.color.white)
                    onFollowClicked.onFollow(item,true)
                    follow_btn.text=context.getString(R.string.follow)

                }
                else{
                    follow_btn.setBackgroundDrawable(context.getDrawable(R.drawable.unfollow_btn)!!)
                    follow_btn.textColor=ContextCompat.getColor(context,R.color.colorAccent)
                    onFollowClicked.onFollow(item,false)
                    follow_btn.text=context.getString(R.string.unfollow)
                }
            }

            setOnClickListener {

            }
        }
    }
}
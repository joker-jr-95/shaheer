package com.aait.shaheer.ui.activity.main.ui.profile.post.comment

import android.content.Intent
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.post_comment_model.Comment
import com.aait.shaheer.data_layer.model.post_comment_model.Reply
import com.aait.shaheer.ui.activity.main.ui.image_details.ImageDetailsActivity
import com.aait.shaheer.ui.activity.main.ui.other_profile.OtherProfileActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.comment_post_item.view.*
import kotlinx.android.synthetic.main.item_more_reply.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.*

class RelpyAdapter(val size:Int,val comment:Comment) : RecyclerView.Adapter<RelpyAdapter.ReolyVH>() {

    var IS_REPLY_COUNT=0
    private var data: List<Reply> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReolyVH {
        if (IS_REPLY_COUNT==0) {
            return ReolyVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_comment_reply, parent, false)
            )
        }
        else{
            return ReolyVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_more_reply, parent, false)
            )
        }
    }

    override fun getItemCount():Int {
        return if (data.size>3) {
            4
        } else{
            data.size
        }

    }

    override fun onBindViewHolder(holder: ReolyVH, position: Int) {
        if (data.size > position) {
            holder.bind(data[position])
        }

        with(holder.itemView) {
            if (IS_REPLY_COUNT == 0) {
                var item: Reply
                if (data.size>3){
                     item=data[position-1]
                }
                else{
                    item=data[position]

                }

                Glide.with(context).load(item.user?.avatar)
                    .into(iv_comment)
                iv_comment.onClick {
                    context.startActivity(Intent(context, OtherProfileActivity::class.java).apply {
                        putExtra("profile",item.user?.id)
                    })
                }
                tv_comment_name.text=item.user?.name
                tv_comment_content.text=item.comment
                if (item.image!!.isNotBlank()) {
                    card_img_comment.visibility=View.VISIBLE
                    Glide.with(context).load(item.image)
                        .into(iv_img)
                }
                else{
                    card_img_comment.visibility=View.GONE
                }
                tv_comment_content.onClick {

                }
                card_img_comment.onClick {
                    context.startActivity(Intent(context, ImageDetailsActivity::class.java).apply {
                        putExtra("img",item.image)
                    })
                }
            }
            else {
                show_more_tv.text=context.getString(R.string.show_more_replies,size - 3)
                setOnClickListener {
                    //go replies
                    context.startActivity(Intent(context,ReplyActivity::class.java).apply {
                        putExtra("comment",comment)
                    })
                    //overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)

                }
            }

        }
    }

    fun swapData(data: List<Reply>) {
        this.data = data
        notifyDataSetChanged()
    }


    override fun getItemViewType(position: Int): Int {

        if (size >3 && position==0) {
            IS_REPLY_COUNT=1
        }
        else{
            IS_REPLY_COUNT=0
        }
        return IS_REPLY_COUNT
    }



    class ReolyVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item:Reply) = with(itemView) {

            setOnClickListener {

            }
        }
    }
}
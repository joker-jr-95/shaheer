package com.aait.shaheer.ui.activity.main.ui.profile.post.comment

import android.content.Intent
import android.content.res.ColorStateList
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.post_comment_model.Comment
import com.aait.shaheer.data_layer.model.post_comment_model.Reply
import com.aait.shaheer.ui.activity.main.ui.image_details.ImageDetailsActivity
import com.aait.shaheer.ui.activity.main.ui.other_profile.OtherProfileActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.comment_post_item.view.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.textColor
import java.util.*

class CommentAdapter(val action:CommentListener) : RecyclerView.Adapter<CommentAdapter.CommentVH>() {

    private var data: MutableList<Comment> = ArrayList()
    private var replies: MutableList<Reply> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentVH {
        return CommentVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_comment, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: CommentVH, position: Int) {
        val mAdapterReplies= data[position].num_replies?.let { data[position].id?.let { it1 ->
            RelpyAdapter(it,
                data[position]
            )
        } }
        with(holder.itemView) {
            replies_comments.layoutManager = LinearLayoutManager(context)
            replies_comments.adapter=mAdapterReplies
            data[position].replies?.let {
                mAdapterReplies?.swapData(it)
                replies_comments.setRecycledViewPool(RecyclerView.RecycledViewPool())

            }
        }
        holder.bind(data[position],action,position)
    }

    fun swapData(data: List<Comment>) {
        this.data = data as MutableList<Comment>
        notifyDataSetChanged()
    }
    fun addReplies(replies: List<Reply>) {
        this.replies = replies as MutableList<Reply>
        notifyDataSetChanged()
    }


    fun addComment(comment: Comment){
        Log.e("data",data.toString())
        if (comment.id==0){
            data.add(comment)
        }
        else{
            data[data.lastIndex] = comment
        }
        notifyDataSetChanged()
    }

    class CommentVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            item: Comment,
            action: CommentListener,
            position: Int
        ) = with(itemView) {
        Glide.with(context).load(item.user?.avatar)
            .into(iv_comment)
            iv_comment.onClick {
                context.startActivity(Intent(context, OtherProfileActivity::class.java).apply {
                    putExtra("profile", item.user?.id)
                })
            }
            tv_comment_name.text=item.user?.name
            tv_comment_content.text=item.comment
            tv_date_comment.text=item.date
            if (item.image!!.isNotBlank()) {
                card_img_comment.visibility=View.VISIBLE
                Glide.with(context).load(item.image)
                    .into(iv_img)
            }
            else{
                card_img_comment.visibility=View.GONE
            }

            //clicks
            tv_reply_comment.onClick {
                    action.onReplyComment(position, item)
            }
            //liked
            if (item.liked==true){
                tv_like_comment.textColor=ContextCompat.getColor(context,R.color.colorAccent)
                tv_like_comment.text=context.getString(R.string.liked)

            }
            else{
                tv_like_comment.textColor=ContextCompat.getColor(context,android.R.color.darker_gray)
                tv_like_comment.text=context.getString(R.string.like)
            }

            if (item.num_likes!! >0){
                like_lay.visibility=View.VISIBLE
                num_likes.text=item.num_likes.toString()
            }
            else{
                like_lay.visibility=View.GONE
            }
            card_img_comment.onClick {
            context.startActivity(Intent(context,ImageDetailsActivity::class.java).apply {
                putExtra("img",item.image)
            })
        }
            tv_like_comment.onClick {

                    if (tv_like_comment.textColors == ColorStateList.valueOf(ContextCompat.getColor(context,R.color.colorAccent)))
                    {
                        tv_like_comment.textColor = ContextCompat.getColor(context,android.R.color.darker_gray)
                        tv_like_comment.text=context.getString(R.string.like)
                        num_likes.text=(num_likes.text.toString().toInt()-1).toString()
                        if (num_likes.text.toString().toInt()==0){
                            like_lay.visibility=View.GONE
                        }
                    }
                    else{
                        tv_like_comment.textColor = ContextCompat.getColor(context,R.color.colorAccent)
                        tv_like_comment.text=context.getString(R.string.liked)
                        num_likes.text=(num_likes.text.toString().toInt()+1).toString()
                        like_lay.visibility=View.VISIBLE


                    }
                    action.onLikeComment(position,item)
            }
            setOnClickListener {

            }
        }
    }
}
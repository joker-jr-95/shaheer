package com.aait.shaheer.ui.activity.main.ui.services

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.collection_model.Post
import com.aait.shaheer.data_layer.model.connections_model.Friend
import com.aait.shaheer.domain.repository.home_repository.homeRepository
import com.aait.shaheer.helper.Tweet
import com.aait.shaheer.ui.activity.details.ProductDetailsActivity
import com.aait.shaheer.ui.activity.main.ui.profile.BottomSheetFollowing
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.collection_bar.view.*
import kotlinx.android.synthetic.main.item_post_collection.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.*

class ItemPostColldection : RecyclerView.Adapter<ItemPostColldection.ItemPostVH>() {

    private var data: MutableList<Post> = ArrayList()

    private lateinit var fragmentManger: FragmentManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemPostVH {
        return ItemPostVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_post_collection, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ItemPostVH, position: Int) {
        holder.bind(data[position])
        val item=data[position]
        with(holder.itemView) {

            post_menu_iv.onClick {
              openMenu(context,item)
            }


        }
    }

            private fun openMenu(
                context: Context,
                item: Post
            ) {
                val friend = with(item.user!!) {
                    val friend = Friend(
                        avatar, bio, category,
                        category_id, following_him,
                        id, name, verified,item.muted,item.blocked
                    )
                    friend
                }
                BottomSheetFollowing(friend) {isFollow, tweet ->
                    when (tweet.name) {
                        Tweet.FOLLOW.name -> {
                            sendRequestFollow(friend,context)
                        }
                        Tweet.MUTE.name->{
                            sendRequestMute(friend,context)
                        }
                        Tweet.BLOCK.name -> {
                            sendRequestBlock(friend,context)
                        }
                    }

                }.show(fragmentManger,"")
            }
    private fun sendRequestMute(item: Friend,context: Context) {
        homeRepository.mute(item.id.toString())
            .subscribeWithLoading({},{},{handleError(it.message,context)},{})
    }
            private fun sendRequestFollow(item: Friend,context: Context) {
                homeRepository.follow(item.id.toString())
                    .subscribeWithLoading({},{},{handleError(it.message,context)},{})
            }

            private fun sendRequestBlock(item: Friend,context: Context) {
                homeRepository.block(item.id.toString())
                    .subscribeWithLoading({},{},{handleError(it.message,context)},{})
            }
            private fun handleError(message: String?, context: Context) {
                Log.e("error",message)
                message?.let { context.toasty(it,2) }





    }

    fun swapData(
        data: List<Post>,
        supportFragmentManager: FragmentManager
    ) {
        this.data = data as MutableList<Post>
        this.fragmentManger=supportFragmentManager
        notifyDataSetChanged()
    }

    class ItemPostVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item:Post) = with(itemView) {
            Picasso.get().load(item.user?.avatar).into(iv_avatar)
            tv_name.text=item.user?.name
            tv_job.text=item.user?.category
            if (item.user?.verified!!){
                iv_active.visibility=View.VISIBLE
            }
            else{
                iv_active.visibility=View.GONE
            }
            published_tv.text=item.date
            tv_desc.text=item.content
            num_starts.text=item.rating
            num_bag.text=item.num_selles.toString()
            more_btn.onClick {
                context.startActivity(Intent(context,ProductDetailsActivity::class.java).apply {
                    putExtra("post_id",item.id.toString())
                })
            }


            setOnClickListener {

            }
        }

    }
}
package com.aait.shaheer.ui.activity.main.ui.profile.post

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.connections_model.Friend
import com.aait.shaheer.data_layer.model.profile_model.Post
import com.aait.shaheer.data_layer.model.profile_model.Profile
import com.aait.shaheer.domain.repository.home_repository.homeRepository
import com.aait.shaheer.helper.Actions
import com.aait.shaheer.helper.Tweet
import com.aait.shaheer.ui.activity.details.ProductDetailsActivity
import com.aait.shaheer.ui.activity.main.ui.image_details.ImageDetailsActivity
import com.aait.shaheer.ui.activity.main.ui.profile.BottomSheetFollowing
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.kareem.presetntation.helper.setImg
import kotlinx.android.synthetic.main.like_bar.view.*
import kotlinx.android.synthetic.main.post_item.view.*
import kotlinx.android.synthetic.main.shop_bar.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.textColor

class PostsAdapter(val onAction:(Actions,action:Post)->Unit) : RecyclerView.Adapter<PostsAdapter.PostsVH>() {

    private var fromHome: Boolean?=false
    private lateinit var fragmentManger: FragmentManager
    private var data: MutableList<Post> = mutableListOf()
    private lateinit var profile:Profile
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsVH {
        if (fromHome==true){
            return PostsVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.post_item_home, parent, false)
            )
        }
        else {
            return PostsVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.post_item, parent, false)
            )
        }
    }

    override fun getItemCount() = data.size



    override fun onBindViewHolder(holder: PostsVH, position: Int) {
          var user: Friend?=null
        holder.bind(data[position],onAction)
        val item=data[position]
        with(holder.itemView) {
            val player= SimpleExoPlayer.Builder(context).build()

            var mImgAdapter =
                ImagesAdapter{
                    context.startActivity(Intent(context,ImageDetailsActivity::class.java).apply {
                        putExtra("img",it)
                    })
                }
            when (item.files?.size){
                1->rec_imgs.layoutManager=LinearLayoutManager(context).apply {
                    initialPrefetchItemCount=1
                }
                2->rec_imgs.layoutManager=GridLayoutManager(context,3).apply {
                    initialPrefetchItemCount=3
                }
                else->rec_imgs.layoutManager=GridLayoutManager(context,3).apply {
                    initialPrefetchItemCount=3
                }
            }
            rec_imgs.setRecycledViewPool(RecyclerView.RecycledViewPool())

            rec_imgs.adapter=mImgAdapter

            if (item.reposted==false){
                retweet_lay.visibility=View.GONE
            }
            else{
                retweet_lay.visibility=View.VISIBLE
            }
            if (item.user?.verified==true){
                iv_active.visibility=View.VISIBLE
            }
            else{
                iv_active.visibility=View.GONE
            }
            //set data profile
            tv_name.text=item.user?.name!!
            tv_job.text= item.user.category!!
            published_tv.text=item.date
            context.setImg(item.user.avatar!!,iv_avatar)
            //set data posts
            tv_desc.text=item.content
            retweet_by_tv.text=item.repost_msg
            Log.e("files1",item.files?.size.toString())
            if (item.files!!.isNotEmpty()){
                if (item.files[0].type=="image"){
                    //set recycler
                    vid_lay.visibility=View.GONE
                    rec_imgs.visibility=View.VISIBLE
                    Log.e("files",item.files.size.toString())
                    mImgAdapter.swapData(item.files)
                }

                else{
                    vid_lay.visibility=View.VISIBLE

                    rec_imgs.visibility=View.GONE
                    vid_lay.visibility=View.VISIBLE
                    val vidUri= Uri.parse(item.files[0].src)
                    val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(context, Util.getUserAgent(context, "shaheer"))
                    val videoSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(vidUri)
                    video_view.player=player
                    player.prepare(videoSource)

                    vid_btn.onClick {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(vidUri, "video/*")
                        context.startActivity(intent)
                    }

                }
            }
            else{
                vid_lay.visibility=View.GONE
                rec_imgs.visibility=View.GONE

            }
            //shop bar
            num_starts.text=item.rating
            num_dollars.text=item.price+" "+item.currency
            num_bag.text=item.num_selles.toString()
            //like bar
            num_likes.text=item.num_likes.toString()
            num_msgs.text=item.num_comments.toString()
            num_retweet.text=item.num_repost.toString()
            //like
            if (item.liked!!){
                like_img.imageTintList =ContextCompat.getColorStateList(context, R.color.colorAccent)

                num_likes.textColor=ContextCompat.getColor(context,R.color.colorAccent)
            }
            else{
                num_likes.textColor=ContextCompat.getColor(context,android.R.color.darker_gray)
                like_img.imageTintList =ContextCompat.getColorStateList(context, android.R.color.darker_gray)
            }

            //retweet
            if (item.reposted!!){
                retweet_img.imageTintList= ContextCompat.getColorStateList(context, R.color.colorAccent)
                num_retweet.textColor=ContextCompat.getColor(context,R.color.colorAccent)

            }
            else{
                retweet_img.imageTintList= ContextCompat.getColorStateList(context, android.R.color.darker_gray)
                num_retweet.textColor=ContextCompat.getColor(context,android.R.color.darker_gray)
            }
            //like action
            like_img.onClick {
                if (like_img.imageTintList ==ContextCompat.getColorStateList(context, R.color.colorAccent))
                {
                    num_likes.text=(num_likes.text.toString().toInt().minus(1)).toString()
                    num_likes.textColor=ContextCompat.getColor(context,android.R.color.darker_gray)
                    like_img.imageTintList =ContextCompat.getColorStateList(context, android.R.color.darker_gray)
                }
                else{
                    num_likes.text=(num_likes.text.toString().toInt().plus(1)).toString()
                    num_likes.textColor=ContextCompat.getColor(context,R.color.colorAccent)
                    like_img.imageTintList =ContextCompat.getColorStateList(context, R.color.colorAccent)
                }
                onAction(Actions.LIKE,item)



            }
            if (item.pin!!){
                pinned_lay.visibility = View.VISIBLE
            }
            else{
                pinned_lay.visibility = View.GONE

            }
            //comment action
            comment_iv.onClick {
                onAction(Actions.COMMENT,item)
            }
            //retweet action
            retweet_img.onClick {
                if (retweet_img.imageTintList ==ContextCompat.getColorStateList(context, R.color.colorAccent))
                {
                    num_retweet.text=(num_retweet.text.toString().toInt().minus(1)).toString()
                    num_retweet.textColor=ContextCompat.getColor(context,android.R.color.darker_gray)
                    retweet_img.imageTintList =ContextCompat.getColorStateList(context, android.R.color.darker_gray)
                }
                else{
                    num_retweet.text=(num_retweet.text.toString().toInt().plus(1)).toString()
                    num_retweet.textColor=ContextCompat.getColor(context,R.color.colorAccent)
                    retweet_img.imageTintList =ContextCompat.getColorStateList(context, R.color.colorAccent)
                }
                onAction(Actions.RETWEET,item)
            }

            //set
            go_iv.setOnClickListener {
                context.startActivity(Intent(context,ProductDetailsActivity::class.java).apply {
                    if (item.type=="repost") {
                        putExtra("post_id", item.parent_id.toString())
                    }
                    else{
                        putExtra("post_id", item.id.toString())
                    }
                })
            }


            var isPin:Boolean?=null

        if (PostsFragment.other_profile && fromHome==false){
                post_menu_iv.visibility=View.GONE
            }
            else{
                post_menu_iv.visibility=View.VISIBLE
            }
        post_menu_iv.onClick {
     if (fromHome!!) {
         if (user==null) {
             user = with(item.user) {
                 val friend = Friend(
                     avatar, bio, category,
                     category_id, following_him,
                     id, name, verified, item.muted, item.blocked
                 )
                 friend
             }
         }

         BottomSheetFollowing(user!!) { user_, tweet ->
             user=user_
             when (tweet.name) {
                 Tweet.FOLLOW.name -> {
                     sendRequestFollow(user!!, context)
                 }
                 Tweet.MUTE.name->{
                     sendRequestMute(user!!,context)
                 }
                 Tweet.BLOCK.name -> {
                     sendRequestBlock(user!!, context)
                 }

             }

         }.show(fragmentManger, "")
     }
     else{
         if (isPin==null){
             isPin=item.pin
         }
             val bottomsheet = BottomSheetPin(
                 isPin!!,
                 { isPin_ ->
                     isPin=isPin_
                     if (isPin_) {
                         pinned_lay.visibility = View.VISIBLE
                     } else {
                         pinned_lay.visibility = View.GONE
                     }
                     if (item.type == "repost") {
                         sendRequestPin(item.parent_id!!)
                     } else {
                         sendRequestPin(item.id!!)
                     }
                 }) { isDelete ->
                 data.removeAt(position)
                 notifyDataSetChanged()
                 if (item.type == "repost") {
                     sendRequestDelete(item.parent_id!!)
                 } else {
                     sendRequestDelete(item.id!!)
                 }
             }
             bottomsheet.show(fragmentManger, "")
         }
     }

            lin_name.onClick {
                onAction(Actions.PROFILE,item)
            }
            iv_avatar.onClick {
                onAction(Actions.PROFILE,item)
            }
            share_lay.onClick {
                onAction(Actions.SHARE,item)
            }
        }

    }
    private fun sendRequestMute(item: Friend,context: Context) {
        homeRepository.mute(item.id.toString())
            .subscribeWithLoading({},{},{handleError(it.message,context)},{})
    }
    private fun sendRequestFollow(
        item: Friend,
        context: Context
    ) {
        homeRepository.follow(item.id.toString())
            .subscribeWithLoading({},{},{handleError(it.message,context)},{})
    }

    private fun handleError(message: String?, context: Context) {
        message?.let { context.toasty(it,2) }
    }

    private fun sendRequestBlock(item: Friend,context: Context) {
        homeRepository.block(item.id.toString())
            .subscribeWithLoading({},{},{handleError(it.message, context)},{})
    }


    private fun sendRequestDelete(postId:Int) {
        homeRepository.delete(postId.toString()).subscribeWithLoading({},{},{},{})
    }

    private fun sendRequestPin(postId:Int) {
        homeRepository.pin(postId.toString()).subscribeWithLoading({},{},{},{})

    }

    fun swapData(
        data: List<Post>,
        fragmentManager: FragmentManager,
        fromHome:Boolean?=false
    ) {
        this.fromHome=fromHome
        this.fragmentManger=fragmentManager
        this.data = data as MutableList<Post>
        notifyDataSetChanged()
    }

    class PostsVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("NewApi")
        fun bind(
            item: Post,
            onAction: (Actions, action: Post) -> Unit
        ) = with(itemView) {

    }}
}
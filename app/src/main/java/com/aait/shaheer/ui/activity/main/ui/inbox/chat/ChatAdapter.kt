package com.aait.shaheer.ui.activity.main.ui.inbox.chat

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.chat_model.Message
import com.aait.shaheer.domain.core.Constant
import com.aait.shaheer.ui.activity.details.ProductDetailsActivity
import com.aait.shaheer.ui.activity.main.ui.image_details.ImageDetailsActivity
import com.davemorrissey.labs.subscaleview.ImageSource
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.squareup.picasso.Picasso
import io.github.ponnamkarthik.richlinkpreview.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_image_details.*
import kotlinx.android.synthetic.main.chat_item_me.view.*
import kotlinx.android.synthetic.main.chat_item_me.view.rich_link_desp
import kotlinx.android.synthetic.main.chat_item_me.view.rich_link_image
import kotlinx.android.synthetic.main.chat_item_me.view.rich_link_title
import kotlinx.android.synthetic.main.chat_item_other.view.*
import kotlinx.android.synthetic.main.item_room.view.*
import kotlinx.android.synthetic.main.media_item.view.*
import kotlinx.android.synthetic.main.text_item.view.*
import kotlinx.android.synthetic.main.text_item_other.view.*
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import java.lang.Exception


class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatVH>() {

    private var VIEW_ITEM_TYPE: Int = 0
    private var data: MutableList<Message> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatVH {
        if (VIEW_ITEM_TYPE==0) {
            return ChatVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_item_me, parent, false)
            )
        }
        else{
            return ChatVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_item_other, parent, false)
            )

        }
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ChatVH, position: Int) = holder.bind(data[position],VIEW_ITEM_TYPE)

    override fun getItemViewType(position: Int): Int {
        if (data[position].sender=="you"){
            VIEW_ITEM_TYPE=0
        }
        else{
            VIEW_ITEM_TYPE=1
        }
        return VIEW_ITEM_TYPE
    }

    fun swapData(data: MutableList<Message>) {
        this.data = data
        notifyDataSetChanged()
    }

    class ChatVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("CheckResult")
        fun bind(item: Message, viewItemType: Int) = with(itemView) {
            //me
            val player=SimpleExoPlayer.Builder(context).build()
            if (viewItemType==0) {
                Log.e("me","1")
                if (item.type == "text" || item.type == "link") {
                    media_item.visibility=View.GONE
                    card_txt_item.visibility=View.VISIBLE
                    if (item.type=="text"){
                        rich_link_card.visibility=View.GONE
                        tv_me_chat.visibility=View.VISIBLE
                        tv_me_chat.text = item.content

                        card_txt_item.setCardBackgroundColor( ColorStateList.valueOf(ContextCompat.getColor(context,R.color.colorAccent)))
                    }
                    else{
                        rich_link_card.visibility=View.VISIBLE
                        tv_me_chat.visibility=View.GONE
                        tv_me_chat.text = item.content
                        card_txt_item.setCardBackgroundColor( ColorStateList.valueOf(ContextCompat.getColor(context,R.color.whats_bg)))
                        rich_link_original_url.text=Constant.POSTS_BASE+item.content
                        rich_link_card.onClick {
                            context.startActivity(Intent(context,ProductDetailsActivity::class.java).apply {
                                putExtra("post_id",item.content)
                            })
                        }
                        rich_link_original_url.onClick {
                            context.startActivity(Intent(context,ProductDetailsActivity::class.java).apply {
                                putExtra("post_id",item.content)
                            })
                        }
                        rich_link_original_url.onLongClick {

                            com.kareem.domain.core.Util.copy(context,Constant.POSTS_BASE+item.content)
                        }

                        val richPreview = RichPreview(object : ResponseListener {
                            override fun onData(metaData: MetaData?) {
                                context.runOnUiThread {
                                    rich_link_title.text = metaData?.sitename
                                    rich_link_desp.text = metaData?.title
                                    if (metaData?.imageurl!!.isNotBlank()) {
                                        Picasso.get().load(metaData.imageurl).into(rich_link_image)
                                    }
                                }

                            }

                            override fun onError(e: Exception?) {
                                Log.e("error",e?.message)
                            }
                        })
                        richPreview.getPreview(Constant.POSTS_BASE+item.content)
                    

                    }

                }
                else if (item.type == "image"){
                    media_item.visibility=View.VISIBLE
                    iv_lay.visibility=View.VISIBLE
                    vid_lay.visibility=View.GONE
                    card_txt_item.visibility=View.GONE

                    context.runOnUiThread {
                        Picasso.get().load(item.content)
                            .resize(600,600)
                            .into(iv_holder)
                    }

                }
                else{
                    media_item.visibility=View.VISIBLE
                    iv_lay.visibility=View.GONE
                    vid_lay.visibility=View.VISIBLE
                    card_txt_item.visibility=View.GONE

                    Observable.just(item.content)
                        .subscribeOn(Schedulers.io())
                        .map {
                            val vidUri= Uri.parse(item.content)
                            //video_view.setVideoURI(vidUri)
                            // video_view.seekTo(10)
                            val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
                                context,
                                Util.getUserAgent(context, "shaheer")
                            )
                            // This is the MediaSource representing the media to be played.
                            // This is the MediaSource representing the media to be played.
                            val videoSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                                .createMediaSource(vidUri)
                            videoSource
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            player.prepare(it)
                            player.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                            video_view.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL

                            video_view.player=player


                        }


                }
                tv_me_date.text=item.date
                iv_holder.onClick {
                    context.startActivity(Intent(context,ImageDetailsActivity::class.java).apply {
                        putExtra("img",item.content)
                    })
                }
            }

            //other
            else{
                if (item.type == "text" || item.type=="link") {
                    media_item.visibility=View.GONE
                    card_txt_other.visibility=View.VISIBLE

                    if (item.type=="text" ){
                      //  richLinkView_other.visibility=View.GONE
                        rich_link_card_other.visibility=View.GONE
                        tv_other_chat.visibility=View.VISIBLE
                        tv_other_chat.text = item.content
                    }
                    else{
                        tv_other_chat.visibility=View.GONE
                        rich_link_card_other.visibility=View.VISIBLE
                        tv_other_chat.text = item.content
                        rich_link_original_url_other.onClick {
                            context.startActivity(Intent(context,ProductDetailsActivity::class.java).apply {
                                putExtra("post_id",item.content)
                            })
                        }
                        rich_link_original_url_other.onLongClick {
                            com.kareem.domain.core.Util.copy(context,Constant.POSTS_BASE+item.content)
                        }

                        rich_link_card_other.onClick {
                        context.startActivity(Intent(context,ProductDetailsActivity::class.java).apply {
                            putExtra("post_id",item.content)
                        })
                        }
                        rich_link_original_url_other.text=Constant.POSTS_BASE+item.content
                        val richPreview = RichPreview(object : ResponseListener {
                            override fun onData(metaData: MetaData?) {
                                context.runOnUiThread {
                                    rich_link_title_other.text = metaData?.sitename

                                    rich_link_desp_other.text = metaData?.title
                                    Picasso.get().load(metaData?.imageurl).into(rich_link_image_other)
                                }

                            }

                            override fun onError(e: Exception?) {

                            }
                        })
                        richPreview.getPreview(Constant.POSTS_BASE+item.content)
                    }
                }
                else if (item.type == "image"){
                    media_item.visibility=View.VISIBLE
                    iv_lay.visibility=View.VISIBLE
                    vid_lay.visibility=View.GONE
                    card_txt_other.visibility=View.GONE

                    context.runOnUiThread {

                        Picasso.get().load(item.content).resize(600,600).into(iv_holder)
                    }

                }
                else{
                    media_item.visibility=View.VISIBLE
                    iv_lay.visibility=View.GONE
                    vid_lay.visibility=View.VISIBLE
                    card_txt_other.visibility=View.GONE

                    context.runOnUiThread {
                        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
                            context,
                            Util.getUserAgent(context, "shaheer")
                        )
                        val vidUri= Uri.parse(item.content)
                        val videoSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(vidUri)
                        player.prepare(videoSource)
                        player.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                        video_view.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                        video_view.player=player
                    }
                    
                }
                tv_other_date.text=item.date
                context.runOnUiThread {
                    Picasso.get().load(item.avatar).into(circle_iv_other)
                }
                iv_holder.onClick {
                    context.startActivity(Intent(context,ImageDetailsActivity::class.java).apply {
                        putExtra("img",item.content)
                    })
                }
            }
            setOnClickListener {

            }
        }
    }
}
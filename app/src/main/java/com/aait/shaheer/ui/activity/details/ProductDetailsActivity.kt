package com.aait.shaheer.ui.activity.details

import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.add_cart_model.AddCartModel
import com.aait.shaheer.data_layer.model.connections_model.Friend
import com.aait.shaheer.data_layer.model.post_model.Data
import com.aait.shaheer.domain.repository.cart_repository.cartRepository
import com.aait.shaheer.domain.repository.home_repository.homeRepository
import com.aait.shaheer.domain.repository.post_repository.postRepository
import com.aait.shaheer.helper.PRICE_BAR
import com.aait.shaheer.helper.Tweet
import com.aait.shaheer.ui.activity.main.ui.profile.BottomSheetFollowing
import com.aait.shaheer.ui.activity.main.ui.profile.post.comment.CommentActivity
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.kareem.presetntation.base.ParentActivity
import com.kareem.presetntation.helper.setImg
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_add_post.*
import kotlinx.android.synthetic.main.activity_product_details.*
import kotlinx.android.synthetic.main.activity_product_details.vid_lay
import kotlinx.android.synthetic.main.activity_product_details.video_view
import kotlinx.android.synthetic.main.like_bar.*
import kotlinx.android.synthetic.main.media_item.view.*
import kotlinx.android.synthetic.main.prices_shop_bar.*
import kotlinx.android.synthetic.main.prices_shop_bar.call_lay
import kotlinx.android.synthetic.main.prices_shop_bar.video_lay
import kotlinx.android.synthetic.main.shop_bar_details.*
import kotlinx.android.synthetic.main.toolbar_normal.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.sdk27.coroutines.onCheckedChange
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.textColor


class ProductDetailsActivity : ParentActivity() {
    private var friend: Friend?=null
    private lateinit var player: SimpleExoPlayer
    private var vidUri: Uri? = null
    private  var mData: Data? =null
    private lateinit var mAdapter: MySliderAdapter
    private var post_id: String? = null
    private var isInCart=false
    override val layoutResource: Int
        get() = R.layout.activity_product_details

    override fun init() {
         player= SimpleExoPlayer.Builder(this).build()
        post_id=intent.getStringExtra("post_id")
        val gradientDrawable = msg_lay.background as GradientDrawable
        gradientDrawable.setStroke(1,ContextCompat.getColor(applicationContext,R.color.colorAccent))
        iv_toolbar_book_mark.visibility=View.VISIBLE
        iv_toolbar_menu.visibility=View.VISIBLE
        setupSlider()
        sendRequest()
        setActions()
    }

    private fun setActions() {

        iv_toolbar_menu.onClick {
            openMenu()
        }
        //book mark action
        iv_toolbar_book_mark.onClick {
            sendRequestBookMark()
        }
        //like action
        like_img.onClick {
            if (like_img.imageTintList ==ContextCompat.getColorStateList(applicationContext, R.color.colorAccent))
            {
                num_likes.text=(num_likes.text.toString().toInt().minus(1)).toString()
                num_likes.textColor=ContextCompat.getColor(applicationContext,android.R.color.darker_gray)
                like_img.imageTintList =ContextCompat.getColorStateList(applicationContext, android.R.color.darker_gray)
            }
            else{
                num_likes.text=(num_likes.text.toString().toInt().plus(1)).toString()
                num_likes.textColor=ContextCompat.getColor(applicationContext,R.color.colorAccent)
                like_img.imageTintList =ContextCompat.getColorStateList(applicationContext, R.color.colorAccent)
            }
            sendRequestLike()
        }
        //comment action
        comment_iv.onClick {
            goComment()
        }
        //retweet action
        retweet_img.onClick {
            if (retweet_img.imageTintList ==ContextCompat.getColorStateList(applicationContext, R.color.colorAccent))
            {
                num_retweet.text=(num_retweet.text.toString().toInt().minus(1)).toString()
                num_retweet.textColor=ContextCompat.getColor(applicationContext,android.R.color.darker_gray)
                retweet_img.imageTintList =ContextCompat.getColorStateList(applicationContext, android.R.color.darker_gray)
            }
            else{
                num_retweet.text=(num_retweet.text.toString().toInt().plus(1)).toString()
                num_retweet.textColor=ContextCompat.getColor(applicationContext,R.color.colorAccent)
                retweet_img.imageTintList =ContextCompat.getColorStateList(applicationContext, R.color.colorAccent)
            }
            sendRequestRetweet()
        }
        //cart btn
        cart_btn.onClick {
            if (isInCart){
                sendRequestRemove()
            }
            else {
                sendRequestAddCart()
            }
        }


        call_check.onCheckedChange { buttonView, isChecked ->
            if (isChecked){
                changeSelectedItem(PRICE_BAR.CALL)
            }
            else{
                val gradientDrawable = call_lay.background as GradientDrawable
                gradientDrawable.setStroke(1,ContextCompat.getColor(applicationContext,android.R.color.darker_gray))
                call_check.buttonTintList= ContextCompat.getColorStateList(applicationContext, android.R.color.darker_gray)
                iv_call.imageTintList=ContextCompat.getColorStateList(applicationContext, android.R.color.darker_gray)
                tv_call_price.setTextColor(ContextCompat.getColorStateList(applicationContext, android.R.color.darker_gray))
            }
        }
        video_box.onCheckedChange { buttonView, isChecked ->
            if (isChecked){
                showPopUpHint(video_lay)
                changeSelectedItem(PRICE_BAR.VIDEO)
            }
            else{
                val gradientDrawable2 = video_lay.background as GradientDrawable
                gradientDrawable2.setStroke(1,ContextCompat.getColor(applicationContext,android.R.color.darker_gray))
                video_box.buttonTintList= ContextCompat.getColorStateList(applicationContext, android.R.color.darker_gray)
                vid_iv.imageTintList=ContextCompat.getColorStateList(applicationContext, android.R.color.darker_gray)
                tv_vid_price.setTextColor(ContextCompat.getColorStateList(applicationContext, android.R.color.darker_gray))

            }
        }
    }

    private fun sendRequestRemove() {
        cartRepository.removePostCart(post_id!!.toInt()).subscribeWithLoading({showProgressDialog()},{handleRemove(it.msg)},{},{})
    }

    private fun handleRemove(msg: String?) {
        hideProgressDialog()
        isInCart=false
        cart_btn.backgroundColor=ContextCompat.getColor(applicationContext,R.color.colorAccent)
        toasty(getString(R.string.removeded_msg_cart))
        cart_btn.text=getString(R.string.add_cart)
    }

    private fun openMenu() {
        if (friend==null) {
            friend = with(mData?.user!!) {
                val friend = Friend(avatar, bio, category,category_id, following_him,id, name, verified, mData?.muted, mData?.blocked)
                friend
            }
        }

        BottomSheetFollowing(friend!!) {friend_, tweet ->
            friend=friend_
            when (tweet.name) {
                Tweet.FOLLOW.name -> {
                    sendRequestFollow(friend!!)
                }
                Tweet.MUTE.name -> {
                    sendRequestMute(friend!!)
                }
                Tweet.BLOCK.name -> {
                    sendRequestBlock(friend!!)
                }
            }

        }.show(supportFragmentManager,"")
    }
    private fun sendRequestMute(item: Friend) {
        homeRepository.mute(item.id.toString())
            .subscribeWithLoading({},{},{handleError(it.message)},{})
    }
    private fun sendRequestFollow(item: Friend) {
        homeRepository.follow(item.id.toString())
            .subscribeWithLoading({},{},{handleError(it.message)},{})
    }

    private fun sendRequestBlock(item: Friend) {
        homeRepository.block(item.id.toString())
            .subscribeWithLoading({},{},{handleError(it.message)},{})
    }

    private fun sendRequestBookMark() {
        postRepository.bookMarked(post_id.toString())
            .subscribeWithLoading({},{handleBookMarked(it.data)},{handleError(it.message)},{})
    }

    private fun handleBookMarked(data: com.aait.shaheer.data_layer.model.add_cart_model.Data?) {
        if (data?.bookmarked==true){
            iv_toolbar_book_mark.setImageResource(R.drawable.bookmark_ribbon)
        }
        else{
            iv_toolbar_book_mark.setImageResource(R.drawable.bookmark_gray)
        }
    }

    private fun sendRequestAddCart() {

        postRepository.addToCart(post_id.toString(),call_check.isChecked,video_box.isChecked)
            .subscribeWithLoading({},{handleAddCart(it)},{handleError(it.message)},{})
    }

    private fun handleAddCart(data: AddCartModel) {
        isInCart=true
        hideProgressDialog()
        data.msg?.let { toasty(getString(R.string.add_to_cart)) }
        cart_btn.text=getString(R.string.removed_msg_cart)
        cart_btn.backgroundColor=ContextCompat.getColor(applicationContext,R.color.errorColor)
    }

    fun changeSelectedItem(action:PRICE_BAR){
        when(action){
            PRICE_BAR.CALL->{
                val gradientDrawable1 = call_lay.background as GradientDrawable
                gradientDrawable1.setStroke(1,ContextCompat.getColor(applicationContext,R.color.colorAccent))
                //call
                call_check.buttonTintList=ContextCompat.getColorStateList(applicationContext, R.color.colorAccent)
                iv_call.imageTintList=ContextCompat.getColorStateList(applicationContext, R.color.colorAccent)
                tv_call_price.setTextColor(ContextCompat.getColorStateList(applicationContext, R.color.colorAccent))
            }
            PRICE_BAR.VIDEO->{
                val gradientDrawable2 = video_lay.background as GradientDrawable
                gradientDrawable2.setStroke(1,ContextCompat.getColor(applicationContext,R.color.colorAccent))
                //video
                video_box.buttonTintList= ContextCompat.getColorStateList(applicationContext, R.color.colorAccent)
                vid_iv.imageTintList=ContextCompat.getColorStateList(applicationContext, R.color.colorAccent)
                tv_vid_price.setTextColor(ContextCompat.getColorStateList(applicationContext, R.color.colorAccent))

            }
            else -> {}
        }
    }


    private fun showPopUpHint(anchorView: View) {
        val popup = PopupWindow(this)
        val layout = layoutInflater.inflate(R.layout.hint_video, null)
        popup.contentView = layout
        // Set content width and height
        val vid_hint = layout.findViewById(R.id.tv_vid_hint) as TextView

        popup.height = WindowManager.LayoutParams.WRAP_CONTENT
        popup.width = WindowManager.LayoutParams.MATCH_PARENT
        // Closes the popup window when touch outside of it - when looses focus
        popup.isOutsideTouchable = true
        popup.isFocusable = true
        // Show anchored to button
        popup.setBackgroundDrawable(BitmapDrawable())
        popup.showAsDropDown(anchorView)
        // add flag
        val container: View?
        if (android.os.Build.VERSION.SDK_INT > 22) {
            container = popup.contentView.parent.parent as View
        } else {
            container = popup.contentView.parent as View
        }
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val p = container.layoutParams as WindowManager.LayoutParams

        p.flags = p.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        p.dimAmount = 0.3f
        wm.updateViewLayout(container, p)


    }

    private fun sendRequest() {
        postRepository.post(post_id.toString()).subscribeWithLoading(
            {showProgressDialog()},
            { it.data?.let { it1 -> handleData(it1) } },{handleError(it.message)},{})
    }

    private fun handleError(message: String?) {
        Log.e("error",message)
        hideProgressDialog()
        toasty(message!!,2)
    }

    private fun handleData(data: Data) {
        hideProgressDialog()
        mData=data

        if (homeRepository.loadUser()?.id==data.user!!.id){
            cart_btn.visibility=View.GONE
        }
        else{
            cart_btn.visibility=View.VISIBLE
        }
        isInCart=data.in_cart!!
        if (isInCart){
            cart_btn.text=getString(R.string.removed_msg_cart)
            cart_btn.backgroundColor=ContextCompat.getColor(applicationContext,R.color.errorColor)
        }
        else{
            cart_btn.text=getString(R.string.add_cart)
            cart_btn.backgroundColor=ContextCompat.getColor(applicationContext,R.color.colorAccent)
        }
        if (data.files!!.isNotEmpty()){
            if (data.files[0].type=="image"){
                sliderView_.visibility=View.VISIBLE
                vid_lay.visibility=View.GONE
                mAdapter.swapData(data.files.map { it.src } as List<String>)
            }
            else {
                //video view
                sliderView_.visibility = View.GONE
                vid_lay.visibility = View.VISIBLE
                vidUri = Uri.parse(data.files[0].src)
                /*vid_view.setVideoURI(vidUri)
                vid_view.seekTo(10)*/
                //video_view.setVideoURI(vidUri)
                // video_view.seekTo(10)
                val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
                    this,
                    Util.getUserAgent(this, "shaheer")
                )
                // This is the MediaSource representing the media to be played.
                // This is the MediaSource representing the media to be played.
                val videoSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(vidUri)

                player.prepare(videoSource)
                player.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                video_view.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT

                video_view.player = player
            }

        }
        else{
            sliderView_.visibility=View.INVISIBLE
                //            media_view.visibility=View.GONE
        }
        //set header
        if (data.bookmarked==true){
            iv_toolbar_book_mark.setImageResource(R.drawable.bookmark_ribbon)
        }
        else{
            iv_toolbar_book_mark.setImageResource(R.drawable.bookmark_gray)
        }
        //set profile
        tv_name.text=data.user!!.name
        tv_job.text= data.user.category
        setImg(data.user.avatar!!,iv_avatar)
        rate.rating=data.rating!!.toFloat()
        if (data.user.verified==true){
            iv_active.visibility=View.VISIBLE
        }
        else{
            iv_active.visibility=View.GONE
        }
        //post
        tv_post.text=data.content
        tv_date.text=data.date
        //shop bar
        num_starts.text=data.rating
        num_dollars.text=data.price+" "+data.currency
        num_bag.text=data.num_selles.toString()
        if (data.deadline!!.isNotBlank()) tv_dead_line.text=getString(R.string.dead_line,data.deadline.toInt())
        //media bar
        tv_call_price.text=data.call_price+" "+data.currency
        tv_vid_price.text=data.video_price+" "+data.currency

        //like bar
        num_likes.text=data.num_likes.toString()
        num_msgs.text=data.num_comments.toString()
        num_retweet.text=data.num_repost.toString()
        //like
        if (data.liked!!){
            like_img.imageTintList = ContextCompat.getColorStateList(applicationContext, R.color.colorAccent)
            num_likes.textColor= ContextCompat.getColor(applicationContext,R.color.colorAccent)
        }
        else{
            num_likes.textColor= ContextCompat.getColor(applicationContext,android.R.color.darker_gray)
            like_img.imageTintList = ContextCompat.getColorStateList(applicationContext, android.R.color.darker_gray)
        }
        //retweet
        if (data.reposted!!){
            retweet_img.imageTintList= ContextCompat.getColorStateList(applicationContext, R.color.colorAccent)
            num_retweet.textColor= ContextCompat.getColor(applicationContext,R.color.colorAccent)
        }
        else{
            retweet_img.imageTintList= ContextCompat.getColorStateList(applicationContext, android.R.color.darker_gray)
            num_retweet.textColor= ContextCompat.getColor(applicationContext,android.R.color.darker_gray)
        }
        if (data.in_cart==true) iv_toolbar_book_mark.visibility=View.VISIBLE


    }

    private fun goComment() {
        startActivity(Intent(applicationContext, CommentActivity::class.java).apply {
            if (mData?.type=="repost"){
                putExtra("post_id", mData?.parent_id.toString())
            }
            else {
                putExtra("post_id", mData?.id.toString())
            }
        })
    }

    private fun sendRequestRetweet() {
        if (mData?.type == "repost") {
            homeRepository.retweet(mData?.parent_id.toString())
                .subscribeWithLoading({}, {}, { handleError(it.message) }, {})
        } else {
            homeRepository.retweet(mData?.id.toString())
                .subscribeWithLoading({}, {}, { handleError(it.message) }, {})
        }
    }

    private fun sendRequestLike() {
        if (mData?.type == "repost") {
            homeRepository.like(mData?.parent_id.toString()).subscribeWithLoading({},{},{handleError(it.message)},{})

        }
        else{
            homeRepository.like(mData?.id.toString()).subscribeWithLoading({},{},{handleError(it.message)},{})
        }
    }

    private fun setupSlider() {
        mAdapter = MySliderAdapter()
        sliderView_.sliderAdapter = mAdapter


    }
}
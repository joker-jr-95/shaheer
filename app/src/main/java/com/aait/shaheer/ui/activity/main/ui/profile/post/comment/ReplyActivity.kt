package com.aait.shaheer.ui.activity.main.ui.profile.post.comment

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.post_comment_model.Comment
import com.aait.shaheer.data_layer.model.post_comment_model.Reply
import com.aait.shaheer.domain.repository.home_repository.homeRepository
import com.aait.shaheer.domain.repository.post_repository.postRepository
import com.aait.shaheer.ui.activity.main.ui.image_details.ImageDetailsActivity
import com.bumptech.glide.Glide
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.kareem.domain.core.Util
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.kareem.presetntation.base.ParentActivity
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.activity_reply.*
import kotlinx.android.synthetic.main.activity_reply.shimmer_comment
import kotlinx.android.synthetic.main.comment_item.*
import kotlinx.android.synthetic.main.comment_post_item.view.*
import kotlinx.android.synthetic.main.item_send_comment.*
import okhttp3.MultipartBody
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.textColor

class ReplyActivity : ParentActivity(),  CommentListener {
    private lateinit var mAdapter: FullReplyAdapter
    private var isLiked: Boolean = false
    private lateinit var comment: Comment
    private var id: Int=0
    private var imgPath: String? = ""
    private var mImg: MultipartBody.Part? = null
    private  var mCommentsList: MutableList<Comment> = mutableListOf()

    override val layoutResource: Int
        get() = R.layout.activity_reply

    override fun init() {
        comment=intent.getSerializableExtra("comment") as Comment
        setTitleToolbar(getString(R.string.replies))
        initRec()
        setLikeComment()
        setActions()
        setComment()
        sendRequest()
    }

    private fun setLikeComment() {
        //liked
        if (comment.liked==true){
            isLiked=true
            tv_like_comment.textColor=ContextCompat.getColor(applicationContext,R.color.colorAccent)
            tv_like_comment.text=getString(R.string.liked)

        }
        else{
            isLiked=false
            tv_like_comment.textColor=ContextCompat.getColor(applicationContext,android.R.color.darker_gray)
            tv_like_comment.text=getString(R.string.like)
        }

        if (comment.num_likes!! >0){
            like_lay.visibility=View.VISIBLE
            num_likes.text=comment.num_likes.toString()
        }
        else{
            like_lay.visibility=View.GONE
        }

    }

    private fun showShimmer() {
        shimmer_comment.visibility= View.VISIBLE
        replies_comments.visibility= View.GONE
    }

    private fun setActions() {
        card_img_comment.onClick {
            startActivity(Intent(applicationContext, ImageDetailsActivity::class.java).apply {
                putExtra("img", comment.image)
            })
        }
        iv_remove.onClick {
            mImg=null
            card_img.visibility=View.GONE
        }
        iv_send.onClick {
            if (imgPath.isNullOrEmpty()){
                if (et_comment.text.isNotBlank()) {
                    addComment(Comment(et_comment.text.toString(), getString(R.string.now), 0, imgPath))
                    sendRequestCommentReply()
                }
            }
            else{
                addComment(Comment(et_comment.text.toString(), getString(R.string.now), 0, imgPath))
                sendRequestCommentReply()
                }
        }
        iv_img_comment.onClick {
            Pix.start(this@ReplyActivity, Options.init().setRequestCode(100))
        }



        tv_like_comment.onClick {

            if (tv_like_comment.textColors == ColorStateList.valueOf(ContextCompat.getColor(applicationContext,R.color.colorAccent)))
            {
                tv_like_comment.textColor = ContextCompat.getColor(applicationContext,android.R.color.darker_gray)
                tv_like_comment.text=getString(R.string.like)
                num_likes.text=(num_likes.text.toString().toInt()-1).toString()
                if (num_likes.text.toString().toInt()==0){
                    like_lay.visibility=View.GONE
                }
            }
            else{
                tv_like_comment.textColor = ContextCompat.getColor(applicationContext,R.color.colorAccent)
                tv_like_comment.text=getString(R.string.liked)
                num_likes.text=(num_likes.text.toString().toInt()+1).toString()
                like_lay.visibility=View.VISIBLE


            }
            sendRequestLike(comment.id.toString())
        }
    }
    private fun addComment(comment:Comment) {
        mCommentsList.add(comment)
        mAdapter.addComment(comment)
        replies_comments.smoothScrollToPosition(mCommentsList.lastIndex)

    }
    private fun sendRequestCommentReply() {
        postRepository.addReply(comment.id.toString(),et_comment.text.toString(),mImg)
            .subscribeWithLoading({},{ it.data?.get(0)?.let { it1 ->
                addComment(it1)
                hideCommentHolder()
            } },{handleError(it.message!!)},{})
    }

    private fun sendRequestLike(id:String) {
        postRepository.like(id).subscribeWithLoading({},{handleLike(it.msg)},{handleError(it.message!!)},{})
    }

    private fun hideCommentHolder() {
        et_comment.text.clear()
        card_img.visibility=View.GONE
        mImg=null
    }

    private fun handleLike(msg: String?) {
        isLiked=!isLiked
    }

    private fun setComment() {
        Glide.with(applicationContext).load(comment.user?.avatar)
            .into(iv_comment)
        tv_comment_name.text = comment.user?.name
        tv_comment_content.text = comment.comment
        like_lay.visibility = View.GONE
        if (comment.num_likes!! > 0) {
            like_lay.visibility = View.VISIBLE
            num_likes.text = comment.num_likes.toString()
        }
        setLikeComment()
        if (comment.image!!.isNotBlank()) {
            card_img_comment.visibility=View.VISIBLE
            Glide.with(applicationContext).load(comment.image)
                .into(iv_img)
        }
        else{
            card_img_comment.visibility=View.GONE
        }
        tv_date_comment.text=comment.date
    }

    private fun initRec() {
        mAdapter=FullReplyAdapter(this)
        replies_comments.layoutManager=LinearLayoutManager(this)
        replies_comments.adapter=mAdapter
    }

    private fun sendRequest() {
        postRepository.commentReplies(comment.id.toString()).subscribeWithLoading({showShimmer()},{handleData(it.data)}
            ,{handleError(it.message!!)},{})
    }

    private fun handleData(data: List<Comment>?) {
        shimmer_comment.visibility= View.GONE
        replies_comments.visibility= View.VISIBLE
        if (data != null) {
            mCommentsList.addAll(data)
            mAdapter.swapData(data)
        }
    }

    private fun handleError(message: String) {
        Log.e("error",message)
        toasty(message,2)
        hideProgressDialog()
    }



    override fun onLikeComment(position: Int, item: Comment) {
        sendRequestLikeReply(item.id.toString())
    }

    private fun sendRequestLikeReply(id: String) {
        postRepository.reply_like(id).subscribeWithLoading({},{},{handleError(it.message!!)},{})
    }

    override fun onReplyComment(position: Int, item: Comment) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== Activity.RESULT_OK) {
            if (requestCode == 100) {
                card_img.visibility=View.VISIBLE
                val returnValue = data!!.getStringArrayListExtra(Pix.IMAGE_RESULTS)[0]
                mImg= Util.convertImgToFilePix("image",returnValue)
                img.setImageBitmap(BitmapFactory.decodeFile(returnValue))
                //path
                imgPath=returnValue
            }

        }
    }

}
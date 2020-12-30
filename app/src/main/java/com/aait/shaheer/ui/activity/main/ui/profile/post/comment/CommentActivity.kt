package com.aait.shaheer.ui.activity.main.ui.profile.post.comment


import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Handler
import android.text.SpannableString
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.post_comment_model.Comment
import com.aait.shaheer.domain.repository.post_repository.postRepository
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.kareem.domain.core.Util
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.kareem.presetntation.base.ParentActivity
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.item_send_comment.*
import okhttp3.MultipartBody
import org.jetbrains.anko.sdk27.coroutines.onClick


class CommentActivity : ParentActivity(), CommentListener {
    private lateinit var word: SpannableString
    private  var mention: String?=null
    private  var mCommentsList: MutableList<Comment> = mutableListOf()
    private var imgPath: String? = ""
    private var mImg: MultipartBody.Part? = null
    private var postId: String = ""
    val mAdapter=CommentAdapter(this)
    override val layoutResource: Int
        get() = R.layout.activity_comment

    override fun init() {
        setTitleToolbar(getString(R.string.comments))
        comment_rec.layoutManager=LinearLayoutManager(applicationContext)
        comment_rec.adapter=mAdapter
        postId=intent.getStringExtra("post_id")!!

        setActions()
    }

    override fun onResume() {
        super.onResume()
        sendRequest(postId)
    }

    private fun setActions() {
        iv_remove.onClick {
            mImg=null
            card_img.visibility=View.GONE
        }
        iv_send.onClick {
            if (imgPath.isNullOrEmpty()){
                if (et_comment.text.isNotBlank() ) {
                    addComment()
                    sendRequestComment()
                }
            }
            else{
                    addComment()
                    sendRequestComment()
            }


        }
        iv_img_comment.onClick {
            Pix.start(this@CommentActivity, Options.init().setRequestCode(100))
        }

    }


    private fun addComment() {
        val comment=Comment(et_comment.text.toString(),getString(R.string.now),0,imgPath)
        mCommentsList.add(comment)
        mAdapter.addComment(comment)
        comment_rec.smoothScrollToPosition(mCommentsList.lastIndex)

    }

    private fun hideCommentHolder() {
        et_comment.text.clear()
        card_img.visibility=View.GONE
        mImg=null
    }

    private fun sendRequestComment() {

        postRepository.addComment(postId,et_comment.text.toString(),mImg)
            .subscribeWithLoading({},{handleData(it.data!!)},{handleError(it.message!!)},{})
    }

    private fun handleData(data: List<Comment>) {
        hideProgressDialog()
        Log.e("size",mCommentsList.size.toString())

        mAdapter.addComment(data[0])
        hideCommentHolder()

    }

    private fun sendRequest(postId: String) {
        postRepository.getComments(postId)
            .subscribeWithLoading({showShimmer()},{ it.data?.let { it1 -> hanldeData(it1) } },{ it.message?.let { it1 ->
                handleError(
                    it1
                )
            } },{})
    }

    private fun handleError(message: String) {
        hideProgressDialog()
        toasty(message,2)
        Log.e("error",message)
    }

    private fun hanldeData(data: List<Comment>) {
        shimmer_comment.visibility= View.GONE
        comment_rec.visibility= View.VISIBLE
        mCommentsList.addAll(data)
        mAdapter.swapData(data)
    }

    private fun showShimmer() {
        shimmer_comment.visibility= View.VISIBLE
        comment_rec.visibility= View.GONE
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

    override fun onLikeComment(position: Int, item: Comment) {
        sendRequestLikeComment(item)
    }

    private fun sendRequestLikeComment(item: Comment) {
        postRepository.like(item.id.toString()).subscribeWithLoading({},{},{ it.message?.let { it1 ->
            Log.e("error",it1)

            handleError(

                it1
            )
        } },{})
    }

    override fun onReplyComment(position: Int, item: Comment) {
        //et_comment.text.clear()
        startActivity(Intent(applicationContext,ReplyActivity::class.java).apply {

            putExtra("comment",item)
        })
        //overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)


    }

}
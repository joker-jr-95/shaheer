package com.aait.shaheer.ui.activity.main.ui.profile.post

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.profile_model.Post
import com.aait.shaheer.domain.repository.home_repository.homeRepository
import com.aait.shaheer.domain.repository.profile_repository.profileRepository
import com.aait.shaheer.helper.Actions
import com.aait.shaheer.ui.activity.main.ui.other_profile.OtherProfileActivity
import com.aait.shaheer.ui.activity.main.ui.profile.post.comment.CommentActivity
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.aait.shaheer.base.BaseFragment
import com.aait.shaheer.domain.core.Constant
import com.aait.shaheer.domain.repository.chat_repository.inboxRepository
import com.aait.shaheer.domain.repository.post_repository.postRepository
import com.aait.shaheer.ui.activity.main.ui.home.ShareBottomSheet
import com.aait.shaheer.ui.activity.main.ui.home.ShareListenr
import com.kareem.domain.core.Util
import com.kareem.presetntation.helper.SocketConnection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_posts.*
import org.jetbrains.anko.toast
import org.json.JSONObject

class PostsFragment: BaseFragment(), ShareListenr {
companion object{
    var other_profile=true
    var profile_id=0
}
    private  var bottomShare: ShareBottomSheet?=null
    private var data = mutableListOf<Post>()
    private var lst = mutableListOf<Post>()
    private lateinit var linearManger: LinearLayoutManager
    val paginator= PublishProcessor.create<Int>()
    var loading = MutableLiveData<Boolean>()
    private var pageNumber = 1
    private val VISIBLE_THRESHOLD = 1

    override val viewId: Int
        get() = R.layout.fragment_posts
    private val mPostsAdapter=
        PostsAdapter { actions: Actions, post: Post ->
            when (actions) {
                Actions.LIKE -> {
                    sendRequetLike(post)
                }
                Actions.PROFILE->{
                    startActivity(Intent(activity,OtherProfileActivity::class.java).apply {
                        putExtra("profile",post.user?.id)
                    })
                }
                Actions.RETWEET -> {
                    sendRequetRetweet(post)
                }
                Actions.COMMENT -> {
                    startActivity(Intent(activity,CommentActivity::class.java).apply {
                        if (post.type=="repost"){
                            putExtra("post_id", post.parent_id.toString())
                        }
                        else {
                            putExtra("post_id", post.id.toString())
                        }
                    })
                }

                Actions.SHARE -> {
                    bottomShare= ShareBottomSheet(post,this)
                    bottomShare!!.show(fragmentManager!!,"asd")
                }
                else -> {
                }
            }
        }

    private fun sendRequetRetweet(post: Post) {
        if (post.type == "repost") {
            homeRepository.retweet(post.parent_id.toString())
                .subscribeWithLoading({}, {}, { handleError(it) }, {})
        } else {
            homeRepository.retweet(post.id.toString())
                .subscribeWithLoading({}, {}, { handleError(it) }, {})
        }
    }

    private fun sendRequetLike(post: Post) {
        if (post.type == "repost") {
            homeRepository.like(post.parent_id.toString()).subscribeWithLoading({},{},{handleError(it)},{})

        }
        else{
        homeRepository.like(post.id.toString()).subscribeWithLoading({},{},{handleError(it)},{})
    }
    }

    private fun handleError(it: Throwable) {
        it.message?.let { it1 -> activity!!.toasty(it1,2) }
        hideProgressDialog()
        Log.e("error",it.message)
    }

    override fun init(view: View) {
        with(view){

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initRec()
        setUpLoadMoreListener()
        sendRequest()
    }

    private fun sendRequest() {
        addDisposable(
            paginator
            .onBackpressureDrop()
            .concatMap { pageNumber ->
                Log.e("key", pageNumber.toString())
                if (!other_profile) {
                    Log.e("other1", other_profile.toString())
                    return@concatMap homeRepository.myProfile(pageNumber.toString())
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                }
                else{
                    Log.e("other", other_profile.toString())
                    return@concatMap profileRepository.getProfileFlow(profile_id.toString(),pageNumber.toString())
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                }

            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe{
                showShimmer()
            }
            .doOnNext {
                loading.value = true
            }
            .doOnError {
                Log.e("error",it.message)
            }
            .subscribe {
                    resp ->
                if (resp.isSuccessful) {
                    if (resp.body()?.value=="1") {
                        if (resp.body()!!.data!!.posts!!.isNotEmpty()) {
                            showData()
                            lst = resp.body()!!.data!!.posts as MutableList<Post>
                            data.addAll(resp.body()!!.data!!.posts!!)
                            mPostsAdapter.swapData(data,fragmentManager!!)
                            loading.postValue(false)
                        }
                        else{
                            loading.postValue(false)
                            if (data.isEmpty()){
                                show_empty_hint()
                               // showError(getString(R.string.no_search_res))
                            }
                        }
                    } else {
//                        show_error()
                      //  showError(resp.body()!!.message!!)
                        loading.postValue(false)
                        Log.e("error", resp.body()!!.msg)

                    }

                }
            })

        paginator.onNext(pageNumber)

    }

    private fun show_empty_hint() {
        empty_tv.visibility=View.VISIBLE
        shimmer_load.visibility=View.GONE
        posts_rec.visibility=View.GONE
    }

    private fun showData() {
        shimmer_load.visibility=View.GONE
        empty_tv.visibility=View.GONE
        posts_rec.visibility=View.VISIBLE

    }

    private fun showShimmer() {
        shimmer_load.visibility=View.VISIBLE
        empty_tv.visibility=View.GONE
        posts_rec.visibility=View.GONE
    }

    private fun setUpLoadMoreListener() {
        posts_rec.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = linearManger.itemCount
                val lastVisibleItem = linearManger
                    .findLastVisibleItemPosition()
                Log.e("total",totalItemCount.toString()+","+(lastVisibleItem+VISIBLE_THRESHOLD).toString())
                if (!loading.value!! && dy>0 && totalItemCount <= lastVisibleItem + VISIBLE_THRESHOLD) {
                    pageNumber++
                    paginator.onNext(pageNumber)
                    loading.value = true
                }

            }
        })
        loading.observe(this, Observer {
            if (it) {
                load_progress.visibility = View.VISIBLE
            }
            else{
                load_progress.visibility = View.INVISIBLE
            }
        })
    }

    private fun initRec() {
        linearManger= LinearLayoutManager(activity)
        posts_rec.layoutManager=linearManger
        posts_rec.adapter=mPostsAdapter
    }


    override fun onCopy(post: Post) {
        //Util.copy(activity!!,post.content.toString())
    }

    override fun onShare(post: Post) {
        Util.share_compat(activity!!, Constant.POSTS_BASE+post.id.toString())
    }

    override fun onBookMark(post: Post) {
        postRepository.bookMarked(post.id.toString()).subscribeWithLoading({
            showProgressDialog()
        },{
            hideProgressDialog()
            if (it.data!!.bookmarked==true){
                activity!!.toasty(getString(R.string.bookmarked_successfully))
            }
            else{
                activity!!.toasty(getString(R.string.unbookmarked))
            }
        },{handleError(it)},{})
    }

    override fun onPersonClick(profileId: String, post: Post) {
        emitSocketMessage(
            type = "link",
            msg = post.id.toString(),
            profileId = profileId
        )
    }
    private fun emitSocketMessage(
        type: String,
        msg: String? = null,
        profileId: String
    ) {
        val jsonObject = JSONObject()
        jsonObject.put("sender_id", homeRepository.loadUser()?.id.toString())
        jsonObject.put("receiver_id", profileId)
        jsonObject.put("time_zone", inboxRepository.loadUser()?.time_zone)
        jsonObject.put("content", msg)
        jsonObject.put("type", type)
        SocketConnection.addEvent("sharePost",jsonObject)
        activity!!.toast(getString(R.string.sent_successfully))
    }
}
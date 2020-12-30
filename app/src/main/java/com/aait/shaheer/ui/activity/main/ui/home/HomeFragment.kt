package com.aait.shaheer.ui.activity.main.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.profile_model.Post
import com.aait.shaheer.data_layer.model.stories_model.Data
import com.aait.shaheer.data_layer.model.stories_model.MyStory
import com.aait.shaheer.domain.core.Constant
import com.aait.shaheer.domain.repository.chat_repository.inboxRepository
import com.aait.shaheer.domain.repository.home_repository.homeRepository
import com.aait.shaheer.domain.repository.post_repository.postRepository
import com.aait.shaheer.helper.Actions
import com.aait.shaheer.ui.activity.main.MainActivity
import com.aait.shaheer.ui.activity.main.ui.other_profile.OtherProfileActivity
import com.aait.shaheer.ui.activity.main.ui.profile.post.PostsAdapter
import com.aait.shaheer.ui.activity.main.ui.profile.post.comment.CommentActivity
import com.aait.shaheer.ui.activity.main.ui.story.StoryActivity
import com.aait.shaheer.ui.activity.main.ui.story.StoryAdapter
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.kareem.domain.core.Util
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.aait.shaheer.base.BaseFragment
import com.kareem.presetntation.helper.SocketConnection
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_posts.empty_tv
import kotlinx.android.synthetic.main.fragment_posts.load_progress
import kotlinx.android.synthetic.main.fragment_posts.posts_rec
import kotlinx.android.synthetic.main.fragment_posts.shimmer_load
import okhttp3.MultipartBody
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast
import org.json.JSONObject

class HomeFragment : BaseFragment(), ShareListenr {
    private  var bottomShare: ShareBottomSheet?=null
    override val viewId: Int
        get() = R.layout.fragment_home

    var mImgs:MutableList<MultipartBody.Part> = mutableListOf()
    private var mData: Data? = null
    private  var myStories: List<MyStory> = mutableListOf()
    private var haveStory: Boolean = false
    private lateinit var mAdapter: StoryAdapter

    private var data = mutableListOf<Post>()
    private var lst = mutableListOf<Post>()
    private lateinit var linearManger: LinearLayoutManager
    val paginator= PublishProcessor.create<Int>()
    var loading = MutableLiveData<Boolean>()
    private var pageNumber = 1
    private val VISIBLE_THRESHOLD = 1
    private val mPostsAdapter=
        PostsAdapter { actions: Actions, post: Post ->
            when (actions) {
                Actions.LIKE -> {
                    sendRequetLike(post)
                }
                Actions.PROFILE->{
                    startActivity(Intent(activity, OtherProfileActivity::class.java).apply {
                        putExtra("profile",post.user?.id)
                    })
                }
                Actions.RETWEET -> {
                    sendRequetRetweet(post)
                }
                Actions.COMMENT -> {
                    startActivity(Intent(activity, CommentActivity::class.java).apply {
                        if (post.type=="repost"){
                            putExtra("post_id", post.parent_id.toString())
                        }
                        else {
                            putExtra("post_id", post.id.toString())
                        }
                    })
                }
                Actions.SHARE -> {
                 bottomShare=ShareBottomSheet(post,this)
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
        it.message?.let { it1 -> activity?.toasty(it1,2) }
        hideProgressDialog()
        Log.e("error",it.message)
    }


    override fun init(view: View) {

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        MainActivity.current_state.value= MainActivity.HOME

        initRec()
        setUpLoadMoreListener()

        setActions()
        setRec()


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sendRequest()
        sendRequestStories()
    }

    private fun setActions() {
        iv_add_story.onClick {
            if (myStories.isNotEmpty()){
                startActivity(Intent(activity, StoryActivity::class.java).apply {
                    putExtra("my_stories",mData)
                })
            }
            else {
                Pix.start(
                    this@HomeFragment,
                    Options.init()
                        .setCount(4)
                        .setExcludeVideos(true)
                        .setRequestCode(200)
                )
            }
        }
    }


    private fun sendRequest() {

        addDisposable(
            paginator
            .onBackpressureDrop()
            .concatMap { pageNumber ->
                Log.e("key", pageNumber.toString())
                                return@concatMap homeRepository.home(pageNumber.toString())
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())


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
                            mPostsAdapter.swapData(data,fragmentManager!!,true)

                            loading.postValue(false)
                        }
                        else{
                            loading.postValue(false)
                            if (data.isEmpty()){

                                show_empty_hint()
                            }
                        }
                    } else {
                        loading.postValue(false)
                        Log.e("error", resp.body()!!.msg)

                    }

                }
            }
        )
        paginator.onNext(pageNumber)
    }

    private fun show_empty_hint() {
        empty_tv.visibility=View.VISIBLE
        shimmer_load.visibility=View.GONE
        posts_rec.visibility=View.GONE
        load_progress.visibility=View.GONE
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

    private fun setRec() {
        mAdapter= StoryAdapter()
        stories_rec.layoutManager=LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
        stories_rec.adapter=mAdapter
    }

    private fun sendRequestStories() {
        addDisposable(
        homeRepository.stories()
            .subscribeWithLoading({showShimmerHoriz()}
                ,{handleStories(it.data)},{handleError(it)},{})
        )
    }

    private fun sendRequestMyStories() {
        addDisposable(
        homeRepository.stories()
            .subscribeWithLoading({/*showShimmer()*/}
                ,{ it.data?.let { it1 -> handleMyStories(it1) } },{handleError(it)},{})
        )
    }

    private fun handleMyStories(data: Data) {
        myStories=data.yourStory
        mData=data
        if (data.have_story){
            rel_bg.background=ContextCompat.getDrawable(context!!,R.drawable.story_bg)
        }
        else{
            rel_bg.background=ContextCompat.getDrawable(context!!,android.R.drawable.screen_background_light_transparent)
        }
        tv_name.text= homeRepository.loadUser()?.name
        Picasso.get().load(homeRepository.loadUser()?.avatar).into(iv_add_story)
        mAdapter.swapData(data.users)
    }


    private fun showShimmerHoriz() {
        shimmer_stories.visibility=View.VISIBLE
        main_story.visibility=View.GONE

    }

    private fun handleStories(data: Data?) {
        shimmer_stories.visibility=View.GONE
        main_story.visibility=View.VISIBLE
        mData=data
        myStories=data!!.yourStory

        if (data.have_story){
            rel_bg.background=ContextCompat.getDrawable(context!!,R.drawable.story_bg)
        }
        else{
            rel_bg.background=ContextCompat.getDrawable(context!!,android.R.drawable.screen_background_light_transparent)
        }
        tv_name.text= homeRepository.loadUser()?.name
        Picasso.get().load(homeRepository.loadUser()?.avatar).into(iv_add_story)
        mAdapter.swapData(data.users)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("res",resultCode.toString())
        if (resultCode == Activity.RESULT_OK && requestCode == 200) {
            val returnValue =
                data!!.getStringArrayListExtra(Pix.IMAGE_RESULTS)
            for (imagPath in returnValue){
                Util.convertImgToFilePix("images[]",imagPath)?.let { mImgs.add(it) }
            }

            sendRequestStory()
        }

    }


    private fun sendRequestStory() {
        homeRepository.createStory(mImgs).subscribeWithLoading({}
            ,{ it.msg?.let { it1 -> handleData(it1) } },{handleError(it)},{sendRequestMyStories()})
    }



    private fun handleData(msg: String) {
        // hideProgressDialog()
        haveStory=true
        activity?.toasty(msg)
    }

    override fun onCopy(post: Post) {
        //Util.copy(activity!!,post.content.toString())
    }

    override fun onShare(post: Post) {
        Util.share_compat(activity!!,Constant.POSTS_BASE+post.id.toString())
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
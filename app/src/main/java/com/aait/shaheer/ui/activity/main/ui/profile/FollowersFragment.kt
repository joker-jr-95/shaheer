package com.aait.shaheer.ui.activity.main.ui.profile

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.connections_model.Friend
import com.aait.shaheer.domain.repository.home_repository.homeRepository
import com.aait.shaheer.domain.repository.profile_repository.profileRepository
import com.aait.shaheer.helper.Tweet
import com.aait.shaheer.ui.activity.connect.OnFollowListener
import com.aait.shaheer.ui.activity.main.ui.profile.post.OnMenuClicked
import com.kareem.domain.core.subscribeWithLoading
import com.aait.shaheer.base.BaseFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.followers_fragment.view.*
import kotlinx.android.synthetic.main.fragment_posts.load_progress
import kotlinx.android.synthetic.main.fragment_posts.view.empty_tv
import kotlinx.android.synthetic.main.fragment_posts.view.shimmer_load

class FollowersFragment: BaseFragment(), OnFollowListener,
    OnMenuClicked {
    companion object{
        var other_profile=true
        var profile_id=0
    }

    private  var user: Friend?=null
    private var data = mutableListOf<Friend>()
    private var lst = mutableListOf<Friend>()
    private lateinit var linearManger: LinearLayoutManager
    val paginator= PublishProcessor.create<Int>()
    var loading = MutableLiveData<Boolean>()
    private var pageNumber = 1
    private val VISIBLE_THRESHOLD = 1

    private val mFollowersAdapter=FollowersAdapter(this)

    override val viewId: Int
        get() = R.layout.followers_fragment

    override fun init(view: View) {
        initRec(view)
        setUpLoadMoreListener(view)
        sendRequest(view)
    }


    override fun onFollow(item: Friend, isFav: Boolean) {

    }

    private fun sendRequest(view: View) {
        val disposable = paginator
            .onBackpressureDrop()
            .concatMap { pageNumber ->
                Log.e("key", pageNumber.toString())
                if (other_profile){
                    return@concatMap profileRepository.followers(profile_id.toString(),pageNumber.toString())
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                }
                else {
                    return@concatMap homeRepository.myFollowers(pageNumber.toString())
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                }

            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe{
                showShimmer(view)
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
                        if (!resp.body()!!.data!!.followers.isNullOrEmpty()) {
                            showData(view)
                            lst = resp.body()!!.data!!.followers as MutableList<Friend>
                            data.addAll(resp.body()!!.data!!.followers!!)
                            mFollowersAdapter.swapData(resp!!.body()!!.data!!.followers!!)
                            loading.postValue(false)
                        }
                        else{
                            loading.postValue(false)
                            if (data.isEmpty()){

                                show_empty_hint(view)
                                // showError(getString(R.string.no_search_res))
                            }
                        }
                    } else {
                        show_error()
                        //  showError(resp.body()!!.message!!)
                        loading.postValue(false)
                        Log.e("error", resp.body()!!.msg)

                    }

                }
            }
        addDisposable(disposable)
        paginator.onNext(pageNumber)


    }
    private fun show_empty_hint(view: View) {
        view.empty_tv.visibility=View.VISIBLE
        view.card.visibility=View.GONE
        view.shimmer_load.visibility=View.GONE
        view.follow_rec_lay.visibility=View.GONE
    }

    private fun showData(view: View) {
        view.shimmer_load.visibility=View.GONE
        view.empty_tv.visibility=View.GONE
        view.card.visibility=View.VISIBLE
        view.follow_rec_lay.visibility=View.VISIBLE

    }

    private fun showShimmer(view: View) {
        view.shimmer_load.visibility=View.VISIBLE
        view.empty_tv.visibility=View.GONE

        view.follow_rec_lay.visibility=View.GONE
    }

    private fun setUpLoadMoreListener(view: View) {
        view.followers_rec.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

    private fun initRec(view: View) {
        linearManger= LinearLayoutManager(activity)
        view.followers_rec.layoutManager=linearManger
        view.followers_rec.adapter=mFollowersAdapter
    }

    override fun onMenuClicked(item: Friend) {
        val bottomSheetFollowing = BottomSheetFollowing(item) { user, tweet ->
            this.user=user
            mFollowersAdapter.updatedUser(user)
            when (tweet.name) {

                Tweet.FOLLOW.name -> {
                    sendRequestFollow(item)
                }
                Tweet.MUTE.name->{
                    sendRequestMute(item)
                }
                Tweet.BLOCK.name -> {
                    sendRequestBlock(item)
                }
            }

        }
        bottomSheetFollowing.show(fragmentManager!!,"menu")
    }

    private fun sendRequestMute(item: Friend) {
        homeRepository.mute(item.id.toString())
            .subscribeWithLoading({},{},{},{})
    }

    private fun sendRequestFollow(item: Friend) {
        homeRepository.follow(item.id.toString())
            .subscribeWithLoading({},{},{},{})
    }

    private fun sendRequestBlock(item: Friend) {
        homeRepository.block(item.id.toString())
            .subscribeWithLoading({},{},{},{})
    }

    override fun onFollowClicked() {

    }

    override fun onMuteClicked() {

    }

    override fun onBlockClicked() {

    }

}
package com.aait.shaheer.ui.activity.main.ui.menu

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.blocked_model.User
import com.aait.shaheer.domain.repository.menu_repository.menuRepository
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.kareem.presetntation.base.ParentActivity
import com.kareem.presetntation.helper.setRecycler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_block_list.*
import kotlinx.android.synthetic.main.activity_block_list.empty_iv
import kotlinx.android.synthetic.main.activity_block_list.load_progress

class BlockListActivity : ParentActivity() {
    private lateinit var mAdapter: BlockAdapter
    override val layoutResource: Int
        get() = R.layout.activity_block_list

    private var data = mutableListOf<User>()
    private var lst = mutableListOf<User>()
    private lateinit var linearManger: LinearLayoutManager
    val paginator= PublishProcessor.create<Int>()
    var loading = MutableLiveData<Boolean>()
    private var pageNumber = 1
    private val VISIBLE_THRESHOLD = 1


    override fun init() {
        setTitleToolbar(getString(R.string.block_list))
        setRec()
        setUpLoadMoreListener()
        sendRequest()
    }
    private fun setRec() {
        linearManger= LinearLayoutManager(this)
         mAdapter= BlockAdapter{
            sendRequestBlock(it)
        }
        rec_block.setRecycler(applicationContext,adapter = mAdapter)
    }

    private fun sendRequestBlock(item: User) {
        menuRepository.blockUser(item.id.toString())
            .subscribeWithLoading({},{handleData(item)},{ it.message?.let { it1 -> handleError(it1) } },{})
    }

    private fun handleData(item: User) {
        mAdapter.remove(item)
    }

    private fun handleError(message: String) {
     toasty(message,2)
    }

    private fun sendRequest() {
        val disposable = paginator
            .onBackpressureDrop()
            .concatMap { pageNumber ->
                    return@concatMap menuRepository.blockList(pageNumber.toString())
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
                        if (resp.body()!!.data.users.isNotEmpty()) {
                            showData()
                            lst = resp.body()!!.data.users as MutableList<User>
                            data.addAll(resp.body()!!.data.users)
                            mAdapter.swapData(data)
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
            }
        compositeDisposable.add(disposable)
        paginator.onNext(pageNumber)

    }

    private fun show_empty_hint() {
        empty_lay.visibility=View.VISIBLE
        shimmer_load.visibility=View.GONE
        rec_block.visibility=View.GONE
    }
    private fun showData() {
        shimmer_load.visibility=View.GONE
        empty_lay.visibility=View.GONE
        rec_block.visibility=View.VISIBLE

    }

    private fun showShimmer() {
        shimmer_load.visibility=View.VISIBLE
        empty_lay.visibility=View.GONE
        rec_block.visibility=View.GONE
    }
    private fun setUpLoadMoreListener() {
        rec_block.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
}
package com.aait.shaheer.ui.activity.main.ui.menu

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.collection_model.Post
import com.aait.shaheer.domain.repository.menu_repository.menuRepository
import com.aait.shaheer.ui.activity.main.ui.services.ItemPostColldection
import com.kareem.presetntation.base.ParentActivity
import com.kareem.presetntation.helper.setRecycler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_book_marks.*

class BookMarksActivity : ParentActivity() {
    private lateinit var mAdapter: ItemPostColldection
    override val layoutResource: Int
        get() = R.layout.activity_book_marks

    private var data = mutableListOf<Post>()
    private var lst = mutableListOf<Post>()
    private lateinit var linearManger: LinearLayoutManager
    val paginator= PublishProcessor.create<Int>()
    var loading = MutableLiveData<Boolean>()
    private var pageNumber = 1
    private val VISIBLE_THRESHOLD = 1

    override fun init() {
        setTitleToolbar(getString(R.string.bookmarks))
        setRec()
        setUpLoadMoreListener()
        sendRequest()
    }

    private fun sendRequest() {
        val disposable = paginator
            .onBackpressureDrop()
            .concatMap { pageNumber ->
                return@concatMap menuRepository.bookMarkes(pageNumber.toString())
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
                        if (resp.body()!!.data.posts.isNotEmpty()) {
                            showData()
                            lst = resp.body()!!.data.posts as MutableList<Post>
                            data.addAll(resp.body()!!.data.posts)
                            mAdapter.swapData(data,supportFragmentManager)
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
        compositeDisposable.add(disposable)
        paginator.onNext(pageNumber)

    }

    private fun showData() {
        shimmer_load.visibility=View.GONE
        empty_lay.visibility=View.GONE
        rec_book_marks.visibility=View.VISIBLE

    }
    private fun show_empty_hint() {
        empty_lay.visibility=View.VISIBLE
        shimmer_load.visibility=View.GONE
        rec_book_marks.visibility=View.GONE
    }
    private fun showShimmer() {
        shimmer_load.visibility=View.VISIBLE
        empty_lay.visibility=View.GONE
        rec_book_marks.visibility=View.GONE
        load_progress.visibility=View.GONE
    }

    private fun setRec() {
        linearManger= LinearLayoutManager(this)
        mAdapter=ItemPostColldection()
        rec_book_marks.setRecycler(applicationContext,adapter = mAdapter)
    }
    private fun setUpLoadMoreListener() {
        rec_book_marks.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
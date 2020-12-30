package com.aait.shaheer.ui.activity.main.ui.inbox.chat

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.Data
import com.aait.shaheer.data_layer.model.search_model.User
import com.aait.shaheer.domain.repository.chat_repository.inboxRepository
import com.jakewharton.rxbinding2.widget.RxTextView
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.kareem.presetntation.base.ParentActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search_users.*
import kotlinx.android.synthetic.main.empty_lay.*
import java.util.concurrent.TimeUnit

class SearchUsersActivity : ParentActivity() {
    private lateinit var mAdapter: SearchUserAdapter
    override val layoutResource: Int
        get() = R.layout.activity_search_users


    lateinit var paginator: PublishProcessor<Int>
    var loading = MutableLiveData<Boolean>()
    private lateinit var layoutManager: LinearLayoutManager


    private var pageNumber = 1
    private val VISIBLE_THRESHOLD = 1

    private var isFirstTime: Boolean = true

    private  var lst: MutableList<User> = mutableListOf()
    private val data:MutableList<User> = mutableListOf()

    override fun init() {
        setTitleToolbar(getString(R.string.new_messages))
        setRec()
        setUpLoadMoreListener()
        setSearch()
    }

    @SuppressLint("CheckResult")
    private fun setSearch() {
        RxTextView.textChanges(txt_search)
            .debounce(500, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            // .filter { it.isNotBlank() && isFirstTime}
            .subscribe {
                paginator= PublishProcessor.create<Int>()
                data.clear()
                Log.e("http:",it.toString())
                if (it.isBlank() && isFirstTime){
                    isFirstTime=false
                }
                else {
                    if (it.isNotBlank()) {
                        subscribeForData(it.toString())
                    }
                }
            }


    }

    private fun subscribeForData(key: String) {
        val disposable = paginator
            .onBackpressureDrop()
            .concatMap { pageNumber ->
                Log.e("key", key)
                return@concatMap inboxRepository.searchUsers(key, pageNumber.toString())
                            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe{
                //  show_progress()
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
                    if (resp.body()!!.value=="1") {
                        if (resp.body()!!.data.users!!.isNotEmpty()) {
                            showData()
                            lst = resp.body()!!.data.users as MutableList<User>
                            data.addAll(resp.body()!!.data.users!!)
                            mAdapter.swapData(data)
                            loading.postValue(false)
                        }
                        else{
                            loading.postValue(false)
                            if (data.isEmpty()){
                                showError(getString(R.string.no_search_res))
                            }
                        }
                    } else {
//                        show_error()
                        resp.body()!!.msg?.let { getString(R.string.no_search_res) }
                        loading.postValue(false)
                        Log.e("error", resp.body()!!.msg)

                    }

                }
            }
        compositeDisposable.add(disposable)
        paginator.onNext(pageNumber)

    }
    private fun showError(msg:String) {
        shimmer_load.visibility= View.GONE
        rec_lay.visibility=View.GONE
        lay_empty.visibility=View.VISIBLE
        text.text=msg
    }

    private fun showShimmer() {
        shimmer_load.visibility= View.VISIBLE
        rec_lay.visibility=View.GONE
        lay_empty.visibility=View.GONE
    }
    private fun showData() {
        shimmer_load.visibility= View.GONE
        rec_lay.visibility=View.VISIBLE
        lay_empty.visibility=View.GONE
    }
    private fun setRec() {
         mAdapter=SearchUserAdapter{
            sendRequestConnect(it.id)
        }
        layoutManager=LinearLayoutManager(this)
        rv_items.layoutManager=layoutManager
        rv_items.adapter=mAdapter
    }

    private fun sendRequestConnect(id: Int?) {
        inboxRepository.openChat(id.toString()).subscribeWithLoading({showProgressDialog()},{handleData(it.data)},{handleError(it.message)},{})
    }

    private fun handleData(data: Data?) {
        hideProgressDialog()
        startActivity(Intent(applicationContext,ChatActivity::class.java).apply {
            putExtra("id",data?.conversation_id.toString())
        })
    }

    private fun handleError(message: String?) {
        hideProgressDialog()
        message?.let { toasty(it,2) }
    }

    private fun setUpLoadMoreListener() {
        rv_items.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager
                    .findLastVisibleItemPosition()
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
package com.aait.shaheer.ui.activity.main.ui.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.profile_model.Post
import com.aait.shaheer.data_layer.model.search_model.User
import com.aait.shaheer.domain.repository.chat_repository.inboxRepository
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jakewharton.rxbinding2.widget.RxTextView
import com.aait.shaheer.domain.core.Constant
import com.aait.shaheer.domain.core.Constant.POSTS_BASE
import com.aait.shaheer.helper.KeyBoardHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kareem.domain.core.Util
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search_users.*
import kotlinx.android.synthetic.main.bottom_sheet_share.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.concurrent.TimeUnit

class ShareBottomSheet(val post: Post,val listener:ShareListenr) :BottomSheetDialogFragment() {
    private lateinit var mAdapter: ShareUserAdapter
    lateinit var paginator: PublishProcessor<Int>
    protected  var compositeDisposable: CompositeDisposable = CompositeDisposable()
    val link=POSTS_BASE+post.id.toString()

    var loading = MutableLiveData<Boolean>()
    private lateinit var layoutManager: GridLayoutManager

    private  var lst: MutableList<User> = mutableListOf()
    private val data:MutableList<User> = mutableListOf()

    private var pageNumber = 1
    private val VISIBLE_THRESHOLD = 1

    private var isFirstTime: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.MyBottomSheetDialogTheme)

    }
    override fun setupDialog(dialog: Dialog, style: Int) {
        val view = View.inflate(context, R.layout.bottom_sheet_share, null)
        dialog.setContentView(view)
        KeyBoardHelper(activity!!,view.rootView)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setRec(view)
        setUpLoadMoreListener(view)
        setSearch(view)
        setActions(view)

    }





    private fun setActions(view: View) {
        with(view) {
            iv_copy.onClick {
                Util.copy(activity!!,link)
            }
            iv_share.onClick {
                listener.onShare(post)
            }
            iv_bookmark.onClick {
                listener.onBookMark(post)
            }

        }
    }

    private fun setUpLoadMoreListener(view: View) {
        with(view) {
            rv_users.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItem = layoutManager
                        .findLastVisibleItemPosition()
                    if (!loading.value!! && dy > 0 && totalItemCount <= lastVisibleItem + VISIBLE_THRESHOLD) {
                        pageNumber++
                        paginator.onNext(pageNumber)
                        loading.value = true
                    }

                }
            })
            loading.observe(activity!!, Observer {
                if (it) {
                    load_progress.visibility = View.VISIBLE
                } else {
                    load_progress.visibility = View.INVISIBLE
                }
            })
        }
    }

    private fun setRec(view: View) {
        with(view) {
            mAdapter = ShareUserAdapter {
                listener.onPersonClick(it.id.toString(), post)
            }
            layoutManager = GridLayoutManager(activity, 3)
            rv_users.layoutManager = layoutManager
            rv_users.adapter = mAdapter
        }
    }

    @SuppressLint("CheckResult")
    private fun setSearch(view: View) {
        with(view) {
            RxTextView.textChanges(this.txt_search)
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                // .filter { it.isNotBlank() && isFirstTime}
                .subscribe {
                    paginator = PublishProcessor.create<Int>()
                    data.clear()
                    Log.e("http:", it.toString())
                    if (it.isBlank() && isFirstTime) {
                        isFirstTime = false
                    } else {
                        if (it.isNotBlank()) {
                            subscribeForData(it.toString(),view)
                        }
                    }
                }
        }

    }
    private fun subscribeForData(key: String, view: View) {
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
               // showShimmer()
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
                            showData(view)
                            lst = resp.body()!!.data.users as MutableList<User>
                            data.addAll(resp.body()!!.data.users!!)
                            mAdapter.swapData(data)
                            loading.postValue(false)
                        }
                        else{
                            loading.postValue(false)
                            if (data.isEmpty()){
                                showError(view,getString(R.string.no_search_res))
                            }
                        }
                    } else {
                        showError(view,getString(R.string.no_search_res))
                        resp.body()!!.msg?.let { getString(R.string.no_search_res) }
                        loading.postValue(false)
                        Log.e("error", resp.body()!!.msg)

                    }

                }
            }
        compositeDisposable.add(disposable)
        paginator.onNext(pageNumber)

    }

    private fun showError(view: View,msg: String) {
        with(view) {
            rec_lay.visibility=View.GONE
            lay_empty_search.visibility=View.VISIBLE
            text.text=msg
        }
    }

    private fun showData(view: View) {
        with(view) {
            rec_lay.visibility=View.VISIBLE
            lay_empty_search.visibility=View.GONE

        }
    }

}
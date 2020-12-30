package com.aait.shaheer.ui.activity.main.ui.inbox

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.notification_model.Notification
import com.aait.shaheer.domain.repository.chat_repository.inboxRepository
import com.aait.shaheer.helper.SwipeHelper
import com.aait.shaheer.ui.activity.main.ui.inbox.chat.NotificationAdapter
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.aait.shaheer.base.BaseFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.android.synthetic.main.fragment_notification.load_progress

class NotificationFragment : BaseFragment() {
   // private val compositeDisposable = CompositeDisposable()
    private val paginator = PublishProcessor.create<Int>()
    var loading = MutableLiveData<Boolean>()
    private var pageNumber = 1
    private val VISIBLE_THRESHOLD = 1
 private lateinit var linearManger: LinearLayoutManager


 private  var mAdapter = NotificationAdapter()

    private  var lst: MutableList<Notification> = mutableListOf()
    private val data:MutableList<Notification> = mutableListOf()

    override val viewId: Int
        get() = R.layout.fragment_notification

    override fun init(view: View) {

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
            return@concatMap inboxRepository.getNotifications(pageNumber.toString())
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
          }
          .observeOn(AndroidSchedulers.mainThread())
          .doOnSubscribe{
            show_progress()
          }
          .doOnNext {
           loading.value = true
          }
          .doOnError {
           show_error()
           Log.e("error",it.message)
          }
          .subscribe {
           resp ->
           if (resp.isSuccessful) {
            if (resp.body()?.value=="1") {
             if (!resp.body()!!.data.notifications.isNullOrEmpty()) {
              //showData(view)
             show_success_results()
              lst = resp.body()!!.data.notifications as MutableList<Notification>
              data.addAll(resp.body()!!.data.notifications)
              mAdapter.swapData(resp.body()!!.data.notifications as MutableList<Notification>)
              loading.postValue(false)
             }
             else{
              loading.postValue(false)
              if (data.isEmpty()){
                  main_recycler.visibility=View.GONE
                  lay_shimmer.visibility=View.GONE
                  lay_empty.visibility=View.VISIBLE
              // show_empty()
              }
             }
            } else {
             show_error()
             loading.postValue(false)
             Log.e("error", resp.body()!!.msg)

            }

           }
          }
     )
  paginator.onNext(pageNumber)


 }
 private fun initRec() {
  linearManger= LinearLayoutManager(activity)
  main_recycler.layoutManager=linearManger
  main_recycler.adapter=mAdapter

     object : SwipeHelper(context, main_recycler) {
         override fun instantiateUnderlayButton(
             viewHolder: RecyclerView.ViewHolder?,
             underlayButtons: MutableList<UnderlayButton>?
         ) {
             underlayButtons!!.add(
                 UnderlayButton(context, getString(R.string.delete),
                     R.drawable.delete_order,
                     Color.parseColor("#FF3c30"),
                     object : UnderlayButtonClickListener {
                         override fun onClick(pos: Int) {
                             sendRequestRemove(data[pos].id.toString())
                             mAdapter.removeItem(pos)
                         }
                     }

                 ))
         }

     }
 }

    private fun sendRequestRemove(id: String) {
        inboxRepository.
            deleteNotification(id).subscribeWithLoading({},{},{handleError(it.message)},{})
    }

    private fun handleError(message: String?) {
        hideProgressDialog()
        message?.let { activity?.toasty(it,2) }
    }

    private fun setUpLoadMoreListener() {
  main_recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
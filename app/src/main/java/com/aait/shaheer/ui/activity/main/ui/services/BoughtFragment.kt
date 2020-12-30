package com.aait.shaheer.ui.activity.main.ui.services

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.service_model.Data
import com.aait.shaheer.data_layer.model.service_model.InprogressOrder
import com.aait.shaheer.domain.repository.home_repository.homeRepository
import com.aait.shaheer.domain.repository.order_repository.orderRepository
import com.aait.shaheer.helper.ORDER
import com.aait.shaheer.helper.SERVICE
import com.aait.shaheer.ui.activity.main.ui.dialog.MyDialogFragment
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.aait.shaheer.base.BaseFragment
import com.aait.shaheer.domain.repository.chat_repository.inboxRepository
import com.aait.shaheer.ui.activity.main.ui.inbox.chat.ChatActivity
import kotlinx.android.synthetic.main.fragment_bought.*
import kotlinx.android.synthetic.main.fragment_bought.view.canceled_lay
import kotlinx.android.synthetic.main.fragment_bought.view.canceled_rec
import kotlinx.android.synthetic.main.fragment_bought.view.canceled_tv
import kotlinx.android.synthetic.main.fragment_bought.view.completed_lay
import kotlinx.android.synthetic.main.fragment_bought.view.completed_rec
import kotlinx.android.synthetic.main.fragment_bought.view.completed_tv
import kotlinx.android.synthetic.main.fragment_bought.view.empty_lay
import kotlinx.android.synthetic.main.fragment_bought.view.inprogress_lay
import kotlinx.android.synthetic.main.fragment_bought.view.inprogress_tv
import kotlinx.android.synthetic.main.fragment_bought.view.main_lay
import kotlinx.android.synthetic.main.fragment_bought.view.progress_rec
import kotlinx.android.synthetic.main.fragment_bought.view.requested_lay
import kotlinx.android.synthetic.main.fragment_bought.view.requested_rec
import kotlinx.android.synthetic.main.fragment_bought.view.requested_tv
import kotlinx.android.synthetic.main.fragment_bought.view.shimmer_load

class BoughtFragment : BaseFragment() {
    private lateinit var mView_: View
    private lateinit var dialog: MyDialogFragment

    override val viewId: Int
        get() = R.layout.fragment_bought

    override fun init(view: View) {

    }

    private val mRequestedAdapter=ItemServiceBoughtAdapter(SERVICE.ORDERED){ order: ORDER, item ->
        when(order){
            ORDER.ACCEPT->{sendRequestAccept(item)}
            ORDER.RETWEET->{sendRequestRetweet(item)}
            ORDER.CANCEL_ORDER->{ showCancelBottom(item) }
            ORDER.WITH_DRAW->{ showCancelBottom(item) }
            ORDER.FINISH->{sendRequestFinish(item)}
            ORDER.RATE->{showRate(item)}
            ORDER.DELETE->{ showDeleteDialog(item) }
            ORDER.CHAT->{
                sendRequestConnect(item.provider?.id)
                //go chat
            }
            else -> { hanldeReport(item)}
        }
    }

    private fun sendRequestConnect(id: Int?) {
        inboxRepository.openChat(id.toString()).subscribeWithLoading({showProgressDialog()},{handleData(it.data)},{ it.message?.let { it1 ->
            handleError(it1)
        } },{})
    }

    private fun handleData(data: com.aait.shaheer.data_layer.model.Data?) {
        hideProgressDialog()
        startActivity(Intent(activity,ChatActivity::class.java).apply {
            putExtra("id",data?.conversation_id.toString())
        })
    }


    private fun hanldeReport(item: InprogressOrder) {
        //report
        val report=BottomSheetReport(item,false)
        report.show(fragmentManager!!,"")
    }

    private fun showCancelBottom(item: InprogressOrder) {
        //TODO {BOUGHT}
        val cancel=BottomSheetCancel(item,false){
            handleCancel(item)
        }
        cancel.show(fragmentManager!!,"")

    }

    private fun showDeleteDialog(item: InprogressOrder) {
        dialog= MyDialogFragment(getString(R.string.sure_delete_order),getString(R.string.delete_order),getString(R.string.cancel)){
            if (it== MyDialogFragment.ACTION){
                mCancelAdapter.removeData(item)
                val cancel_num=canceled_tv.text.toString().toInt()-1
                canceled_tv.text=cancel_num.toString()
                sendRequestDelete(item.id!!)
            }
            else{
                dialog.dismiss()
            }
        }
        dialog.show(fragmentManager!!,"delete")
    }

    private fun sendRequestDelete(id: Int) {
        orderRepository.deleteOrder(id.toString())
            .subscribeWithLoading({},{},{ it.message?.let { it1 -> handleError(it1) } },{})
    }


    private fun showRate(item: InprogressOrder) {
        val bottomSheetRate=BottomSheetRate(item,false)
        bottomSheetRate.show(fragmentManager!!,"rate")
    }

    private fun sendRequestFinish(item: InprogressOrder) {
        orderRepository.finishOrder(item.id.toString()).subscribeWithLoading({showProgressDialog()},{handleFinish(it.msg,item)},{ it.message?.let { it1 ->
            handleError(it1)
        } },{})
    }


    private fun handleFinish(
        msg: String?,
        item: InprogressOrder
    ) {
        hideProgressDialog()
        val progress_num=inprogress_tv.text.toString().toInt()-1
        val completed_num=completed_tv.text.toString().toInt()+1
        completed_lay.visibility=View.VISIBLE
        inprogress_tv.text=progress_num.toString()
        completed_tv.text=completed_num.toString()
        mCompletedAdapter.addData(item)
        mProgressAdapter.removeData(item)
        msg?.let { activity?.toasty(it) }
    }


    private fun handleCancel(item: InprogressOrder) {
        mRequestedAdapter.removeData(item)
        mCancelAdapter.addData(item)
        canceled_lay.visibility=View.VISIBLE
        val canceled_num=canceled_tv.text.toString().toInt()+1
        val requested_num=requested_tv.text.toString().toInt()-1
        canceled_tv.text=canceled_num.toString()
        requested_tv.text=requested_num.toString()

    }
    private fun handleCancelProgress(item: InprogressOrder) {
        mProgressAdapter.removeData(item)
        mCancelAdapter.addData(item)
        canceled_lay.visibility=View.VISIBLE
        val canceled_num=canceled_tv.text.toString().toInt()+1
        val progress_num=inprogress_tv.text.toString().toInt()-1
        inprogress_tv.text=progress_num.toString()
        canceled_tv.text=canceled_num.toString()
    }
    private fun sendRequestRetweet(item: InprogressOrder) {
        homeRepository.retweet(item.post_id.toString())
            .subscribeWithLoading({},{ it.msg?.let { it1 -> activity?.toasty(it1) } },{
                it.message?.let { it1 -> handleError(it1) }
            },{})
    }

    private fun sendRequestAccept(item: InprogressOrder) {

        orderRepository.acceptOrder(item.id.toString())
            .subscribeWithLoading({showProgressDialog()},{handleAccept(item)},{ it.message?.let { it1 -> handleError(it1) } },{})
    }

    private fun handleAccept(item: InprogressOrder) {
        hideProgressDialog()
        mProgressAdapter.addData(item)
        mRequestedAdapter.removeData(item)
        val progress_num=inprogress_tv.text.toString().toInt()+1
        val requested_num=requested_tv.text.toString().toInt()-1
        inprogress_tv.text=progress_num.toString()
        requested_tv.text=requested_num.toString()



    }

    private val mProgressAdapter=ItemServiceBoughtAdapter(SERVICE.PROGRESS){ order: ORDER, item ->
        when(order){
            ORDER.ACCEPT->{sendRequestAccept(item)}
            ORDER.RETWEET->{sendRequestRetweet(item)}
            ORDER.CANCEL_ORDER->{
                handleCancelProgress(item)
                showCancelBottom(item)
            }
            ORDER.WITH_DRAW->{
                handleCancelProgress(item)
                showCancelBottom(item)
            }
            ORDER.FINISH->{sendRequestFinish(item)}
            ORDER.RATE->{showRate(item)}
            ORDER.DELETE->{ showDeleteDialog(item) }
            ORDER.CHAT->{
                sendRequestConnect(item.provider?.id)
                //chat
            }
            else -> { hanldeReport(item)}
        }
    }
    private val mCompletedAdapter=ItemServiceBoughtAdapter(SERVICE.COMPLETED){ order: ORDER, item ->
        when(order){
            ORDER.ACCEPT->{sendRequestAccept(item)}
            ORDER.RETWEET->{sendRequestRetweet(item)}
            ORDER.CANCEL_ORDER->{
                handleCancel(item)
                showCancelBottom(item)
            }
            ORDER.WITH_DRAW->{
                handleCancel(item)
                showCancelBottom(item)
            }
            ORDER.FINISH->{sendRequestFinish(item)}
            ORDER.RATE->{showRate(item)}
            ORDER.DELETE->{ showDeleteDialog(item) }
            ORDER.CHAT->{
                sendRequestConnect(item.provider?.id)
                //chat
            }
            else -> { hanldeReport(item)}
        }
    }
    private val mCancelAdapter=ItemServiceBoughtAdapter(SERVICE.CANCELED){ order: ORDER, item ->
        when(order){
            ORDER.ACCEPT->{sendRequestAccept(item)}
            ORDER.RETWEET->{sendRequestRetweet(item)}
            ORDER.CANCEL_ORDER->{
                handleCancel(item)
                showCancelBottom(item)
            }
            ORDER.WITH_DRAW->{
                handleCancel(item)
                showCancelBottom(item)
            }
            ORDER.FINISH->{sendRequestFinish(item)}
            ORDER.RATE->{showRate(item)}
            ORDER.DELETE->{ showDeleteDialog(item) }
            ORDER.CHAT->{
                sendRequestConnect(item.provider?.id)
                //go chat
            }
            else -> { hanldeReport(item)}
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView_=view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setRec(mView_)
        sendRequest(mView_)
    }
    private fun setRec(view: View) {
        with(view) {
            requested_rec.layoutManager = LinearLayoutManager(activity)
            requested_rec.adapter = mRequestedAdapter

            progress_rec.layoutManager = LinearLayoutManager(activity)
            progress_rec.adapter = mProgressAdapter

            completed_rec.layoutManager = LinearLayoutManager(activity)
            completed_rec.adapter = mCompletedAdapter

            canceled_rec.layoutManager = LinearLayoutManager(activity)
            canceled_rec.adapter = mCancelAdapter

        }
    }
    private fun sendRequest(view: View) {
        addDisposable(
        orderRepository.boughtOrders().subscribeWithLoading({showLoading()},{ it.data?.let { it1 ->
            handleData(
                view,
                it1
            )
        } },{ it.message?.let { it1 -> handleError(it1) } },{})
        )
    }

    private fun showLoading() {
        shimmer_load.visibility=View.VISIBLE
        main_lay.visibility=View.GONE
    }

    private fun handleError(message: String) {
        hideProgressDialog()
        activity?.toasty(message,2)
    }

    private fun handleData(view: View,data: Data) {
        shimmer_load.visibility=View.GONE
        main_lay.visibility=View.VISIBLE
        if (data.closedOrders.isEmpty()&&data.inprogressOrders.isEmpty()
            && data.openOrders.isEmpty()&& data.finishedOrders.isEmpty()
        ){
            view.empty_lay.visibility=View.VISIBLE
            view.main_lay.visibility=View.GONE
            view.shimmer_load.visibility=View.GONE

        }
        if (data.openOrders.isEmpty()){
            view.requested_lay.visibility=View.GONE
        }
        else{
            view.requested_tv.text=data.openOrders.size.toString()
            mRequestedAdapter.swapData(data.openOrders as MutableList<InprogressOrder>)

        }
        if (data.inprogressOrders.isEmpty()){
            view.inprogress_lay.visibility=View.GONE
        }
        else{
            view.inprogress_tv.text=data.inprogressOrders.size.toString()
            mProgressAdapter.swapData(data.inprogressOrders as MutableList<InprogressOrder>)

        }
        if (data.finishedOrders.isEmpty()){
            view.completed_lay.visibility=View.GONE
        }
        else{
            view.completed_tv.text=data.finishedOrders.size.toString()
            mCompletedAdapter.swapData(data.finishedOrders as MutableList<InprogressOrder>)

        }
        if (data.closedOrders.isEmpty()){
            view.canceled_lay.visibility=View.GONE
        }
        else{
            view.canceled_tv.text=data.closedOrders.size.toString()
            mCancelAdapter.swapData(data.closedOrders as MutableList<InprogressOrder>)

        }
    }
}
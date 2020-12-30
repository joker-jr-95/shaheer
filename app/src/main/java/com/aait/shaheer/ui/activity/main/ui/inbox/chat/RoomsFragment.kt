package com.aait.shaheer.ui.activity.main.ui.inbox.chat

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.rooms_model.DataItem
import com.aait.shaheer.domain.repository.chat_repository.inboxRepository
import com.aait.shaheer.domain.repository.home_repository.homeRepository
import com.aait.shaheer.helper.SwipeHelper
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.aait.shaheer.base.BaseFragment
import kotlinx.android.synthetic.main.activity_rooms.*

class RoomsFragment : BaseFragment(){
    private lateinit var mSwipe_: SwipeHelper
    private var data: List<DataItem> = mutableListOf()
    override val viewId: Int
        get() = R.layout.activity_rooms

    private val mAdapter = RoomAdapter()

    override fun init(view: View) {

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initRec()
        sendRequest()

    }

    private fun initRec() {
        chat_rec.layoutManager= LinearLayoutManager(activity)
        chat_rec.adapter = mAdapter

         mSwipe_ = object : SwipeHelper(context, chat_rec) {
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
                underlayButtons.add(
                    UnderlayButton(context, getString(R.string.delete),
                        R.drawable.blocked_white,
                        Color.parseColor("#ff9e20"),
                        object : UnderlayButtonClickListener {
                            override fun onClick(pos: Int) {
                                mAdapter.updateItem(pos)
                                sendRequestMute(data[pos].id.toString())
                                //mAdapter.removeItem(pos)

                            }
                        }

                    ))

                underlayButtons.add(
                    UnderlayButton(context, getString(R.string.delete),
                        R.drawable.flag_white,
                        Color.parseColor("#0383eb"),
                        object : UnderlayButtonClickListener {
                            override fun onClick(pos: Int) {
                                mAdapter.updateItem(pos)
                                sendRequestBlock(data[pos].seconduser.id.toString())
                            }
                        }

                    ))

            }

        }
    }

    private fun sendRequestBlock(id: String) {
        homeRepository.block(id).subscribeWithLoading({},{
            it.msg?.let { it1 -> activity?.toasty(it1) }
        },{handleError(it.message)},{})

    }

    private fun sendRequestMute(id: String) {
        inboxRepository.muteChat(id).subscribeWithLoading({},{
            it.msg?.let { it1 -> activity?.toasty(it1) }
        },{handleError(it.message)},{})
    }

    private fun sendRequestRemove(id: String) {
            inboxRepository.deleteChat(id).subscribeWithLoading({},{},{handleError(it.message)},{})
    }

    private fun handleError(message: String?) {
        message?.let { activity?.toasty(it,2) }
    }

    private fun sendRequest() {
       addDisposable( inboxRepository.rooms()
            .subscribeWithLoading({
                handleLoad()
            },{

                if (it.data.isEmpty()){
                    empty_lay.visibility=View.VISIBLE
                    shimmer_load.visibility=View.GONE
                    chat_rec.visibility=View.GONE
                }
                else {
                    empty_lay.visibility=View.GONE
                    chat_rec.visibility=View.VISIBLE
                    shimmer_load.visibility=View.GONE
                    data=it.data
                    mAdapter.swapData(it.data)
                }
            },{
                it.message?.let { it1 -> activity!!.toasty(it1,2) }
            },{

            })
       )
    }

    private fun handleLoad() {
        shimmer_load.visibility=View.VISIBLE
        chat_rec.visibility=View.GONE
        empty_lay.visibility=View.GONE
    }

}
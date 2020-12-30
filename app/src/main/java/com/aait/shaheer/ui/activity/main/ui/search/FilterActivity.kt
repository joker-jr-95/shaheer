package com.aait.shaheer.ui.activity.main.ui.search

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.categories_model.Data
import com.aait.shaheer.data_layer.model.collection_model.Post
import com.aait.shaheer.domain.repository.chat_repository.inboxRepository
import com.aait.shaheer.domain.repository.profile_repository.profileRepository
import com.aait.shaheer.helper.PRICE_BAR
import com.github.guilhe.views.SeekBarRangedView
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.kareem.presetntation.base.ParentActivity
import kotlinx.android.synthetic.main.activity_filter.*
import kotlinx.android.synthetic.main.option_bar.*
import kotlinx.android.synthetic.main.toolbar_filter.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class FilterActivity : ParentActivity() {
    private lateinit var mAdapter: TagsAdapter
    override val layoutResource: Int
        get() = R.layout.activity_filter

    companion object{
        var mData:MutableList<Post> = mutableListOf()
    }

    private var ids= mutableListOf<Int>()


    override fun init() {

        setTitleToolbar(getString(R.string.filter))
        changeSelectedItem(PRICE_BAR.MESSAGE)
        setActions()
        setRange()
        setRecServices()
        sendRequestCats()
    }

    private fun setActions() {
        range_bar.setOnSeekBarRangedChangeListener(object : SeekBarRangedView.OnSeekBarRangedChangeListener {
            override fun onChanged(view: SeekBarRangedView?, minValue: Float, maxValue: Float) {
                min_price_tv.text= minValue.toInt().toString()
                max_price_tv.text= maxValue.toInt().toString()/*toBigDecimal().setScale(2, BigDecimal.ROUND_HALF_UP).toString()*/
            }

            override fun onChanging(view: SeekBarRangedView?, minValue: Float, maxValue: Float) {

            }

        })
        arr_remove.onClick {
            startActivity(Intent(applicationContext,SearchActivity::class.java))
            finish()
        }
        btn_filter.onClick {
            sendRequestFilter()
        }
    }

    private fun sendRequestFilter() {
        inboxRepository.filter(min_price_tv.text.toString(),max_price_tv.text.toString(),rate.rating.toString(),ids)
            .subscribeWithLoading({showProgressDialog()},{handleRes(it.data)},{handleError(it.message)},{})
    }

    private fun handleRes(data: List<Post>) {
        hideProgressDialog()
        mData= data as MutableList<Post>
        startActivity(Intent(applicationContext,SearchResultActivity::class.java))
    }

    private fun sendRequestCats() {
        profileRepository.getCategories().subscribeWithLoading({showProgressDialog()},{handleData(it.data)},{handleError(it.message)},{})
    }

    private fun handleData(data: List<Data>?) {
        hideProgressDialog()
        mAdapter.swapData(data as MutableList<Data>)

    }

    private fun handleError(message: String?) {
        hideProgressDialog()
        message?.let { toasty(it,2) }
    }

    private fun setRecServices() {
        mAdapter=TagsAdapter{item: Data, isSelect: Boolean ->
            if (isSelect){
                if (!ids.contains(item.id)){
                    ids.add(item.id!!)
                }
            }
            else{

                if (ids.contains(item.id)){
                    ids.remove(item.id!!)
                }

            }
        }
        rv_services.layoutManager=LinearLayoutManager(applicationContext,LinearLayoutManager.HORIZONTAL,false)
        rv_services.adapter=mAdapter
    }

    private fun setRange() {
        max_price_tv.text=SearchActivity.max
        min_price_tv.text=SearchActivity.min
        range_bar.maxValue=SearchActivity.max.toFloat()
        range_bar.minValue=SearchActivity.min.toFloat()


        msg_lay.onClick {
            if (iv_msg.imageTintList==ContextCompat.getColorStateList(applicationContext, android.R.color.darker_gray)){
                changeSelectedItem(PRICE_BAR.MESSAGE)
            }
            else{
                val gradientDrawable = msg_lay.background as GradientDrawable
                gradientDrawable.setStroke(1,ContextCompat.getColor(applicationContext,android.R.color.darker_gray))
                iv_msg.imageTintList=ContextCompat.getColorStateList(applicationContext, android.R.color.darker_gray)
            }
        }
        call_lay.onClick {
            if (iv_call.imageTintList==ContextCompat.getColorStateList(applicationContext, android.R.color.darker_gray)){
                changeSelectedItem(PRICE_BAR.CALL)
            }
            else{
                val gradientDrawable = call_lay.background as GradientDrawable
                gradientDrawable.setStroke(1,ContextCompat.getColor(applicationContext,android.R.color.darker_gray))
                iv_call.imageTintList=ContextCompat.getColorStateList(applicationContext, android.R.color.darker_gray)
            }
        }
        video_lay.onClick {
            if (vid_iv.imageTintList==ContextCompat.getColorStateList(applicationContext, android.R.color.darker_gray)){
                changeSelectedItem(PRICE_BAR.VIDEO)
            }
            else{
                val gradientDrawable = video_lay.background as GradientDrawable
                gradientDrawable.setStroke(1,ContextCompat.getColor(applicationContext,android.R.color.darker_gray))
                vid_iv.imageTintList=ContextCompat.getColorStateList(applicationContext, android.R.color.darker_gray)
            }
        }
    }


    fun changeSelectedItem(action: PRICE_BAR){
        when(action){
            PRICE_BAR.CALL->{
                val gradientDrawable1 = call_lay.background as GradientDrawable
                gradientDrawable1.setStroke(1,ContextCompat.getColor(applicationContext,R.color.colorAccent))
                //call
                iv_call.imageTintList= ContextCompat.getColorStateList(applicationContext, R.color.colorAccent)
            }
            PRICE_BAR.VIDEO->{
                val gradientDrawable2 = video_lay.background as GradientDrawable
                gradientDrawable2.setStroke(1,ContextCompat.getColor(applicationContext,R.color.colorAccent))
                //video
                vid_iv.imageTintList= ContextCompat.getColorStateList(applicationContext, R.color.colorAccent)

            }
            else -> {
                val gradientDrawable2 = msg_lay.background as GradientDrawable
                gradientDrawable2.setStroke(1,ContextCompat.getColor(applicationContext,R.color.colorAccent))
                //video
                iv_msg.imageTintList= ContextCompat.getColorStateList(applicationContext, R.color.colorAccent)

            }
        }
    }

}
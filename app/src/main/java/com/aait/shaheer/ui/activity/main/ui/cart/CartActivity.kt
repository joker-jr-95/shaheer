package com.aait.shaheer.ui.activity.main.ui.cart

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.ItemCartItem
import com.aait.shaheer.data_layer.model.shoping_model.Data
import com.aait.shaheer.domain.repository.cart_repository.cartRepository
import com.aait.shaheer.ui.activity.main.MainActivity
import com.google.gson.Gson
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.kareem.presetntation.base.ParentActivity
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.toolbar_normal.*
import org.jetbrains.anko.sdk27.coroutines.onCheckedChange
import org.jetbrains.anko.sdk27.coroutines.onClick

class CartActivity : ParentActivity(), CartListener {
    private  var model: MutableList<Data> = mutableListOf()

    var isEmpty=false
    private lateinit var mAdapter: CartAdapter
    val ids:MutableList<Int> = mutableListOf()

    override val layoutResource: Int
        get() = R.layout.activity_cart

    var total=MutableLiveData<Float>()

    override fun init() {
        initRec()
        tv_toolbar.text=getString(R.string.cart)

        total.observe(this, Observer {
            tv_total.text=it.toString()
            if (isEmpty){
                empty_lay.visibility= View.VISIBLE
                cart_lay.visibility= View.GONE
            }
            else{
                empty_lay.visibility= View.GONE
                cart_lay.visibility= View.VISIBLE
            }
        })
        sendRequestCart()
        setActions()
    }

    private fun setActions() {
        tv_remove.onClick {
            mAdapter.removePos(ids)
            removeSelectedItemsFromList()
            if (model.isEmpty()){ isEmpty=true}
            total.value=0f
            tv_remove.isEnabled=false
            tv_remove.alpha=0.5f
            sendRequestDelete()

        }

        apply_btn.onClick {
            if (etCode.text.isNotBlank()){
                sendRequestCoupon()
            }
        }
        checkbox_all.onCheckedChange { buttonView, isChecked ->

            if (isChecked) {
                tv_remove.isEnabled=true
                tv_remove.alpha=1f
                CartAdapter.selectAll =1
                setSelectedItems()
            }
            else{
                tv_remove.isEnabled=false
                tv_remove.alpha=0.5f
                CartAdapter.selectAll =0
                ids.clear()
                CartAdapter.selectedItems.clear()
            }
            mAdapter.notifyDataSetChanged()
        }
        proceed_btn.onClick {
            if (tv_remove.alpha==1f) {
                sendRequestConfirm()
            }
            else{
                toasty(getString(R.string.at_least_one),2)
            }
        }
    }

    private fun sendRequestConfirm() {
        Log.e("items",CartAdapter.selectedItems.toString())
        var mCode:String?=null
        if (!etCode.text.toString().isNullOrBlank()){
            mCode=etCode.text.toString()
        }

        if (CartAdapter.selectedItems.isEmpty()){
            toasty(getString(R.string.select_item),2)
        }
        else {
            cartRepository.cartConfirmOrder(Gson().toJson(CartAdapter.selectedItems),mCode).subscribeWithLoading({showProgressDialog()},
                {handleData(it.msg)},{ it.message?.let { it1 -> handleError(it1) } },{})
        }
    }

    private fun handleData(msg: String?) {
        hideProgressDialog()
        msg?.let { toasty(it) }
        startActivity(Intent(applicationContext,MainActivity::class.java))
        finish()
    }

    private fun setSelectedItems() {
        ids.clear()
        for (item in model){
            ids.add(item.id!!)

        }
    }

    @SuppressLint("NewApi")
    private fun removeSelectedItemsFromList() {
        for(id in ids) {
            model.removeIf { it.id == id }

        }

    }

    private fun sendRequestCoupon() {
        cartRepository.addCoupon(etCode.text.toString())
            .subscribeWithLoading({showProgressDialog()},{handleCoupon(it.msg)},{ it.message?.let { it1 ->
                handleError(
                    it1
                )
            } },{})
    }

    private fun handleCoupon(msg: String?) {
        hideProgressDialog()
        msg?.let { toasty(it) }
    }

    private fun initRec() {
        CartAdapter.selectAll=2
         mAdapter=CartAdapter(this)
        rec_cart.layoutManager=LinearLayoutManager(this)
        rec_cart.adapter=mAdapter
    }

    private fun sendRequestCart() {
        cartRepository.getCart().subscribeWithLoading({showProgressDialog()}
            ,{handleCart(it.data)},{ it.message?.let { it1 -> handleError(it1) } },{})
    }

    private fun handleCart(model: List<Data>) {
        hideProgressDialog()
        isEmpty=model.isEmpty()
        total.value=0f
        this.model= model as MutableList<Data>
        mAdapter.swapData(model)
    }

    private fun handleError(message: String) {
        hideProgressDialog()
        toasty(message,2)
    }

    private fun sendRequestDelete(){
        cartRepository.removeItems(ids).subscribeWithLoading({},{

        },{ it.message?.let { it1 ->
            handleError(
                it1
            )
        } },{})
    }

    override fun onCallListener(
        item: Data,
        position: Int,
        checked: Boolean,
        quantity: Float
    ) {
        var mTotal:Float= total.value!!
        if (checked){
            total.value = mTotal.plus(item.call_price!!.toFloat()*quantity)
        }
        else{
            total.value = mTotal.minus(item.call_price!!.toFloat()*quantity)
        }
    }

    override fun onVideo(
        item: Data,
        position: Int,
        checked: Boolean,
        quantity: Float
    ) {
        var mTotal:Float= total.value!!
        if (checked){
             total.value= mTotal.plus(item.video_price!!.toFloat()*quantity)
        }
        else{
            total.value= mTotal.minus(item.video_price!!.toFloat()*quantity)
        }
    }

    override fun onSelectItem(
        item: Data,
        position: Int,
        select: Boolean,
        extra: Float,
        quantity: Float
    )
    {

        var mTotal: Float = total.value!!
        if (select) {
            if (!ids.contains(item.id)) {
                ids.add(item.id!!)
                tv_remove.isEnabled=true
                tv_remove.alpha=1f
            }
            total.value = mTotal.plus(((item.price!!.toFloat() + extra) * quantity))
        }
        else{
            if (ids.contains(item.id)) {
                ids.remove(item.id!!)
                if (ids.isEmpty()){
                    tv_remove.isEnabled=false
                    tv_remove.alpha=0.5f


                }
            }
            total.value = mTotal.minus(((item.price!!.toFloat() + extra) * quantity))
        }
    }

    override fun onPlus(
        item: Data,
        position: Int,
        extra: Float
    ) {
        val mTotal:Float= total.value!!
        total.value=mTotal+(item.price!!.toFloat()+extra)
    }

    override fun onMinus(
        item: Data,
        position: Int,
        extra: Float
    ) {
        val mTotal:Float= total.value!!
        total.value=mTotal-(item.price!!.toFloat()+extra)
    }


}
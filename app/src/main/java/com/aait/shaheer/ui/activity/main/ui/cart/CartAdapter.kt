package com.aait.shaheer.ui.activity.main.ui.cart

import android.graphics.drawable.GradientDrawable
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.ItemCartItem
import com.aait.shaheer.data_layer.model.shoping_model.Data
import com.aait.shaheer.helper.PRICE_BAR
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cart_item.view.*
import kotlinx.android.synthetic.main.prices_shop_bar.view.*
import org.jetbrains.anko.sdk27.coroutines.onCheckedChange
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.*

class CartAdapter(val listeners: CartListener) : RecyclerView.Adapter<CartAdapter.CartVH>() {

    private var data: MutableList<Data> = ArrayList()

    companion object Select{
         var selectAll=2
        var selectedItems:MutableList<ItemCartItem> = mutableListOf()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartVH {
        return CartVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cart_item, parent, false)
        )
    }

    override fun getItemCount() = data.size



    fun removePos(ids: MutableList<Int>){
        for(id in ids) {
            val item=data.find { it.id == id }
            val index=data.indexOf(item)
            data.removeIf { it.id == id }
            notifyItemRemoved(index)
            notifyItemRangeChanged(index,itemCount)
        }


    }


    override fun onBindViewHolder(holder: CartVH, position: Int) {
        holder.bind(data[position])
        val item=data[position]

        with(holder.itemView){
            if (selectAll==1){
                if (selectedItems.find { it.id.toString()==item.id.toString() }==null) {
                    selectedItems.add(ItemCartItem(call_check.isChecked.toString(),video_box.isChecked.toString(),item.id.toString(),
                        tv_num.text.toString()
                        ))
                }
                if (!checkbox_item.isChecked) {
                    checkbox_item.isChecked = true
                }
            }
            else {
                checkbox_item.isChecked = false
            }
            Picasso.get().load(item.category_image).into(iv_cart)
            tv_order.text=item.category_name
            tv_price.text=item.price+" "+context.getString(R.string.sar)
            tv_call_price.text=item.call_price+" "+context.getString(R.string.sar)
            tv_vid_price.text=item.video_price+" "+context.getString(R.string.sar)

            if (selectAll==2) {
                if (item.choose_call == "true") {
                    call_check.isChecked = true
                    changeSelectedItem(PRICE_BAR.CALL, holder.itemView)
                } else {
                    call_check.isChecked = false
                    val gradientDrawable = call_lay.background as GradientDrawable
                    gradientDrawable.setStroke(1, context.getColor(android.R.color.darker_gray))
                    call_check.buttonTintList =
                        ContextCompat.getColorStateList(context, android.R.color.darker_gray)
                    iv_call.imageTintList =
                        ContextCompat.getColorStateList(context, android.R.color.darker_gray)
                    tv_call_price.setTextColor(
                        ContextCompat.getColorStateList(
                            context,
                            android.R.color.darker_gray
                        )
                    )
                }
            }
            if ( selectAll==2) {
                if (item.choose_video == "true") {
                    video_box.isChecked = true
                    changeSelectedItem(PRICE_BAR.VIDEO, holder.itemView)
                } else {
                    video_box.isChecked = false
                    val gradientDrawable2 = video_lay.background as GradientDrawable
                    gradientDrawable2.setStroke(1, context.getColor(android.R.color.darker_gray))
                    video_box.buttonTintList =
                        ContextCompat.getColorStateList(context, android.R.color.darker_gray)
                    vid_iv.imageTintList =
                        ContextCompat.getColorStateList(context, android.R.color.darker_gray)
                    tv_vid_price.setTextColor(
                        ContextCompat.getColorStateList(
                            context,
                            android.R.color.darker_gray
                        )
                    )
                }

            }


            iv_min.onClick {
                if (tv_num.text.toString().toInt()>1){
                    val num =tv_num.text.toString().toInt().minus(1)
                    tv_num.text=num.toString()
                    if (checkbox_item.isChecked) {
                        var extra=0f
                        if (call_check.isChecked){
                            extra+=item.call_price!!.toFloat()
                        }
                        if (video_box.isChecked){
                            extra+=item.video_price!!.toFloat()
                        }
                        selectedItems.find { it.id.toString()==item.id.toString() }?.quantity = tv_num.text.toString()
                        listeners.onMinus(item, position,extra)
                    }

                }
            }

            call_check.onCheckedChange { buttonView, isChecked ->

                if (isChecked ){
                    changeSelectedItem(PRICE_BAR.CALL,holder.itemView)
                    if (checkbox_item.isChecked) {
                        selectedItems.find { it.id.toString()==item.id.toString() }?.choose_call = "true"
                        listeners.onCallListener(item, position,isChecked,tv_num.text.toString().toFloat())
                    }
                }
                else{
                    if (checkbox_item.isChecked) {
                        selectedItems.find { it.id.toString()==item.id.toString() }?.choose_call = "false"
                        listeners.onCallListener(item, position, isChecked,tv_num.text.toString().toFloat())
                    }
                    val gradientDrawable = call_lay.background as GradientDrawable
                    gradientDrawable.setStroke(1,context.getColor(android.R.color.darker_gray))
                    call_check.buttonTintList= ContextCompat.getColorStateList(context, android.R.color.darker_gray)
                    iv_call.imageTintList= ContextCompat.getColorStateList(context, android.R.color.darker_gray)
                    tv_call_price.setTextColor(ContextCompat.getColorStateList(context, android.R.color.darker_gray))
                }

            }
            video_box.onCheckedChange { buttonView, isChecked ->

                if (isChecked){
                    changeSelectedItem(PRICE_BAR.VIDEO,holder.itemView)
                    if (checkbox_item.isChecked) {
                        selectedItems.find { it.id.toString()==item.id.toString() }?.choose_video = "true"
                        listeners.onVideo(item, position,isChecked,tv_num.text.toString().toFloat())
                    }
                }
                else{
                    if (checkbox_item.isChecked) {
                        selectedItems.find { it.id.toString()==item.id.toString() }?.choose_video = "false"
                        listeners.onVideo(item, position, isChecked,tv_num.text.toString().toFloat())
                    }
                    val gradientDrawable2 = video_lay.background as GradientDrawable
                    gradientDrawable2.setStroke(1,context.getColor(android.R.color.darker_gray))
                    video_box.buttonTintList= ContextCompat.getColorStateList(context, android.R.color.darker_gray)
                    vid_iv.imageTintList=ContextCompat.getColorStateList(context, android.R.color.darker_gray)
                    tv_vid_price.setTextColor(ContextCompat.getColorStateList(context, android.R.color.darker_gray))

                }
            }

            iv_plus.onClick {
                val num =tv_num.text.toString().toInt().plus(1)
                tv_num.text=num.toString()
                if (checkbox_item.isChecked) {
                    var extra=0f
                    if (call_check.isChecked){
                        extra+=item.call_price!!.toFloat()
                    }
                    if (video_box.isChecked){
                        extra+=item.video_price!!.toFloat()
                    }
                    selectedItems.find { it.id.toString()==item.id.toString() }?.quantity = tv_num.text.toString()
                    listeners.onPlus(item, position,extra)
                }

            }
            checkbox_item.onCheckedChange { buttonView, isChecked ->
                var extra=0f

                if (call_check.isChecked){
                    extra+=item.call_price!!.toFloat()
                }
                if (video_box.isChecked){
                    extra+=item.video_price!!.toFloat()
                }

                if (isChecked){
                    selectedItems.add(ItemCartItem(call_check.isChecked.toString(),video_box.isChecked.toString(),item.id.toString(),tv_num.text.toString()))
                }
                else{
                    selectedItems.remove(selectedItems.find { it.id.toString()==item.id.toString() })
                }
                listeners.onSelectItem(item,position,isChecked,extra,tv_num.text.toString().toFloat())

            }

            if (position==data.lastIndex){
                view_sep.visibility=View.INVISIBLE
            }
            else{
                view_sep.visibility=View.GONE
            }

        }

    }

    private fun changeSelectedItem(action: PRICE_BAR, itemView: View) {
        with(itemView) {
            when (action) {
                PRICE_BAR.CALL -> {
                    val gradientDrawable1 = call_lay.background as GradientDrawable
                    gradientDrawable1.setStroke(1, context.getColor(R.color.colorAccent))
                    //call
                    call_check.buttonTintList =
                        ContextCompat.getColorStateList(context, R.color.colorAccent)
                    iv_call.imageTintList =
                        ContextCompat.getColorStateList(context, R.color.colorAccent)
                    tv_call_price.setTextColor(
                        ContextCompat.getColorStateList(
                            context,
                            R.color.colorAccent
                        )
                    )
                }
                PRICE_BAR.VIDEO -> {
                    val gradientDrawable2 = video_lay.background as GradientDrawable
                    gradientDrawable2.setStroke(1, context.getColor(R.color.colorAccent))
                    //video
                    video_box.buttonTintList =
                        ContextCompat.getColorStateList(context, R.color.colorAccent)
                    vid_iv.imageTintList =
                        ContextCompat.getColorStateList(context, R.color.colorAccent)
                    tv_vid_price.setTextColor(
                        ContextCompat.getColorStateList(
                            context,
                            R.color.colorAccent
                        )
                    )

                }
                else -> {
                }
            }
        }
    }


    fun swapData(data: List<Data>) {
        this.data = data as MutableList<Data>
        notifyDataSetChanged()
    }

    class CartVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Data) = with(itemView) {

            setOnClickListener {

            }
        }
    }
}
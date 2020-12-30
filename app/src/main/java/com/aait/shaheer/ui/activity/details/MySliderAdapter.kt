package com.aait.shaheer.ui.activity.details

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aait.shaheer.R
import com.aait.shaheer.ui.activity.main.ui.image_details.ImageDetailsActivity
import com.bumptech.glide.Glide
import com.smarteist.autoimageslider.SliderViewAdapter
import kotlinx.android.synthetic.main.image_slider_layout_item.view.*
import java.util.*

class MySliderAdapter : SliderViewAdapter<MySliderAdapter.MySliderVH>() {
    override fun onCreateViewHolder(parent: ViewGroup?): MySliderVH {
        return MySliderVH(
            LayoutInflater.from(parent?.context)
                .inflate(R.layout.image_slider_layout_item, parent, false)
        )
    }

    override fun getCount(): Int {
        return data.size
    }

    private var data: List<String> = ArrayList()


    override fun onBindViewHolder(holder: MySliderVH, position: Int) = holder.bind(data[position])

    fun swapData(data: List<String>) {
        this.data = data
        notifyDataSetChanged()
    }

    class MySliderVH(val itemView: View) :
        SliderViewAdapter.ViewHolder(itemView) {

        fun bind(item:String) = with(itemView) {
            Glide.with(context)
                .load(item)
                .into(iv_auto_image_slider)
            setOnClickListener {
                context.startActivity(Intent(context,ImageDetailsActivity::class.java).apply {
                    putExtra("img",item)
                })
            }
        }
    }
}

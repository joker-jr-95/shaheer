package com.aait.shaheer.ui.activity.main.ui.profile.post

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.profile_model.File
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.image_item.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.*

class ImagesAdapter(val onClickAction:(item:String)->Unit) : RecyclerView.Adapter<ImagesAdapter.ImagesVH>() {

    private var data: List<File> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesVH {
        return ImagesVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.image_item, parent, false)
        )
    }

    override fun getItemCount():Int {
        Log.e("size",data.size.toString())
        return data.size
    }

    override fun onBindViewHolder(holder: ImagesVH, position: Int) = holder.bind(data[position],onClickAction)

    fun swapData(data: List<File>) {
        this.data = data
        notifyDataSetChanged()
    }
    class ImagesVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            item: File,
            onClickAction: (item: String) -> Unit
        ) = with(itemView) {

            /*Glide.with(context).load(item.src)
                .into(iv_img)*/
            Picasso.get().load(item.src).resize(300,300).into(iv_img)
            iv_img.onClick {

                item.src?.let { it1 -> onClickAction(it1) }
            }
            setOnClickListener {

            }
        }
    }
}
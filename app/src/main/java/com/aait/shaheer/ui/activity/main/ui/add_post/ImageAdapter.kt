package com.aait.shaheer.ui.activity.main.ui.add_post

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.common.ImageModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.image_item_add_post.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.*

class ImageAdapter(val onRemove:(pos:Int,item:ImageModel)->Unit) : RecyclerView.Adapter<ImageAdapter.ImageVH>() {

    private var data: MutableList<ImageModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageVH {
        return ImageVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.image_item_add_post, parent, false)
        )
    }

    fun removeItem(pos: Int) {
        data.removeAt(pos)
        notifyItemRemoved(pos)
        notifyItemRangeChanged(pos,itemCount)


    }
    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ImageVH, position: Int) {
        holder.bind(data[position])
        holder.itemView.iv_remove.onClick {
            onRemove(position,data[position])


        }
    }

    fun swapData(data: List<ImageModel>) {
        this.data = data as MutableList<ImageModel>
        notifyDataSetChanged()
    }

    fun addImg(data: ImageModel) {
        this.data.add(data)
        notifyDataSetChanged()
    }

    class ImageVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ImageModel) = with(itemView) {
            if (item.url==null) {
                if (item.src!!.endsWith(".mp4",true)){
                    Uri.parse(item.src)
                }
                else {
                    iv_img.setImageBitmap(BitmapFactory.decodeFile(item.src))
                }
            }
            else{
                Picasso.get()
                    .load(item.url)
                    .into(iv_img)
            }
            setOnClickListener {

            }
        }
    }
}
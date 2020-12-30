package com.aait.shaheer.ui.activity.main.ui.search

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.categories_model.Data
import kotlinx.android.synthetic.main.item_tag.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.textColor
import java.util.*

class TagsAdapter(val onClickTag:(item:Data,isSelect:Boolean)->Unit) : RecyclerView.Adapter<TagsAdapter.TagsVH>() {

    private var data: MutableList<Data> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagsVH {
        return TagsVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_tag, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: TagsVH, position: Int) = holder.bind(data[position],onClickTag)

    fun swapData(data: MutableList<Data>) {
        this.data = data
        notifyDataSetChanged()
    }

    fun removeItem(pos: Int) {
        data.removeAt(pos)
        notifyItemRemoved(pos)
        notifyItemRangeChanged(pos, itemCount)


    }

    class TagsVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            item: Data,
            onClickTag: (item: Data, isSelect: Boolean) -> Unit
        ) = with(itemView) {
            service_btn.text=item.name
            service_btn.onClick {
                service_btn.textColors.defaultColor
                if (service_btn.textColors.defaultColor==context.getColor(R.color.colorAccent)) {
                    service_btn.setBackgroundDrawable(context.getDrawable(R.drawable.btn_service_unselect)!!)
                    service_btn.textColor = ContextCompat.getColor(context, android.R.color.white)
                    onClickTag(item,true)

                }
                else{
                    service_btn.setBackgroundDrawable(context.getDrawable(R.drawable.btn_service_bg)!!)
                    service_btn.textColor = ContextCompat.getColor(context, R.color.colorAccent)
                    onClickTag(item,false)
                }
            }
            setOnClickListener {

            }
        }
    }
}
package com.aait.shaheer.ui.activity.main.ui.menu

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.blocked_model.User
import com.kareem.presetntation.helper.setImg
import kotlinx.android.synthetic.main.item_blocked.view.*
import kotlinx.android.synthetic.main.suggested_item.view.iv_avatar
import kotlinx.android.synthetic.main.suggested_item.view.tv_desc
import kotlinx.android.synthetic.main.suggested_item.view.tv_job
import kotlinx.android.synthetic.main.suggested_item.view.tv_name
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.*

class BlockAdapter(val onBlock:(item:User)->Unit) : RecyclerView.Adapter<BlockAdapter.BlockVH>() {

    private var data: MutableList<User> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockVH {
        return BlockVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_blocked, parent, false)
        )
    }

    override fun getItemCount() = data.size

    fun remove(item: User){
        val index=data.indexOf(item)
        data.removeIf { item.id==it.id }
        notifyItemRemoved(index)
        notifyItemRangeChanged(index,itemCount)
    }

    override fun onBindViewHolder(holder: BlockVH, position: Int) = holder.bind(data[position],onBlock)

    fun swapData(data: MutableList<User>) {
        this.data = data
        notifyDataSetChanged()
    }

    class BlockVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            item: User,
            onBlock: (item: User) -> Unit
        ) = with(itemView) {
            context.setImg(item.avatar!!,iv_avatar)
            tv_name.text=item.name
            tv_job.text=item.category
            tv_desc.text=item.bio
            block_btn.onClick {
                onBlock(item)
            }
            setOnClickListener {

            }
        }
    }
}
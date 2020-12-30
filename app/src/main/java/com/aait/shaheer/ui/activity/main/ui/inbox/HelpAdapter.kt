package com.aait.shaheer.ui.activity.main.ui.inbox

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.helps_model.Data
import kotlinx.android.synthetic.main.item_help.view.*
import java.util.*

class HelpAdapter : RecyclerView.Adapter<HelpAdapter.HelpVH>() {

    private var data: List<Data> = ArrayList()

    private var IS_SEEN=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelpVH {
        if (IS_SEEN==0)
        return HelpVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_help, parent, false)
        )
        else{
            return HelpVH( LayoutInflater.from(parent.context)
                .inflate(R.layout.item_help_answer, parent, false))
        }
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: HelpVH, position: Int) = holder.bind(data[position])

    fun swapData(data: List<Data>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        IS_SEEN=0
        if (data[position].seen!!){
            IS_SEEN=1
        }

        return IS_SEEN
    }

    class HelpVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item:Data) = with(itemView) {
            tv_subject.text=item.message
            if (item.answer.isNullOrBlank()) {
                tv_date.text = item.created_at
            }
            else{
                tv_date.text = item.answer_at
            }
            setOnClickListener {
                context.startActivity(Intent(context,QustAnswActivity::class.java).
                    apply { putExtra("help",item) })
            }
        }
    }
}
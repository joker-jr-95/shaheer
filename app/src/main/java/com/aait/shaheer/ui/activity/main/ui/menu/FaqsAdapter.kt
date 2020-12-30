package com.aait.shaheer.ui.activity.main.ui.menu

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.faqs_model.FaqsData
import com.aait.shaheer.data_layer.model.faqs_model.FaqsModel
import kotlinx.android.synthetic.main.item_faqs.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.*

class FaqsAdapter : RecyclerView.Adapter<FaqsAdapter.FaqsVH>() {

    private var data: List<FaqsData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqsVH {
        return FaqsVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_faqs, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: FaqsVH, position: Int) = holder.bind(data[position])

    fun swapData(data: List<FaqsData>) {
        this.data = data
        notifyDataSetChanged()
    }

    class FaqsVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item:FaqsData) = with(itemView) {
            tv_quest.text=item.question
            tv_answer.text=item.answer
            tv_quest.onClick {
                if (tv_answer.visibility == View.VISIBLE) {
                    tv_answer.visibility = View.GONE
                } else {
                    tv_answer.visibility = View.VISIBLE
                }
            }
            setOnClickListener {

            }
        }
    }
}
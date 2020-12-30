package com.aait.shaheer.ui.activity.main.ui.search

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.search_history_model.History
import com.aait.shaheer.domain.repository.chat_repository.inboxRepository
import com.aait.shaheer.ui.activity.main.ui.other_profile.OtherProfileActivity
import com.kareem.domain.core.subscribeWithLoading
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_search.view.*
import java.util.*

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.SearchVH>() {

    private var isHistory: Boolean=false
    private var data: MutableList<History> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchVH {
        return SearchVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_search, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: SearchVH, position: Int) = holder.bind(data[position],isHistory,data[position]==data.last())

    fun swapData(data: MutableList<History>,isHistory: Boolean) {
        this.data = data
        this.isHistory=isHistory
        notifyDataSetChanged()
    }


    fun removeItem(pos: Int) {
        data.removeAt(pos)
        notifyItemRemoved(pos)
        notifyItemRangeChanged(pos, itemCount)


    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    class SearchVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            item: History,
            history: Boolean,
            isLast: Boolean
        ) = with(itemView) {
            if (item.image!!.isNotBlank()) Picasso.get().load(item.image).into(iv_avatar)
                tv_action.text=item.name
            if (history){
                history_iv.visibility=View.VISIBLE
            }
            else{
                history_iv.visibility=View.GONE
            }
            if (isLast){
                sep_view.visibility=View.GONE
            }
            else{
                sep_view.visibility=View.VISIBLE
            }

            setOnClickListener {
                if (history){
                    if (item.type=="user"){
                    context.startActivity(Intent(context, OtherProfileActivity::class.java).apply {
                        putExtra("profile", item.item_id)
                    })
                        }
                    else{
                        context.startActivity(Intent(context, SearchResultActivity::class.java).apply {
                            putExtra("id", item.item_id.toString())
                        })
                    }
                }
                //non history
                else{
                    if (SearchTabActivity.type==SearchTabActivity.SearchType.USER){
                        sendRequestSave(item.id!!, "user")
                        context.startActivity(Intent(context, OtherProfileActivity::class.java).apply {
                            putExtra("profile", item.id)

                        })
                    }
                    else{
                        sendRequestSave(item.id!!, "category")
                        context.startActivity(Intent(context, SearchResultActivity::class.java).apply {
                            putExtra("id", item.id.toString())
                        })
                    }
                }
            }
        }

        private fun sendRequestSave(id: Int, type: String) {
            inboxRepository.saveHistory(id.toString(),type).subscribeWithLoading({},{},{},{})
        }


    }
}
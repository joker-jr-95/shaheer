package com.aait.shaheer.ui.activity.main.ui.services

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.collection_model.Data
import kotlinx.android.synthetic.main.item_collection.view.*
import java.util.*

class CollectionAdapter(val supportFragmentManager: FragmentManager) : RecyclerView.Adapter<CollectionAdapter.CollectionVH>() {

    private var data: List<Data> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionVH {
        return CollectionVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_collection, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: CollectionVH, position: Int) = holder.bind(data[position],supportFragmentManager)

    fun swapData(data: List<Data>) {
        this.data = data
        notifyDataSetChanged()
    }

    class CollectionVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
            item: Data,
            supportFragmentManager: FragmentManager
        ) = with(itemView) {
            tv_cat.text=item.name
            tv_num.text=item.num_posts.toString()
            rec_posts.layoutManager=LinearLayoutManager(context)
            //set posts adapter
            val adapter=ItemPostColldection()
            rec_posts.adapter=adapter
            adapter.swapData(item.posts!!,supportFragmentManager)
            //action
            setOnClickListener {

            }
        }
    }
}
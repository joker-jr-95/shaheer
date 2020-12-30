package com.aait.shaheer.ui.activity.main.ui.story

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.stories_model.Viewer
import com.aait.shaheer.ui.activity.main.ui.other_profile.OtherProfileActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.viewer_adapter.view.*
import java.util.*

class ViewerAdapter : RecyclerView.Adapter<ViewerAdapter.ViewerVH>() {

    private var data: List<Viewer> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewerVH {
        return ViewerVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.viewer_adapter, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewerVH, position: Int) = holder.bind(data[position])

    fun swapData(data: List<Viewer>) {
        this.data = data
        notifyDataSetChanged()
    }

    class ViewerVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item:Viewer) = with(itemView) {
            Picasso.get().load(item.avatar).into(iv_avatar)
            tv_name.text=item.name
            if (item.verified!!){
                iv_active.visibility=View.VISIBLE
            }
            else{
                iv_active.visibility=View.GONE
            }
            setOnClickListener {
                context.startActivity(Intent(context,OtherProfileActivity::class.java).apply {
                    putExtra("profile",item.id)
                })

            }
        }
    }
}
package com.aait.shaheer.ui.activity.main.ui.story

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.stories_model.YourStory
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_story.view.*
import java.util.*

class StoryAdapter : RecyclerView.Adapter<StoryAdapter.StoryVH>() {

    private var data: List<YourStory> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryVH {
        return StoryVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_story, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: StoryVH, position: Int) = holder.bind(data[position])

    fun swapData(data: List<YourStory>) {
        this.data = data
        notifyDataSetChanged()
    }

    class StoryVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item:YourStory) = with(itemView) {
            Picasso.get().load(item.avatar).into(iv_add_story)
            tv_name.text=item.name
            if (item.seen==0){
                rel_bg.background=context.getDrawable(R.drawable.story_clear)
            }
            else{
                rel_bg.background=context.getDrawable(R.drawable.story_bg)
            }

            setOnClickListener {
                    context.startActivity(Intent(context,StoryActivity::class.java).apply {
                        putExtra("other_story",item)

                    })
            }
        }
    }
}
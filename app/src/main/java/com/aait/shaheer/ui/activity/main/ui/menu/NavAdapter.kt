package com.aait.shaheer.ui.activity.main.ui.menu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aait.shaheer.R
import com.kareem.datalayer.model.common.NavigationModel
import kotlinx.android.synthetic.main.nav_row_item.view.*


class NavAdapter(val context: Context?, val models:MutableList<NavigationModel>, val listeners: Navigation_Listeners, val onSelect:(indicator:Int)-> Unit, var indicator:Int=-1) : RecyclerView.Adapter<NavAdapter.NavViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.nav_row_item, parent, false)
        return NavViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return models.size
    }

    override fun onBindViewHolder(holder: NavViewHolder, position: Int) {
        val model = models[position]
        if (model.name==indicator){
            holder.mlay!!.setBackgroundColor(ContextCompat.getColor(context!!,android.R.color.darker_gray))
        }
        else{
            holder.mlay!!.setBackgroundColor(ContextCompat.getColor(context!!,android.R.color.transparent))
        }


        holder.title!!.setText(model.name)
        holder.icon!!.setImageResource(model.src)
        holder.mlay!!.setOnClickListener {
            when (model.name) {
                R.string.settings -> {
                    indicator=R.string.settings
                    onSelect(R.string.settings)
                    listeners.onSettings()
                    holder.mlay!!.setBackgroundColor(ContextCompat.getColor(context!!,R.color.selected_item))
                }
                R.string.bookmarks -> {
                    indicator=R.string.bookmarks
                    onSelect(R.string.bookmarks)
                    listeners.onBookMark()
                    holder.mlay!!.setBackgroundColor(ContextCompat.getColor(context!!,R.color.selected_item))
                }
                R.string.tickets -> {
                    indicator=R.string.tickets
                    onSelect(R.string.tickets)
                    listeners.onTicket()
                    holder.mlay!!.setBackgroundColor(ContextCompat.getColor(context!!,R.color.selected_item))
                }
                R.string.faqs-> {
                    indicator=R.string.faqs
                    onSelect(R.string.faqs)
                    listeners.onFaqs()
                    holder.mlay!!.setBackgroundColor(ContextCompat.getColor(context!!,R.color.selected_item))
                }
                R.string.terms->{
                    indicator=R.string.terms
                    onSelect(indicator)
                    listeners.onTerms()
                    holder.mlay!!.setBackgroundColor(ContextCompat.getColor(context!!,R.color.selected_item))
                }
                R.string.privacy -> {
                    indicator=R.string.privacy
                    onSelect(R.string.privacy)
                    listeners.onPrivacy()
                    holder.mlay!!.setBackgroundColor(ContextCompat.getColor(context!!,R.color.selected_item))
                }

                R.string.sign_out -> {
                    indicator=R.string.sign_out
                    onSelect(R.string.sign_out)
                    listeners.onLogout()
                    holder.mlay!!.setBackgroundColor(ContextCompat.getColor(context!!,R.color.selected_item))
                }
            }

            notifyDataSetChanged()
        }
    }

    class NavViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var mlay=itemView?.mainLay
        var icon=itemView?.nav_src
        var title=itemView?.nav_title
    }
}
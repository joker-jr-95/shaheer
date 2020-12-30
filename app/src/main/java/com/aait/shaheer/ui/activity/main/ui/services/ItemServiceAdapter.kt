package com.aait.shaheer.ui.activity.main.ui.services

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.service_model.InprogressOrder
import com.aait.shaheer.helper.ORDER
import com.aait.shaheer.helper.SERVICE
import com.aait.shaheer.ui.activity.details.ProductDetailsActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_servicd_requested.view.*
import kotlinx.android.synthetic.main.item_service_cancelled.view.*
import kotlinx.android.synthetic.main.item_service_completed.view.*
import kotlinx.android.synthetic.main.item_service_completed.view.bottom_lay
import kotlinx.android.synthetic.main.item_service_completed.view.btn_rate
import kotlinx.android.synthetic.main.item_service_completed.view.btn_retweet
import kotlinx.android.synthetic.main.item_service_completed.view.iv_arrow
import kotlinx.android.synthetic.main.item_service_completed.view.iv_service
import kotlinx.android.synthetic.main.item_service_completed.view.iv_service_owner
import kotlinx.android.synthetic.main.item_service_completed.view.iv_verified
import kotlinx.android.synthetic.main.item_service_completed.view.tv_hours
import kotlinx.android.synthetic.main.item_service_completed.view.tv_name_owner
import kotlinx.android.synthetic.main.item_service_completed.view.tv_service
import kotlinx.android.synthetic.main.item_service_completed.view.tv_service_price
import kotlinx.android.synthetic.main.item_service_ordered.view.*
import kotlinx.android.synthetic.main.item_service_ordered.view.btn_cancel
import kotlinx.android.synthetic.main.item_service_progress.view.*
import kotlinx.android.synthetic.main.item_service_progress.view.btn_report
import kotlinx.android.synthetic.main.item_service_progress_completed.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.*

class ItemServiceAdapter(val type: SERVICE,val onAction:(ORDER, item:InprogressOrder)->Unit) : RecyclerView.Adapter<ItemServiceAdapter.ItemVH>() {

    private var data: MutableList<InprogressOrder> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemVH {
        when(type) {
            SERVICE.ORDERED->{return ItemVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_servicd_requested, parent, false)
            )}
            SERVICE.PROGRESS->{return ItemVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_service_progress, parent, false)
            )}
            SERVICE.COMPLETED->{return ItemVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_service_completed, parent, false)
            )}
            SERVICE.CANCELED->{return ItemVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_service_cancelled, parent, false)
            )}


            else->{return ItemVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_servicd_requested, parent, false)
            )}

        }
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ItemVH, position: Int) = holder.bind(data[position],onAction)

    fun swapData(data: MutableList<InprogressOrder>) {
        this.data = data
        notifyDataSetChanged()
    }

    fun addData(item:InprogressOrder){
        data.add(item)
        notifyDataSetChanged()
    }
    fun removeData(item:InprogressOrder){
        data.remove(item)
        notifyDataSetChanged()
    }


    class ItemVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            item: InprogressOrder,
            onAction: (ORDER,InprogressOrder) -> Unit
        ) = with(itemView) {
            //titles
            Picasso.get().load(item.category_image).into(iv_service)
            tv_service.text=item.category_name

            // if sold get client data
            Picasso.get().load(item.client?.avatar).into(iv_service_owner)
            tv_name_owner.text=item.client?.name
            if (item.client?.verified!!){iv_verified.visibility=View.VISIBLE} else{iv_verified.visibility=View.GONE}
            tv_hours.text=item.date
            var price=item.price!!.toFloat()
            var call=item.call_price!!.toFloat()
            var vid=item.video_price!!.toFloat()
            if (item.choose_call=="true"||item.choose_video=="true"){
                if (item.choose_call=="true"){
                    price += call
                }
                 if (item.choose_video=="true"){
                    price+=vid
                }
                tv_service_price.text=price.toString()+" "+item.currency
            }
            else{
                tv_service_price.text=price.toString()+" "+item.currency
            }


            tv_deadline_days?.text=item.deadline


            iv_arrow.onClick {
                if (iv_arrow.rotation==0f) {
                    iv_arrow.rotation = 180f
                    bottom_lay.visibility=View.VISIBLE
                }
                else{
                    iv_arrow.rotation = 0f
                    bottom_lay.visibility=View.GONE
                }
            }
            btn_report?.onClick {onAction(ORDER.REPORT,item)   }

            btn_rate?.onClick { onAction(ORDER.RATE,item) }

            btn_retweet?.onClick { onAction(ORDER.RETWEET,item)  }

            btn_finish?.onClick {  onAction(ORDER.FINISH,item)   }

            iv_chat?.onClick { onAction(ORDER.CHAT,item) }

            btn_cancel?.onClick { onAction(ORDER.CANCEL_ORDER,item) }

            btn_accept?.onClick { onAction(ORDER.ACCEPT,item) }

            btn_delete?.onClick { onAction(ORDER.DELETE,item) }

            setOnClickListener {
                context.startActivity(Intent(context, ProductDetailsActivity::class.java).apply {
                    putExtra("post_id",item.post_id.toString())
                })
            }
        }
    }
}
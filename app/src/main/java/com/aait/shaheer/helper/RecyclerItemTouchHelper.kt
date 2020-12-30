package com.kareem.presetntation.helper

import android.graphics.Canvas
import android.view.View
import android.widget.RelativeLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/*

open class RecyclerItemTouchHelper(
    dragDirs: Int,
    swipeDirs: Int,
    private val listener: RecyclerItemTouchHelperListener
) :
    ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return true
    }

    override fun onSelectedChanged(
        viewHolder: RecyclerView.ViewHolder?,
        actionState: Int
    ) {
        if (viewHolder != null) {
            if (viewHolder is TimeAdapter.TimeVH){
            val foregroundView: RelativeLayout? = (viewHolder as TimeAdapter.TimeVH).view_foreground
            ItemTouchHelper.Callback.getDefaultUIUtil()
                .onSelected(foregroundView)
        }
            else{
                val foregroundView: RelativeLayout? = (viewHolder as RoomsAdapter.RoomsVH).view_foreground
                ItemTouchHelper.Callback.getDefaultUIUtil()
                    .onSelected(foregroundView)
            }
        }
    }

    override fun onChildDrawOver(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder?,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (viewHolder is TimeAdapter.TimeVH){
        val foregroundView: RelativeLayout? = (viewHolder as TimeAdapter.TimeVH).view_foreground
        ItemTouchHelper.Callback.getDefaultUIUtil().onDrawOver(
            c, recyclerView, foregroundView, dX, dY,
            actionState, isCurrentlyActive)
    }
        else{
            val foregroundView: RelativeLayout? = (viewHolder as RoomsAdapter.RoomsVH).view_foreground
            ItemTouchHelper.Callback.getDefaultUIUtil().onDrawOver(
                c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive)
        }
    }


    override fun clearView(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ) {
        if (viewHolder is TimeAdapter.TimeVH) {
            val foregroundView: RelativeLayout? = (viewHolder as TimeAdapter.TimeVH).view_foreground
            ItemTouchHelper.Callback.getDefaultUIUtil()
                .clearView(foregroundView)
        }
        else{
            val foregroundView: RelativeLayout? = (viewHolder as RoomsAdapter.RoomsVH).view_foreground
            ItemTouchHelper.Callback.getDefaultUIUtil()
                .clearView(foregroundView)
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (viewHolder is TimeAdapter.TimeVH) {
            val foregroundView: RelativeLayout? = (viewHolder as TimeAdapter.TimeVH).view_foreground
            ItemTouchHelper.Callback.getDefaultUIUtil().onDraw(
                c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive
            )
        }
        else{
            val foregroundView: RelativeLayout? = (viewHolder as RoomsAdapter.RoomsVH).view_foreground
            ItemTouchHelper.Callback.getDefaultUIUtil().onDraw(
                c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive
            )
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        listener.onSwiped(viewHolder, direction, viewHolder.adapterPosition)
    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {

        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    interface RecyclerItemTouchHelperListener {
        fun onSwiped(
            viewHolder: RecyclerView.ViewHolder?,
            direction: Int,
            position: Int
        )
    }

}*/

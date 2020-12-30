package com.aait.shaheer.helper

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.*

abstract class SwipeHelper(
    context: Context?,
    private val recyclerView: RecyclerView
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
    private var buttons: MutableList<UnderlayButton>?= mutableListOf()
    private lateinit var  gestureDetector: GestureDetector
    private var swipedPos = -1
    private var swipeThreshold = 0.5f
    private val buttonsBuffer: MutableMap<Int, MutableList<UnderlayButton>?>
    private var recoverQueue: Queue<Int> = LinkedList<Int>()
    private val gestureListener: SimpleOnGestureListener = object : SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            for (button in buttons!!) {
                if (button.onClick(e.x, e.y)) break
            }
            return true
        }
    }
    private val onTouchListener = OnTouchListener { view, e ->
        if (swipedPos < 0) return@OnTouchListener false
        val point =
            Point(e.rawX.toInt(), e.rawY.toInt())
        val swipedViewHolder =
            recyclerView.findViewHolderForAdapterPosition(swipedPos)
        val swipedItem = swipedViewHolder!!.itemView
        val rect = Rect()
        swipedItem.getGlobalVisibleRect(rect)
        if (e.action == MotionEvent.ACTION_DOWN || e.action == MotionEvent.ACTION_UP || e.action == MotionEvent.ACTION_MOVE) {
            if (rect.top < point.y && rect.bottom > point.y) gestureDetector.onTouchEvent(e) else {
                recoverQueue.add(swipedPos)
                swipedPos = -1
                recoverSwipedItem()
            }
        }
        false
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val pos = viewHolder.adapterPosition
        if (swipedPos != pos) recoverQueue.add(swipedPos)
        swipedPos = pos
        if (buttonsBuffer.containsKey(swipedPos)) buttons =
            buttonsBuffer[swipedPos] else buttons!!.clear()
        buttonsBuffer.clear()
        swipeThreshold = 0.5f * buttons!!.size * BUTTON_WIDTH
        recoverSwipedItem()
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return swipeThreshold
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return 0.1f * defaultValue
    }

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
        return 5.0f * defaultValue
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
        val pos = viewHolder.adapterPosition
        var translationX = dX
        val itemView = viewHolder.itemView
        if (pos < 0) {
            swipedPos = pos
            return
        }
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {
                var buffer: MutableList<UnderlayButton>? =
                    ArrayList()
                if (!buttonsBuffer.containsKey(pos)) {
                    instantiateUnderlayButton(viewHolder, buffer)
                    buttonsBuffer[pos] = buffer
                } else {
                    buffer = buttonsBuffer[pos]
                }
                translationX =
                    dX * buffer!!.size * BUTTON_WIDTH / itemView.width
                drawButtons(c, itemView, buffer, pos, translationX)
            }
        }
        super.onChildDraw(
            c,
            recyclerView,
            viewHolder,
            translationX,
            dY,
            actionState,
            isCurrentlyActive
        )
    }

    @Synchronized
    private fun recoverSwipedItem() {
        while (!recoverQueue.isEmpty()) {
            val pos = recoverQueue.poll()
            if (pos > -1) {
                recyclerView.adapter!!.notifyItemChanged(pos)
            }
        }
    }

    private fun drawButtons(
        c: Canvas,
        itemView: View,
        buffer: List<UnderlayButton>?,
        pos: Int,
        dX: Float
    ) {
        var right = itemView.right.toFloat()
        val dButtonWidth = -1 * dX / buffer!!.size
        for (button in buffer) {
            val left = right - dButtonWidth
            button.onDraw(
                c,
                RectF(
                    left,
                    itemView.top.toFloat(),
                    right,
                    itemView.bottom.toFloat()
                ),
                pos
            )
            right = left
        }
    }

    fun attachSwipe() {
        val itemTouchHelper = ItemTouchHelper(this)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    abstract fun instantiateUnderlayButton(
        viewHolder: RecyclerView.ViewHolder?,
        underlayButtons: MutableList<UnderlayButton>?
    )

    class UnderlayButton(
        private val context: Context?,
        private val text: String,
        private val imageResId: Int,
        private val color: Int,
        private val clickListener: UnderlayButtonClickListener
    ) {
        private var pos = 0
        private var clickRegion: RectF? = null
        fun onClick(x: Float, y: Float): Boolean {
            if (clickRegion != null && clickRegion!!.contains(x, y)) {
                clickListener.onClick(pos)
                return true
            }
            return false
        }

        fun onDraw(c: Canvas, rect: RectF, pos: Int) {
            val p = Paint()
            // Draw background
            p.color = color
            c.drawRect(rect, p)
            // Draw Text
            p.color = Color.WHITE
            p.textSize = 20f
            val r = Rect()
            val cHeight = rect.height()
            val cWidth = rect.width()
            p.textAlign = Paint.Align.LEFT
            p.getTextBounds(text, 0, text.length, r)
            var x =cWidth / 2f - r.width() / 2f - r.left
            var y = cHeight / 2f + r.height() / 2f - r.bottom
            //c.drawText(text, rect.left + x, rect.top + y, p)
           if (imageResId==0){
               c.drawText(text, rect.left + x, rect.top + y, p)
                /*x=cWidth/2f-r.width()/2f-r.left.toFloat()
                y=cHeight/2f-r.height()/2f-r.bottom.toFloat()
               c.drawText(text,rect.left+x,rect.top+y,p)
*/           }
            else{
                val d=ContextCompat.getDrawable(context!!,imageResId)
               val bitmap=convertToBitMap(d)
                c.drawBitmap(bitmap,(rect.left+rect.right -30)/2,
                    (rect.top+rect.bottom-40)/2,p
                    )

           }

            clickRegion = rect
            this.pos = pos
        }

        private fun convertToBitMap(d: Drawable?): Bitmap {
         if (d is BitmapDrawable) return d.bitmap
         val bitmap=Bitmap.createBitmap(d!!.intrinsicWidth,d.intrinsicHeight,Bitmap.Config.ARGB_8888)
            val canvas=Canvas(bitmap)
            d.setBounds(0,0,canvas.width,canvas.height)
            d.draw(canvas)
            return bitmap
        }

    }

    interface UnderlayButtonClickListener {
        fun onClick(pos: Int)
    }

    companion object {
        const val BUTTON_WIDTH = 150
    }

    init {
        buttons = ArrayList()
        gestureDetector = GestureDetector(context, gestureListener)
        recyclerView.setOnTouchListener(onTouchListener)
        buttonsBuffer = HashMap()
        recoverQueue = object : LinkedList<Int>() {
            override fun add(o: Int): Boolean {
                return if (contains(o)) false else super.add(o)
            }
        }
        attachSwipe()
    }
}
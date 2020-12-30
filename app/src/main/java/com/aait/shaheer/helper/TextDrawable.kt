package com.aait.shaheer.helper

import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import com.hbb20.CountryCodePicker
import java.lang.ref.WeakReference

class TextDrawable(paint: Paint?, private var mText: String?) : Drawable(), TextWatcher {
    private lateinit var ref: WeakReference<TextView?>
    private val ref2: WeakReference<CountryCodePicker>? = null
    private var mPaint: Paint
    private val mHeightBounds: Rect
    private var mBindToViewPaint = false
    private var mPrevTextSize = 0f
    private var mInitFitText = false
    private var mFitTextEnabled = false

    /*public TextDrawable(CountryCodePicker cp,String s) {
        mText = s;
        */
/*mPaint = new Paint(paint);*/ /*
        mHeightBounds = new Rect();
        initCountry(cp);
    }*/
    constructor(
        tv: TextView,
        initialText: String?,
        bindToViewsText: Boolean,
        bindToViewsPaint: Boolean
    ) : this(tv.paint, initialText) {
        ref = WeakReference(tv)
        if (bindToViewsText || bindToViewsPaint) {
            if (bindToViewsText) {
                tv.addTextChangedListener(this)
            }
            mBindToViewPaint = bindToViewsPaint
        }
    }

    /* public TextDrawable(CountryCodePicker tv, String initialText, boolean bindToViewsText, boolean bindToViewsPaint) {
        this(tv, initialText);
       */
/* ref = new WeakReference<>(tv);
        if (bindToViewsText || bindToViewsPaint) {
            if (bindToViewsText) {
                tv.addTextChangedListener(this);
            }
            mBindToViewPaint = bindToViewsPaint;
        }*/
/*
    }*/
    @JvmOverloads
    constructor(
        tv: TextView,
        bindToViewsText: Boolean = false,
        bindToViewsPaint: Boolean = false
    ) : this(tv, tv.text.toString(), bindToViewsText, bindToViewsPaint) {
    }

    constructor(tv: TextView, s: String?) : this(tv, s, false, false) {}

    override fun draw(canvas: Canvas) {
        if (mBindToViewPaint && ref.get() != null) {
            val p: Paint = ref.get()!!.paint
            canvas.drawText(mText!!, 0f, bounds.height().toFloat(), p)
        } else {
            if (mInitFitText) {
                fitTextAndInit()
            }
            canvas.drawText(mText!!, 0f, bounds.height().toFloat(), mPaint)
        }
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mPaint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        val alpha = mPaint.alpha
        return if (alpha == 0) {
            PixelFormat.TRANSPARENT
        } else if (alpha == 255) {
            PixelFormat.OPAQUE
        } else {
            PixelFormat.TRANSLUCENT
        }

    }

    private fun init() {
        val bounds = bounds
        //We want to use some character to determine the max height of the text.
//Otherwise if we draw something like "..." they will appear centered
//Here I'm just going to use the entire alphabet to determine max height.
        mPaint.getTextBounds(
            "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz",
            0,
            1,
            mHeightBounds
        )
        mPaint.color=Color.BLACK
        val width = mPaint.measureText(mText)
        bounds.top = mHeightBounds.top
        bounds.bottom = mHeightBounds.bottom
        bounds.right = width.toInt()
        bounds.left = 0
        setBounds(bounds)
    }

    private fun initCountry(cp: CountryCodePicker) {
        val bounds = bounds
        //We want to use some character to determine the max height of the text.
//Otherwise if we draw something like "..." they will appear centered
//Here I'm just going to use the entire alphabet to determine max height.
//cp.getTextBounds("1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", 0, 1, mHeightBounds);
        val width = mPaint.measureText(mText)
        bounds.top = mHeightBounds.top
        bounds.bottom = mHeightBounds.bottom
        bounds.right = width.toInt()
        bounds.left = 0
        setBounds(bounds)
    }

    //Since this can change the font used, we need to recalculate bounds.
    var paint: Paint?
        get() = mPaint
        set(paint) {
            mPaint = Paint(paint)
            //Since this can change the font used, we need to recalculate bounds.
            if (mFitTextEnabled && !mInitFitText) {
                fitTextAndInit()
            } else {
                init()
            }
            invalidateSelf()
        }

    //Since this can change the bounds of the text, we need to recalculate.
    var text: String?
        get() = mText
        set(text) {
            mText = text
            //Since this can change the bounds of the text, we need to recalculate.
            if (mFitTextEnabled && !mInitFitText) {
                fitTextAndInit()
            } else {
                init()
            }
            invalidateSelf()
        }

    override fun beforeTextChanged(
        s: CharSequence,
        start: Int,
        count: Int,
        after: Int
    ) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable) {
        text = s.toString()
    }

    /**
     * Make the TextDrawable match the width of the View it's associated with.
     *
     *
     * Note: While this option will not work if bindToViewPaint is true.
     *
     * @param fitText
     */
    fun setFillText(fitText: Boolean) {
        mFitTextEnabled = fitText
        if (fitText) {
            mPrevTextSize = mPaint.textSize
            if (ref.get() != null) {
                if (ref.get()!!.width > 0) {
                    fitTextAndInit()
                } else {
                    mInitFitText = true
                }
            }
        } else {
            if (mPrevTextSize > 0) {
                mPaint.textSize = mPrevTextSize
            }
            init()
        }
    }

    private fun fitTextAndInit() {
        val fitWidth = ref.get()!!.width.toFloat()
        val textWidth = mPaint.measureText(mText)
        val multi = fitWidth / textWidth
        mPaint.textSize = mPaint.textSize * multi

        mInitFitText = false
        init()
    }

    init {
        mPaint = Paint(paint)
        mHeightBounds = Rect()
        init()
    }
}
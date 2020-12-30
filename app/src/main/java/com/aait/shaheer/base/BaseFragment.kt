package com.aait.shaheer.base

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.TextView
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.aait.shaheer.R
import com.facebook.shimmer.ShimmerFrameLayout
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.base_recycler.*
import org.jetbrains.anko.findOptional


abstract class BaseFragment : Fragment() {

    protected fun addDisposable(disposable: Disposable) = compositeDisposable.add(disposable)
    protected fun dispose() = compositeDisposable.dispose()
    private var mShimmer: ShimmerFrameLayout? = null
    protected  var compositeDisposable: CompositeDisposable = CompositeDisposable()

    internal fun onSwipe(onSwipeBack:()-> Unit){
        swipe?.setOnRefreshListener {
            swipe!!.isNestedScrollingEnabled=false
            swipe!!.isRefreshing=false
            onSwipeBack()

        }
    }

    fun showProgressDialog(message: String?=null) {
        progressDialog = ACProgressFlower.Builder(activity)
            .direction(ACProgressConstant.DIRECT_CLOCKWISE)
            .themeColor(Color.GRAY)
            .fadeColor(Color.WHITE)
            .build()
        progressDialog!!.show()
    }



    internal abstract val viewId: Int
    internal fun show_progress() {
        if (mainLay != null) {
            mainLay!!.visibility = View.VISIBLE
        }
        //mProg!!.visibility = View.VISIBLE
        lay_progress?.visibility=View.VISIBLE
        mShimmer?.visibility=View.VISIBLE
        lay_no_internet?.visibility=View.GONE
        lay_empty?.visibility=View.GONE
        swipe?.visibility=View.GONE
        /*mConn= this.findOptional(R.id.lay_no_internet)
        mEmpty= this.findOptional(R.id.lay_empty)
        mSwipe= this.findOptional(R.id.swipe)
        mConn!!.visibility = View.GONE
        mEmpty!!.visibility = View.GONE*/
    }

    internal fun show_success_results() {
        if (mainLay != null) {
            mainLay!!.visibility = View.GONE
        }
        /*mProg!!.visibility = View.GONE
        mConn!!.visibility = View.GONE
        mEmpty!!.visibility = View.GONE*/
        lay_progress?.visibility=View.GONE
        lay_no_internet?.visibility=View.GONE
        lay_empty?.visibility=View.GONE
        swipe?.visibility=View.GONE
        mShimmer?.visibility=View.GONE
    }

    internal fun show_error() {
        if (mainLay != null) {
            mainLay!!.visibility = View.VISIBLE
        }
        /*mProg!!.setVisibility(View.GONE)
        mConn!!.visibility = View.VISIBLE
        mEmpty!!.visibility = View.GONE*/
        lay_progress?.visibility=View.GONE
        lay_no_internet?.visibility=View.VISIBLE
        lay_empty?.visibility=View.GONE
        swipe?.visibility=View.GONE
        mShimmer?.visibility=View.GONE
    }


    internal fun show_empty() {
        if (mainLay != null) {
            mainLay!!.visibility = View.VISIBLE
        }/*
        mProg!!.visibility = View.GONE
        mConn!!.visibility = View.GONE
        mEmpty!!.visibility = View.VISIBLE*/
        lay_progress?.visibility=View.GONE
        lay_no_internet?.visibility=View.GONE
        lay_empty?.visibility=View.VISIBLE
        swipe?.visibility=View.GONE
        mShimmer?.visibility=View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(viewId, container, false)
        // globalPreferences = new GlobalPreferences(getActivity());
        this.savedInstanceState = savedInstanceState
        //toaster = new Toaster(getActivity());
        //customeProgressDialog = new CustomeProgressDialog(getActivity(), "");
        initViews(view)
        init(view)
        //calligrapher = new Calligrapher(getActivity());
        //calligrapher.setFont(getActivity(),"fonts/cairo_regular.ttf",true);
        return view
    }

     // var mProg: RelativeLayout?=null
      var savedInstanceState: Bundle? = null
    private var progressDialog: ACProgressFlower? = null
      /*var mEmpty: RelativeLayout?=null
      var mSwipe: SwipeRefreshLayout?=null
      var mConn: RelativeLayout?=null
*/
      var mainLay: RelativeLayout?=null

    private fun initViews(view: View) {
       with(view){
           /*mConn= this.findOptional(R.id.lay_no_internet)
         mEmpty= this.findOptional(R.id.lay_empty)
         mSwipe= this.findOptional(R.id.swipe)*/
      //   mainLay= this.findOptional(R.id.main_lay)
      //  mProg= this.findOptional(R.id.lay_progress)
         mShimmer= this.findOptional(R.id.lay_shimmer)
       }

    }






    override fun onDestroy() {
        dispose()
        super.onDestroy()
    }
    internal abstract fun init(view: View)

    internal fun SetTitle():String {
        return ""
    }

    fun makeSnack(view: View, msg: Int) {
        var snackbar: Snackbar? = null
        val finalSnackbar = snackbar

        snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_INDEFINITE).setAction("تم") {
            //  finalSnackbar.dismiss();
        }
        snackbar.setActionTextColor(context!!.resources.getColor(R.color.colorPrimary))
        // Changing action button text color
        val sbView = snackbar.view
        val textView = sbView.findViewById<View>(R.id.snackbar_text) as TextView
        textView.setTextColor(Color.YELLOW)
        snackbar.show()

    }

/*
    internal fun showProgressDialog(message: String) {
        progressDialog = DialogUtil.showProgressDialog(activity)
        progressDialog!!.window.setBackgroundDrawableResource(android.R.color.transparent)
        progressDialog!!.setContentView(R.layout.loading_progress_layout)
        progressDialog!!.show()
    }
*/

    internal fun hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog!!.hide()
            progressDialog!!.dismiss()
        }
    }
    override fun onStop() {
        if (progressDialog!=null){
            progressDialog?.dismiss()
            progressDialog?.hide()
        }
        super.onStop()

    }
    fun View.clickWithDebounce(debounceTime: Long = 600L, action: () -> Unit) {
        this.setOnClickListener(object : View.OnClickListener {
            private var lastClickTime: Long = 0

            override fun onClick(v: View) {
                if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
                else action()

                lastClickTime = SystemClock.elapsedRealtime()
            }
        })
    }
    fun Activity.hideKeyboard() {
        if (currentFocus == null) View(this) else currentFocus?.let { hideKeyboard(it) }
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}//public GlobalPreferences globalPreferences;

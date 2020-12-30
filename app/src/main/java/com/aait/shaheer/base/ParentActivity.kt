package com.kareem.presetntation.base


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.aait.shaheer.R
import com.aait.shaheer.domain.repository.auth_repository.authRepository
import com.aait.shaheer.domain.repository.base_repository.baseRepository
import com.aait.shaheer.domain.repository.chat_repository.inboxRepository
import com.google.android.material.snackbar.Snackbar
import com.kareem.domain.core.Util
import com.kareem.domain.core.setLanguage
import com.kareem.presetntation.helper.SocketConnection
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.toolbar_normal.*
import org.jetbrains.anko.findOptional
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.json.JSONObject

abstract class ParentActivity : AppCompatActivity() {

companion object{


}
    protected  var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var mBack: ImageView? = null
    private var progressDialog: ACProgressFlower? = null
    private var menuId: Int = 0

     abstract val layoutResource: Int

      var isFullScreen: Boolean = false

     val isEnableBack: Boolean = false
      var mProg: RelativeLayout?=null
     var savedInstanceState: Bundle? = null
     var mEmpty: RelativeLayout?=null
     var mSwipe: SwipeRefreshLayout?=null
     var mainLay: RelativeLayout?=null
     var mConn: RelativeLayout? = null


    private fun initViews(view: View) {
        with(view){
            mConn= this.findOptional(R.id.lay_no_internet)
            mEmpty= this.findOptional(R.id.lay_empty)
            mSwipe= this.findOptional(R.id.swipe)
           // mainLay= this.findOptional(R.id.main_lay)
            mProg= this.findOptional(R.id.lay_progress)
        }

    }

    fun handleVisitor(myView: View){
        if (myView!=null) {
            Snackbar.make(myView!!,"يرجى تسجيل الدخول أولا", Snackbar.LENGTH_LONG)
                .setActionTextColor(ContextCompat.getColor(applicationContext,R.color.colorAccent))
                .setAction("تسجيل الدخول") {
                   // startActivity(Intent(applicationContext, LoginActivity::class.java))
                }.show()

        }
    }
    private fun initViews() {
        mConn = findOptional(R.id.lay_no_internet)
        mEmpty= findOptional(R.id.lay_empty)
        mSwipe= findOptional(R.id.swipe)
       // mainLay=findOptional(R.id.main_lay)
        mProg= findOptional(R.id.lay_progress)
        mBack=findOptional(R.id.arr_back)

    }
    internal fun show_progress() {
        if (mainLay != null) {
            mainLay!!.visibility = View.VISIBLE
        }
        mProg!!.setVisibility(View.VISIBLE)
        mConn!!.visibility = View.GONE
        mEmpty!!.visibility = View.GONE
    }

    internal fun show_success_results() {
        if (mainLay != null) {
            mainLay!!.visibility = View.GONE
        }
        mProg!!.setVisibility(View.GONE)
        mConn!!.visibility = View.GONE
        mEmpty!!.visibility = View.GONE
    }

    internal fun show_error() {
        if (mainLay != null) {
            mainLay!!.visibility = View.VISIBLE
        }
        mProg!!.setVisibility(View.GONE)
        mConn!!.visibility = View.VISIBLE
        mEmpty!!.visibility = View.GONE
    }


    internal fun show_empty() {
        if (mainLay != null) {
            mainLay!!.visibility = View.VISIBLE
        }
        mProg!!.visibility = View.GONE
        mConn!!.visibility = View.GONE
        mEmpty!!.visibility = View.VISIBLE
    }

    fun changeStatusBar(color: String){

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.parseColor(color)
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        resources.setLanguage(baseRepository.lang)
        if (isFullScreen) {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        if (hideInputeType()) {
            hideInputtype()
        }
        // set layout resources
        setContentView(layoutResource)

        this.savedInstanceState = savedInstanceState
        initViews()
        mBack?.onClick {
            onBackPressed()
        }
        init()

    }



    @SuppressLint("CheckResult")
    fun setPermissionsCamera(onCameraGranted:()-> Unit){
        val rxPermissions= RxPermissions(this)
        rxPermissions
            .request(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE

            )
            .subscribe { granted ->
                if (granted) {
                    onCameraGranted()
                }
                else {

                }
            }
    }
    fun setTitleToolbar(title:String){
        tv_toolbar?.text=title
    }

    @SuppressLint("CheckResult")
    fun setPermissionsPhone(onPhoneGranted:()-> Unit){
        val rxPermissions= RxPermissions(this)
        rxPermissions
            .request(
                Manifest.permission.CALL_PHONE

            )
            .subscribe { granted ->
                if (granted) {
                    onPhoneGranted()
                }
                else {

                }
            }
    }
    @SuppressLint("CheckResult")
    fun setPermissionsContact(onContactPicked:()-> Unit){
        val rxPermissions= RxPermissions(this)
        rxPermissions
            .request(
                Manifest.permission.READ_CONTACTS

            )
            .subscribe { granted ->
                if (granted) {
                    onContactPicked()
                }
                else {

                }
            }
    }

    @SuppressLint("CheckResult")
    fun setPermissionsLocation(onLocationGranted:(granted:Boolean)-> Unit){
        val rxPermissions= RxPermissions(this)
        rxPermissions
            .request(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .subscribe { granted ->
                onLocationGranted(granted)
            }
    }
    override fun onDestroy() {
        super.onDestroy()
      //  dispose()
    }
    protected fun has_dataBinding(): Boolean {
        return false
    }

    protected abstract fun init()


    protected  fun hideInputeType(): Boolean {return false}


    fun createOptionsMenu(menuId: Int) {
        this.menuId = menuId
        invalidateOptionsMenu()
    }



    /**
     * function is used to create a menu
     */
    fun removeOptionsMenu() {
        menuId = 0
        invalidateOptionsMenu()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (menuId != 0) {
            menuInflater.inflate(menuId, menu)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }


    fun hideInputtype() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }

    }

    // to override typekit in all activities we use
    @SuppressLint("CheckResult")
    override fun attachBaseContext(newBase: Context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            super.attachBaseContext(Util.changeLang("en",newBase))
        }
        else {
            super.attachBaseContext(newBase)
        }
    }



    fun showProgressDialog(message: String?=null) {
        progressDialog = ACProgressFlower.Builder(this)
            .direction(ACProgressConstant.DIRECT_CLOCKWISE)
            .themeColor(Color.GRAY)
            .fadeColor(Color.WHITE)
            .build()
        progressDialog!!.show()
    }

    fun hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog!!.hide()
            progressDialog!!.dismiss()
        }
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
    internal fun onSwipe(onSwipeBack:()-> Unit){
        mSwipe?.setOnRefreshListener {
            mSwipe!!.isNestedScrollingEnabled=false
            mSwipe!!.isRefreshing=false
            onSwipeBack()

        }
    }
    fun Activity.hideKeyboard() {
        if (currentFocus == null) View(this) else currentFocus?.let { hideKeyboard(it) }
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun addSocketConnection() {
        var myId= inboxRepository.loadUser()?.id
        val jsonObject = JSONObject()
        jsonObject.put("client", myId.toString())
        Log.e("addUser","added")
        SocketConnection.addEvent("adduser",jsonObject)
    }

    override fun onStart() {
        super.onStart()
        if (authRepository.isLogin()) {
            addSocketConnection()
        }
    }

    override fun onStop() {
        super.onStop()
        SocketConnection.cancelEvent("adduser")
    }
}



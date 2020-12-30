package com.kareem.domain.core

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.LocationManager
import android.net.Uri
import android.os.RemoteException
import android.util.AndroidException
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.common.BaseResponse
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding2.widget.RxCompoundButton
import com.jakewharton.rxbinding2.widget.RxTextView
import es.dmoral.toasty.Toasty
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import io.reactivex.functions.Function4
import io.reactivex.functions.Function6
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.*
import java.util.*
import javax.net.ssl.HttpsURLConnection

fun <T> Observable<Response<T>>.runOnMain(runLoad:()->Unit): Observable<Response<T>> = subscribeOn(Schedulers.io())
    .doOnSubscribe {
        runLoad()
    }
    .observeOn(AndroidSchedulers.mainThread())



fun <T> Observable<Response<T>>
        .subscribeWithLoading(onLoad: () -> Unit,onSuccess: (T) -> Unit, onError: (throwable:Throwable) -> Unit = { throw  it}, onComplete:()->Unit
) : Disposable {
    return runOnMain{
       // states!!.postValue(Resource.loading())
    }
        .doOnSubscribe {
            onLoad()
        }
        .subscribe({

            val baseResponse = it.body() as BaseResponse
            if (it.isSuccessful && baseResponse.value!="0") {
                onSuccess(it.body()!!)
            }
            else{
                 Throwable(baseResponse.msg).handleException().also {msg-> onError(Throwable(msg)) }

            }
        },{
            //Throwable(it.message).handleException().also {msg-> onError(Throwable(msg)) }
            it.handleException().also {
                    msg-> onError(Throwable(msg))
            }
        },{
            onComplete()
        }
        )
}

fun Throwable.handleException():String{

    var API_STATUS_CODE_LOCAL_ERROR=0
    Log.e("excc",this.javaClass.name)
     if (this is HttpException) {
         Log.e("exccc",this.code().toString())
             val msg:String=when (this.code()) {
                 HttpsURLConnection.HTTP_INTERNAL_ERROR-> "خطأ في الإتصال"
                 HttpsURLConnection.HTTP_BAD_REQUEST->"bad request"
                 API_STATUS_CODE_LOCAL_ERROR->"لا يوجد اتصال بالانترنت"

                    else->{"خطأ في الإتصال"}
            }
         return msg
        }
         else if (this is AndroidException||this is RemoteException || this is BindException||this is PortUnreachableException||this is SocketTimeoutException || this is UnknownServiceException ||this is UnknownHostException || this is IOException  ||this is ConnectException || this is NoRouteToHostException){
             Log.e("exccc3",this.message)
            return "خطأ في الإتصال"
         }
        else if (this is IndexOutOfBoundsException){
         return "لم يتم اضافة بيانات بعد في هدا الحقل"
     }
    else{
         Log.e("exccc2",this.message)
         return this.message!!
     }
}

//phone
@SuppressLint("CheckResult")
fun EditText.validateGeneral(conition:Boolean, onSuccess: () -> Unit,onError: () -> Unit):Observable<Boolean>{
    return RxTextView.textChanges(this)
        .skipInitialValue()
        .map { inputText -> conition/*Util.isPhone(inputText.toString())*/}
        .filter {
            if (!it) {
                onSuccess()
            }
            else{
                onError()
            }
            it
        }
        .distinctUntilChanged()
}
//mail
@SuppressLint("CheckResult")
fun EditText.validateMail(btn: MaterialButton?,inputLayout:TextInputLayout):Observable<Boolean>{
    return RxTextView.textChanges(this)
        .skipInitialValue()
        .map { inputText -> Util.isEmailValid(inputText.toString())}
        .filter {
            if (!it) {
                inputLayout.error="الإيميل غير صحيح"
                inputLayout.errorIconDrawable=null
                btn?.change(false)
            }
            else{
                inputLayout.error=null
                btn?.change(true)

            }
            it
        }
        .doOnSubscribe {
            inputLayout.helperText="مثال : user@example.com"
        }
        .distinctUntilChanged()
}

fun EditText.validateConfPass(btn:MaterialButton?=null,etPass: TextInputEditText, etPassConf: TextInputLayout):Observable<Boolean>{
    return RxTextView.textChanges(this)
        .skipInitialValue()
        .map { inputText -> inputText.trim().isNotBlank() && inputText.toString() == etPass.text.toString() }
        .filter {
            if (!it) {
                etPassConf.error="خطأ في التطابق"
                etPassConf.errorIconDrawable=null
                btn?.change(false)
            }
            else{
                etPassConf.error=null
                btn?.change(true)
            }

            it
        }
        .distinctUntilChanged()
}

fun EditText.validatePass(etPassLay: TextInputLayout):Observable<Boolean>{
    return RxTextView.textChanges(this)
        .skipInitialValue()
        .map { inputText -> inputText.trim().length > 5}

        .distinctUntilChanged()
}
/*
//check pass
fun EditText.checkPass(inputLayout: TextInputLayout): Observable<Boolean>? {
    return RxTextView.afterTextChangeEvents(this)
        //.skipInitialValue()
        .map { inputText -> inputText.view().text.isNotBlank() && inputText.view().text.length > 5}
        .doOnNext{
            if (it){
                inputLayout.helperText=""
                inputLayout.error=null
                inputLayout.boxBackgroundColor=android.R.color.transparent

            }
            else{
                inputLayout.error="خطأ في البيانات"
                this.error=null

               // inputLayout.error="خطأ في البيانات"


            }
        }
        .distinctUntilChanged()
}*/

fun TextInputLayout.checkVer(editText: EditText): Observable<Boolean>? {
    return RxTextView.textChanges(editText)
        .map { inputText -> inputText.trim().isNotBlank() && inputText.trim().length > 3}
        .filter {
            if (it){
                helperText=""
            }
            else{
                helperText=context.getString(R.string.check_ver)
            }
            it
        }
        .distinctUntilChanged()
}


/*fun TextInputLayout.checkConfPass(currentPass: TextInputEditText,
                                  newPass:TextInputEditText,
                                  newPassLay:TextInputLayout): Observable<Boolean>? {
    return Observable.combineLatest(
        currentPass.checkPass(this),
        newPass.checkPass(newPassLay),
        newPassLay.checkMatchObsservalbe(currentPass,newPass),
        Function3 { t1:Boolean, t2:Boolean, t3:Boolean ->
            val isCorrect = when{
                // all accepted
                t1 && t2 && t3 -> true

                else->false
            }
            isCorrect
        }

    )
        .distinctUntilChanged()

}*//*
fun TextInputLayout.checkConfPass(

    currentPass: TextInputEditText,
                                  newPass:TextInputEditText,
                                  newPassLay:TextInputLayout,
                                  confirmNew:TextInputEditText): Observable<Boolean>? {
    return Observable.combineLatest(
        checkPass(this),
        checkPass(newPassLay),
        checkMatchObsservalbe(newPass,confirmNew),
        Function3 { t1:Boolean, t2:Boolean, t3:Boolean ->
            val isCorrect = when{
                // all accepted
                t1 && t2 && t3 -> true

                else->false
            }
            isCorrect
        }
    )
        .distinctUntilChanged()

}*/

fun Resources.setLanguage(lang:String){
// Change locale settings in the app.
        val dm = this.displayMetrics
        val conf = this.configuration
        conf.setLocale( Locale(lang)) // API 17+ only.
        this.updateConfiguration(conf, dm)

}


//name

fun EditText.validateName(btn:MaterialButton?=null,inputLayout:TextInputLayout):Observable<Boolean>{
    return RxTextView.afterTextChangeEvents(this)
        .skipInitialValue()
        .map { inputText -> inputText.view().text.isNotBlank()}
        .filter {
            if (!it) {
                inputLayout.error="الإسم غير صحيح"
                inputLayout.errorIconDrawable=null
                btn?.change(false)
            }
            else{
                inputLayout.error=null
                btn?.change(true)
            }
            it
        }
        .doOnSubscribe {
            inputLayout.helperText="مثال : أحمد"
        }
        .distinctUntilChanged()
}

fun EditText.validateInput(btn:MaterialButton?=null,inputLayout:TextInputLayout):Observable<Boolean>{
    return RxTextView.afterTextChangeEvents(this)
        .skipInitialValue()
        .map { inputText -> inputText.view().text.isNotBlank()}
        .filter {
            if (!it) {
                inputLayout.error="يرجى ادخال البيانات"
                inputLayout.errorIconDrawable=null
                btn?.change(false)
            }
            else{
                inputLayout.error=null
                btn?.change(true)
            }
            it
        }
        .distinctUntilChanged()
}


fun MaterialButton.change(isEnable:Boolean){
  /*  if (isEnable){
        this.isEnabled=true
        this.alpha=1f
    }
    else{

        this.isEnabled=false
        this.alpha=0.5f
    }*/
}

fun MaterialButton.checkAllInputs(email:Observable<Boolean>?=null,name:Observable<Boolean>,
                  phone:Observable<Boolean>?=null,pass:Observable<Boolean>,
                  confPass:Observable<Boolean>?=null,checkTerms:Observable<Boolean>?=null): Observable<Boolean>? {
    return Observable.combineLatest(email,name,phone,pass,confPass,checkTerms,
        Function6<Boolean?,Boolean,Boolean?,Boolean,Boolean?,Boolean?,Boolean>{email_:Boolean?,name_,phone_:Boolean?,pass_:Boolean,confPass_:Boolean?,terms_:Boolean?->
            val isCorrect:Boolean=when{
                email_!=null && email_ && name_ && phone_!=null && phone_&& pass_&& confPass_!=null && confPass_ && terms_!=null && terms_ -> {
                    Log.e("obs","1")
                    this.alpha=1f
                    this.isEnabled=true
                    true
                }
                 email_!! && name_ &&  phone_!!&& pass_&&  confPass_!! && terms_!!-> {
                     Log.e("obs","2")
                     this.alpha=1f
                     this.isEnabled=true
                    true
                }
                else->{
                  /*  Log.e("obs","3")
                    this.alpha=0.5f
                    this.isEnabled=false*/
                    false
                }
            }

             isCorrect
        }
        )
        .map {
            if (it){
                this.alpha=1f
                this.isEnabled=true
                Log.e("obs","3")
                true
            }
            else{
                this.alpha=0.5f
                this.isEnabled=false
                Log.e("obs","2")
                false
            }

        }
        /*.distinctUntilChanged()*/
}
//phone
@SuppressLint("CheckResult")
fun EditText.validatePhone(inputLayout:TextInputLayout):Observable<Boolean>{
    return RxTextView.textChanges(this)
        .skipInitialValue()
        .map { inputText -> Util.isPhone(inputText.toString())}
        .distinctUntilChanged()
}
/*checkMatchObsservalbe(newPass,confirmNew)*/
fun MaterialButton.checkConfPass(
    newPass: Observable<Boolean>,
    confPass: Observable<Boolean>,
    matchPass:Observable<Boolean>,
    verCode: Observable<Boolean>
): Observable<Boolean>? {
    return Observable.combineLatest(
        newPass,
        confPass,
        matchPass,
        verCode,
        Function4 { t1:Boolean, t2:Boolean, t3:Boolean, t4:Boolean ->
            if (t1 && t2 && t3 && t4){
                isEnabled=true
                return@Function4 true
            }
            else if (!t1 && t2 && t3 && t4){
            isEnabled=false

            return@Function4 false
            }
            else if (t1 && !t2 && t3 && t4){
            isEnabled=false

            return@Function4 false
            }

            else{
                isEnabled=false
                return@Function4 false
            }

        }
    )
        .distinctUntilChanged()

}



fun TextInputLayout.checkMatchObsservalbe(etPass:TextInputEditText,etPassConf:TextInputEditText):Observable<Boolean>?{
    return RxTextView.textChanges(etPassConf)
        .skipInitialValue()
        .map { inputText -> inputText.trim().isNotBlank() && inputText.toString() == etPass.text.toString() }
        .filter {
            if (it){
                helperText=""
            }
            else{
                helperText=context.getString(R.string.error_match)
            }
            it
        }
        .distinctUntilChanged()
}

fun MaterialButton.checkAuthValidation(
                  phone:Observable<Boolean>,
                  pass:Observable<Boolean>
                  ): Observable<Boolean>? {
    return Observable.combineLatest(phone,pass,
        BiFunction <Boolean?,Boolean,Boolean>{phone_:Boolean?,pass_:Boolean->
            val isCorrect:Boolean=when{
                //password incorrect
                 phone_!!&& !pass_-> {
                    Log.e("obs","1")
                     this.alpha=0.5f
                     this.isEnabled=false
                    false
                }
                //phone incorrect
                !phone_ && pass_-> {
                     Log.e("obs","2")
                    this.alpha=0.5f
                    this.isEnabled=false
                     false
                }
                //phone & password incorrect
                !phone_&& !pass_-> {
                     Log.e("obs","3")
                    this.alpha=0.5f
                    this.isEnabled=false
                    false
                }
                //all correct
                else->{
                    Log.e("obs","4")
                    this.alpha=1f
                    this.isEnabled=true
                    true
                }
            }

             isCorrect
        }
        )
        .distinctUntilChanged()
        /*.distinctUntilChanged()*/
}

//terms
@SuppressLint("CheckResult")
fun View.validateTerms():Observable<Boolean>{
    return RxCompoundButton.checkedChanges(this as CompoundButton)
        .distinctUntilChanged()
}


fun AutoCompleteTextView.setMyAdapter(keys: List<*>, arrayAdapter: ArrayAdapter<*>,isHint:Boolean?=false):String
    {
    var selection=keys[0] as String
    isCursorVisible = false
    if (!isHint!!) {
        setText(selection)
    }

    setAdapter(arrayAdapter)
    onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
        this@setMyAdapter.showDropDown()
        this@setMyAdapter.error = null
        selection = parent.getItemAtPosition(position) as String

    }
    setOnClickListener{
        this@setMyAdapter.showDropDown()
    }
    return selection

}

fun checkLocation(context: Context):Boolean {
    return ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

fun Context.isGpsEnabled():Boolean{
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

}

fun FragmentManager.showGpsAlert(ctx:Activity){
   /* val builder = AlertDialog.Builder(ctx)
    builder.setTitle(ctx.getString(R.string.alert))
    builder.setMessage(R.string.gps_msg)
    builder.setPositiveButton(R.string.ok) { dialogInterface, i -> ctx.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
    builder.setNegativeButton(R.string.cancel
    ) { dialogInterface, i -> dialogInterface.dismiss() }
    val alertDialog = builder.create()
    alertDialog.show()*/
   /* val dialog = MyAlertDialog{
        if (it==1){
            ctx.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }
    dialog.show(this,"logout")
*/

}

fun Activity.hideKeyboard() {
    hideKeyboard((if (currentFocus == null) View(this) else currentFocus)!!)
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.forwardMap(lat:String,lng:String) {
    /*https://www.google.com/maps/dir/?api=1&query=$lat,$lng*/
    //www.google.com/maps/search/?api=1&query=47.5951518,-122.3316393&query_place_id=ChIJKxjxuaNqkFQR3CK6O1HNNqY
    val url = "https://google.com/maps/search/?api=1&query=$lat,$lng"/*"https://maps.google.com//?daddr=$lat,$lng"*/
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(intent)
}

@SuppressLint("CheckResult")
fun Context.toasty(msg:String,choice:Int?=1) {
    if (choice == 1) {
        Toasty.success(this, msg).show()
    } else {
        Toasty.error(this, msg).show()
    }

fun RotateAnimation.rotation(img:ImageView,fromAngle:Float,toAngle:Float){
        RotateAnimation(fromAngle,toAngle, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            .also {
                duration = 1000
                interpolator = LinearInterpolator()
            }
    }


}

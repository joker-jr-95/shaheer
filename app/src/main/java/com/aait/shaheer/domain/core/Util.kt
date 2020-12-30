package com.kareem.domain.core


//import com.squareup.picasso.Picasso;

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.*
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.*
import android.location.LocationManager
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Environment
import android.os.LocaleList
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.text.format.DateUtils
import android.text.format.DateUtils.MINUTE_IN_MILLIS
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ShareCompat
import com.aait.shaheer.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding2.widget.RxCompoundButton
import com.jakewharton.rxbinding2.widget.RxTextView
import com.aait.shaheer.domain.core.Constant.REQUEST_CODE_PICK_PHOTO
import es.dmoral.toasty.Toasty
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object Util {
    var isALog = true

    val language: String
        get() {
            var language = Locale.getDefault().displayLanguage
            if (language.equals("English", ignoreCase = true)) {
                language = "en"
            } else {
                language = "ar"
            }
            return language

        }



     fun openGallery(activity: Activity) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_PICK_PHOTO)

    }

    fun getTime( hours:Int):Int{

        // Initialize a new variable to hold 12 hour format hour value
        val hour_of_12_hour_format: Int

        if (hours > 11) {

            // If the hour is greater than or equal to 12
            // Then we subtract 12 from the hour to make it 12 hour format time

            if (hours==12) {
                hour_of_12_hour_format = 12
            }


            else{
                hour_of_12_hour_format = hours - 12
            }

        }

        else if (hours==0){
            hour_of_12_hour_format = 12
        }

        else {
            hour_of_12_hour_format = hours
        }
        return hour_of_12_hour_format
    }

/*
     fun takePhoto(activity:Activity):String {
        val picturesPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val cameraFilePath =
            picturesPath.toString() +
                    File.separator +
                    SimpleDateFormat(
                        "yyyy-MMM-dd_HH-mm-ss",
                        Locale.getDefault()
                    ).format(Date()) +
                    ".jpg"
        takePicture.putExtra(
            MediaStore.EXTRA_OUTPUT,
            uriFromFile(
                activity,
                activity.application.packageName,
                File(cameraFilePath)
            )
        )

        takePicture.putExtra(MediaStore.EXTRA_SIZE_LIMIT, CAMERA_MAX_FILE_SIZE_BYTE)

        activity.startActivityForResult(
            takePicture,
            REQUEST_CODE_TAKE_PHOTO
        )
         return cameraFilePath
    }*/
    fun initToast(){
        Toasty.Config.getInstance()
            .tintIcon(false) // optional (apply textColor also to the icon)
            .setTextSize(16) // optional
            .apply()
    }
    fun convertUriToBitmap(context: Context, uri: Uri): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return bitmap
    }
    fun prepareMap(context: Activity, onProvide:()->Unit) {

        val locationManager = context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            onProvide()
        }
        else {
            val builder = android.app.AlertDialog.Builder(context)
            builder.setTitle(context.getString(R.string.alert))
            builder.setMessage(R.string.gps_msg)
            //builder.setView(R.layout.abc_alert_dialog_material)
            builder.setPositiveButton(
                R.string.ok
            ) { dialogInterface, i -> context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            /*builder.setNegativeButton(R.string.cancel
            )
            { dialogInterface, i -> dialogInterface.dismiss() }*/
            val alertDialog = builder.create()
            alertDialog.show()
        }
    }

  /*


    //1-> all ,2-camera only 3->gallery
    fun setUpCamera(act: Activity, mode: ReturnMode, isMulti: Boolean) {

        val imagePicker = ImagePicker.create(act)
            .returnMode(mode) // set whether pick and / or camera action should return immediate result or not.
            .folderMode(true) // folder mode (false by default_)
            .toolbarFolderTitle("Folder") // folder selection title
            .toolbarImageTitle("Tap to select") // image selection title
            .toolbarArrowColor(Color.BLACK)// Toolbar 'up' arrow color
        //.includeVideo(true) // Show video on image picker
        if (!isMulti) {
            imagePicker.single() // single mode
        } else {
            imagePicker.multi()
        } // multi mode (default_ mode)
        imagePicker.limit(10) // max images can be selected (99 by default_)
            .showCamera(true) // show camera or not (true by default_)
            .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default_)
            //   .imageLoader(new GrayscaleImageLoder()) // custom image loader, must be serializeable
            .start() // start image picker activity with request code
    }
*/
    fun convertImgToFile(parm_name: String, context: Activity, uri: Uri?=null,path:String?=null, fileName: String): MultipartBody.Part {
if (uri!=null) {
    if (getFileType(uri, context).equals("jpg") || getFileType(
            uri,
            context
        ).equals("png") || getFileType(uri, context).equals("gif")
    ) {
        var realPathFromURI = getRealPathFromURI(context, uri)

        var imgFile = File(realPathFromURI)
        var imgBody = RequestBody.create("image/*".toMediaTypeOrNull(), imgFile)
        // request body for the image content
        var file = MultipartBody.Part.createFormData(parm_name, imgFile.name, imgBody)
        return file
    } else {
        val file = File(uri.path)
        val imgBody = RequestBody.create("*/*".toMediaTypeOrNull(), file)
        //request body for the image content
        return MultipartBody.Part.createFormData(parm_name, fileName, imgBody)
    }
}
      else{
            val file = File(path)
            val imgBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            //request body for the image content
            return MultipartBody.Part.createFormData(parm_name, file.name, imgBody)
      }
    }

    //}

    fun getFileType(uri: Uri, context: Activity): String? {
        val cR = context.contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }

    fun openWhatsapp(activity: Activity, phone: String) {
        //    String formattedNumber = Util.formatPhone(phone);
        try {
            val sendIntent = Intent("android.intent.action.MAIN")
            sendIntent.component = ComponentName("com.whatsapp", "com.whatsapp.Conversation")
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.type = "text/plain"
            sendIntent.putExtra(Intent.EXTRA_TEXT, "")
            sendIntent.putExtra("jid", "$phone@s.whatsapp.net")
            sendIntent.setPackage("com.whatsapp")
            activity.startActivity(sendIntent)
        } catch (e: Exception) {
            Toast.makeText(activity, "Error/n$e", Toast.LENGTH_SHORT).show()
        }

    }

    private fun formatPhone(phone: String): String {
        var phone = phone
        phone = phone.trim { it <= ' ' }
        if (phone.contains("+")) {
            phone = phone.replace("+", "")
        }
        return phone
    }

    fun shareDirecctToSingleWhatsAppUser(pkg: PackageManager, activity: Activity, phone: String, msg: String) {

        val i = Intent(Intent.ACTION_VIEW)

        try {
            val url = "https://api.whatsapp.com/send?phone=" + phone + "&text=" + URLEncoder.encode("", "UTF-8")
            i.setPackage("com.whatsapp")
            i.data = Uri.parse(url)
            if (i.resolveActivity(pkg) != null) {
                activity.startActivity(i)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }



    fun changeLang(language: String, context: Context): Context {
        var context = context
        val res = context.resources
        val configuration = res.configuration
        val newLocale = Locale(language)

        if (VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(newLocale)
            var localeList = LocaleList(newLocale)
            LocaleList.setDefault(localeList)
            configuration.setLocales(localeList)
            context = context.createConfigurationContext(configuration)
        } else
            if (VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                configuration.setLocale(newLocale)
            }
        if (VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            context = context.createConfigurationContext(configuration)
        }
        return context
    }



    fun isPackageInstalled(packagename: String, packageManager: PackageManager): Boolean {
        try {
            packageManager.getPackageInfo(packagename, 0)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }

    }

    fun scaleBitmap(bitmap: Bitmap, wantedWidth: Int, wantedHeight: Int): Bitmap {
        val output = Bitmap.createBitmap(wantedWidth, wantedHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val m = Matrix()
        m.setScale(wantedWidth.toFloat() / bitmap.width, wantedHeight.toFloat() / bitmap.height)
        canvas.drawBitmap(bitmap, m, Paint())

        return output
    }

    fun setNormalDate(format: String): String {
        val sdf = SimpleDateFormat("dd:MM:yyyy")
        var format1 = ""
        try {
            val parse = sdf.parse(format)
            format1 = sdf.format(parse)

        } catch (e: ParseException) {
            e.printStackTrace()
        }

        Log.e("format", format1)
        return format1
    }

    fun openFacebook(pm: PackageManager, url: String, act: Activity) {
        var uri = Uri.parse(url)
        try {
            val applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0)
            if (applicationInfo.enabled) {
                uri = Uri.parse("fb://facewebmodal/f?href=$url")
            }
        } catch (ignored: PackageManager.NameNotFoundException) {
        }

        val intent = Intent(Intent.ACTION_VIEW, uri)
        act.startActivity(intent)


    }

    fun onCall(act: Activity, phone: String?) {
        val intent = Intent(Intent.ACTION_CALL)
        if (phone != null) {
            intent.data = Uri.parse("tel:$phone")
            if (ActivityCompat.checkSelfPermission(
                    act,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                // ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }

            act.startActivity(intent)
        }
    }

    fun openUrl(context: Context, url: String) {
        try {


            val i = Intent(Intent.ACTION_VIEW)
            if (i.resolveActivity(context.packageManager) != null) {

                i.data = Uri.parse(url)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(i)
            } else {
                Toast.makeText(context, "its empty", Toast.LENGTH_SHORT).show()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }


    fun getBitmapFromURL(src: String): Bitmap? {

        var url: URL? = null
        try {
            url = URL(src)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            return BitmapFactory.decodeStream(input)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            return null
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

    }

    /* public static void setBitmapFromURL(String src, final CircleImageView mImg, Context context) {
        Picasso.with(context).load(src).resize(100,100).into(mImg);
    }*/
    fun setToasty(context: Context) {
        /*    Toasty.Config.getInstance()
            .setErrorColor(context.getResources().getColor(R.color.colorError)) // optional
    //.setInfoColor(@ColorInt int infoColor) // optional
    .setSuccessColor(context.getResources().getColor(R.color.colorPrimary)) // optional
    .setWarningColor(context.getResources().getColor(R.color.colorAccent)) // optional
//    .setTextColor(@ColorInt int textColor) // optional
    .tintIcon(false) // optional (apply textColor also to the icon)
         //   .setTextSize(int sizeInSp) // optional
    .apply(); // required*/

    }

    fun setSpinnerListners(context: Context, atc: AutoCompleteTextView, isPop: BooleanArray, data: List<String>) {

        val adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, data)
        atc.setAdapter(adapter)

        atc.setOnClickListener {
            if (!isPop[0]) {
                atc.showDropDown()
                isPop[0] = !isPop[0]

            } else if (isPop[0]) {
                atc.dismissDropDown()
                isPop[0] = !isPop[0]
            }
        }
    }


    fun makeURL(sourcelat: Double, sourcelog: Double, destlat: Double, destlog: Double): String {
        val urlString = StringBuilder()
        urlString.append("http://maps.googleapis.com/maps/api/directions/json")
        urlString.append("?origin=")// from
        urlString.append(java.lang.Double.toString(sourcelat))
        urlString.append(",")
        urlString.append(java.lang.Double.toString(sourcelog))
        urlString.append("&destination=")// to
        urlString.append(java.lang.Double.toString(destlat))
        urlString.append(",")
        urlString.append(java.lang.Double.toString(destlog))
        urlString.append("&sensor=false&mode=driving&alternatives=true")
        return urlString.toString()
    }

    fun requestFocus(view: View, window: Window) {
        if (view.requestFocus()) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    fun HideKeyboard(context: Context) {
        val inputMethodManager = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow((context as Activity).currentFocus!!.windowToken, 0)

    }


    fun ShareApp(context: Context,code: String?="") {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            String.format("","https://play.google.com/store/apps/details?id=com.aait.taxiawamer",code))
        sendIntent.type = "text/plain"
        sendIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(sendIntent)
    }

    fun onShowAlertMesasge(activity: Activity, message: String) {
        val alertDialogBuilder = AlertDialog.Builder(
            activity
        )

        // set title
        //  alertDialogBuilder.setTitle("Your Title");

        // set dialog message
        alertDialogBuilder
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(activity.resources.getString(R.string.yes)) { dialog, id ->
                // if this button is clicked, close
                // current activity
                activity.finish()
            }

        // create alert dialog
        val alertDialog = alertDialogBuilder.create()

        // show it
        alertDialog.show()
    }

    fun RateApp(context: AppCompatActivity) {
        val appPackageName = context.packageName // getPackageName() from Context or Activity object
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (anfe: android.content.ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }

    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 70, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    fun isNetworkAvailable(context: Context): Boolean {
        var isAvailable = false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        isAvailable = connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected
        if (!isAvailable) {
            //makeToast(context, "please check internet connection");
        }
        return isAvailable

    }


    fun isValid(input: String): Boolean {
        var valid = true
        if (input.trim { it <= ' ' }.isEmpty()) {
            valid = false
        }
        return valid
    }

    fun isEmailValid(email: String): Boolean {
        var valid = true

        if (!email.matches("^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$".toRegex()) || email.trim { it <= ' ' }.isEmpty()) {
            valid = false
        }
        return valid
    }

    fun isPhone(phone: String): Boolean {
        var valid = true
        if (phone.trim { it <= ' ' }.isEmpty() || phone.trim { it <= ' ' }.length < 9) {
            valid = false
        }
        return valid
    }

    fun isVideoValid(video: String): Boolean {
        var valid = true
        valid = (video.contains("youtube") || video.contains("youtu.be")
                || video.matches("([^\\s]+(\\.(?i)(mp4|flv|3gp))$)".toRegex()))
        return valid
    }

    fun checkError(editText: EditText, context: Context): Boolean {
        if (!isValid(editText.text.toString())) {
            editText.error = context.resources.getString(R.string.check_error)
            return false
        } else {
            return true
        }

    }

    fun checkErrorLay(editText: EditText, inputLay: TextInputLayout, msg: String): Boolean {
        if (!isValid(editText.text.toString())) {
            inputLay.error = msg
            return false
        } else {
            return true
        }

    }
    fun checkMailLay(editText: EditText,inputLay:TextInputLayout,msg: String): Boolean {
        if (!isEmailValid(editText.text.toString())) {
            inputLay.error = msg
            return false
        } else {
            return true
        }


    }
    fun checkErrorLayLimit(editText: EditText,inputLay:TextInputLayout,msg: String): Boolean {
        if (editText.text.toString().trim().length<10) {
            inputLay.error = msg
            return false
        } else {
            return true
        }
    }


    //check phone
    @SuppressLint("CheckResult")
    fun checkPhone(editText: EditText, textInputLayout: TextInputLayout, msg: String): Boolean {
        var isValid=false
        /*return*/ RxTextView.textChanges(editText)
            .map { inputText -> isPhone(inputText.toString()) /*&& inputText.startsWith("05")*/}
            .distinctUntilChanged()
            .subscribe {
                if (!it){
                    textInputLayout.isErrorEnabled=true
                    textInputLayout.error=msg
                }
                else{
                    textInputLayout.isErrorEnabled=false
                }
                isValid=it
            }
        return isValid
    }
    //check email
    @SuppressLint("CheckResult")
    fun checkMail(editText: EditText, textInputLayout: TextInputLayout, msg: String): Boolean {
        var isValid=false
        RxTextView.textChanges(editText)
            .map { inputText -> isEmailValid(inputText.toString()) }
            .distinctUntilChanged()
            .subscribe {
                if (!it){
                    textInputLayout.isErrorEnabled=true
                    textInputLayout.error=msg
                }
                else{
                    textInputLayout.isErrorEnabled=false
                }
                isValid=it
            }
        return isValid
    }

    //check name
    @SuppressLint("CheckResult")
    fun checkName(editText: EditText, textInputLayout: TextInputLayout, msg: String): Boolean {
        var isValid=false
        RxTextView.textChanges(editText)
            .map { inputText -> inputText.trim().isNotBlank()}
            .distinctUntilChanged()
            .subscribe {
                if (!it){
                   // textInputLayout.isErrorEnabled=true
                    textInputLayout.error=msg
                }
                else{
                    textInputLayout.isErrorEnabled=false
                }
                isValid=it
            }
        return isValid
    }
    //check name
    @SuppressLint("CheckResult")
    fun checkNameLimit(editText: EditText, textInputLayout: TextInputLayout, msg: String): Boolean {
        var isValid=false
        RxTextView.textChanges(editText)
            .map { inputText -> inputText.trim().isNotBlank() && inputText.length>9}
            .distinctUntilChanged()
            .subscribe {
                if (!it){
                    textInputLayout.isErrorEnabled=true
                    textInputLayout.error=msg
                }
                else{
                    textInputLayout.isErrorEnabled=false
                }
                isValid=it
            }
        return isValid
    }

    //check terms
    fun checkTerms(checkBox: CheckBox): Observable<Boolean>? {
        return RxCompoundButton.checkedChanges(checkBox)
            .distinctUntilChanged()
    }

    //check pass
    @SuppressLint("CheckResult")
    fun checkPass(editText: EditText,textInputLayout: TextInputLayout,msg: String): Boolean {
        var isValid=false
        /*return*/ RxTextView.textChanges(editText)
            .map { inputText -> inputText.trim().isNotBlank() && inputText.trim().length > 5}
            .distinctUntilChanged()
            .subscribe {
                if (!it){
                    textInputLayout.isErrorEnabled=true
                    textInputLayout.error=msg
                }
                else{
                    textInputLayout.isErrorEnabled=false
                }
                isValid=it
            }
        return isValid
    }


    fun checkPhoneLay(editText: EditText,inputLay:TextInputLayout,msg: String): Boolean {
        if (!isPhone(editText.text.toString())) {
            inputLay.error = msg
            return false
        } else {
            return true
        }
    }
    fun checkError(editText: EditText, context: Context, msg: String): Boolean {
        if (!isValid(editText.text.toString())) {
            editText.error = msg
            return false
        } else {
            return true
        }

    }



    fun checkEmail(email: EditText, context: Context): Boolean {
        if (!isEmailValid(email.text.toString())) {
            email.error = context.resources.getString(R.string.check_mail)
            return false
        } else {
            return true
        }

    }

    fun checkError(editText: AutoCompleteTextView, context: Context): Boolean {
        if (!isValid(editText.text.toString())) {
            editText.error = context.resources.getString(R.string.check_error)
            return false
        } else {
            return true
        }

    }

    fun checkPassSize(editText: EditText, context: Context): Boolean {
        if (editText.text.toString().length < 6) {
            editText.error = context.resources.getString(R.string.check_pass)
            return false
        } else {
            return true
        }

    }
    fun checkPassSize(editText: EditText,inputLay: TextInputLayout,msg: String): Boolean {
        if (editText.text.toString().length < 6) {
            inputLay.error=msg
            return false
        } else {
            return true
        }

    }

    @SuppressLint("CheckResult")
    fun checkMatch(etPass: EditText, etPassMatch: EditText, inputLay: TextInputLayout, msg: String): Boolean {
        var isValid=false
        /*return*/ RxTextView.textChanges(etPassMatch)
            .map { inputText ->  inputText.matches(etPass.text.toString().toRegex()) }
            .distinctUntilChanged()
            .subscribe {
                if (!it){
                    inputLay.isErrorEnabled=true
                    inputLay.error=msg
                }
                else{
                    inputLay.isErrorEnabled=false
                }
                isValid=it
            }
        return isValid
    }

    fun checkNamePref(editText: EditText): Boolean {
        if (editText.text.toString().matches("^[a-zA-Z].*".toRegex())) {

            return true
        } else {
            editText.error = "اسم المستخدم يجب ألا يبدأ بحرف كابيتال"
            return false
        }

    }

    fun checkMatch(context: Context, pass: EditText, confirm_pass: EditText): Boolean {
        var isMatch = false
        if (pass.text.toString().matches(confirm_pass.text.toString().toRegex())) {
            isMatch = true
        } else {
            confirm_pass.error = context.resources.getString(R.string.error_match)
        }

        return isMatch
    }




    fun getEncoded64ImageStringFromBitmap(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
        val byteFormat = stream.toByteArray()
        // get the base 64 string
        return Base64.encodeToString(byteFormat, Base64.NO_WRAP)
    }


    fun getRealPathFromURI(context: Activity, uri: Uri): String? {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        if (cursor == null) {
            return uri.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            return cursor.getString(idx)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun getPath(context: Context, uri: Uri): String? {

        val isKitKat = VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {

                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )

                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                return getDataColumn(context, contentUri, selection, selectionArgs)
            }// MediaProvider
            // DownloadsProvider
        } else if ("content".equals(uri.scheme!!, ignoreCase = true)) {

            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(context, uri, null, null)

        } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
            return uri.path
        }// File
        // MediaStore (and general)

        return null
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {

        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }


    /*String path=null;
    String[] filePathColumn = {MediaStore.Images.Media.DATA};
    // Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
    Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
        if (cursor.moveToFirst()) {
        int idx = cursor.getColumnIndex(filePathColumn[0]);
        path = cursor.getString(idx);
    }
        return path;*/
    fun decodeAndResizeFile(f: File): Bitmap? {
        try {
            // Decode image size
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            BitmapFactory.decodeStream(FileInputStream(f), null, o)

            // The new size we want to scale to
            val REQUIRED_SIZE = 150

            // Find the correct scale value. It should be the power of 2.
            var width_tmp = o.outWidth
            var height_tmp = o.outHeight
            var scale = 1
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break
                width_tmp /= 3
                height_tmp /= 3
                scale *= 2
            }
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            return BitmapFactory.decodeStream(FileInputStream(f), null, o2)
        } catch (e: FileNotFoundException) {
        }

        return null
    }

    fun MonthPicker(number: Int): String {
        val MONTHS = arrayOf(
            " يناير ",
            " فبراير ",
            " مارس ",
            " أبريل ",
            " مايو ",
            " يونيو ",
            " يوليو ",
            " أغسطس ",
            " سبتمبر ",
            " أكتوبر ",
            " نوفمبر ",
            " ديسمبر "
        )

        return MONTHS[number]
    }
/*
    fun handleException(context: Context, t: Throwable): Int {
        if (t is SocketTimeoutException) {
            //Toast.makeText(context, R.string.time_out_error,Toast.LENGTH_LONG);
            return R.string.time_out_error
        } else if (t is UnknownHostException) {
            Toast.makeText(context, R.string.connection_error, Toast.LENGTH_LONG)
            return R.string.connection_error
        } else if (t is ConnectException) {
            //  makeToast(context, R.string.connection_error);
            Toast.makeText(context, t.message, Toast.LENGTH_LONG)
            return R.string.connection_error
        } else if (t is NoRouteToHostException) {
            //  makeToast(context, R.string.connection_error);
            Toast.makeText(context, t.message, Toast.LENGTH_LONG)
            return R.string.connection_error
        } else if (t is PortUnreachableException) {
            //makeToast(context, R.string.connection_error);
            Toast.makeText(context, t.message, Toast.LENGTH_LONG)
            return R.string.connection_error
        } else if (t is UnknownServiceException) {
            //            makeToast(context, R.string.connection_error);
            Toast.makeText(context, R.string.connection_error, Toast.LENGTH_SHORT).show()
            return R.string.connection_error
        } else if (t is BindException) {
            //makeToast(context, R.string.connection_error);
            Toast.makeText(context, R.string.connection_error, Toast.LENGTH_SHORT).show()
            return R.string.connection_error
        } else {
            makeToast(context, t.localizedMessage)
            return R.string.connection_error
        }
    }*/

    /*public static void makeToast(Context context, int msgId) {
        Toaster toaster = new Toaster(context);
        toaster.makeToast(context.getString(msgId));

    }*/

    fun makeToast(context: Context, msg: String) {
        /*Toaster toaster = new Toaster(context);
        toaster.makeToast(msg);*/

    }

    /*  public static ArrayList<TutorialItem> getTutorialItems(Context context) {
        TutorialItem tutorialItem1 = new TutorialItem(R.string.title_1, R.string.intro_1_subtitle,
                R.color.slide_1, R.mipmap.intro_one);

        TutorialItem tutorialItem2 = new TutorialItem(R.string.title_2, R.string.intro_2_subtitle,
                R.color.slide_2, R.mipmap.intro_two);

        TutorialItem tutorialItem3 = new TutorialItem(R.string.title_3, R.string.intro_3_subtitle,
                R.color.slide_3, R.mipmap.intro_three);


        ArrayList<TutorialItem> tutorialItems = new ArrayList<>();
        tutorialItems.add(tutorialItem1);
        tutorialItems.add(tutorialItem2);
        tutorialItems.add(tutorialItem3);
        return tutorialItems;
    }
*/
    fun TabCustomFontSize(context: Context, tabLayout: TabLayout, Font: String) {
        val vg = tabLayout.getChildAt(0) as ViewGroup
        val tabsCount = vg.childCount
        for (j in 0 until tabsCount) {
            val vgTab = vg.getChildAt(j) as ViewGroup
            val tabChildCount = vgTab.childCount
            for (i in 0 until tabChildCount) {
                val tabViewChild = vgTab.getChildAt(i)
                if (tabViewChild is TextView) {
                    FonTChange(context, tabViewChild, "JF-Flat-regular.ttf")
                }
            }
        }
    }

    private fun FonTChange(con: Context, textView: TextView, Fonts: String) {
        val fontPath = "fonts/$Fonts"
        // Loading Font Face
        val tf = Typeface.createFromAsset(con.assets, fontPath)
        // Applying font
        textView.typeface = tf
    }

    fun showDialog(msg: String, context: Context): AlertDialog {
        val builder = AlertDialog.Builder(context)
        // builder.setTitle(title);
        builder.setMessage(msg)
        // builder.setView(R.layout.dialog_confirm);
        return builder.create()


    }

    fun modifyImgOrientation(img: Bitmap, path: String): Bitmap {
        var postImg = img
        try {

            val exifInterface = ExifInterface(path)
            val orientation =
                exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> postImg = rotate(img, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> postImg = rotate(img, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> postImg = rotate(img, 270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> postImg = flip(img, true, false)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> postImg = flip(img, false, true)
                else -> return img
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return postImg
    }

    fun rotate(img: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)

        return Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
    }

    fun flip(img: Bitmap, horizental: Boolean, vertical: Boolean): Bitmap {

        val matrix = Matrix()
        matrix.preScale((if (horizental) -1 else 1).toFloat(), (if (vertical) -1 else 1).toFloat())
        return Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
    }

    fun encodedBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
    }

    fun decodedBase64(img64: String): Bitmap {
        val bytes = Base64.decode(img64, Base64.NO_WRAP)
//        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)


    }

    fun loadProgress(context: Activity): ProgressDialog {
        val progressDialog = ProgressDialog(context)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("please wait ...")
        return progressDialog

    }


    fun setConfig(language: String, context: Context) {
        /*Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getApplicationContext().getResources().updateConfiguration(config, null);
*/
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        context.resources.updateConfiguration(
            config,
            context.resources.displayMetrics
        )


    }


    fun share_twitter(activity: Activity, msg: String, app_package_name: String) {
        val url = "http://www.twitter.com/intent/tweet?text=$msg &url=$app_package_name"
        val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        i.data = Uri.parse(url)
        activity.startActivity(i)
    }

    fun share_whats(activity: Activity, msg: String, app_package_name: String) {
        val whatsappIntent = Intent(Intent.ACTION_SEND)
        whatsappIntent.type = "text/plain"
        whatsappIntent.setPackage("com.whatsapp")
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, msg + "\n" + app_package_name)
        try {
            activity.startActivity(whatsappIntent)
        } catch (ex: android.content.ActivityNotFoundException) {
            Toast.makeText(activity, "not installed", Toast.LENGTH_SHORT).show()
        }

    }

    fun share_face(activity: Activity, app_package_name: String) {
        val sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=$app_package_name"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl))
        activity.startActivity(intent)
    }

    fun share_face_result(activity: Activity, app_package_name: String, code: Int) {
        val sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=$app_package_name"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl))
        activity.startActivityForResult(intent, code)
    }

    fun share_app_twitter(activity: Activity, app_package_name: String) {
        val url = "http://www.twitter.com/intent/tweet?url=$app_package_name"
        val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        i.data = Uri.parse(url)
        activity.startActivity(i)
    }

    fun share_app_whats(activity: Activity, app_package_name: String) {
        val whatsappIntent = Intent(Intent.ACTION_SEND)
        whatsappIntent.type = "text/plain"
        whatsappIntent.setPackage("com.whatsapp")
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, app_package_name)
        try {
            activity.startActivity(whatsappIntent)
        } catch (ex: android.content.ActivityNotFoundException) {
            Toast.makeText(activity, "not installed", Toast.LENGTH_SHORT).show()
        }

    }

    fun getFormattedTime(date: String): String {
        var parse: Date? = null
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            parse = sdf.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val calendar = Calendar.getInstance()
        calendar.time = parse
        val updated = calendar.timeInMillis
        return DateUtils.getRelativeTimeSpanString(updated, System.currentTimeMillis(), MINUTE_IN_MILLIS).toString()
    }

    fun sendShare(context: Activity, sharePackage: String, title: String, description: String, img_path: String) {
        val intent = context.packageManager.getLaunchIntentForPackage(sharePackage)
        if (intent != null) {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.setPackage(sharePackage)
            shareIntent.putExtra(Intent.EXTRA_TITLE, title)
            shareIntent.putExtra(Intent.EXTRA_TEXT, description)
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(img_path))
            // Start the specific social application
            context.startActivity(shareIntent)
        } else {
            Toast.makeText(context, "sorry application not exist", Toast.LENGTH_SHORT).show()
        }
    }

    fun checkPhoneSize(mPhone: EditText, context: Context): Boolean {
        if (mPhone.text.toString().length < 9) {
            mPhone.error = context.resources.getString(R.string.check_phone)
            return false
        } else {
            return true
        }
    }

    fun share_google(activity: Activity, url: String) {
        try {

            if (isPackageInstalled("com.google.android.apps.plus", activity.packageManager)) {
                val shareIntent = ShareCompat.IntentBuilder.from(activity)
                    .setText(url)
                    .setType("text/plain")
                    .intent
                    .setPackage("com.google.android.apps.plus")

                activity.startActivity(shareIntent)
            } else {
             //   Toast.makeText(activity, R.string.package_installed, Toast.LENGTH_SHORT).show()
            }
        } catch (ex: Exception) {
            Toast.makeText(activity, ex.localizedMessage, Toast.LENGTH_SHORT).show()
        }

    }

    fun copy(actv: Context, word: String) {
        val clipboard = actv.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("word", word)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(actv, R.string.copied, Toast.LENGTH_SHORT).show()
    }

    fun openMsgs(activity: Activity) {

        try {
            val sendIntent = Intent(Intent.ACTION_VIEW)
            sendIntent.type = "vnd.android-dir/mms-sms"
            sendIntent.putExtra(
                Intent.EXTRA_PHONE_NUMBER,
                ""
            )
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "")
            sendIntent.putExtra(Intent.EXTRA_TEXT, "")
            //sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(attachment));
            activity.startActivity(
                Intent.createChooser(
                    sendIntent,
                    "Send via which Application?"
                )
            )
        } catch (e: Exception) {
            Toast.makeText(
                activity, "No activity was found to handle this action",
                Toast.LENGTH_SHORT
            ).show()
        }

    }
    fun openMail(activity: Activity,mail:String?="") {
        val intent = Intent(Intent.ACTION_VIEW)
        val data = Uri.parse("mailto:$mail?subject=&body=")
        intent.data = data
        activity.startActivity(intent)
    }

    fun openMail(activity: Activity) {
        val intent = Intent(Intent.ACTION_VIEW)
        val data = Uri.parse("mailto:?subject=" + "&body=")
        intent.data = data
        activity.startActivity(intent)
    }

    fun resize(image: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        var image = image
        if (maxHeight > 0 && maxWidth > 0) {
            val width = image.width
            val height = image.height
            val ratioBitmap = width.toFloat() / height.toFloat()
            val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()

            var finalWidth = maxWidth
            var finalHeight = maxHeight
            if (ratioMax > 1) {
                finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
            } else {
                finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true)
            return image
        } else {
            return image
        }
    }

    fun share_compat(activity: Activity, url: String) {
        val mimeType = "text/plain"
        val title = "hi my friend you can surf this"

        val shareIntent = ShareCompat.IntentBuilder.from(activity)
            .setChooserTitle(title)
            .setType(mimeType)
            .setText(url)
            .intent
        if (shareIntent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(shareIntent)
        }
    }
    fun convertImgToFilePix(
        parm_name: String?,
        path: String?
    ): MultipartBody.Part? {
        val file = File(path)
        val imgBody =
            RequestBody.create("image/*".toMediaTypeOrNull(), file)
        //request body for the image content
        return parm_name?.let { MultipartBody.Part.createFormData(it, file.name, imgBody) }
    }
    fun convertVideoToFilePix(
        parm_name: String?,
        path: String?
    ): MultipartBody.Part? {
        val file = File(path)
        val imgBody =
            RequestBody.create("video/*".toMediaTypeOrNull(), file)
        //request body for the image content
        return parm_name?.let { MultipartBody.Part.createFormData(it, file.name, imgBody) }
    }

    class MyProgress(internal var context: Context) {

        init {
            progressDialog = ProgressDialog(context)
            progressDialog!!.setCancelable(false)
            progressDialog!!.setMessage("انتظر من فضلك ...")

        }

        fun showProgress() {
            progressDialog!!.show()
        }

        fun hideProgress() {
            progressDialog!!.dismiss()
        }

        companion object {

            private var progressDialog: ProgressDialog? = null
        }


    }
    /*
     * Copyright 2014 Manuel Peinado
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     *     http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */


    object Blur {

        @SuppressLint("NewApi")
        fun fastblur(context: Context, sentBitmap: Bitmap, radius: Int): Bitmap? {

            if (VERSION.SDK_INT > 16) {
                val bitmap = sentBitmap.copy(sentBitmap.config, true)

                val rs = RenderScript.create(context)
                val input = Allocation.createFromBitmap(
                    rs, sentBitmap, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT
                )
                val output = Allocation.createTyped(rs, input.type)
                val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
                script.setRadius(radius.toFloat() /* e.g. 3.f */)
                script.setInput(input)
                script.forEach(output)
                output.copyTo(bitmap)
                return bitmap
            }

            // Stack Blur v1.0 from
            // http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
            //
            // Java Author: Mario Klingemann <mario at quasimondo.com>
            // http://incubator.quasimondo.com
            // created Feburary 29, 2004
            // Android port : Yahel Bouaziz <yahel at kayenko.com>
            // http://www.kayenko.com
            // ported april 5th, 2012

            // This is a compromise between Gaussian Blur and Box blur
            // It creates much better looking blurs than Box Blur, but is
            // 7x faster than my Gaussian Blur implementation.
            //
            // I called it Stack Blur because this describes best how this
            // filter works internally: it creates a kind of moving stack
            // of colors whilst scanning through the image. Thereby it
            // just has to add one new block of color to the right side
            // of the stack and remove the leftmost color. The remaining
            // colors on the topmost layer of the stack are either added on
            // or reduced by one, depending on if they are on the right or
            // on the left side of the stack.
            //
            // If you are using this algorithm in your code please add
            // the following line:
            //
            // Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

            val bitmap = sentBitmap.copy(sentBitmap.config, true)

            if (radius < 1) {
                return null
            }

            val w = bitmap.width
            val h = bitmap.height

            val pix = IntArray(w * h)
            bitmap.getPixels(pix, 0, w, 0, 0, w, h)

            val wm = w - 1
            val hm = h - 1
            val wh = w * h
            val div = radius + radius + 1

            val r = IntArray(wh)
            val g = IntArray(wh)
            val b = IntArray(wh)
            var rsum: Int
            var gsum: Int
            var bsum: Int
            var x: Int
            var y: Int
            var i: Int
            var p: Int
            var yp: Int
            var yi: Int
            var yw: Int
            val vmin = IntArray(Math.max(w, h))

            var divsum = div + 1 shr 1
            divsum *= divsum
            val dv = IntArray(256 * divsum)
            i = 0
            while (i < 256 * divsum) {
                dv[i] = i / divsum
                i++
            }

            yi = 0
            yw = yi

            val stack = Array(div) { IntArray(3) }
            var stackpointer: Int
            var stackstart: Int
            var sir: IntArray
            var rbs: Int
            val r1 = radius + 1
            var routsum: Int
            var goutsum: Int
            var boutsum: Int
            var rinsum: Int
            var ginsum: Int
            var binsum: Int

            y = 0
            while (y < h) {
                bsum = 0
                gsum = bsum
                rsum = gsum
                boutsum = rsum
                goutsum = boutsum
                routsum = goutsum
                binsum = routsum
                ginsum = binsum
                rinsum = ginsum
                i = -radius
                while (i <= radius) {
                    p = pix[yi + Math.min(wm, Math.max(i, 0))]
                    sir = stack[i + radius]
                    sir[0] = p and 0xff0000 shr 16
                    sir[1] = p and 0x00ff00 shr 8
                    sir[2] = p and 0x0000ff
                    rbs = r1 - Math.abs(i)
                    rsum += sir[0] * rbs
                    gsum += sir[1] * rbs
                    bsum += sir[2] * rbs
                    if (i > 0) {
                        rinsum += sir[0]
                        ginsum += sir[1]
                        binsum += sir[2]
                    } else {
                        routsum += sir[0]
                        goutsum += sir[1]
                        boutsum += sir[2]
                    }
                    i++
                }
                stackpointer = radius

                x = 0
                while (x < w) {

                    r[yi] = dv[rsum]
                    g[yi] = dv[gsum]
                    b[yi] = dv[bsum]

                    rsum -= routsum
                    gsum -= goutsum
                    bsum -= boutsum

                    stackstart = stackpointer - radius + div
                    sir = stack[stackstart % div]

                    routsum -= sir[0]
                    goutsum -= sir[1]
                    boutsum -= sir[2]

                    if (y == 0) {
                        vmin[x] = Math.min(x + radius + 1, wm)
                    }
                    p = pix[yw + vmin[x]]

                    sir[0] = p and 0xff0000 shr 16
                    sir[1] = p and 0x00ff00 shr 8
                    sir[2] = p and 0x0000ff

                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]

                    rsum += rinsum
                    gsum += ginsum
                    bsum += binsum

                    stackpointer = (stackpointer + 1) % div
                    sir = stack[stackpointer % div]

                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]

                    rinsum -= sir[0]
                    ginsum -= sir[1]
                    binsum -= sir[2]

                    yi++
                    x++
                }
                yw += w
                y++
            }
            x = 0
            while (x < w) {
                bsum = 0
                gsum = bsum
                rsum = gsum
                boutsum = rsum
                goutsum = boutsum
                routsum = goutsum
                binsum = routsum
                ginsum = binsum
                rinsum = ginsum
                yp = -radius * w
                i = -radius
                while (i <= radius) {
                    yi = Math.max(0, yp) + x

                    sir = stack[i + radius]

                    sir[0] = r[yi]
                    sir[1] = g[yi]
                    sir[2] = b[yi]

                    rbs = r1 - Math.abs(i)

                    rsum += r[yi] * rbs
                    gsum += g[yi] * rbs
                    bsum += b[yi] * rbs

                    if (i > 0) {
                        rinsum += sir[0]
                        ginsum += sir[1]
                        binsum += sir[2]
                    } else {
                        routsum += sir[0]
                        goutsum += sir[1]
                        boutsum += sir[2]
                    }

                    if (i < hm) {
                        yp += w
                    }
                    i++
                }
                yi = x
                stackpointer = radius
                y = 0
                while (y < h) {
                    // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                    pix[yi] = -0x1000000 and pix[yi] or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]

                    rsum -= routsum
                    gsum -= goutsum
                    bsum -= boutsum

                    stackstart = stackpointer - radius + div
                    sir = stack[stackstart % div]

                    routsum -= sir[0]
                    goutsum -= sir[1]
                    boutsum -= sir[2]

                    if (x == 0) {
                        vmin[y] = Math.min(y + r1, hm) * w
                    }
                    p = x + vmin[y]

                    sir[0] = r[p]
                    sir[1] = g[p]
                    sir[2] = b[p]

                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]

                    rsum += rinsum
                    gsum += ginsum
                    bsum += binsum

                    stackpointer = (stackpointer + 1) % div
                    sir = stack[stackpointer]

                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]

                    rinsum -= sir[0]
                    ginsum -= sir[1]
                    binsum -= sir[2]

                    yi += w
                    y++
                }
                x++
            }

            bitmap.setPixels(pix, 0, w, 0, 0, w, h)
            return bitmap
        }

    }
}

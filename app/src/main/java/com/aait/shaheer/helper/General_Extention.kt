package com.kareem.presetntation.helper

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.database.Cursor
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.core.widget.ImageViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fxn.pix.Pix
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.kareem.domain.core.Util
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.net.URLEncoder
import java.util.ArrayList

fun Context.setImg(path:String,target:CircleImageView){

        Glide.with(this).asBitmap()
            .load(path)
            .into(target)

}

fun TextInputEditText.isCorrect():Boolean{
    if (text.toString().isNotBlank() && text.toString().isNotEmpty()){
        Log.e("valid","true")
        return true
    }
    Log.e("valid2","false")
    return false
}
fun AppCompatAutoCompleteTextView.isCorrect():Boolean{
    if ( text.toString().isNotBlank() && text.toString().isNotEmpty()){
        Log.e("valid","true")
        return true
    }
    Log.e("valid2","false")
    return false
}
fun ImageView.tint(color:Int){
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))
}
fun ImageView.setImgFile(pixValue:String,imgName:String): MultipartBody.Part {
    val decodeFile = BitmapFactory.decodeFile(pixValue)
    this.setImageBitmap(decodeFile)
    val file = File(pixValue)
    val imgBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
    return MultipartBody.Part.createFormData(imgName, file.name, imgBody)
}
fun String.getPixPartOnly(imgName:String): MultipartBody.Part {
    val file = File(this)
    val imgBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
    return MultipartBody.Part.createFormData(imgName, file.name, imgBody)
}

 fun MaterialButton.enable() {
    alpha=1.0f
    isEnabled=true
    isClickable = true
}

 fun MaterialButton.disable() {
    alpha=0.5f
    isEnabled=false
    isClickable = false
}

 fun Uri.convertToMultiPart(name: String): MultipartBody.Part {
    val file = File(this.path)
    val imgBody = RequestBody.create("*/*".toMediaTypeOrNull(), file)
    return MultipartBody.Part.createFormData(name, file.name, imgBody)
}

 fun Uri.convertToMultiPartPdf(parm_name: String,context: Context): MultipartBody.Part {
    val file = File(this.path)
     val realName = this.realName(context, file)
     Log.e("display_name",realName)
    val imgBody = RequestBody.create("/".toMediaTypeOrNull(), file)
    return MultipartBody.Part.createFormData(parm_name, URLEncoder.encode(realName, "utf-8"), imgBody)
}

 fun Uri.realName(context: Context,file: File):String{
    var cursor:Cursor?=null
    var displayName=file.name
    try {
        cursor = context.contentResolver.query(this, null, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            displayName =
                cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))!!

        }
    }
finally { cursor?.close() }

    return displayName
}

fun Context.setImg(path:String,target:ImageView){
    Glide.with(this).asBitmap()
        .load(path).into(target)
}
fun DrawerLayout.setNavAction(drawer: View){
    if (this.isDrawerOpen(drawer)) {
        this.closeDrawer(this)
    }
    else {
        this.openDrawer(drawer)
    }

}
fun FragmentManager.replaceFragment(fragment: Fragment,nav_id:Int){
    this.beginTransaction().replace(nav_id, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
}

fun RecyclerView.setRecycler(context: Context,type:Int?=0,gridNum:Int?=2,adapter:RecyclerView.Adapter<*>){

    when(type){
    1->{this.layoutManager=LinearLayoutManager(context).apply {orientation=LinearLayoutManager.HORIZONTAL}}
    2->{this.layoutManager=GridLayoutManager(context,gridNum!!)}
    else->{this.layoutManager=LinearLayoutManager(context)}
    }
    this.adapter=adapter
}
fun Intent?.getPixValues(): ArrayList<String>? {
    return this!!.getStringArrayListExtra(Pix.IMAGE_RESULTS)

}

fun ImageView.rotate(fromDegree: Float, toDegree: Float) {
    rotation=fromDegree
    val pVH = PropertyValuesHolder.ofFloat(View.ROTATION, toDegree)
    val animator = ObjectAnimator.ofPropertyValuesHolder(this, pVH)
    animator.start()
}

fun AppCompatAutoCompleteTextView.setMyAdapter(keys: List<*>, arrayAdapter: ArrayAdapter<*>, isHint:Boolean?=false,onItemSelected:(item:String)->Unit):String
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
        onItemSelected(selection)



    }
    setOnClickListener{
        this@setMyAdapter.showDropDown()
    }
    return selection

}

fun AppCompatAutoCompleteTextView.setMyAdapter(keys: List<*>, arrayAdapter: ArrayAdapter<*>, isHint:Boolean?=false):String
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
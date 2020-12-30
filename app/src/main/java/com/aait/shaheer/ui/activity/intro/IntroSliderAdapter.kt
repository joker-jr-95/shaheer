package com.kareem.presetntation.features.on_boarding

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.viewpager.widget.PagerAdapter
import com.aait.shaheer.R
import com.aait.shaheer.ui.activity.auth.LoginActivity
import com.aait.shaheer.ui.activity.auth.RegisterActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_intro.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class IntroSliderAdapter(val context: Context,val imgs:List<String>,val titles:List<String>,val descs:List<String>) : PagerAdapter() {

    override fun getCount(): Int {
       return imgs.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view==obj as RelativeLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.slider_layout_, container, false)
        val imageView = view.findViewById<ImageView>(R.id.src_img)
        val desc = view.findViewById<TextView?>(R.id.desc)
        val title = view.findViewById<TextView?>(R.id.title)
        val loginBtn = view.findViewById<Button?>(R.id.login_btn)
        val registerBtn = view.findViewById<Button?>(R.id.register_btn)
        Picasso.get().load(imgs[position]).into(imageView)
        desc?.text=descs[position]
        title?.text=titles[position]
        container.addView(view)
        registerBtn!!.onClick {
            context.startActivity(Intent(context, RegisterActivity::class.java))
        }
        loginBtn!!.onClick {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeAllViews()/*removeView(`object` as RelativeLayout)*/
    }
}
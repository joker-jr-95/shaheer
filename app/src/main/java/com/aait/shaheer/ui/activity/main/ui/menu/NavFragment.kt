package com.aait.shaheer.ui.activity.main.ui.menu

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.aait.shaheer.R
import com.aait.shaheer.domain.repository.menu_repository.menuRepository
import com.aait.shaheer.ui.activity.SplashActivity
import com.aait.shaheer.ui.activity.main.ui.dialog.MyDialogFragment
import com.kareem.datalayer.model.common.NavigationModel
import com.aait.shaheer.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_nav.*
import kotlinx.android.synthetic.main.toolbar_normal.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.ArrayList

class NavFragment: BaseFragment() {
    override val viewId: Int
        get() = R.layout.fragment_nav

    override fun init(view: View) {

    }
    companion object{
        private var position: Int=-1
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tv_toolbar.text=getString(R.string.menu)
        setRecycler()
        setActions()
    }

    private fun setRecycler() {
        nav_recycler.layoutManager= LinearLayoutManager(context!!)
        val models = ArrayList<NavigationModel>()
        models.add(NavigationModel(R.string.settings, R.drawable.settings_menu))
        models.add(NavigationModel(R.string.bookmarks, R.drawable.bookmark_white))
        models.add(NavigationModel(R.string.tickets, R.drawable.receipt))
        models.add(NavigationModel(R.string.faqs, R.drawable.text_document))
        models.add(NavigationModel(R.string.terms, R.drawable.clipboard_outline))
        models.add(NavigationModel(R.string.privacy, R.drawable.locked_menu))
        models.add(NavigationModel(R.string.sign_out, R.drawable.logout_menu))


        val listeners = setActionListeners()
        val adapter = NavAdapter(
            context,
            models,
            listeners,
            {
                position =
                    it
            },
            position
        )
        nav_recycler.adapter=adapter

    }

    private fun setActions() {
        arr_back.onClick {
            activity?.onBackPressed()
        }
    }
    private fun setActionListeners(): Navigation_Listeners {
        return object :
            Navigation_Listeners {
            override fun onSettings() {
                startActivity(Intent(activity,SettingsActivity::class.java))
            }

            override fun onBookMark() {
                startActivity(Intent(activity,BookMarksActivity::class.java))
            }

            override fun onTicket() {
                startActivity(Intent(activity,TicketActivity::class.java))
            }

            override fun onFaqs() {
                startActivity(Intent(activity,FaqsActivity::class.java))
            }

            override fun onTerms() {
                startActivity(Intent(activity,TermsActivity::class.java))
            }

            override fun onPrivacy() {
                startActivity(Intent(activity,PrivacyActivity::class.java))
            }

            override fun onLogout() {
                val dialog=MyDialogFragment(getString(R.string.logout_msg),getString(R.string.sign_out),getString(R.string.cancel)) {
                    if (it== MyDialogFragment.ACTION){
                        menuRepository.logout()
                        val intent = Intent(activity, SplashActivity::class.java)
                        intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        activity!!.finish()
                    }

                }
                dialog.show(fragmentManager!!,"logout")
            }

        }
    }
}
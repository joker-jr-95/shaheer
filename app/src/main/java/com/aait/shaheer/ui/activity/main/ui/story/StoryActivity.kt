package com.aait.shaheer.ui.activity.main.ui.story

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.stories_model.Data
import com.aait.shaheer.data_layer.model.stories_model.MyStory
import com.aait.shaheer.data_layer.model.stories_model.YourStory
import com.aait.shaheer.domain.repository.home_repository.homeRepository
import com.aait.shaheer.ui.activity.main.ui.dialog.MyDialogFragment
import com.aait.shaheer.ui.activity.main.ui.other_profile.OtherProfileActivity
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.kareem.domain.core.Util
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.kareem.presetntation.base.ParentActivity
import com.squareup.picasso.Picasso
import jp.shts.android.storiesprogressview.StoriesProgressView
import kotlinx.android.synthetic.main.activity_story.*
import okhttp3.MultipartBody
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import org.jetbrains.anko.sdk27.coroutines.onTouch

class StoryActivity : ParentActivity(), StoriesProgressView.StoriesListener {
    private var isShown: Boolean=true
    private val seenStories: MutableList<Int> = mutableListOf()
    private  var storyId: String = "0"
    private var isPause: Boolean=false
    private var counter: Int=0
    private  var otherStory: YourStory?=null
    private  var myStories: List<MyStory> = mutableListOf()
    var PROGRESS_COUNT=0
    var mImgs:MutableList<MultipartBody.Part> = mutableListOf()

    override val layoutResource: Int
        get() = R.layout.activity_story

    override fun init() {
        if (intent.hasExtra("my_stories")){
            val data = intent.getSerializableExtra("my_stories") as Data
             myStories = data.yourStory

        }
        else{
            otherStory=intent.getSerializableExtra("other_story") as YourStory

        }
        setProfile()
        initStoryBar()
        createStory()
        setActions()

    }

    override fun onResume() {
        super.onResume()
        stories.resume()
        isPause=false
    }

    override fun onStop() {
        super.onStop()
        stories.pause()
        isPause=true
    }

    override fun onDestroy() {
        super.onDestroy()
        stories.destroy()
    }
    private fun setActions() {
        view_lay.onClick {
            stories.pause()
            isPause=true
            val dialog=MyViewerDialog(myStories[counter].viewers){
                stories.resume()
            }
            dialog.show(supportFragmentManager,"story")

        }
        delete_lay.onClick {
            stories.pause()
            isPause=true
            val dialog=MyDialogFragment(getString(R.string.sure_story),getString(R.string.delete),getString(R.string.cancel)){
                if (it==MyDialogFragment.ACTION){
                    sendRequestDeleteStory()
                }
                else{
                    stories.resume()
                }
            }
            dialog.show(supportFragmentManager,"")
        }
        reverse.onClick {
            stories.reverse()
        }

        skip.onClick {
            stories.skip()
        }
        skip.onTouch { v, event ->
            when(event.action){
                MotionEvent.ACTION_UP->{
                    isPause=false
                    stories.resume()
                }
                MotionEvent.ACTION_DOWN->{
                    isPause=true
                    stories.pause()
                }
            }
        }
        reverse.onTouch { v, event ->
            when(event.action){
                MotionEvent.ACTION_UP->{
                    isPause=false
                    stories.resume()
                }
                MotionEvent.ACTION_DOWN->{
                    isPause=true
                    stories.pause()
                }
            }
        }
        rel_bg.onClick {
            if (otherStory!=null){
                startActivity(Intent(applicationContext,OtherProfileActivity::class.java).
                    apply {
                        isShown=false
                        putExtra("profile", otherStory!!.user_story[0].user_id.toInt())
                    })

            }
            else{
                Pix.start(
                    this@StoryActivity,
                    Options.init()
                        .setCount(4)
                        .setExcludeVideos(true)
                        .setRequestCode(200)
                )
            }
        }
    }

    private fun sendRequestDeleteStory() {
        homeRepository.deleteStory(storyId.toInt()).subscribeWithLoading({},{
            stories.skip()
        },{},{})
    }

    private fun initStoryBar() {
        stories?.setStoriesCount(PROGRESS_COUNT) // <- set stories
        stories?.setStoryDuration(3000L) // <- set a story duration
        stories?.setStoriesListener(this) // <- set listener
        stories?.startStories(counter) // <- start progress
        if (!seenStories.contains(storyId.toInt())) {
            seenStories.add(storyId.toInt())
        }
    }

    private fun setProfile() {
        if (otherStory!=null){
            iv_add_icon.visibility= View.GONE
            actions_lay.visibility=View.GONE
            with(otherStory!!) {
                Picasso.get().load(avatar).into(iv_add_story)
                tv_name.text=name
                tv_date.text=user_story[counter].date
                storyId=user_story[counter].story_id
            }
            PROGRESS_COUNT= otherStory!!.user_story.size
        }
        else{
            iv_add_icon.visibility= View.VISIBLE
            actions_lay.visibility=View.VISIBLE
            with(homeRepository.loadUser()!!) {
                Picasso.get().load(avatar).into(iv_add_story)
                tv_name.text=name
                tv_date.text=myStories[counter].date
            }
            storyId=myStories[counter].story_id
            PROGRESS_COUNT= myStories.size
        }
    }

    private fun createStory() {
        if (otherStory!=null){
            Picasso.get().load(otherStory!!.user_story[counter].image).into(iv_story)
        }
        else {
            Picasso.get().load(myStories[counter].image).into(iv_story)
        }
    }

    override fun onComplete() {
        sendRequestSeen()
        if (isShown) {
            finish()
        }
    }

    private fun sendRequestSeen() {
        Log.e("stories",seenStories.toString())

        homeRepository.storySeen(seenStories).subscribeWithLoading({},{},{},{})
    }

    override fun onPrev() {
        if (counter>0) {
            counter--
            createStory()
        }

    }

    override fun onNext() {
        counter++
        if (!seenStories.contains(storyId.toInt())) {
            seenStories.add(storyId.toInt())
        }
        createStory()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("res",resultCode.toString())
        if (resultCode == Activity.RESULT_OK && requestCode == 200) {
            val returnValue =
                data!!.getStringArrayListExtra(Pix.IMAGE_RESULTS)
            for (imagPath in returnValue){
                Util.convertImgToFilePix("images[]",imagPath)?.let { mImgs.add(it) }
            }

            sendRequestStory()
        }

    }


    private fun sendRequestStory() {
        homeRepository.createStory(mImgs).subscribeWithLoading({}
            ,{ it.msg?.let { it1 -> handleData(it1) } },{handleError(it)},{})
    }

    private fun handleError(it: Throwable) {
        // hideProgressDialog()
        it.message?.let { it1 -> toasty(it1,2) }
    }

    private fun handleData(msg: String) {
        // hideProgressDialog()
        toasty(msg)
    }

}
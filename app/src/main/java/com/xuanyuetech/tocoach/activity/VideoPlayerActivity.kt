package com.xuanyuetech.tocoach.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.database.ContentObserver
import android.graphics.Color
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.xuanyuetech.tocoach.R
import com.xuanyuetech.tocoach.adapter.ViewPagerVideoEditorAdapter
import com.xuanyuetech.tocoach.data.DatabaseHelper
import com.xuanyuetech.tocoach.data.GlobalVariable
import com.xuanyuetech.tocoach.data.Student
import com.xuanyuetech.tocoach.data.Video
import com.xuanyuetech.tocoach.fragment.video_player.VideoPlayerInfoEditFragment
import com.xuanyuetech.tocoach.fragment.video_player.VideoPlayerInfoFragment
import com.xuanyuetech.tocoach.util.ActivityUtil
import com.xuanyuetech.tocoach.util.VideoUtil
import com.xuanyuetech.tocoach.util.videoeditor.DisableScrollViewPager
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

/**
 * Video Player Activity
 */
class VideoPlayerActivity : BasicActivity(){

    //region properties

    //data
    private var video : Video? = null
    private var student : Student? = null

    private lateinit var databaseHelper : DatabaseHelper

    //player
    private lateinit var playerView : PlayerView
    private lateinit var player : SimpleExoPlayer
    private lateinit var fullscreenButton : ImageView
    private lateinit var videoToolbar : Toolbar

    //video information view
    private lateinit var videoTimeView : TextView
    private lateinit var studentNameView : TextView

    //viewPager
    private lateinit var videoPlayerInfoFragment : VideoPlayerInfoFragment
    private lateinit var videoPlayerInfoEditFragment : VideoPlayerInfoEditFragment
    private lateinit var viewPager : DisableScrollViewPager

    //video and window sizes
    private var windowsWidth = 0
    private var windowsHeight = 0
    private var videoWidth = 0
    private var videoHeight = 0

    //orientation change
    private var isWiderVideo = false
    private var isFullScreen = false

    //endregion

    //region onCreate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_video_player)

        initView()

        initData()

        bindListeners()

        initializePlayer()

        initViewPager()

        setRotationListener()

        checkOrientationSetting()
    }

    //endregion

    //override fun

    /**
     * ini views
     */
    private fun initView(){
        player = SimpleExoPlayer.Builder(this).build()
        playerView = findViewById(R.id.exo_player)
        fullscreenButton = playerView.findViewById(R.id.exo_fullscreen_icon)
        videoToolbar = findViewById(R.id.video_player_tool_bar)
        videoTimeView = findViewById(R.id.textView_videoPlayer_time)
        studentNameView = findViewById(R.id.textView_videoPlayer_studentName)
        viewPager = findViewById(R.id.video_player_view_pager)

        videoToolbar.title = ""
        videoToolbar.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * init data
     */
    private fun initData(){
        val videoId = ActivityUtil.getVideoIdFromIntent(intent)

        databaseHelper = DatabaseHelper(this)
        video = databaseHelper.findVideoByIdFromID(videoId)
        student = databaseHelper.findStudentById(video!!.studentId)

        if(video == null || student == null){
            Toast.makeText(this, "视频地址错误", Toast.LENGTH_SHORT).show()
            finish()
        }

        //texts
        videoTimeView.text = video!!.initTime
        studentNameView.text = student!!.name

        //get video size
        val videoSize = VideoUtil.getSizeOfVideo(video!!.localUrl, this)
        if(videoSize == null) {
            //which means the video is from raw folder
            videoWidth = 720
            videoHeight = 1380
        }else{
            videoWidth = videoSize.width
            videoHeight = videoSize.height
        }

        isWiderVideo = videoWidth > videoHeight

        //get windows size
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        windowsWidth = size.x
        windowsHeight = size.y
    }

    /**
     * bind listeners
     */
    @SuppressLint("SourceLockedOrientationActivity")
    private fun bindListeners(){

        //full screen button
        fullscreenButton.setOnClickListener{
            if(isFullScreen) {
                isFullScreen = false
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                mPortrait() //in case of not set to portrait if orientation changed not be caught
            }else{
                if(!isWiderVideo) {
                    //if it is a wider video, we want a portrait fullscreen mode
                    mPortraitFullScreen()
                }
                else {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
                isFullScreen = true
            }
        }
    }

    /**
     * init player
     */
    private fun initializePlayer() {
        playerView.player = player

        if(isWiderVideo){
            //if it is a wider video, we want to fixed the width
            playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
        }else{
            playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
        }

        playerView.controllerShowTimeoutMs = 2000
        
        //touch screen to control the visibility
        playerView.setControllerVisibilityListener {
                visibility ->
            if (View.GONE == visibility) { videoToolbar.visibility = View.GONE }
            else if (View.VISIBLE == visibility) { videoToolbar.visibility = View.VISIBLE}
        }

        //prepare the video source
        val dataSourceFactory = DefaultDataSourceFactory(
            applicationContext,
            Util.getUserAgent(applicationContext!!, "myExoPlayer")
        )

        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource( Uri.parse( video!!.localUrl))

        player.prepare(mediaSource)
        player.playWhenReady = false
    }

    /**
     * landscape mode
     */
    private fun mLandScape(){
        changeFullScreenIcon(false)

        //always fixed the height
        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT

        //full screen flags
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

        val params = playerView.layoutParams as RelativeLayout.LayoutParams
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        playerView.layoutParams = params

        checkOrientationSetting()
    }

    /**
     * we want to show in portrait full screen way if video is high
     */
    private fun mPortraitFullScreen(){
        changeFullScreenIcon(false)

        //scaled video width cannot be wider than windows width
        if(windowsWidth >  videoWidth * (windowsHeight.toFloat()/videoHeight.toFloat()) ){
            playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
        }else{
            playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
        }

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)

        val params = playerView.layoutParams as RelativeLayout.LayoutParams
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        playerView.layoutParams = params

        checkOrientationSetting()
    }


    /**
     * portrait mode as normal start mode
     */
    private fun mPortrait(){
        changeFullScreenIcon(true)

        //if it is wider video, fix the width
        if(isWiderVideo){
            playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
        }else{
            playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
        }

        //change all flags back
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

        val params = playerView.layoutParams as RelativeLayout.LayoutParams
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = (250 * applicationContext.resources.displayMetrics.density).toInt()
        playerView.layoutParams = params

        checkOrientationSetting()
    }

    /**
     * change the full screen icon
     */
    private fun changeFullScreenIcon(toOpen : Boolean){
        if(toOpen){
            fullscreenButton.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_open)
            )
        }else{
            fullscreenButton.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_close )
            )
        }
    }


    /**
     * init all fragments and viewPager
     */
    private fun initViewPager(){

        videoPlayerInfoFragment = VideoPlayerInfoFragment(
            video!!.id,
            View.OnClickListener{ viewPager.currentItem = 1 }
        )

        videoPlayerInfoEditFragment = VideoPlayerInfoEditFragment(video!!.id)

        //init view pager adapter
        val mViewPagerAdapter = ViewPagerVideoEditorAdapter(supportFragmentManager)

        mViewPagerAdapter.addFragment( videoPlayerInfoFragment )
        mViewPagerAdapter.addFragment( videoPlayerInfoEditFragment )

        viewPager.adapter = mViewPagerAdapter
    }

    /**
     * auto-rotation setting listener
     */
    private fun setRotationListener(){
        contentResolver.registerContentObserver(Settings.System.getUriFor
            (Settings.System.ACCELEROMETER_ROTATION),
            true, MObserver(Handler(), this) )
    }

    /**
     * listen auto-rotation setting
     */
    class MObserver(handler: Handler, private val act : BasicActivity) : ContentObserver(handler){
        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            if(selfChange) act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
            else act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
        }
    }

    /**
     * check if auto-rotation setting is on or off
     */
    private fun checkOrientationSetting(){
        requestedOrientation = if (Settings.System.getInt(
                contentResolver, Settings.System.ACCELEROMETER_ROTATION, 0) == 1){
            ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        }else{
            val currRotation = resources.configuration.orientation
            if(currRotation == Configuration.ORIENTATION_PORTRAIT){
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }else{
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        }
    }

    //endregion

    //region override

    /**
     * change to dark status bar
     */
    override fun initSystemUI() {
        super.initSystemUI()

        //black color status with light icon
        window.statusBarColor = Color.BLACK
        window.decorView.systemUiVisibility = 0
    }

    /**
     * catch orientation change
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
            mLandScape()
            isFullScreen = true
        }else if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT){
            mPortrait()
            isFullScreen = false
        }
    }

    /**
     * on back pressed override
     */
    override fun onBackPressed() {

        if(viewPager.currentItem == 1){
            //if it is in the edit page, save the edit and back to info page
            if(!videoPlayerInfoEditFragment.saveEdit()) return

            videoPlayerInfoFragment.refreshData()
            viewPager.currentItem = 0

            //have edit, need refresh lists
            setResult(GlobalVariable().RESULT_NEED_REFRESH_STUDENT_LIST_OR_HOME_EVENT_OR_ARCHIVE_LIST)
        }else{
            //should release the file before finish the activity
            player.stop()
            player.release()
            super.onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()
        //pause the video
        findViewById<ImageButton>(R.id.exo_pause).callOnClick()
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }

    //endregion
}

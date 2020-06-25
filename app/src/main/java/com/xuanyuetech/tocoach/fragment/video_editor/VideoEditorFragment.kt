package com.xuanyuetech.tocoach.fragment.video_editor

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.opengl.GLSurfaceView
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.widget.doOnTextChanged
import com.xuanyuetech.tocoach.R
import com.xuanyuetech.tocoach.adapter.ViewPagerVideoEditorAdapter
import com.xuanyuetech.tocoach.data.*
import com.xuanyuetech.tocoach.fragment.BasicFragment
import com.xuanyuetech.tocoach.util.*
import com.xuanyuetech.tocoach.util.setMaxLength
import com.xuanyuetech.tocoach.util.videoeditor.*
import com.getbase.floatingactionbutton.FloatingActionsMenu
import com.jakewharton.threetenabp.AndroidThreeTen
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.qiniu.pili.droid.shortvideo.*
import org.threeten.bp.LocalDateTime
import java.io.File

/**
 * Video Editor Fragment
 */
class VideoEditorFragment constructor(private val inputVideoUri: Uri) :
    BasicFragment(), PLVideoSaveListener {

    //region properties

    private lateinit var databaseHelper: DatabaseHelper

    //video data
    private lateinit var inputFilePath: String
    private lateinit var outputFilePath: String
    private lateinit var mediaFile: PLMediaFile
    private lateinit var coverPath: String
    private var durationMs = 0.0.toLong()

    //用来记录视频的原大小
    private var videoBeginMs = 0f
    private var videoEndMs = 0f

    //GLSurface Preview and video controller
    private lateinit var previewView: GLSurfaceView
    private lateinit var videoEditorController: PLShortVideoEditor
    private lateinit var playbackButton: ImageButton
    private var previewStatus = PreviewStatus.Idle
    private enum class PreviewStatus {
        Idle, Playing, Paused,
    }

    //video frame list
    private lateinit var frameListView: LinearLayout
    private lateinit var currentPosIndicatorView : TextView
    private val sliceCount = 8
    private var frameListAsyncTask: AsyncTask<Void, PLVideoFrame, Void>? = null

    //fab menu
    private lateinit var addRangeBarMenu: FloatingActionsMenu
    private lateinit var addRangBarButtonTrimBtn: ImageButton
    private lateinit var addRangBarButtonSpeedBtn: ImageButton
    private lateinit var addRangBarButtonGraffitiBtn: ImageButton

    //view pager and its fragments
    private lateinit var mViewPager: DisableScrollViewPager
    private lateinit var viewPagerMain: VideoEditorViewPagerMain
    private lateinit var trimFragment: VideoEditorTrimFragment
    private lateinit var speedFragment: VideoEditorSpeedFragment
    private lateinit var graffitiFragment: VideoEditorGraffitiFragment

    //handler related
    private var hasSpeedControl = false

    //max and min control
    private val minLengthOfValidVideo = 5000
    private val maxLengthOfValidVideo = 180000 //3 mins
    private val maxSpeedControllerNum = 2
    private val maxGraffitiControllerNum = 4

    //frame list shadow usage
    private lateinit var videoPlayTrackSeekBar: SeekBar
    private lateinit var seekBarVideoShadowLeft: SeekBar
    private lateinit var seekBarVideoShadowRight: ReversedSeekBar

    // Range helper related
    // 信息都放在整合的list中做保存 主要是为了知道位置
    private lateinit var timeRangeList: ArrayList<VideoEditorTimeRange>
    private var indexInIimeRangeListOfCurrBar = -1
    private var isMovingLeft = false //to help track moving which thumb of range bar

    //watermark setting
    private lateinit var saveWatermarkSetting: PLWatermarkSetting

    //handler to control the video play progress
    //which is controlling the trim and speed dynamic settings
    private var handler = Handler()
    private var speedRangeHasChanged = false //helper to indicate the speed range has been changed
    private var outSpeedRangeHasChanged = false //helper to indicate the normal speed x1.0 has been changed
    private var handlerIndex = 0

    //Dialogs to help saving
    private var saveProgressDialog: AlertDialog? = null
    private var notesDialog: AlertDialog? = null

    //video title and notes
    private var title = ""
    private var notes = ""

    //endregion

    //region Three components properties

    /**
     * TRIMMER
     */
    private var trimBeginMs = 0L
    private var trimEndMs = 0L

    private val trimmerRangeBarListener = object : OnRangeChangedListener {

        override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
            stopTrackPlayProgress()
            isMovingLeft = isLeft

            for (i in 0 until viewPagerMain.rangeBarList.size) {
                if (view.hashCode() == viewPagerMain.rangeBarList[i].rangeSeekBarHashCode) {
                    indexInIimeRangeListOfCurrBar = i
                    break
                }
            }

        }

        override fun onRangeChanged(
            view: RangeSeekBar?,
            leftValue: Float,
            rightValue: Float,
            isFromUser: Boolean
        ) {
            if(isFromUser){

                //moving frame list's shadow
                if (!isMovingLeft) {
                    //move right thumb
                    moveCurrentVideoPosToAndPause(rightValue.toVideoMsLong())
                    seekBarVideoShadowRight.progress = (durationMs.toInt() - rightValue.toInt())
                } else {
                    //move left thumb
                    moveCurrentVideoPosToAndPause(leftValue.toVideoMsLong())
                    seekBarVideoShadowLeft.progress = leftValue.toVideoMsLong().toInt()
                }

                //set indicators
                trimFragment.trimTextLeft.text = VideoEditorUtil.convertMsProgressToString(leftValue)
                trimFragment.trimTextRight.text = VideoEditorUtil.convertMsProgressToString(rightValue)
            }
        }

        override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
            trimBeginMs = view!!.leftSeekBar.progress.toVideoMsLong()
            trimEndMs = view.rightSeekBar.progress.toVideoMsLong()

            //refresh bar in mainPage
            viewPagerMain.rangeBarList[indexInIimeRangeListOfCurrBar].rangeThumbChange(
                view.leftSeekBar.progress,
                view.rightSeekBar.progress
            )

            //since trim's change might change the list of speed control need refresh
            refreshStandardizedSpeedList()
            startTrackPlayProgress()
            playFromTrimBegin()
        }
    }

    /**
     * SPEED
     */

    //speed list to store all speed control
    private lateinit var speedChangeList: ArrayList<VideoEditorTimeRange>

    //保存规格化后的speed
    private lateinit var speedChangeListStandardize: ArrayList<VideoEditorTimeRange>

    //保存变速时间 单位为ms
    private lateinit var speedChangeListTime: ArrayList<Int>
    private lateinit var speedChangeListMerge: ArrayList<VideoEditorTimeRange>

    private val speedRangeBarListener = object : OnRangeChangedListener {
        override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {

            stopTrackPlayProgress()
            isMovingLeft = isLeft

            for (i in 0 until viewPagerMain.rangeBarList.size) {
                if (view.hashCode() == viewPagerMain.rangeBarList[i].rangeSeekBarHashCode) {
                    indexInIimeRangeListOfCurrBar = i
                    break
                }
            }

        }

        override fun onRangeChanged(
            view: RangeSeekBar?,
            leftValue: Float,
            rightValue: Float,
            isFromUser: Boolean
        ) {

            if (isFromUser) {
                speedFragment.speedTextLeft.text = VideoEditorUtil.convertMsProgressToString(leftValue)
                speedFragment.speedTextRight.text = VideoEditorUtil.convertMsProgressToString(rightValue)

                if (!isMovingLeft) {
                    var rangeEnd = (rightValue.toVideoMsLong())
                    if(rightValue>trimEndMs){
                        //force to not larger than endMs
                        rangeEnd = trimEndMs
                    }
                    moveCurrentVideoPosToAndPause(rangeEnd)
                } else {
                    var rangeStart = (leftValue.toVideoMsLong())
                    if(leftValue<trimBeginMs){
                        //force to not smaller than beginMs
                        rangeStart = trimBeginMs
                    }
                    moveCurrentVideoPosToAndPause(rangeStart)
                }

                //force range bar cannot excess than trim range
                if (leftValue < trimBeginMs) {
                    view!!.setProgress(trimBeginMs.toProgressValue(), rightValue)
                    speedFragment.speedTextLeft.text = VideoEditorUtil.convertMsProgressToString(trimBeginMs.toProgressValue())
                } else if (rightValue > trimEndMs) {
                    view!!.setProgress(leftValue, trimEndMs.toProgressValue())
                    speedFragment.speedTextRight.text = VideoEditorUtil.convertMsProgressToString(trimEndMs.toProgressValue())
                }

            }
        }

        override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
            val left = view!!.leftSeekBar.progress
            val right = view.rightSeekBar.progress

            //有新的预览用的range 重新换新
            val indexInTimeRangeList = timeRangeList[indexInIimeRangeListOfCurrBar].indexInItsTypeList
            speedChangeList[indexInTimeRangeList].resetLeftRight(left, right)

            //refresh bar in mainPage
            viewPagerMain.rangeBarList[indexInIimeRangeListOfCurrBar].rangeThumbChange(left, right)

            //重新合并时间
            refreshStandardizedSpeedList()
            moveCurrentVideoPosToAndPause(left.toVideoMsLong())
            startTrackPlayProgress()
        }
    }

    /**
     * GRAFFITI
     */
    //the list to store all graffiti
    private lateinit var graffitiChangeList: ArrayList<VideoEditorTimeRange>

    private val graffitiRangeBarListener = object : OnRangeChangedListener {

        override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
            stopTrackPlayProgress()

            isMovingLeft = isLeft

            for (i in 0 until viewPagerMain.rangeBarList.size) {
                if (view.hashCode() == viewPagerMain.rangeBarList[i].rangeSeekBarHashCode) {
                    indexInIimeRangeListOfCurrBar = i
                    break
                }
            }

            //Set all elements to be whole video to archive the preview during the range change
            val element = graffitiChangeList[timeRangeList[indexInIimeRangeListOfCurrBar].indexInItsTypeList]

            videoEditorController.setViewTimeline(
                element.paintView,
                videoBeginMs.toLong(),
                this@VideoEditorFragment.videoEndMs.toLong()
            )
            for (text in element.stickerTextViewList) {
                videoEditorController.setViewTimeline(
                    text,
                    videoBeginMs.toLong(),
                    this@VideoEditorFragment.videoEndMs.toLong()
                )
            }
            for (image in element.stickerImageViewList) {
                videoEditorController.setViewTimeline(
                    image,
                    videoBeginMs.toLong(),
                    this@VideoEditorFragment.videoEndMs.toLong()
                )
            }
        }

        override fun onRangeChanged(
            view: RangeSeekBar?,
            leftValue: Float,
            rightValue: Float,
            isFromUser: Boolean
        ) {

            if (isFromUser) {

                graffitiFragment.graffitiTextLeft.text = VideoEditorUtil.convertMsProgressToString(leftValue)
                graffitiFragment.graffitiTextRight.text = VideoEditorUtil.convertMsProgressToString(rightValue)

                if (leftValue < trimBeginMs) {
                    view!!.setProgress(trimBeginMs.toProgressValue(), rightValue)
                    graffitiFragment.graffitiTextLeft.text =
                        VideoEditorUtil.convertMsProgressToString(trimBeginMs.toProgressValue())
                } else if (rightValue > trimEndMs) {
                    view!!.setProgress(leftValue, trimEndMs.toProgressValue())
                    graffitiFragment.graffitiTextRight.text =
                        VideoEditorUtil.convertMsProgressToString(trimEndMs.toProgressValue())
                }

                if (!isMovingLeft) {
                    moveCurrentVideoPosToAndPause(view!!.rightSeekBar.progress.toVideoMsLong())
                } else {
                    moveCurrentVideoPosToAndPause(view!!.leftSeekBar.progress.toVideoMsLong())
                }
            }
        }

        override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {

            val indexInGraffitiRangeList = timeRangeList[indexInIimeRangeListOfCurrBar].indexInItsTypeList

            graffitiChangeList[indexInGraffitiRangeList].resetLeftRight(
                view!!.leftSeekBar.progress.toVideoMsLong().toFloat(),
                view.rightSeekBar.progress.toVideoMsLong().toFloat()
            )

            val element = graffitiChangeList[indexInGraffitiRangeList]

            //set all elements time
            videoEditorController.setViewTimeline(
                element.paintView,
                element.left.toVideoMsLong(),
                element.right.toVideoMsLong() - element.left.toVideoMsLong()
            )

            for (text in element.stickerTextViewList) {
                videoEditorController.setViewTimeline(
                    text,
                    element.left.toVideoMsLong(),
                    element.right.toVideoMsLong() - element.left.toVideoMsLong()
                )
            }

            for (image in element.stickerImageViewList) {
                videoEditorController.setViewTimeline(
                    image,
                    element.left.toVideoMsLong(),
                    element.right.toVideoMsLong() - element.left.toVideoMsLong()
                )
            }

            //refresh the bar in mainPage
            viewPagerMain.rangeBarList[indexInIimeRangeListOfCurrBar].rangeThumbChange(
                view.leftSeekBar.progress,
                view.rightSeekBar.progress
            )

            //移动到该graffiti的起始位置进行播放
            if(!isLeft) {
                moveCurrentVideoPosToAndPause( view.leftSeekBar.progress.toVideoMsLong())
            }

            startTrackPlayProgress()
        }
    }

    //endregion

    //region onCreateView

    /**
     * First prepare the views and data
     * Then init Controller
     * Then bind all listeners
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //init the threeTen as req for 七牛
        AndroidThreeTen.init(context)

        databaseHelper = DatabaseHelper(activity!!)

        mView = inflater.inflate(R.layout.fragment_video_editor, container, false)

        initData()

        initViews()

        setupVideoEditorController()

        initFragments()

        setUpViewPager()

        setUpFloatingActionMenu()

        setCustomToolbarButtonListener()

        //start tracking progress
        startTrackPlayProgress()

        //To make sure show the view of the video
        handler.postDelayed({
            startPlayback()
            pausePlayback()
        },500)

        return mView
    }


    //endregion

    //region methods

    /**
     * bind the listener of toolbar buttons
     */
    private fun setCustomToolbarButtonListener() {
        mView.findViewById<ImageButton>(R.id.video_editor_back).setOnClickListener {
            this.onBackPressed()
        }
        mView.findViewById<ImageButton>(R.id.video_editor_save).setOnClickListener {
            finishEdit()
        }
    }

    /**
     * init data
     */
    private fun initData() {

        //video data
        inputFilePath = GetPathFromUri.getPath(context, inputVideoUri)
        outputFilePath = FilePathHelper(context!!).newEditedVideoPath
        coverPath = FilePathHelper(context!!).newVideoCoverPath
        mediaFile = PLMediaFile(inputFilePath)
        durationMs = mediaFile.durationMs

        //check video length
        if (durationMs < minLengthOfValidVideo) {
            showVideoNotQualifiedAndFinish("请选择时长至少 ${minLengthOfValidVideo / 1000} 秒的视频")
        }else if(durationMs > maxLengthOfValidVideo){
            showVideoNotQualifiedAndFinish("请选择时长最长 ${maxLengthOfValidVideo / 60000} 分钟的视频")
        }

        //init controllers req data
        videoEndMs = durationMs.toFloat()
        trimEndMs = durationMs

        //all time range list
        timeRangeList = ArrayList()

        //speed controller
        speedChangeList = ArrayList()
        speedChangeListStandardize = ArrayList()
        speedChangeListTime = ArrayList()
        speedChangeListMerge = ArrayList()

        //graffiti controller
        graffitiChangeList = ArrayList()
    }

    /**
     * init views
     */
    @SuppressLint("SetTextI18n")
    private fun initViews() {
        previewView = mView.findViewById(R.id.preview)
        previewView.setOnClickListener { graffitiFragment.setUnSelected() }
        playbackButton = mView.findViewById(R.id.pause_playback)
        videoPlayTrackSeekBar = mView.findViewById(R.id.seekBar_current_pos)
        seekBarVideoShadowLeft = mView.findViewById(R.id.seekBar_video_shadow_left)
        seekBarVideoShadowRight = mView.findViewById(R.id.seekBar_video_shadow_right)
        currentPosIndicatorView = mView.findViewById(R.id.textView_current)

        mView.findViewById<TextView>(R.id.textView_end).text = " / " +
                VideoEditorUtil.convertMsProgressToString(trimEndMs.toProgressValue())

        //init video frame list
        initVideoFrameListAndShadowSeekBar()
    }


    /**
     * init video frame list
     */
    private fun initVideoFrameListAndShadowSeekBar() {
        frameListView = mView.findViewById(R.id.video_frame_list)

        //Use global layout listener to measure the size of the frame
        frameListView.viewTreeObserver.addOnGlobalLayoutListener(

            object : ViewTreeObserver.OnGlobalLayoutListener {

                override fun onGlobalLayout() {
                    frameListView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    val sliceEdge = frameListView.width / sliceCount

                    //init shadow seekBar and player seekBar when we have width data
                    initVideoPlayTrackSeekBar(sliceEdge)
                    initVideoRangeShadow(sliceEdge, seekBarVideoShadowLeft)
                    initVideoRangeShadow(sliceEdge, seekBarVideoShadowRight)

                    //init frame list
                    val px = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        2.0f,
                        resources.displayMetrics
                    )

                    @SuppressLint("StaticFieldLeak")
                    frameListAsyncTask =
                        object : AsyncTask<Void, PLVideoFrame, Void>() {

                            override fun doInBackground(vararg params: Void?): Void? {
                                for (i in 0 until sliceCount) {
                                    val frame = mediaFile.getVideoFrameByTime(
                                        ((1.0f * i / sliceCount) * durationMs).toLong(),
                                        true,
                                        sliceEdge,
                                        sliceEdge
                                    )
                                    publishProgress(frame)
                                }
                                return null
                            }

                            override fun onProgressUpdate(vararg values: PLVideoFrame?) {
                                super.onProgressUpdate(*values)

                                //edit the bitMap if necessary
                                val frame = values[0]
                                if (frame != null) {
                                    val root =
                                        LayoutInflater.from(context)
                                            .inflate(R.layout.component_video_editor_frame_list, null)

                                    val rotation = frame.rotation
                                    val thumbnail = root.findViewById<ImageView>(R.id.frameImage)
                                    thumbnail.setImageBitmap(frame.toBitmap())
                                    thumbnail.rotation = rotation.toFloat()

                                    val thumbnailLP =
                                        thumbnail.layoutParams as FrameLayout.LayoutParams

                                    if (rotation == 90 || rotation == 270) {
                                        thumbnailLP.leftMargin = px.toInt()
                                        thumbnailLP.rightMargin = px.toInt()
                                    } else {
                                        thumbnailLP.topMargin = px.toInt()
                                        thumbnailLP.bottomMargin = px.toInt()
                                    }

                                    thumbnail.layoutParams = thumbnailLP

                                    val rootLP = LinearLayout.LayoutParams(sliceEdge, sliceEdge)
                                    frameListView.addView(root, rootLP)
                                }
                            }
                        }.execute()
                }
            }
        )
    }

    /**
     * init seekBar to track video play
     */
    private fun initVideoPlayTrackSeekBar(height: Int) {

        //resize the thumb as same size as frame list height
        val thumb = videoPlayTrackSeekBar.thumb
        val offset = videoPlayTrackSeekBar.thumbOffset
        val newThumb = thumb.toBitmap(height = height).toDrawable(resources)
        videoPlayTrackSeekBar.thumb = newThumb
        videoPlayTrackSeekBar.thumbOffset = offset

        videoPlayTrackSeekBar.max = durationMs.toInt()

        videoPlayTrackSeekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {

                        when {
                            progress < trimBeginMs -> {
                                //is moving excess than trim begin
                                moveCurrentVideoPosToAndPause(trimBeginMs)
                            }
                            progress > trimEndMs -> {
                                //is moving excess than trim end
                                moveCurrentVideoPosToAndPause(trimEndMs)
                            }
                            else -> {
                                moveCurrentVideoPosToAndPause(progress.toLong())
                            }
                        }
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    stopTrackPlayProgress()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    startTrackPlayProgress()
                }
            }
        )
    }

    /**
     * move video track seekBar to particular position
     */
    private fun moveCurrentVideoSeekBarTo(toMsInt : Int){
        videoPlayTrackSeekBar.progress = toMsInt
        currentPosIndicatorView.text = VideoEditorUtil.convertMsProgressToString(toMsInt/1000)
    }

    /**
     * move video track seekBar to particular position and pause the video
     */
    private fun moveCurrentVideoPosToAndPause(toMsLong : Long){
        videoEditorController.seekTo(toMsLong.toInt())
        moveCurrentVideoSeekBarTo(toMsLong.toInt())
        pausePlayback()
    }

    /**
     * set up video editor controller by video data
     */
    private fun setupVideoEditorController() {
        val setting = PLVideoEditSetting()
        setting.sourceFilepath = inputFilePath
        setting.destFilepath = outputFilePath
        setting.isKeepOriginFile = true

        videoEditorController = PLShortVideoEditor(previewView)
        videoEditorController.setVideoEditSetting(setting)
        videoEditorController.setVideoSaveListener(this)

        //watermark setting
        saveWatermarkSetting =  createWatermarkSetting()
        videoEditorController.setWatermark(saveWatermarkSetting)
        videoEditorController.updateSaveWatermark(saveWatermarkSetting) // to make sure the watermark has been succussfully added
        videoEditorController.setPlaybackLoop(true)

        //bind playback controller listener
        playbackButton.setOnClickListener {
            if (previewStatus == PreviewStatus.Playing) {
                graffitiFragment.setUnSelected()
                pausePlayback()
            } else {
                graffitiFragment.setUnSelected()
                startPlayback()
            }
        }
    }

    /**
     * init all viewPagers fragments
     */
    private fun initFragments() {
        viewPagerMain = VideoEditorViewPagerMain()
        trimFragment = VideoEditorTrimFragment(this.videoEndMs, trimmerRangeBarListener)
        speedFragment = VideoEditorSpeedFragment(this.videoEndMs, speedRangeBarListener)
        graffitiFragment = VideoEditorGraffitiFragment(
            videoEndMs,
            previewView,
            videoEditorController,
            graffitiRangeBarListener
        )
    }

    /**
     * set up view pager
     */
    private fun setUpViewPager() {

        //init view pager adapter
        val mViewPagerAdapter =
            ViewPagerVideoEditorAdapter(activity!!.supportFragmentManager)

        mViewPagerAdapter.addFragment(viewPagerMain)
        mViewPagerAdapter.addFragment(trimFragment)
        mViewPagerAdapter.addFragment(speedFragment)
        mViewPagerAdapter.addFragment(graffitiFragment)

        //set adapter to ViewPager
        mViewPager = mView.findViewById(R.id.video_editor_view_pager)

        //do not recreate any of the fragment
        mViewPager.offscreenPageLimit = mViewPagerAdapter.count - 1
        mViewPager.adapter = mViewPagerAdapter

        bindViewPagerMainListener()

        bindTrimFragmentListener()

        bindSpeedFragmentListener()

        bindGraffitiFragmentListener()
    }

    /**
     * bind view pager main listeners
     */
    private fun bindViewPagerMainListener(){
        //viewPagerMain to manage all rangeBarList
        viewPagerMain.setOnItemEditClickListener(object :
            VideoEditorViewPagerMain.CustomOnItemEditClickListener {

            override fun onItemEditClickListener(clickedPosition: Int) {

                indexInIimeRangeListOfCurrBar = clickedPosition

                //move to the rangBar type's fragment
                //currentItem is same as controllerType
                mViewPager.currentItem = viewPagerMain.rangeBarList[clickedPosition].controllerType
                addRangeBarMenu.visibility = View.GONE

                when (mViewPager.currentItem) {
                    1 -> {
                        trimFragment.setRangeBarThumb(
                            viewPagerMain.rangeBarList[clickedPosition].startThumb,
                            viewPagerMain.rangeBarList[clickedPosition].endThumb
                        )
                    }
                    2 -> {
                        speedFragment.setRangeBarThumb(
                            viewPagerMain.rangeBarList[clickedPosition].startThumb,
                            viewPagerMain.rangeBarList[clickedPosition].endThumb
                        )
                    }
                    3 -> {
                        val indexOfElementInItsTypeList =
                            timeRangeList[indexInIimeRangeListOfCurrBar].indexInItsTypeList

                        graffitiFragment.paintView = graffitiChangeList[indexOfElementInItsTypeList].paintView
                        graffitiFragment.resetView()
                        graffitiFragment.setRangeBarThumb(
                            viewPagerMain.rangeBarList[clickedPosition].startThumb,
                            viewPagerMain.rangeBarList[clickedPosition].endThumb
                        )
                    }
                }
            }
        })

        //delete rangBar item
        viewPagerMain.setOnItemDeleteListener(object :
            VideoEditorViewPagerMain.CustomOnItemDeleteClickListener {

            override fun onDeleteItemListener(deletedPosition: Int) {
                pausePlayback()
                stopTrackPlayProgress()
                deleteRangeBarConfirm(deletedPosition)
            }
        })
    }

    /**
     * bind trim fragment listener
     */
    private fun bindTrimFragmentListener(){
        //裁剪 range bar 完成监听事件
        trimFragment.setOnFinishButtonClickListener(
            object : VideoEditorTrimFragment.CustomOnFinishButtonClickListener {

                override fun onFinishButtonClickListener() {
                    //reset the rangeBar thumb in the fragment
                    viewPagerMain.rangeBarList[indexInIimeRangeListOfCurrBar].rangeThumbChange(
                        trimFragment.rangeSeekBar.leftSeekBar.progress,
                        trimFragment.rangeSeekBar.rightSeekBar.progress
                    )

                    viewPagerMain.notifyRangBarThumbChange(indexInIimeRangeListOfCurrBar)
                    addRangeBarMenu.visibility = View.VISIBLE
                    mViewPager.currentItem = 0
                }
            }
        )
    }

    /**
     * bind speed fragment listeners
     */
    private fun bindSpeedFragmentListener(){
        //变速 range bar 完成监听事件
        speedFragment.setOnFinishButtonClickListener(
            object : VideoEditorSpeedFragment.CustomOnFinishButtonClickListener {

                override fun onFinishButtonClickListener() {
                    viewPagerMain.rangeBarList[indexInIimeRangeListOfCurrBar].rangeThumbChange(
                        speedFragment.rangeSeekBar.leftSeekBar.progress,
                        speedFragment.rangeSeekBar.rightSeekBar.progress
                    )
                    viewPagerMain.notifyRangBarThumbChange(indexInIimeRangeListOfCurrBar)
                    mViewPager.currentItem = 0
                    addRangeBarMenu.visibility = View.VISIBLE
                }
            })

        //变速 改变变速大小 监听事件
        speedFragment.setSpeedTypeChangeListener(
            object :
                VideoEditorSpeedFragment.SpeedTypeChangeListener {

                override fun speedTypeChangeClickListener(speed: Double) {
                    stopTrackPlayProgress()
                    speedChangeList[timeRangeList[indexInIimeRangeListOfCurrBar].indexInItsTypeList].speedType = speed
                    refreshStandardizedSpeedList()
                    playFromTrimBegin()
                    startTrackPlayProgress()
                }
            }
        )
    }

    /**
     * bind graffiti fragment listeners
     */
    private fun bindGraffitiFragmentListener(){
        //绘图 range bar 完成监听事件
        graffitiFragment.setOnFinishButtonClickListener(
            object : VideoEditorGraffitiFragment.CustomOnFinishedButtonClickListener {

                override fun onGraffitiFinishedListener() {
                    graffitiFragment.setUnSelected()
                    viewPagerMain.rangeBarList[indexInIimeRangeListOfCurrBar].rangeThumbChange(
                        graffitiFragment.rangeSeekBar.leftSeekBar.progress,
                        graffitiFragment.rangeSeekBar.rightSeekBar.progress
                    )
                    viewPagerMain.notifyRangBarThumbChange(indexInIimeRangeListOfCurrBar)
                    addRangeBarMenu.visibility = View.VISIBLE
                    mViewPager.currentItem = 0
                }
            }
        )

        //绘图 添加text
        graffitiFragment.setOnTextCreateListener(object :
            VideoEditorGraffitiFragment.CustomOnTextCreateListener {
            override fun onTextCreateListener(text: StickerTextView) {

                val indexInItsTypeList = timeRangeList[indexInIimeRangeListOfCurrBar].indexInItsTypeList
                graffitiChangeList[indexInItsTypeList].addText(text)

                val left = graffitiChangeList[indexInItsTypeList].left
                val right = graffitiChangeList[indexInItsTypeList].right

                if (left != trimBeginMs.toProgressValue() || right != trimEndMs.toProgressValue()) {
                    videoEditorController.setViewTimeline(
                        text,
                        left.toLong(),
                        (right - left).toLong()
                    )
                }
            }
        })
        //绘图 添加image
        graffitiFragment.setOnImageCreateListener(object :
            VideoEditorGraffitiFragment.CustomOnImageCreateListener {
            override fun onImageCreateListener(image: StickerImageView) {
                val indexInItsTypeList = timeRangeList[indexInIimeRangeListOfCurrBar].indexInItsTypeList
                graffitiChangeList[indexInItsTypeList].addImage(image)
                val left = graffitiChangeList[indexInItsTypeList].left
                val right = graffitiChangeList[indexInItsTypeList].right
                if (left != trimBeginMs.toProgressValue() || right != trimEndMs.toProgressValue()) {
                    videoEditorController.setViewTimeline(
                        image,
                        left.toLong(),
                        (right - left).toLong()
                    )
                }
            }
        })
        //绘图删除 sticker
        graffitiFragment.setOnStickerDeleteListener(object :
            VideoEditorGraffitiFragment.CustomOnStickerDeleteListener {
            override fun onStickerDeleteListener(view: View) {
                val indexInItsTypeList = timeRangeList[indexInIimeRangeListOfCurrBar].indexInItsTypeList
                if (view is StickerTextView) {
                    graffitiChangeList[indexInItsTypeList].removeText(view)
                } else {
                    graffitiChangeList[indexInItsTypeList].removeImage(view as StickerImageView)
                }
            }
        })
    }

    /**
     * setup floating action menu
     */
    private fun setUpFloatingActionMenu() {
        addRangeBarMenu = mView.findViewById(R.id.video_editor_floating_menu)
        addRangBarButtonTrimBtn = mView.findViewById(R.id.video_editor_floating_btn_trim)
        addRangBarButtonSpeedBtn = mView.findViewById(R.id.video_editor_floating_btn_speed)
        addRangBarButtonGraffitiBtn = mView.findViewById(R.id.video_editor_floating_btn_graffiti)

        //add trim button
        addRangBarButtonTrimBtn.setOnClickListener {

            pausePlayback()

            //refresh viewPagerMain
            viewPagerMain.rangeBarList.add(
                RangeBar(
                    1,
                    videoBeginMs,
                    this.videoEndMs,
                    trimmerRangeBarListener
                )
            )
            viewPagerMain.videoEditorRangeBarAdapter.notifyItemInserted(viewPagerMain.rangeBarList.size - 1)

            indexInIimeRangeListOfCurrBar = viewPagerMain.rangeBarList.size - 1

            //update time range list
            timeRangeList.add(VideoEditorTimeRange(1,0))

            //因为fragment在初始化时用的是最原始的时常
            //所以进入时需要变成现在可能经过改变后的时常
            trimFragment.setRangeBarThumb(
                trimBeginMs.toProgressValue(),
                trimEndMs.toProgressValue()
            )

            //open edit fragment
            mViewPager.currentItem = 1

            //only allowed once
            addRangBarButtonTrimBtn.visibility = View.GONE
            addRangeBarMenu.collapse()
            addRangeBarMenu.visibility = View.GONE
        }

        //add speed button
        addRangBarButtonSpeedBtn.setOnClickListener {
            pausePlayback()

            //have to stop the progress track since need to recalculate the speed controller
            stopTrackPlayProgress()

            viewPagerMain.rangeBarList.add(
                RangeBar(
                    2,
                    videoBeginMs,
                    this.videoEndMs,
                    speedRangeBarListener
                )
            )

            viewPagerMain.videoEditorRangeBarAdapter.notifyItemInserted(viewPagerMain.rangeBarList.size - 1)

            indexInIimeRangeListOfCurrBar = viewPagerMain.rangeBarList.size - 1

            //默认速度为0.5
            speedChangeList.add(
                VideoEditorTimeRange(
                    2,
                    trimBeginMs.toProgressValue(),
                    trimEndMs.toProgressValue(),
                    0.5
                )
            )

            //重新计算speed控制器
            refreshStandardizedSpeedList()

            timeRangeList.add(
                VideoEditorTimeRange(
                    2,
                    speedChangeList.size - 1
                )
            )

            //因为fragment在初始化时用的是最原始的时常
            //所以进入时需要变成现在可能经过改变后的时常
            speedFragment.setRangeBarThumb(
                trimBeginMs.toProgressValue(),
                trimEndMs.toProgressValue()
            )

            hasSpeedControl = true
            startTrackPlayProgress()

            //move to the speed fragment
            mViewPager.currentItem = 2

            if(speedChangeList.size >= maxSpeedControllerNum) addRangBarButtonSpeedBtn.visibility = View.GONE
            addRangeBarMenu.collapse()
            addRangeBarMenu.visibility = View.GONE
        }

        //add graffiti button
        addRangBarButtonGraffitiBtn.setOnClickListener {

            pausePlayback()

            viewPagerMain.rangeBarList.add(
                RangeBar(
                    3,
                    videoBeginMs,
                    this.videoEndMs,
                    graffitiRangeBarListener
                )
            )

            viewPagerMain.videoEditorRangeBarAdapter.notifyItemInserted(viewPagerMain.rangeBarList.size - 1)
            indexInIimeRangeListOfCurrBar = viewPagerMain.rangeBarList.size - 1

            //添加一块画布
            graffitiFragment.addPaint()

            //信息与画布都放在专属的list中做保存
            graffitiChangeList.add(
                VideoEditorTimeRange(
                    3,
                    trimBeginMs.toProgressValue(),
                    trimEndMs.toProgressValue(),
                    graffitiFragment.paintView
                )
            )

            timeRangeList.add(
                VideoEditorTimeRange(
                    3,
                    graffitiChangeList.size - 1
                )
            )

            graffitiFragment.setRangeBarThumb(
                trimBeginMs.toProgressValue(),
                trimEndMs.toProgressValue()
            )

            mViewPager.currentItem = 3

            if(graffitiChangeList.size >= maxGraffitiControllerNum) addRangBarButtonGraffitiBtn.visibility = View.GONE
            graffitiFragment.resetView()
            addRangeBarMenu.collapse()
            addRangeBarMenu.visibility = View.GONE
        }
    }


    /**
     * pause the video
     */
    private fun pausePlayback() {
        videoEditorController.pausePlayback()
        previewStatus = PreviewStatus.Paused
        playbackButton.setImageResource(R.drawable.ic_play_arrow_black_24dp)
    }

    /**
     * stop play back
     */
    private fun stopPlayBack() {
        videoEditorController.stopPlayback()
        previewStatus = PreviewStatus.Idle
        playbackButton.setImageResource(R.drawable.ic_play_arrow_black_24dp)
    }


    /**
     * play the video
     */
    private fun startPlayback() {
        if (previewStatus == PreviewStatus.Idle) {
            videoEditorController.startPlayback()
            previewStatus = PreviewStatus.Playing
        } else if (previewStatus == PreviewStatus.Paused) {
            videoEditorController.resumePlayback()
            previewStatus = PreviewStatus.Playing
        }
        playbackButton.setImageResource(R.drawable.ic_pause_black_24dp)
    }

    /**
     * init the shadow of video frame list
     * resize the height of the seekBar to make it be like a shadow
     */
    @SuppressLint("RtlHardcoded")
    private fun initVideoRangeShadow(height: Int, seekBar: SeekBar) {
        val width = 1

        //dark grey as progress color
        var shape = GradientDrawable(
            GradientDrawable.Orientation.BOTTOM_TOP,
            intArrayOf(
                ContextCompat.getColor(context!!, R.color.colorBlack_70),
                ContextCompat.getColor(context!!, R.color.colorBlack_70)
            )
        )

        shape.cornerRadius = 0f
        shape.setSize(width, height)
        shape.setBounds(0, 0, width, height)

        val clip = ClipDrawable(shape, Gravity.LEFT, ClipDrawable.HORIZONTAL)

        //transparent as default seekBar color
        shape = GradientDrawable(
            GradientDrawable.Orientation.BOTTOM_TOP,
            intArrayOf(
                ContextCompat.getColor(context!!, R.color.transparent),
                ContextCompat.getColor(context!!, R.color.transparent)
            )
        )

        shape.cornerRadius = 0f //change the corners of the rectangle

        shape.setSize(width, height)
        shape.setBounds(0, 0, width, height)

        val progressLayer = LayerDrawable(arrayOf(shape, clip))

        seekBar.progressDrawable = progressLayer
        seekBar.max = durationMs.toInt()
    }

    /**
     * play the video from video begin
     */
    private fun playFromTrimBegin() {
        videoEditorController.seekTo(trimBeginMs.toInt())
    }

    /**
     * handler to control the progress (trimRange, speed) since trim range and speed cannot be in preview
     */
    private fun startTrackPlayProgress() {
        stopTrackPlayProgress()

        handler.postDelayed(object : Runnable {
            override fun run() {

                //since seekTo will make video start play, we need a helper to check progress
                var needCheckPause = false

                //bind seekBar
                moveCurrentVideoSeekBarTo(videoEditorController.currentPosition)

                //track trimmer
                if (videoEditorController.currentPosition >= trimEndMs) {
                    videoEditorController.seekTo(trimBeginMs.toInt())
                } else if (videoEditorController.currentPosition < trimBeginMs) {
                    videoEditorController.seekTo(trimBeginMs.toInt())
                    needCheckPause = true
                }

                //control the speed range
                if (speedChangeList.isNotEmpty()) {
                    if (
                        videoEditorController.currentPosition >= speedChangeListStandardize[handlerIndex].left.toInt()
                        && videoEditorController.currentPosition < speedChangeListStandardize[handlerIndex].right.toInt()
                        && !speedRangeHasChanged
                    ) {
                        videoEditorController.setSpeed(speedChangeListStandardize[handlerIndex].speedType, true)
                        handlerIndex = (handlerIndex + 1) % speedChangeListStandardize.size

                        needCheckPause = true
                        speedRangeHasChanged = true

                    } else if (speedChangeListStandardize.size!=1 &&
                        videoEditorController.currentPosition >= speedChangeListStandardize[handlerIndex].left.toInt()
                        && videoEditorController.currentPosition < speedChangeListStandardize[handlerIndex].right.toInt()
                    ) {
                        //这个if只是用来管有没有进入下一个handler
                        speedRangeHasChanged = false
                    }
                } else {
                    if (speedRangeHasChanged) {
                        videoEditorController.setSpeed(1.0, true)
                        speedRangeHasChanged = false
                        outSpeedRangeHasChanged = false
                        needCheckPause = true
                    }
                }

                if (needCheckPause && (previewStatus == PreviewStatus.Paused || previewStatus == PreviewStatus.Idle)) {
                    pausePlayback()
                }

                //need a loop to always run
                handler.post(this)
            }
        }, 100)
    }

    /**
     * stop handler progress control
     */
    private fun stopTrackPlayProgress() {
        relocateHandlerIndex()
        speedRangeHasChanged = false
        outSpeedRangeHasChanged = false
        handler.removeCallbacksAndMessages(null)

    }

    /**
     * 优化算法来判断下一个变速区间
     */
    private fun relocateHandlerIndex(){
        for(i in 0 until speedChangeListStandardize.size){
            if(videoEditorController.currentPosition >= speedChangeListStandardize[i].left.toInt()
                && videoEditorController.currentPosition < speedChangeListStandardize[i].right.toInt()){
                handlerIndex = i
                break
            }
        }
    }

    /**
     * refresh the speed setting lists
     */
    private fun refreshStandardizedSpeedList(){
        speedRangeHasChanged = false
        outSpeedRangeHasChanged = false
        speedChangeListMerge.clear()
        speedChangeListMerge = merge(speedChangeList)
        speedChangeListStandardize.clear()
        speedChangeListStandardize = standardizeSpeedList(speedChangeListMerge)
    }

    /**
     * 创造符合保存和播放适宜的array list
     */
    private fun standardizeSpeedList(arr: ArrayList<VideoEditorTimeRange>):ArrayList<VideoEditorTimeRange>{
        val result: ArrayList<VideoEditorTimeRange> = ArrayList()

        for (i in 0 until arr.size) {
            //先检查首位
            val left = arr[i].left
            val right = arr[i].right
            if (i == 0) {
                if (arr[i].left != trimBeginMs.toProgressValue()) {
                    //代表第一位和开头之间有gap
                    result.add(
                        VideoEditorTimeRange(
                            trimBeginMs.toFloat(),
                            left,
                            1.0
                        )
                    )
                    result.add(
                        VideoEditorTimeRange(
                            left,
                            right,
                            arr[i].speedType
                        )
                    )
                } else {
                    result.add(
                        VideoEditorTimeRange(
                            left,
                            right,
                            arr[i].speedType
                        )
                    )
                }
                if (arr.size == 1 && right < trimEndMs.toProgressValue()) {
                    //如果只有一个speed 而且和结尾有gap
                    result.add(
                        VideoEditorTimeRange(
                            right,
                            trimEndMs.toFloat(),
                            1.0
                        )
                    )
                }
            } else if (i == arr.size - 1) {
                //最后一位
                if (left != arr[i - 1].right) {
                    //代表最后一位到上一位有gap
                    result.add(
                        VideoEditorTimeRange(
                            arr[i - 1].right,
                            left,
                            1.0
                        )
                    )
                    result.add(
                        VideoEditorTimeRange(
                            left,
                            right,
                            arr[i].speedType
                        )
                    )
                } else {
                    //代表最后一位到结尾有gap
                    if (arr[i].right != trimEndMs.toProgressValue()) {
                        result.add(
                            VideoEditorTimeRange(
                                left,
                                right,
                                speedChangeListMerge[i].speedType
                            )
                        )
                        result.add(
                            VideoEditorTimeRange(
                                right,
                                trimEndMs.toFloat(),
                                1.0
                            )
                        )
                    } else {
                        result.add(
                            VideoEditorTimeRange(
                                left,
                                right,
                                arr[i].speedType
                            )
                        )
                    }
                }
            } else {
                //其他位置
                if (left != arr[i - 1].right) {
                    //如果和上一位有gap
                    result.add(
                        VideoEditorTimeRange(
                            arr[i - 1].right,
                            left,
                            1.0
                        )
                    )
                    result.add(
                        VideoEditorTimeRange(
                            left,
                            right,
                            arr[i].speedType
                        )
                    )
                } else {
                    result.add(
                        VideoEditorTimeRange(
                            left,
                            right,
                            arr[i].speedType
                        )
                    )
                }
            }
        }
        return result
    }

    /**
     * add speed range to the video controller
     */
    private fun saveSpeedRangeSetting() {
        val speedTimeRanges = ArrayList<PLSpeedTimeRange>()

        for (element in speedChangeListStandardize) {
            speedTimeRanges.add(PLSpeedTimeRange(element.speedType,element.left.toLong(),element.right.toLong()))
        }
        videoEditorController.setSpeedTimeRanges(speedTimeRanges)
    }

    /**
     * finish the edit and want to save it
     */
    @SuppressLint("InflateParams")
    private fun finishEdit() {

        graffitiFragment.setUnSelected()
        pausePlayback()
        stopTrackPlayProgress()

        //show notes dialog before saving the video
        if (notesDialog == null) {
            val inflater = activity!!.layoutInflater
            val builder: AlertDialog.Builder =
                AlertDialog.Builder(activity!!, R.style.CustomDialogTheme)
            val view: View = inflater.inflate(R.layout.fragment_video_editor_note, null)
            builder
                .setView(view)
                .setPositiveButton("保存") { dialog, _ ->
                    saveNotesChange()
                    dialog.cancel()
                    startSaveVideo()
                }
                .setNegativeButton("取消") { dialog, _ ->
                    //need start back the track progress
                    startTrackPlayProgress()
                    dialog.cancel()
                }
                .setOnCancelListener {
                    saveNotesChange()
                }

            notesDialog = builder.create()
            notesDialog!!.setCanceledOnTouchOutside(false)
            notesDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        }
        notesDialog!!.show()


        //set back the title and notes
        val titleText = notesDialog!!.findViewById<EditText>(R.id.video_title)
        titleText.setMaxLength(Video().maxTitleLength)
        notesDialog!!.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context!!.getColorCompat(R.color.colorBlack_20))
        notesDialog!!.getButton(DialogInterface.BUTTON_POSITIVE).isEnabled = false
        titleText.doOnTextChanged { text, _, _, _ ->
            if(text!=null && text.isNotBlank()){
                notesDialog!!.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context!!.getColorCompat(R.color.colorBlack_70))
                notesDialog!!.getButton(DialogInterface.BUTTON_POSITIVE).isEnabled = true
            }else{
                notesDialog!!.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context!!.getColorCompat(R.color.colorBlack_20))
                notesDialog!!.getButton(DialogInterface.BUTTON_POSITIVE).isEnabled = false
            }
        }
        titleText!!.setText(title)
        notesDialog!!.findViewById<EditText>(R.id.video_notes)!!.setText(notes)
    }

    /**
     * save title and notes change
     */
    private fun saveNotesChange() {
        title = notesDialog!!.findViewById<EditText>(R.id.video_title)!!.text.toString()
        notes = notesDialog!!.findViewById<EditText>(R.id.video_notes)!!.text.toString()
    }

    /**
     * start save video
     */
    private fun startSaveVideo() {
        if (saveProgressDialog == null) initProgressDialog()
        saveProgressDialog!!.show()

        //save current options setting
        videoEditorController.setVideoRange(trimBeginMs, trimEndMs)

        if (speedChangeList.isNotEmpty()) {
            saveSpeedRangeSetting()
        } else {
            videoEditorController.setSpeed(1.0)
        }

        videoEditorController.save(object : PLVideoFilterListener {
            override fun onDrawFrame(
                texId: Int,
                texWidth: Int,
                texHeight: Int,
                timestampNs: Long,
                transformMatrix: FloatArray?
            ): Int {
                return texId
            }

            override fun onSurfaceChanged(p0: Int, p1: Int) {
            }

            override fun onSurfaceCreated() {
            }

            override fun onSurfaceDestroy() {
            }
        })

    }

    /**
     * init the save progress dialog
     */
    @SuppressLint("InflateParams")
    private fun initProgressDialog() {
        val inflater = activity!!.layoutInflater
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity!!)
        val view: View = inflater.inflate(R.layout.dialog_video_editor_save_progress, null)
        builder.setView(view)
            .setOnCancelListener { onSaveVideoCanceled() }
        saveProgressDialog = builder.create()
        saveProgressDialog!!.setCanceledOnTouchOutside(false)
    }

    /**
     * update the progress status in progress dialog
     */
    private fun updateSaveProgressStatus(status: String) {
        saveProgressDialog!!.findViewById<TextView>(R.id.textView_progress_status)!!.text = status
    }

    /**
     * watermark size setting calculation
     */
    private fun createWatermarkSetting(): PLWatermarkSetting {

        val videoSize = VideoUtil.getSizeOfVideo(inputFilePath, context!!)
        val videoHeight = videoSize!!.height
        val videoWidth = videoSize.width

        val watermarkWidthPer: Float
        val watermarkHeightPer: Float

        if(videoWidth > videoHeight){
            watermarkHeightPer = 0.1f
            watermarkWidthPer = watermarkHeightPer * videoHeight * 4 / videoWidth
        }else{
            watermarkWidthPer =  0.4f
            watermarkHeightPer = videoWidth * watermarkWidthPer / 4 / videoHeight
        }

        val watermarkSetting = PLWatermarkSetting()
        watermarkSetting.resourceId = R.drawable.coach_archive_bucket_logo_small
        watermarkSetting.setPosition(0.99f, 0.01f)
        watermarkSetting.setSize(watermarkWidthPer, watermarkHeightPer)
        watermarkSetting.alpha = 150
        return watermarkSetting
    }

    /**
     * save the video cover
     */
    private fun saveCover(): Boolean {
        val coverFrameBitMap =
            mediaFile.getVideoFrameByTime((trimBeginMs + trimEndMs) / 2, true).toBitmap()
        return ImageUtil.reduceSizeOfImageAndSave(coverFrameBitMap, coverPath, 300)
    }

    /**
     * post data to database
     */
    private fun saveVideoData() {
        val folderId = arguments!!.getInt("folder_id")
        val createdTime = LocalDateTime.now()

        val video = Video(
            folderId = folderId,
            initTime = createdTime,
            title = title,
            cloudUrl = "",
            localUrl = outputFilePath,
            notes = notes,
            coverUrl = coverPath,
            folderName = databaseHelper.findFolderById(folderId)!!.name
        )
        DataHelper().addVideo(video, databaseHelper)
    }

/*
//TODO:算法优化 未来更新
private fun merge_test(arr: ArrayList<VideoEditorTimeRange>): ArrayList<VideoEditorTimeRange> {
val result: ArrayList<VideoEditorTimeRange> = ArrayList()
return result
}
*/

    /**
     *合并变速区段
     */
    private fun merge(arr: ArrayList<VideoEditorTimeRange>): ArrayList<VideoEditorTimeRange> {
        val result: ArrayList<VideoEditorTimeRange> = ArrayList()

        //只有一个元素 不需要合并
        if (arr.size == 1) {
            result.add(arr[0])
            return result
        }

        //新建tmp动态数组
        //因为speedChangeList顺序不能变
        //创建arr用来排序
        val sortedArr: ArrayList<VideoEditorTimeRange> = ArrayList()
        sortedArr.addAll(arr)

        //start排序 speed其次
        sortedArr.sortWith(compareBy({ it.left }, { it.speedType }))

        //用来保存结果list
        val len: Int = arr.size

        // 合并重叠区间
        var i = 0

        var slow: VideoEditorTimeRange
        var fast: VideoEditorTimeRange
        val times = (len / 2)
        for (a in 0 until times) {
            while (i < len - 1) {

                //速度不一致 分开讨论
                if (sortedArr[i].speedType != sortedArr[i + 1].speedType) {
                    //这个东西真的麻烦
                    //获得速度不相同的实例
                    if (sortedArr[i].speedType > sortedArr[i + 1].speedType) {
                        slow = sortedArr[i + 1]
                        fast = sortedArr[i]
                    } else {
                        slow = sortedArr[i]
                        fast = sortedArr[i + 1]
                    }

                    /**
                     * l---------l s
                     *   l----------l f
                     *  l--------l f
                     */
                    if (slow.left <= fast.left && (slow.right >= fast.left)) {
                        result.add(
                            VideoEditorTimeRange(
                                slow.left,
                                slow.right,
                                slow.speedType
                            )
                        )
                        /**
                         * l------l s
                         * l--------l f
                         *   l--------l f
                         */
                        if (slow.right < fast.right) {
                            result.add(
                                VideoEditorTimeRange(
                                    slow.right,
                                    fast.right,
                                    fast.speedType
                                )
                            )
                        }
                    }
                    /**
                     *   l--------l s
                     * l-------l f
                     * l----------l f
                     */
                    else if (slow.left > fast.left && slow.left <= fast.right) {
                        result.add(
                            VideoEditorTimeRange(
                                fast.left,
                                slow.left,
                                fast.speedType
                            )
                        )
                        result.add(
                            VideoEditorTimeRange(
                                slow.left,
                                slow.right,
                                slow.speedType
                            )
                        )
                        /**
                         *   l---l s
                         * l-------l f
                         */
                        if (slow.right < fast.right) {
                            result.add(
                                VideoEditorTimeRange(
                                    slow.right,
                                    fast.right,
                                    fast.speedType
                                )
                            )
                        }
                    } else {
                        result.add(sortedArr[i])
                        result.add(sortedArr[i + 1])
                    }
                }

                //速度一致 一般合并
                else {
                    //只是为了复用 减少变量使用 名字无实质含义
                    slow = sortedArr[i]
                    fast = sortedArr[i + 1]
                    if (fast.left > slow.right) {
                        result.add(slow)
                        result.add(fast)
                    } else {
                        if (fast.right <= slow.right) {
                            result.add(
                                VideoEditorTimeRange(
                                    slow.left,
                                    slow.right,
                                    slow.speedType
                                )
                            )
                        } else {
                            result.add(
                                VideoEditorTimeRange(
                                    slow.left,
                                    fast.right,
                                    slow.speedType
                                )
                            )
                        }
                    }
                }

                //算法一次要采用两个index 故 +=2
                i += 2
            }
        }

        return result
    }

    /**
     * TODO:可以考虑去除timeRangeList省去排序等操作
     * 对于timeRangeList进行排序
     * 因为删除可能删除的不是最后一位
     * 那么需要更新相对应的typePostion
     */
    private fun sortTimeRangeList(type: Int) {
        var countType = 0
        var indexNum = 0
        for (element in timeRangeList) {
            if (element.typeRangeBar == type) {
                countType++
            }
        }
        if (countType == 0) {
            return
        } else {
            for (element in timeRangeList) {
                if (element.typeRangeBar == type) {
                    element.indexInItsTypeList = indexNum++
                }
            }
        }
    }


    /**
     * want to give up the change
     */
    private fun giveUpChange() {
        val dialogBuilder = AlertDialog.Builder(activity!!, R.style.CustomDialogTheme)
            .setCancelable(true)
            .setTitle("退出")
            .setMessage("确定要放弃更改吗?")
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
                startTrackPlayProgress()
            }
            .setPositiveButton("确定") { dialog, _ ->
                dialog.dismiss()
                stopPlayBack()
                mediaFile.release()
                activity!!.setResult(Activity.RESULT_CANCELED)
                activity!!.finish()
            }

        dialogBuilder.show()
    }

    /**
     * want to delete rangeBar
     */
    private fun deleteRangeBarConfirm(position: Int) {
        val dialogBuilder = AlertDialog.Builder(activity!!, R.style.CustomDialogTheme)
            .setTitle("警告")
            .setMessage("你确定要删除 ${viewPagerMain.rangeBarList[position].labelText} 吗?")
            .setCancelable(false)
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
                startTrackPlayProgress()
            }
            .setPositiveButton("确定") { dialog, _ ->
                dialog.dismiss()
                
                val indexInItsTypeList = timeRangeList[position].indexInItsTypeList
                
                //删除对应的表中的值
                when (viewPagerMain.rangeBarList[position].controllerType) {
                    1 -> {
                        //trim

                        //remove trim should reset all related bar
                        trimBeginMs = videoBeginMs.toLong()
                        trimEndMs = videoEndMs.toLong()
                        timeRangeList.removeAt(position)

                        seekBarVideoShadowLeft.progress = 0
                        seekBarVideoShadowRight.progress = 0

                        //remove trim might affect speed
                        refreshStandardizedSpeedList()
                        addRangBarButtonTrimBtn.visibility = View.VISIBLE
                    }

                    2 -> {
                        //speed

                        speedChangeList.removeAt(indexInItsTypeList)
                        timeRangeList.removeAt(position)

                        //need recalculate the speed
                        refreshStandardizedSpeedList()
                        if (speedChangeList.size == 0)  {
                            hasSpeedControl = false
                            videoEditorController.setSpeed(1.0, true)
                            pausePlayback()
                        }
                        sortTimeRangeList(2)

                        addRangBarButtonSpeedBtn.visibility = View.VISIBLE
                    }

                    3 -> {
                        //graffiti

                        videoEditorController.removePaintView(graffitiChangeList[indexInItsTypeList].paintView)
                        for (element in graffitiChangeList) {

                            for (text in element.stickerTextViewList) {
                                videoEditorController.removeTextView(text as PLTextView)
                            }
                            for (image in element.stickerImageViewList) {
                                videoEditorController.removeImageView(image as PLImageView)
                            }

                        }

                        graffitiChangeList.removeAt(indexInItsTypeList)
                        timeRangeList.removeAt(position)
                        sortTimeRangeList(3)

                        addRangBarButtonGraffitiBtn.visibility = View.VISIBLE
                    }
                }

                moveCurrentVideoPosToAndPause(trimBeginMs)

                viewPagerMain.rangeBarList.removeAt(position)

                viewPagerMain.notifyRangBarDelete()

                startTrackPlayProgress()
            }

        dialogBuilder.show()
    }

    /**
     * show video not qualified and finish the activity
     */
    private fun showVideoNotQualifiedAndFinish(string: String) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
        activity!!.setResult(Activity.RESULT_CANCELED)
        activity!!.finish()
    }

    //endregion

    //region override SaveVideo interface

    override fun onSaveVideoCanceled() {
        videoEditorController.cancelSave()
        updateSaveProgressStatus("0%")
        saveProgressDialog!!.dismiss()
        startTrackPlayProgress()
    }

    override fun onProgressUpdate(percentage: Float) {
        activity!!.runOnUiThread {
            updateSaveProgressStatus((100 * percentage).toInt().toString() + "%")
        }
    }

    override fun onSaveVideoSuccess(p0: String?) {
        stopPlayBack()
        stopTrackPlayProgress()
        if (!saveCover()) {
            File(outputFilePath).delete()
            updateSaveProgressStatus("保存错误")
            return
        }
        mediaFile.release()

        saveVideoData()
        saveProgressDialog!!.dismiss()

        activity!!.setResult(GlobalVariable().RESULT_NEED_REFRESH_FOLDER_LIST_OR_HOME_EVENT_OR_ARCHIVE_LIST)
        activity!!.finish()
    }

    override fun onSaveVideoFailed(errorCode: Int) {
        activity!!.runOnUiThread {
            ToastUtils.toastErrorCode(activity, errorCode)
        }
        stopPlayBack()
        stopTrackPlayProgress()
        mediaFile.release()
        saveProgressDialog!!.dismiss()
        activity!!.setResult(Activity.RESULT_CANCELED)
        activity!!.finish()
    }

    override fun onPause() {
        super.onPause()
        //pause the fragment will pause the play and stock track
        pausePlayback()
        stopTrackPlayProgress()
    }

    override fun onResume() {
        super.onResume()
        //resume the fragment will back the track progress
        handler.postDelayed({startTrackPlayProgress()},500)
    }

    //endregion

    //region override

    override fun onBackPressed(): Boolean {
        graffitiFragment.setUnSelected()
        pausePlayback()
        stopTrackPlayProgress()
        if (frameListAsyncTask != null && !(frameListAsyncTask!!.isCancelled)) frameListAsyncTask!!.cancel(
            true
        )
        if (timeRangeList.size == 0) {
            stopPlayBack()
            mediaFile.release()
            activity!!.setResult(Activity.RESULT_CANCELED)
            activity!!.finish()
            return true
        }
        giveUpChange()
        return true
    }

    //endregion

    //region internal conversion
    internal fun Float.toVideoMsLong() = this.toLong()
    internal fun Long.toProgressValue() = this.toFloat()
    //endregion
}


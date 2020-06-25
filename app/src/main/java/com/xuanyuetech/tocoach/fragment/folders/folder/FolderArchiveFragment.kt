package com.xuanyuetech.tocoach.fragment.folders.folder

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.getbase.floatingactionbutton.FloatingActionButton
import com.getbase.floatingactionbutton.FloatingActionsMenu
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.Behavior.DragCallback
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.xuanyuetech.tocoach.R
import com.xuanyuetech.tocoach.adapter.FolderListCardViewAdapter
import com.xuanyuetech.tocoach.adapter.setUpWith
import com.xuanyuetech.tocoach.data.*
import com.xuanyuetech.tocoach.fragment.BasicFragment
import com.xuanyuetech.tocoach.util.ActivityUtil
import com.xuanyuetech.tocoach.util.ActivityUtil.startPickVideoWithCamera
import com.xuanyuetech.tocoach.util.MediaIntentUtil.checkIfUriIsValid
import com.xuanyuetech.tocoach.util.MediaIntentUtil.videosMimeTypes
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.math.abs


/**
 * Folder archive fragment
 */
class FolderArchiveFragment : BasicFragment(), OnOffsetChangedListener {

    //region properties

    private val percentageToShowTitleAtToolbar = 0.9f
    private val percentageToHideImage = 0.4f
    private val alphaAnimationsDuration = 200

    private val handler = Handler()

    private var mForceHiddenTitle = false
    private var mIsTheTitleVisible = false
    private var mIsImageVisible = true

    private lateinit var mTitleContainer: LinearLayoutCompat
    private lateinit var mTitle: TextView

    //data
    private lateinit var videoList: ArrayList<Video>
    private lateinit var diaryList : ArrayList<Diary>
    private lateinit var folderArchiveObjectList : ArrayList<FolderArchiveObject>
    private lateinit var folder: Folder
    private lateinit var databaseHelper: DatabaseHelper
    private var folderId = -1

    //object recyclerView
    private lateinit var recyclerView : RecyclerView

    private lateinit var folderBackgroundImageView : ImageView
    private lateinit var profileImageView : CircleImageView
    private lateinit var folderNameView : TextView
    private lateinit var folderNotesView : TextView
    private lateinit var endTextView : TextView
    private lateinit var appBarLayout:AppBarLayout
    private lateinit var searchInputEditText: EditText
    private lateinit var menuItemSearch: MenuItem
    private lateinit var scrollView:NestedScrollView
    private lateinit var searchHint:TextView
    private var forbidAppBarScroll =false

    private lateinit var mActionsMenu: FloatingActionsMenu

    private var isInFilter = false

    private lateinit var folderArchiveCardViewAdapter: FolderListCardViewAdapter

    private var sortStrategy = SortStrategy.ByInitTime
    enum class SortStrategy{
        ByInitTime, ByUpdateTime, ByTitle
    }

    //endregion

    //companion object
    /**
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mView = inflater.inflate(R.layout.fragment_folder_archive, container, false)

        initData()

        activity!!.invalidateOptionsMenu()

        initView()

        setUpCardView()

        setUpFab()

        refreshItemList()

        refreshViews()
        return mView
    }

    //region fun

    /**
     * init data
     */
    private fun initData() {
        folderId = ActivityUtil.getFolderIdFromIntent(activity!!.intent)

        databaseHelper = DatabaseHelper(context!!)

        folder = databaseHelper.findFolderById(folderId)!!

        videoList = ArrayList()
        diaryList = ArrayList()
        folderArchiveObjectList = ArrayList()
    }

    /**
     * init views
     */
    private fun initView(){
        folderBackgroundImageView = mView.findViewById(R.id.imageview_placeholder)

        folderNameView = mView.findViewById(R.id.info_name)
        folderNotesView = mView.findViewById(R.id.info_notes)
        profileImageView = mView.findViewById(R.id.profile_image)
        endTextView = mView.findViewById(R.id.textView_end_label)

        //titleBar
        mTitleContainer = mView.findViewById(R.id.user_info_holder_linear)
        mTitle = mView.findViewById(R.id.toolbar_title)
        appBarLayout = mView.findViewById(R.id.collapsing_app_bar)

        //collapsing toolbar listeners
        appBarLayout.addOnOffsetChangedListener(this)
        scrollView = mView.findViewById(R.id.folder_archive_nested_scroll_view)
        searchHint = mView.findViewById(R.id.folder_archive_search_hint)

        searchHint.setOnClickListener{startEditSearch()}
        initSearchInputBox()
    }

    /**
     * init search input editText
     */
    private fun initSearchInputBox(){
        searchInputEditText = mView.findViewById(R.id.folder_archive_search_edit_text)

        searchInputEditText.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isInFilter = !s.isNullOrBlank()
                refreshViews()
            }
        })

        //press enter in soft keyboard
        searchInputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) { finishEditSearch()}
            false
        }
    }

    /**
     * start search input
     */
    private fun startEditSearch(){
        //title text invisible
        startAlphaAnimation( mTitle, 0L, View.INVISIBLE)
        mForceHiddenTitle = true

        //do not want toolbar scrollable
        appBarLayout.setExpanded(false,true)
        forbidAppBarScroll(true)

        searchHint.visibility = View.GONE
        ViewCompat.setNestedScrollingEnabled(scrollView, false)

        searchInputEditText.visibility = View.VISIBLE

        //start edit search box
        searchInputEditText.requestFocus()
        val imm: InputMethodManager =  activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(searchInputEditText, 0)

        //set back navigation icon
        mView.findViewById<Toolbar>(R.id.toolbar).setNavigationIcon(R.drawable.ic_close_black_24dp)

        //hide search icon
        menuItemSearch.isVisible = false
    }

    /**
     * out of search box editing
     */
    @SuppressLint("SetTextI18n")
    private fun finishEditSearch(){
        if(searchInputEditText.text.toString().isNotBlank()){
            //添加空格 用户视觉体验更好
            searchHint.text = " "+searchInputEditText.text.toString()
            searchHint.visibility = View.VISIBLE
        }else{
            menuItemSearch.isVisible = true
        }

        mView.findViewById<Toolbar>(R.id.toolbar).setNavigationIcon(R.drawable.ic_back)

        searchInputEditText.visibility = View.GONE
        mForceHiddenTitle = false
        startAlphaAnimation( mTitle, alphaAnimationsDuration.toLong(), View.VISIBLE)
        mIsTheTitleVisible = true

        ViewCompat.setNestedScrollingEnabled(scrollView,true)
        hideKeyBoard()
    }

    /**
     * refresh views
     */
    @SuppressLint("SetTextI18n")
    private fun refreshViews(){
        handler.post {

            folderArchiveCardViewAdapter.filter.filter(searchInputEditText.text.toString())

            folderArchiveCardViewAdapter.notifyDataSetChanged()

            folderNameView.text = folder.name
            folderNotesView.text = folder.notes

            if (folder.backgroundImagePath.isNotBlank())
                folderBackgroundImageView.setImageURI(Uri.parse(folder.backgroundImagePath))
            else folderBackgroundImageView.setImageResource(R.drawable.logo)

            if (folder.profileImagePath.isNotBlank())
                profileImageView.setImageURI( Uri.parse( folder.profileImagePath))
            else profileImageView.setImageResource(R.drawable.profile_default)

            if (folderArchiveObjectList.size <= 0) {
                endTextView.text = "赶紧添加新的视频和文本日志吧！"
            } else {
                val videoSize = folderArchiveCardViewAdapter.filterFolderArchiveObjectList.count { it is Video }
                val diarySize = folderArchiveCardViewAdapter.filterFolderArchiveObjectList.count { it is Diary }
                if(isInFilter){
                    if(videoSize == 0 && diarySize == 0){
                        endTextView.text = "未找到任何日志"
                    }else{
                        endTextView.text = "当前检索下有 $videoSize 个视频日志和 $diarySize 个文本日志"
                    }
                }else{
                    endTextView.text = "总共有 $videoSize 个视频日志和 $diarySize 个文本日志"
                }
            }
        }
    }

    /**
     * refresh item list
     */
    private fun refreshItemList(){
        videoList.clear()
        diaryList.clear()
        folderArchiveObjectList.clear()

        videoList.addAll(databaseHelper.getAllFolderVideoByFolderId(folderId))
        diaryList.addAll(databaseHelper.getAllDairyByFolderId(folderId))

        val unSortedList = ArrayList<FolderArchiveObject>()
        unSortedList.addAll(videoList)
        unSortedList.addAll(diaryList)

        //different soring strategies
        when(sortStrategy){
            SortStrategy.ByInitTime ->{
                folderArchiveObjectList.addAll(unSortedList.sortedByDescending {
                    it.getArchiveInitTime()
                })
            }
            SortStrategy.ByUpdateTime ->{
                folderArchiveObjectList.addAll(unSortedList.sortedByDescending {
                    it.getArchiveUpdateTime()
                })
            }
            SortStrategy.ByTitle ->{
                folderArchiveObjectList.addAll( unSortedList.sortedBy {
                    it.getArchiveTitle()
                })
            }
        }
    }

    /**
     * Setup the CardView
     */
    private fun setUpCardView() {
            folderArchiveObjectList = ArrayList()

            folderArchiveCardViewAdapter = FolderListCardViewAdapter(folderArchiveObjectList)
            folderArchiveCardViewAdapter.setOnItemClickListener(
                object : FolderListCardViewAdapter.CustomOnItemClickListener {
                    override fun onItemClickListener(position: Int) {
                        if(isValidClick()) {
                            val item = folderArchiveCardViewAdapter.filterFolderArchiveObjectList[position]

                            if (item is Video) {
                                ActivityUtil.startVideoPlayer(this@FolderArchiveFragment, item.id)
                            } else if (item is Diary) {
                                ActivityUtil.startDairy(this@FolderArchiveFragment, item.id, folderId)
                            }
                        }
                    }
                }
            )

            //set up recyclerView
            recyclerView = mView.findViewById(R.id.folder_archive_recycler_view)
            recyclerView.setUpWith(folderArchiveCardViewAdapter)

    }

    /**
     * Set up the floating action button to navigate to video editor fragment
     */
    private fun setUpFab() {
        mActionsMenu = mView.findViewById(R.id.fab_menu)

        mView.findViewById<FloatingActionButton>(R.id.gallery_fab)
            .setOnClickListener {
                if(isValidClick()) {
                    startPickVideoWithCamera(this)
                    mActionsMenu.collapse()
                }
            }

        mView.findViewById<FloatingActionButton>(R.id.diary_fab)
            .setOnClickListener {
                if(isValidClick()){
                    mActionsMenu.collapse()
                    handler.postDelayed({startNewDairyActivity()}, 40)
                    //to avoid collapse pause issue, have to wait collapse finished and then start activity
                    //TODO: solve the activity starting slow issue
                }
            }
    }

    /**
     * start new diary activity
     */
    private fun startNewDairyActivity(){
        ActivityUtil.startDairy(this, -1, folderId)
    }

    /**
     * start new editor
     */
    private fun startNewEditorActivity(uri: Uri) {
        ActivityUtil.startNewVideoEditor(this, folderId, uri)
    }

    /**
     * disable the toolbar scrollable
     */
    @Suppress("SameParameterValue")
    private fun forbidAppBarScroll(forbid: Boolean) {
        if (forbid == forbidAppBarScroll) {
            return
        }
        if (forbid) {
            forbidAppBarScroll = true
            if (ViewCompat.isLaidOut(appBarLayout)) {
                setAppBarDragCallback(object : DragCallback() {
                    override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                        return false
                    }
                })
            } else {
                appBarLayout.viewTreeObserver
                    .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            appBarLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            setAppBarDragCallback(object : DragCallback() {
                                override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                                    return false
                                }
                            })
                        }
                    })
            }
        } else {
            forbidAppBarScroll = false
            if (ViewCompat.isLaidOut(appBarLayout)) {
                setAppBarDragCallback(null)
            } else {
                appBarLayout.viewTreeObserver
                    .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            appBarLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            setAppBarDragCallback(null)
                        }
                    })
            }
        }
    }

    /**
     *
     */
    private fun setAppBarDragCallback(dragCallback: DragCallback?) {
        val params =
            appBarLayout.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior as AppBarLayout.Behavior?
        behavior!!.setDragCallback(dragCallback)
    }

    //endregion

    //region override

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == globalVariable.REQUEST_VIDEO_PICK ||
                requestCode == globalVariable.REQUEST_VIDEO_CAPTURE) {
                //video capture or video pick result
                val uri = data!!.data

                if (uri != null && checkIfUriIsValid(activity!!, uri, videosMimeTypes)) {
                    startNewEditorActivity(uri)
                } else {
                    Toast.makeText(context, R.string.toast_cannot_retrieve_selected_video, Toast.LENGTH_SHORT).show()
                }
            }
        }else if(resultCode == GlobalVariable().RESULT_NEED_REFRESH_FOLDER_LIST_OR_HOME_EVENT_OR_ARCHIVE_LIST) {
            folder = databaseHelper.findFolderById(folderId)!!
            refreshItemList()
            refreshViews()
            activity!!.setResult(GlobalVariable().RESULT_NEED_REFRESH_FOLDER_LIST_OR_HOME_EVENT_OR_ARCHIVE_LIST)
        }else if(resultCode == GlobalVariable().RESULT_DELETE_FOLDER){
            activity!!.setResult(GlobalVariable().RESULT_NEED_REFRESH_FOLDER_LIST_OR_HOME_EVENT_OR_ARCHIVE_LIST)
            activity!!.finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            globalVariable.REQUEST_VIDEO_PICK ->
                //permission to pick video
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startPickVideoWithCamera(this)
                }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun customizeToolbar() {

        mTitle.text = folder.name
        startAlphaAnimation(mTitle, 0, View.INVISIBLE)

        val toolbar = mView.findViewById<Toolbar>(R.id.toolbar)
        toolbar.inflateMenu(R.menu.toolbar_folder_archive)

        toolbar.setNavigationOnClickListener {
            activity!!.onBackPressed()
        }
        menuItemSearch = toolbar.menu.findItem(R.id.folder_archive_menuItem_search)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menuItem_folder_profile -> {
                    //action nav to folder profile activity
                    ActivityUtil.startFolderProfile(this, folderId)
                }
                R.id.menuItem_sort_init -> {
                    sortStrategy = SortStrategy.ByInitTime
                    refreshItemList()
                    refreshViews()
                }
                R.id.menuItem_sort_update -> {
                    sortStrategy = SortStrategy.ByUpdateTime
                    refreshItemList()
                    refreshViews()
                }
                R.id.menuItem_sort_title -> {
                    sortStrategy = SortStrategy.ByTitle
                    refreshItemList()
                    refreshViews()
                }
                R.id.folder_archive_menuItem_search -> {
                    startEditSearch()
                }
            }
            true
        }
    }

    override fun onBackPressed(): Boolean {
        return if(searchInputEditText.isFocused){
            //stop search
            searchInputEditText.setText("")
            finishEditSearch()
            true
        }else{
            false
        }
    }


    //endregion

    //region handle collapsing animation

    /**
     * handle percentage alpha changes
     */
    override fun onOffsetChanged(appBarLayout: AppBarLayout, offset: Int) {
        val maxScroll = appBarLayout.totalScrollRange
        val percentage =
            abs(offset).toFloat() / maxScroll.toFloat()
        handleToolbarTitleVisibility(percentage)
    }


    /**
     * change toolbar title alpha
     */
    private fun handleToolbarTitleVisibility(percentage: Float) {
        if(!mForceHiddenTitle){
            if (percentage >= percentageToShowTitleAtToolbar) {
                if (!mIsTheTitleVisible) {
                    startAlphaAnimation( mTitle, alphaAnimationsDuration.toLong(), View.VISIBLE)
                    mIsTheTitleVisible = true
                }
            } else {
                if (mIsTheTitleVisible) {
                    startAlphaAnimation( mTitle, alphaAnimationsDuration.toLong(), View.INVISIBLE)
                    mIsTheTitleVisible = false
                }
            }
        }
        if (percentage >= percentageToHideImage) {
            if (mIsImageVisible) {
                startAlphaAnimation( profileImageView, alphaAnimationsDuration.toLong(), View.INVISIBLE)
                mIsImageVisible = false
            }
        } else {
            if (!mIsImageVisible) {
                startAlphaAnimation( profileImageView, alphaAnimationsDuration.toLong(),View.VISIBLE)
                mIsImageVisible = true
            }
        }
    }

    /**
     * start alpha animation
     */
    private fun startAlphaAnimation(v: View, duration: Long, visibility: Int) {
        val alphaAnimation =
            if (visibility == View.VISIBLE) AlphaAnimation( 0f, 1f )
            else AlphaAnimation(1f, 0f)
        alphaAnimation.duration = duration
        alphaAnimation.fillAfter = true
        v.startAnimation(alphaAnimation)
    }

    //endregion
}
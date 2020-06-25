package com.xuanyuetech.tocoach.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.View.GONE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.xuanyuetech.tocoach.R
import com.xuanyuetech.tocoach.adapter.FolderArchiveCardViewAdapter
import com.xuanyuetech.tocoach.adapter.FolderCardViewAdapter
import com.xuanyuetech.tocoach.adapter.setUpWith
import com.xuanyuetech.tocoach.data.*
import com.xuanyuetech.tocoach.util.ActivityUtil
import com.xuanyuetech.tocoach.util.setMaxLength

class SearchActivity : BasicActivity() {

    //region properties

    private lateinit var databaseHelper : DatabaseHelper
    private val handler = Handler()

    //textViews
    private lateinit var folderTextView: TextView
    private lateinit var videoTextView : TextView
    private lateinit var diaryTextView : TextView

    //input text
    private lateinit var searchInputEditText : EditText
    private val maxSearchLength = 7

    //related lists
    private var folderFullList = ArrayList<Folder>()
    private var videoFullList = ArrayList<Video>()
    private var diaryFullList = ArrayList<Diary>()

    private var folderSearchList = ArrayList<Folder>()
    private var videoSearchList = ArrayList<Video>()
    private var diarySearchList = ArrayList<Diary>()


    //adapters
    private lateinit var folderCardViewAdapter : FolderCardViewAdapter
    private lateinit var videoCardViewAdapter : FolderArchiveCardViewAdapter
    private lateinit var diaryCardViewAdapter : FolderArchiveCardViewAdapter

    private lateinit var cancelInputButton : ImageButton

    //endregion

    //region onCreate

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        databaseHelper = DatabaseHelper(this)

        setContentView(R.layout.activity_search)

        initView()

        initData()

        bindAdapter()

        initToolbar()

        bindListener()

        searchAndRefreshView("")

    }

    //endregion

    //region fun

    /**
     * init all views
     */
    private fun initView(){
        folderTextView = findViewById(R.id.search_activity_textView_folder)
        folderTextView.visibility = GONE
        videoTextView = findViewById(R.id.textView_video)
        videoTextView.visibility = GONE
        diaryTextView = findViewById(R.id.textView_diary)
        diaryTextView.visibility = GONE

        cancelInputButton = findViewById(R.id.cancel_input)
        cancelInputButton.visibility = GONE

        searchInputEditText = findViewById(R.id.search_input)
        searchInputEditText.setMaxLength(maxSearchLength)

    }

    /**
     * init all data
     */
    private fun initData(){
        folderFullList = databaseHelper.getAllFolder()
        videoFullList = databaseHelper.getAllFolderVideo()
        diaryFullList = databaseHelper.getAllDairy()
    }

    /**
     * Setup the CardView listener
     */
    private fun bindAdapter(){

        //folder search
        folderCardViewAdapter = FolderCardViewAdapter(folderSearchList)
        folderCardViewAdapter.setOnItemClickListener(
            object : FolderCardViewAdapter.CustomOnItemClickListener {

                //start the folder activity
                override fun onItemClickListener(position: Int) {
                    if(isValidClick()){
                        ActivityUtil.startFolder(
                            this@SearchActivity,
                            folderSearchList[position].id
                        )
                    }
                }
            })

        val folderRecyclerView : RecyclerView = findViewById(R.id.search_folder_list)
        folderRecyclerView.setUpWith(folderCardViewAdapter)

        //video search
        videoCardViewAdapter = FolderArchiveCardViewAdapter(videoSearchList)
        videoCardViewAdapter.setOnItemClickListener(
            object : FolderArchiveCardViewAdapter.CostomOnItemClickListener {

                //start the video player activity
                override fun onItemClickListener(position: Int) {
                    if(isValidClick()) {
                        ActivityUtil.startVideoPlayer(
                            this@SearchActivity,
                            videoSearchList[position].id
                        )
                    }
                }
            }
        )
        val videoRecyclerView : RecyclerView = findViewById(R.id.search_video_list)
        videoRecyclerView.setUpWith(videoCardViewAdapter)

        //diary search
        diaryCardViewAdapter = FolderArchiveCardViewAdapter(diarySearchList)
        diaryCardViewAdapter.setOnItemClickListener(
            object : FolderArchiveCardViewAdapter.CostomOnItemClickListener {

                //start the diary activity
                override fun onItemClickListener(position: Int) {
                    if(isValidClick()){
                        ActivityUtil.startDairy(
                            this@SearchActivity,
                            diarySearchList[position].id,
                            -1)
                    }
                }
            }
        )
        val diaryRecyclerView : RecyclerView = findViewById(R.id.search_diary_list)
        diaryRecyclerView.setUpWith(diaryCardViewAdapter)

    }

    /**
     * Set up toolbar
     */
    private fun initToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setToolbarToActionbar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        //toolbar title
        findViewById<TextView>(R.id.toolbar_title).text = "搜索"
    }

    /**
     * refresh all views by the
     */
    private fun searchAndRefreshView(input : String){

        folderSearchList.clear()
        videoSearchList.clear()
        diarySearchList.clear()

        if(input.isBlank()) {
            folderTextView.visibility = GONE
            videoTextView.visibility = GONE
            diaryTextView.visibility = GONE
            folderCardViewAdapter.notifyDataSetChanged()
            videoCardViewAdapter.notifyDataSetChanged()
            diaryCardViewAdapter.notifyDataSetChanged()
            return
        }

        handler.post{
            folderSearchList.addAll(folderFullList.filter { folder -> folder.name.contains(input)  })
            if(folderSearchList.isEmpty()) folderTextView.visibility = GONE
            else folderTextView.visibility = View.VISIBLE
            folderCardViewAdapter.notifyDataSetChanged()
        }

        handler.post{
            videoSearchList.addAll(videoFullList.filter { video -> video.title.contains(input)  })
            if(videoSearchList.isEmpty()) videoTextView.visibility = GONE
            else videoTextView.visibility = View.VISIBLE
            videoCardViewAdapter.notifyDataSetChanged()
        }

        handler.post{
            diarySearchList.addAll(diaryFullList.filter { diary -> diary.title.contains(input)  })
            if(diarySearchList.isEmpty()) diaryTextView.visibility = GONE
            else diaryTextView.visibility = View.VISIBLE
            diaryCardViewAdapter.notifyDataSetChanged()
        }

    }

    /**
     * bind input listener
     */
    private fun bindListener(){

        //edit search text
        searchInputEditText.addTextChangedListener {
            val searchInput = it.toString()

            if(searchInput.isBlank()) cancelInputButton.visibility = GONE
            else{cancelInputButton.visibility = View.VISIBLE}

            searchAndRefreshView(searchInput)
        }

        cancelInputButton.setOnClickListener{
            searchInputEditText.setText("")
        }

        searchInputEditText.setOnEditorActionListener {
                _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm =
                    getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                var view = currentFocus
                if (view == null) view = View(this)
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
            false
        }
    }

    //endregion

    //region override

    /**
     * back pressed is meaning canceling adding
     */
    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
    }

    /**
     * once finish from intent, directly finish this search activity
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        setResult(GlobalVariable().RESULT_NEED_REFRESH_FOLDER_LIST_OR_HOME_EVENT_OR_ARCHIVE_LIST)
        finish()
    }

    //endregion

}
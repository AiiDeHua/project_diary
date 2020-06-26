package com.yiqisport.yiqiapp.fragment.video_player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.yiqisport.yiqiapp.R
import com.yiqisport.yiqiapp.data.DatabaseHelper
import com.yiqisport.yiqiapp.data.Video
import com.yiqisport.yiqiapp.fragment.BasicFragment
import com.yiqisport.yiqiapp.util.ActivityUtil

/**
 * video player infor fragment
 */
class VideoPlayerInfoFragment(private val videoId : Int, private val onEditButtonClickListener: View.OnClickListener) : BasicFragment() {

    //region properties

    private lateinit var databaseHelper : DatabaseHelper

    private lateinit var videoTitleView : TextView
    private lateinit var videoNotesView : TextView

    private lateinit var video : Video

    //endregion

    //region fun

    /**
     * onCreateView
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_video_player_info, container, false)

        databaseHelper = DatabaseHelper(context!!)

        initView()

        refreshData()

        bindListener()

        return mView
    }

    /**
     * init views
     */
    private fun initView(){
        videoTitleView = mView.findViewById(R.id.textView_videoPlayer_title)
        videoNotesView = mView.findViewById(R.id.textView_videoPlayer_notes)
    }

    /**
     * bind listener
     */
    private fun bindListener(){
        val editButton = mView.findViewById<Button>(R.id.button_videoPlayer_edit)
        editButton.setOnClickListener(onEditButtonClickListener)

        val shareButton = mView.findViewById<Button>(R.id.button_videoPlayer_share)
        shareButton.setOnClickListener {
            ActivityUtil.startSendMP4File(this, video.localUrl)
        }
    }

    /**
     * refresh all data
     */
    fun refreshData(){
        video = databaseHelper.findVideoByIdFromID(videoId)!!

        videoTitleView.text = video.title
        videoNotesView.text = video.notes
    }

}
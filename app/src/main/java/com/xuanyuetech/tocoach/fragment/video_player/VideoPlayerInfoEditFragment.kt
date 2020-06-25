package com.xuanyuetech.tocoach.fragment.video_player

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.xuanyuetech.tocoach.R
import com.xuanyuetech.tocoach.data.DatabaseHelper
import com.xuanyuetech.tocoach.data.DataHelper
import com.xuanyuetech.tocoach.data.GlobalVariable
import com.xuanyuetech.tocoach.data.Video
import com.xuanyuetech.tocoach.fragment.BasicFragment
import com.xuanyuetech.tocoach.util.MessageHelper
import com.xuanyuetech.tocoach.util.setMaxLength
import org.threeten.bp.LocalDateTime

/**
 * video player info edit fragment
 */
class VideoPlayerInfoEditFragment(private val videoId : Int) : BasicFragment() {

    //region properties

    private lateinit var databaseHelper : DatabaseHelper

    private lateinit var titleText : EditText
    private lateinit var notesText : EditText
    private lateinit var backButtonView : ImageButton
    private lateinit var deleteButtonView : ImageButton

    private lateinit var video : Video

    //endregion

    //region onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_video_player_info_edit, container, false)

        databaseHelper = DatabaseHelper(context!!)

        initView()

        bindListener()

        initData()

        return mView
    }

    //endregion

    //region fun

    /**
     * init view
     */
    private fun initView(){
        titleText = mView.findViewById(R.id.editText_video_title)
        titleText.setMaxLength(Video().maxTitleLength)
        notesText = mView.findViewById(R.id.editText_video_notes)
        deleteButtonView = mView.findViewById(R.id.video_player_edit_delete)
        backButtonView = mView.findViewById(R.id.video_player_edit_back)
    }

    /**
     * init data
     */
    private fun initData(){
        video = databaseHelper.findVideoByIdFromID(videoId)!!

        titleText.setText(video.title)
        notesText.setText(video.notes)
    }

    /**
     * save the edit
     */
    fun saveEdit() : Boolean{
        if(titleText.text.toString().isBlank()) {
            MessageHelper.noticeDialog(context!!,"标题不能为空")
            return false
        }
        video.title = titleText.text.toString()
        video.notes = notesText.text.toString()
        video.setUpdateTime(LocalDateTime.now())
        DataHelper().updateVideo(video, databaseHelper)
        return true
    }

    /**
     * bind listener
     */
    private fun bindListener(){
        backButtonView.setOnClickListener {
            //have to close the keyboard when press back
            val inputMethodManager = activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(mView.windowToken, 0)

            activity!!.onBackPressed()
        }

        deleteButtonView.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(activity!!,R.style.CustomDialogTheme)
                .setCancelable(true)
                .setTitle("警告")
                .setMessage("你确定要删除当前视频日志吗?")
                .setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
                .setPositiveButton("确定"){
                        dialog, _ ->dialog.dismiss()

                    DataHelper().deleteFolderVideo(video, databaseHelper)
                    Toast.makeText(context,"成功删除!", Toast.LENGTH_SHORT).show()
                    activity!!.setResult(GlobalVariable().RESULT_NEED_REFRESH_FOLDER_LIST_OR_HOME_EVENT_OR_ARCHIVE_LIST)
                    activity!!.finish()
                }

            dialogBuilder.show()

        }
    }

    //endregion

    //region override

    override fun onPause() {
        hideKeyBoard()
        super.onPause()
    }

    //endregion

}
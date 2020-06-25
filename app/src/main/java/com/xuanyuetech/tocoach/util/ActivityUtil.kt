package com.xuanyuetech.tocoach.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.xuanyuetech.tocoach.activity.*
import com.xuanyuetech.tocoach.data.GlobalVariable
import com.xuanyuetech.tocoach.util.MediaIntentUtil.videosMimeTypes


object ActivityUtil {

    /**
     *
     */
    fun startMain(activity: Activity){
        val intent = Intent(activity, MainActivity::class.java)
        activity.startActivity(intent);
        activity.finish()
    }

    /**
     *
     */
    fun startSearch(fragment: Fragment){
        val intent = searchIntent(fragment.context!!)
        fragment.startActivityForResult(intent, GlobalVariable().REQUEST_SEARCH)
    }

    /**
     *
     */
    fun startNewVideoEditor(fragment: Fragment, folderId : Int, uri : Uri){
        val intent = Intent(fragment.context, VideoEditorActivity::class.java)
        intent.putExtra("folder_id", folderId)
        intent.putExtra("EXTRA_INPUT_URI", uri)
        fragment.startActivityForResult(intent, GlobalVariable().REQUEST_NEW_VIDEO_EDITOR)
    }

    /**
     *
     */
    fun startDairy(activity: Activity, diaryId: Int, folderId: Int){
        val intent = diaryIntent(activity, diaryId, folderId)
        activity.startActivityForResult(intent, GlobalVariable().REQUEST_DAIRY)
    }

    /**
     *
     */
    fun startDairy(fragment: Fragment, diaryId: Int, folderId: Int){
        val intent = diaryIntent(fragment.context!!, diaryId, folderId)
        fragment.startActivityForResult(intent, GlobalVariable().REQUEST_DAIRY)
    }

    /**
     *
     */
    fun startVideoPlayer(activity : Activity, videoId : Int) {
        val intent = videoPlayerIntent(activity, videoId)
        activity.startActivityForResult(intent, GlobalVariable().REQUEST_VIDEO_PLAY)
    }

    /**
     *
     */
    fun startVideoPlayer(fragment: Fragment, videoId : Int) {
        val intent = videoPlayerIntent(fragment.context!!, videoId)
        fragment.startActivityForResult(intent, GlobalVariable().REQUEST_VIDEO_PLAY)
    }

    /**
     *
     */
    fun startFolder(activity: Activity, folderId: Int){
        val intent = folderIntent(activity, folderId)
        activity.startActivityForResult(intent, GlobalVariable().REQUEST_FOLDER)
    }

    /**
     *
     */
    fun startFolderProfile(fragment: Fragment, folderId: Int){
        val intent = folderProfileIntent(fragment.context!!, folderId)
        fragment.startActivityForResult(intent, GlobalVariable().REQUEST_FOLDER_PROFILE)
    }

    /**
     *
     */
    fun startFolderProfileEdit(fragment: Fragment, folderId: Int){
        val intent = folderProfileEditIntent(fragment.context!!, folderId)
        fragment.startActivityForResult(intent, GlobalVariable().REQUEST_FOLDER_PROFILE_EDIT)
    }

    /**
     *
     */
    fun startFolder(fragment: Fragment, folderId: Int){
        val intent = folderIntent(fragment.context!!, folderId)
        fragment.startActivityForResult(intent, GlobalVariable().REQUEST_FOLDER)
    }

    /**
     *
     */
    fun startCreateNewFolder(fragment: Fragment){
        val intentNewFolder = Intent(fragment.context, NewFolderActivity::class.java)
        fragment.startActivityForResult(intentNewFolder, GlobalVariable().REQUEST_NEW_FOLDER)
    }

    /**
     *
     */
    fun getFolderIdFromIntent(intent: Intent) : Int{
        return intent.getIntExtra("folder_id",-1)
    }

    /**
     *
     */
    fun getVideoIdFromIntent(intent : Intent) : Int{
        return intent.getIntExtra("folder_video_id", -1)
    }

    /**
     *
     */
    fun getDairyIdFromIntent(intent : Intent) : Int{
        return intent.getIntExtra("diary_id", -1)
    }

    /**
     *
     */
    fun getVideoUriFromIntent(intent: Intent) : Uri{
        return intent.getParcelableExtra("EXTRA_INPUT_URI")!!
    }

    /**
     *
     */
    fun startPickVideoWithCamera(fragment: Fragment) {
        val intentForChoosingVideos =
            MediaIntentUtil.intentForPickVideoMedia(
                fragment, videosMimeTypes
            )

        if (intentForChoosingVideos != null)
            fragment.startActivityForResult(
                intentForChoosingVideos,
                GlobalVariable().REQUEST_VIDEO_PICK
            )
    }

    /**
     *
     */
    fun startSendMP4File(fragment : Fragment, fileAbsPath : String){
        val intent = MediaIntentUtil.mp4SendIntent(fragment.context!!, fileAbsPath) ?: return
        fragment.startActivity(Intent.createChooser(intent, "请选择分享方式"))

    }


    //Region private helper

    /**
     *
     */
    private fun searchIntent(context : Context) : Intent{
        return Intent(context, SearchActivity::class.java)
    }

    /**
     *
     */
    private fun videoPlayerIntent(context : Context, videoId : Int) : Intent{
        val intent = Intent(context, VideoPlayerActivity::class.java)
        intent.putExtra("folder_video_id", videoId)
        return intent
    }

    /**
     *
     */
    private fun folderIntent(context: Context, folderId : Int) : Intent{
        val intent = Intent(context, FolderActivity()::class.java)
        intent.putExtra("folder_id",folderId)
        return intent
    }

    private fun folderProfileIntent(context: Context, folderId: Int) : Intent{
        val intent = Intent(context, FolderProfileActivity :: class.java)
        intent.putExtra("folder_id", folderId)
        return intent
    }

    private fun folderProfileEditIntent(context: Context, folderId: Int) : Intent{
        val intent = Intent(context, FolderProfileEditActivity::class.java)
        intent.putExtra("folder_id", folderId)
        return intent
    }

    /**
     *
     */
    private fun diaryIntent(context : Context, diaryId: Int, folderId : Int) : Intent{
        val intent = Intent(context, DiaryActivity()::class.java)
        intent.putExtra("folder_id",folderId)
        intent.putExtra("diary_id",diaryId)
        return intent
    }


    //endregion
}

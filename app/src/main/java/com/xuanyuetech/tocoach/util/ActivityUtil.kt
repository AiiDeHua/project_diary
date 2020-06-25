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
    fun startNewVideoEditor(fragment: Fragment, studentId : Int, uri : Uri){
        val intent = Intent(fragment.context, VideoEditorActivity::class.java)
        intent.putExtra("student_id", studentId)
        intent.putExtra("EXTRA_INPUT_URI", uri)
        fragment.startActivityForResult(intent, GlobalVariable().REQUEST_NEW_VIDEO_EDITOR)
    }

    /**
     *
     */
    fun startDairy(activity: Activity, diaryId: Int, studentId: Int){
        val intent = diaryIntent(activity, diaryId, studentId)
        activity.startActivityForResult(intent, GlobalVariable().REQUEST_DAIRY)
    }

    /**
     *
     */
    fun startDairy(fragment: Fragment, diaryId: Int, studentId: Int){
        val intent = diaryIntent(fragment.context!!, diaryId, studentId)
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
    fun startStudent(activity: Activity, studentId: Int){
        val intent = studentIntent(activity, studentId)
        activity.startActivityForResult(intent, GlobalVariable().REQUEST_STUDENT)
    }

    /**
     *
     */
    fun startStudentProfile(fragment: Fragment, studentId: Int){
        val intent = studentProfileIntent(fragment.context!!, studentId)
        fragment.startActivityForResult(intent, GlobalVariable().REQUEST_STUDENT_PROFILE)
    }

    /**
     *
     */
    fun startStudentProfileEdit(fragment: Fragment, studentId: Int){
        val intent = studentProfileEditIntent(fragment.context!!, studentId)
        fragment.startActivityForResult(intent, GlobalVariable().REQUEST_STUDENT_PROFILE_EDIT)
    }

    /**
     *
     */
    fun startStudent(fragment: Fragment, studentId: Int){
        val intent = studentIntent(fragment.context!!, studentId)
        fragment.startActivityForResult(intent, GlobalVariable().REQUEST_STUDENT)
    }

    /**
     *
     */
    fun startCreateNewStudent(fragment: Fragment){
        val intentNewStudent = Intent(fragment.context, NewFolderActivity::class.java)
        fragment.startActivityForResult(intentNewStudent, GlobalVariable().REQUEST_NEW_STUDENT)
    }

    /**
     *
     */
    fun getStudentIdFromIntent(intent: Intent) : Int{
        return intent.getIntExtra("student_id",-1)
    }

    /**
     *
     */
    fun getVideoIdFromIntent(intent : Intent) : Int{
        return intent.getIntExtra("student_video_id", -1)
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
        intent.putExtra("student_video_id", videoId)
        return intent
    }

    /**
     *
     */
    private fun studentIntent(context: Context, studentId : Int) : Intent{
        val intent = Intent(context, StudentActivity()::class.java)
        intent.putExtra("student_id",studentId)
        return intent
    }

    private fun studentProfileIntent(context: Context, studentId: Int) : Intent{
        val intent = Intent(context, StudentProfileActivity :: class.java)
        intent.putExtra("student_id", studentId)
        return intent
    }

    private fun studentProfileEditIntent(context: Context, studentId: Int) : Intent{
        val intent = Intent(context, StudentProfileEditActivity::class.java)
        intent.putExtra("student_id", studentId)
        return intent
    }

    /**
     *
     */
    private fun diaryIntent(context : Context, diaryId: Int, studentId : Int) : Intent{
        val intent = Intent(context, DiaryActivity()::class.java)
        intent.putExtra("student_id",studentId)
        intent.putExtra("diary_id",diaryId)
        return intent
    }


    //endregion
}

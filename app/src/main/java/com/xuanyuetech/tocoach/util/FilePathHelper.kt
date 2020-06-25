package com.xuanyuetech.tocoach.util

import android.content.Context
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class FilePathHelper(val context: Context) {

    //store in external storage file, only support android 4.0+
    private fun appRootExternalDirPath() = context.getExternalFilesDir(null)!!.absolutePath
    private val appDataDirPath = appRootExternalDirPath() + "/data/"
    private val appProfileDirPath = appDataDirPath + "profile/"
    private val appDiaryDirPath = appDataDirPath + "textDiary/"
    private val appVideoDirPath =  appDataDirPath + "videoDiary/"
    val appTempPath = appDataDirPath + "temp/"
    val appCrushDirPath = appRootExternalDirPath() + "crush/"

    val newEditedVideoPath = appVideoDirPath +
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMdHHmmss")) + ".mp4"
    val newVideoCoverPath = appVideoDirPath +
            "cover" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMdHHmm")) + ".jpg"

    fun diaryDirPath(id : Int) = "$appDiaryDirPath$id/"

    fun folderProfileImagePath(id : Int) = appProfileDirPath + "folderProfilePic$id.jpg"
    fun folderBackgroundImagePath(id : Int) = appProfileDirPath + "folderBackgroundPic$id.jpg"
}

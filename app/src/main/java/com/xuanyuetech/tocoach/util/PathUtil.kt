package com.xuanyuetech.tocoach.util

import com.xuanyuetech.tocoach.BuildConfig

object PathUtil {

    fun getPathFromRaw(rawFile : Int) = "android.resource://"+ BuildConfig.APPLICATION_ID +"/raw/" + rawFile

}
package com.yiqisport.yiqiapp.util

import com.yiqisport.yiqiapp.BuildConfig

object PathUtil {

    fun getPathFromRaw(rawFile : Int) = "android.resource://"+ BuildConfig.APPLICATION_ID +"/raw/" + rawFile

}
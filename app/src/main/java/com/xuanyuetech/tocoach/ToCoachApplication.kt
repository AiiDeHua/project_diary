package com.xuanyuetech.tocoach

import android.app.Application
import android.content.Context
import com.xuanyuetech.tocoach.util.CrashHandler
import com.qiniu.pili.droid.shortvideo.PLShortVideoEnv


class ToCoachApplication : Application() {

    private var mContext: Context? = null

    var instace: ToCoachApplication? = null


    override fun onCreate() {
        super.onCreate()
        PLShortVideoEnv.init(applicationContext)

        instace = this

        mContext = applicationContext
        val crashHandler = CrashHandler.getInstance()

        crashHandler.init()

    }

    companion object{
        val instance = ToCoachApplication()
    }


}
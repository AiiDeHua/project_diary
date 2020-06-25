package com.xuanyuetech.tocoach.util

import android.app.AlertDialog
import android.content.Context
import com.xuanyuetech.tocoach.R

object MessageHelper {

    fun noticeDialog(context: Context, string: String){
        val builder = AlertDialog.Builder(context, R.style.CustomDialogTheme)
        builder
            .setMessage(string)
            .setPositiveButton("确定") { _, _ ->
            }
        builder.show()
    }
}
package com.xuanyuetech.tocoach.util.videoeditor


object VideoEditorUtil {

    fun convertMsProgressToString(ms : Float) : String{
        return convertMsProgressToString((ms/1000).toInt())
    }

    fun convertMsProgressToString(msInt : Int) : String{
        val string: String
        val res = msInt %60

        string = if(res<10){
            "${(msInt / 60)}:0${res}"
        }else{
            "${(msInt / 60)}:${res}"
        }
        return string
    }


}
package com.yiqisport.yiqiapp.util

import java.io.*


object ReaderUtil {

    fun inputStreamConvector(inputStream: InputStream) : String{
        var ret = ""

        try {
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            var receiveString: String? = ""
            val stringBuilder = StringBuilder()
            while (bufferedReader.readLine().also { receiveString = it } != null) {
                stringBuilder.append("\n").append(receiveString)
            }
            inputStream.close()
            ret = stringBuilder.toString()
        } catch (e: FileNotFoundException) {
        } catch (e: IOException) {
        }

        return ret
    }
}
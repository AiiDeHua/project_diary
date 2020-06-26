package com.yiqisport.yiqiapp.util

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Size
import java.lang.Exception

object VideoUtil {

    /**
     * get the width and height of the video
     */
    fun getSizeOfVideo(videoPath : String,context :Context) : Size?{

        try{

            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, Uri.parse(videoPath))
            var videoWidth =
                Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))
            var videoHeight =
                Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))

            val rotation = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION))
            retriever.release()

            if(rotation !=0 && (rotation == 90 || rotation == 270)){
                val swipe = videoWidth
                videoWidth = videoHeight
                videoHeight = swipe
            }

            return Size(videoWidth,videoHeight)


        }catch (e : Exception){
            return null
        }
    }

}
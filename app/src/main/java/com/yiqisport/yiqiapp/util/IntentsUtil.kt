package com.yiqisport.yiqiapp.util

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import java.util.*

object IntentsUtil {

    /**note that this only requests to choose the files, but it's not guaranteed that this is what you will get*/
    @JvmStatic
    fun getPickFileIntent(context: Context, mainType: String = "*/*", extraMimeTypes: Array<String>? = null): Intent? {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = mainType
        if (!extraMimeTypes.isNullOrEmpty())
            intent.putExtra(Intent.EXTRA_MIME_TYPES, extraMimeTypes)
        if (context.packageManager.queryIntentActivities(intent, 0).isNullOrEmpty())
            return null
        return intent
    }


    fun getVideoCaptureOrVideoGalleryIntent(
        context: Context
    ): Intent? {
        var allIntents: ArrayList<Intent> = ArrayList()
        val chooserIntent : Intent

        val pickIntent = Intent()
        pickIntent.type = "video/*"
        pickIntent.action = Intent.ACTION_GET_CONTENT
        val videoCaptureIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        allIntents = addIntentsToList(context, allIntents, videoCaptureIntent)
        allIntents = addIntentsToList(context, allIntents, pickIntent)
        if(allIntents.isNotEmpty()){
            chooserIntent = Intent.createChooser(
                allIntents.removeAt(allIntents.size - 1),
                "请选择视频")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toTypedArray<Parcelable>())
            return chooserIntent
        }

        return null
    }

    private fun addIntentsToList(
        context: Context,
        list: ArrayList<Intent>,
        intent: Intent
    ): ArrayList<Intent>{
        val resInfo: List<ResolveInfo> =
            context.packageManager.queryIntentActivities(intent, 0)
        for (resolveInfo in resInfo) {
            val packageName: String = resolveInfo.activityInfo.packageName
            val targetedIntent = Intent(intent)
            targetedIntent.setPackage(packageName)
            list.add(targetedIntent)
        }
        return list
    }

    fun getMimeType(context: Context, uri: Uri): String? {
        return if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            val cr = context.contentResolver
            cr.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase(Locale.getDefault()))
        }
    }

}

package com.yiqisport.yiqiapp.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.yiqisport.yiqiapp.R
import com.yiqisport.yiqiapp.data.GlobalVariable
import java.util.*

object MediaIntentUtil {

    //region Pick Media

    /**
     * pick file from gallery
     */
    fun intentForPickVideoMedia(
        fragment: Fragment, types: ArrayList<String>
    ): Intent?
    {
        val requestCode: Int = when (types) {
            picsMimeTypes ->
                GlobalVariable().REQUEST_IMAGE
            videosMimeTypes ->
                GlobalVariable().REQUEST_VIDEO_PICK
            else -> return null
        }

        if (ContextCompat.checkSelfPermission(
                fragment.context!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission(
                fragment,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                requestCode
            )
        } else {

            if (ContextCompat.checkSelfPermission(
                    fragment.context!!,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermission(
                    fragment,
                    Manifest.permission.CAMERA,
                    requestCode
                )
            }else{
                val intentForChoosingGallery = IntentsUtil.getVideoCaptureOrVideoGalleryIntent(
                    fragment.context!!
                )

                if (intentForChoosingGallery == null)
                    IntentsUtil.getPickFileIntent(
                        fragment.context!!,
                        VIDEOS_AND_PICS_MAIN_TYPES, types.toTypedArray()
                    )

                return intentForChoosingGallery
            }

        }
        return null
    }

    /**
     *
     */
    fun checkIfUriIsValid(activity: Activity, uri: Uri, types: ArrayList<String>): Boolean {

        val mimeType = IntentsUtil.getMimeType(activity, uri)
        val identifiedAsVideo = mimeType != null && types.contains(mimeType)
        if (!identifiedAsVideo){
            return false
        }
        try {
            //check that it can be opened and trimmed using our technique
            val fileDescriptor =
                activity.contentResolver?.openFileDescriptor(uri, "r")?.fileDescriptor
            val inputStream =
                (if (fileDescriptor == null) null else activity.contentResolver?.openInputStream(uri)) ?: return false
            inputStream.close()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * Requests given permission.
     * If the permission has been denied previously, a Dialog will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private fun requestPermission(
        fragment: Fragment,
        permission: String,
        requestCode: Int
    ) {
        fragment.requestPermissions(
            arrayOf(permission),
            requestCode
        )
    }

    /**
     *
     */
    fun mp4SendIntent(context: Context, fileAbsPath: String): Intent? {

        val mediaUri = getVideoContentUri(context, fileAbsPath) ?: return null

        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(Intent.EXTRA_STREAM, mediaUri)
        intent.type = "video/mp4"

        return intent
    }

    /**
     * get the video uri in media table or insert the new one
     * //TODO: NEED REMOVE MEDIA DATA ONCE REMOVE THE APP
     */
    @SuppressLint("Recycle")
    private fun getVideoContentUri(context: Context, fileAbsPath: String): Uri? {

        if (fileAbsPath.isBlank()) return null

        val cursor = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Video.Media._ID),
            MediaStore.Video.Media.DATA + "=? ",
            arrayOf(fileAbsPath),
            null
        )

        return if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
            val baseUri = Uri.parse("content://media/external/video/media")
            Uri.withAppendedPath(baseUri, "" + id)
        } else {
            val values = ContentValues()
            values.put(MediaStore.Video.Media.DATA, fileAbsPath)
            context.contentResolver.insert(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                values
            )
        }
    }


    private const val VIDEOS_AND_PICS_MAIN_TYPES = "image/* video/*,"

    val videosMimeTypes : ArrayList<String>
        get() {
            val allowedVideoFileExtensions = arrayOf("mp4","mov")
            val videosMimeTypes = ArrayList<String>(allowedVideoFileExtensions.size)
            val mimeTypeMap = MimeTypeMap.getSingleton()
            for (fileExtension in allowedVideoFileExtensions) {
                val mimeTypeFromExtension = mimeTypeMap.getMimeTypeFromExtension(fileExtension)
                if (mimeTypeFromExtension != null) videosMimeTypes.add(mimeTypeFromExtension)
            }
            return videosMimeTypes
        }

    val picsMimeTypes : ArrayList<String>
        get() {
            val allowedVideoFileExtensions = arrayOf("jpg", "jpeg", "png")
            val picsMimeTypes = ArrayList<String>(allowedVideoFileExtensions.size)
            val mimeTypeMap = MimeTypeMap.getSingleton()
            for (fileExtension in allowedVideoFileExtensions) {
                val mimeTypeFromExtension = mimeTypeMap.getMimeTypeFromExtension(fileExtension)
                if (mimeTypeFromExtension != null) picsMimeTypes.add(mimeTypeFromExtension)
            }
            return picsMimeTypes
        }

}

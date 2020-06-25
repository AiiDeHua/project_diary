@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.xuanyuetech.tocoach.util

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import com.xuanyuetech.tocoach.BuildConfig
import java.io.File
import java.io.FileOutputStream


object ImageUtil {

    /**
     * reduce the size of image to the required size and save the reduced image
     */
    fun reduceSizeOfImageAndSave(imageUri: Uri, outputFilePath : String, context: Context, requiredSize : Int = 75) : Boolean{

        try {
            Log.d("nmd2","${imageUri.path}")
            // BitmapFactory options to downsize the image
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            o.inSampleSize = 6

            // Find the correct scale value. It should be the power of 2.

            Log.d("nmd2","${getPathFromUri(context,imageUri)}")
            var scale = 1
            while (o.outWidth / scale / 2 >= requiredSize &&
                o.outHeight / scale / 2 >= requiredSize
            ) {
                scale *= 2
            }
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2) ?: return false
            inputStream!!.close()

            // here i override the original image file
            val outputFile = File(outputFilePath)
            outputFile.parentFile.mkdirs()
            outputFile.createNewFile()
            val outputStream = FileOutputStream(outputFile)
            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

            return true

        } catch (e: Exception) {

            return false
        }
    }




    /**
     * reduce the size of bitmap to the required size and save the reduced image
     */
    fun reduceSizeOfImageAndSave(bitmap: Bitmap, outputFilePath : String, requiredSize : Int = 75) : Boolean{
        return try {
            val selectedBitmap =  getResizedBitmap(bitmap, requiredSize)

            // here i override the original image file
            val outputFile = File(outputFilePath)
            outputFile.createNewFile()
            val outputStream = FileOutputStream(outputFile)
            selectedBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

            true

        } catch (e: Exception) {
            false
        }
    }

    @SuppressLint("NewApi")
    fun getPathFromUri(
        context: Context,
        uri: Uri?
    ): String? {
        if (uri == null) {
            return null
        }
        // 判斷是否為Android 4.4之後的版本
        val after44 = Build.VERSION.SDK_INT >= 19
        if (after44 && DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是Android 4.4之後的版本，而且屬於文件URI
            val authority = uri.authority
            // 判斷Authority是否為本地端檔案所使用的
            if ("com.android.externalstorage.documents" == authority) {
                // 外部儲存空間
                val docId = DocumentsContract.getDocumentId(uri)
                val divide = docId.split(":").toTypedArray()
                val type = divide[0]
                return if ("primary" == type) {
                    context.getExternalFilesDir(null)!!.absolutePath +("/")+(divide[1])
                } else {
                    "/storage/" + type + "/" + divide[1]
                }
            } else if ("com.android.providers.downloads.documents" == authority) {
                // 下載目錄
                val docId = DocumentsContract.getDocumentId(uri)
                if (docId.startsWith("raw:")) {
                    return docId.replaceFirst("raw:".toRegex(), "")
                }
                val downloadUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    docId.toLong()
                )
                return queryAbsolutePath(context, downloadUri)
            } else if ("com.android.providers.media.documents" == authority) {
                // 圖片、影音檔案
                val docId = DocumentsContract.getDocumentId(uri)
                val divide = docId.split(":").toTypedArray()
                val type = divide[0]
                var mediaUri: Uri
                mediaUri = if ("image" == type) {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                } else {
                    return null
                }
                mediaUri = ContentUris.withAppendedId(mediaUri, divide[1].toLong())
                return queryAbsolutePath(context, mediaUri)
            }
        } else {
            // 如果是一般的URI
            val scheme = uri.scheme
            var path: String? = null
            if ("content" == scheme) {
                // 內容URI
                path = queryAbsolutePath(context, uri)
            } else if ("file" == scheme) {
                // 檔案URI
                path = uri.path
            }
            return path
        }
        return null
    }

    private fun queryAbsolutePath(
        context: Context,
        uri: Uri?
    ): String? {
        val projection =
            arrayOf(MediaStore.MediaColumns.DATA)
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(uri!!, projection, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index: Int = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                return cursor.getString(index)
            }
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
            cursor?.close()
        }
        return null
    }

    /**
     * Save profile image
     */
    fun saveFolderProfileImage(imageFileUri : Uri?, folderId : Int, context: Context) : String {

        if(imageFileUri == null) return ""

        val imagePath = FilePathHelper(context).folderProfileImagePath(folderId)
        val isSuccess = reduceSizeOfImageAndSave(imageFileUri, imagePath, context, 75)

        if(isSuccess) return imagePath
        return ""
    }

    /**
     *
     */
    fun saveFolderBackgroundImage(imageFileUri : Uri?, folderId : Int, context: Context) : String {
        if(imageFileUri == null) return ""

        //获取外部存贮目录
        val imagePath = FilePathHelper(context).folderBackgroundImagePath(folderId)
        val isSuccess = reduceSizeOfImageAndSave(imageFileUri, imagePath, context, 150)

        if(isSuccess) return imagePath
        return ""
    }

    /**
     * reduces the size of the image
     */
    private fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    fun getPathForDrawable(drawable : Int): String {
        return "android.resource://" + BuildConfig.APPLICATION_ID +"/" + drawable
    }

}

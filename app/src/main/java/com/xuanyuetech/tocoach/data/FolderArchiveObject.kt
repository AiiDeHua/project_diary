package com.xuanyuetech.tocoach.data

import org.threeten.bp.LocalDateTime

/**
 * folder archive object interface
 */
interface FolderArchiveObject {

    /**
     * get the title of the archive
     */
    fun getArchiveTitle() : String

    /**
     * get the image shown
     */
    fun getArchiveImagePath() : String

    /**
     * get the subtitle, usually the time
     */
    fun getArchiveSubtitle() : String

    /**
     * show the folder name
     */
    fun getArchiveFolderName() : String

    /**
     * get the update time for sort
     */
    fun getArchiveUpdateTime() : LocalDateTime

    /**
     * get the init time for sort
     */
    fun getArchiveInitTime() : LocalDateTime
}
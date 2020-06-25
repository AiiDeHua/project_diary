package com.xuanyuetech.tocoach.data

import org.threeten.bp.LocalDateTime

/**
 * student archive object interface
 */
interface StudentArchiveObject {

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
     * show the student name
     */
    fun getArchiveStudentName() : String

    /**
     * get the update time for sort
     */
    fun getArchiveUpdateTime() : LocalDateTime

    /**
     * get the init time for sort
     */
    fun getArchiveInitTime() : LocalDateTime
}
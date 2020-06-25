package com.xuanyuetech.tocoach.data

import org.threeten.bp.LocalDateTime

/**
 * home event object interface
 */
interface HomeEventObject {

    /**
     * the title text of home event
     */
    fun getHomeEventTitle() : String

    /**
     * the image show of home event
     */
    fun getHomeEventImagePath() : String

    /**
     * the type of home event
     */
    fun getHomeEventType() : String

    /**
     * the reference id of home event
     */
    fun getHomeEventRefId() : String

    /**
     * the init time of the home event
     */
    fun getHomeEventInitTime() : LocalDateTime

    /**
     * the student id of the home event
     */
    fun getStudentId() : String
}
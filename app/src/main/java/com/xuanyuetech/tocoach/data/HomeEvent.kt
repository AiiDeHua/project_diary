package com.xuanyuetech.tocoach.data

import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

/**
 * Home event data
 */
class HomeEvent() {

    //region properties

    var id = -1
    var title = ""
    var notes = ""
    var imagePath = ""
    var type = ""
    var refId = ""
    var time = ""
    var studentId = ""

    //internal conversion usage
    private val timeFormatter = DateTimeFormatter.ofPattern("yyyy年 M月 d日 HH:mm EEEE")

    //endregion

    //region constructor

    /**
     * for database conversion
     */
    constructor(id : Int, title : String, notes: String, imageUrl : String,
                type : String, refId : String, time : String, studentId : String) : this() {
        this.id = id
        this.title = title
        this.notes = notes
        this.imagePath = imageUrl
        this.type = type
        this.refId = refId
        this.time = time
        this.studentId = studentId
    }

    /**
     * for new object create
     */
    constructor(homeEventObject : HomeEventObject) : this(){
        this.title = homeEventObject.getHomeEventTitle()
        this.imagePath = homeEventObject.getHomeEventImagePath()
        this.notes = homeEventObject.getHomeEventInitTime().toNotes()
        this.type = homeEventObject.getHomeEventType()
        this.refId = homeEventObject.getHomeEventRefId()
        this.time = homeEventObject.getHomeEventInitTime().toStr()
        this.studentId = homeEventObject.getStudentId()
    }

    //endregion

    //region fun

    /**
     * update the object data
     */
    fun update(homeEventObject : HomeEventObject){
        this.title = homeEventObject.getHomeEventTitle()
        this.imagePath = homeEventObject.getHomeEventImagePath()
        this.notes = homeEventObject.getHomeEventInitTime().toNotes()
        this.type = homeEventObject.getHomeEventType()
        this.time = homeEventObject.getHomeEventInitTime().toStr()
    }

    /**
     * the type indicator of video
     */
    fun videoType() = "video"

    /**
     * the type indicator of diary
     */
    fun diaryType() = "diary"

    /**
     * conversion to notes
     */
    private fun LocalDateTime.toNotes() = this.format(DateTimeFormatter.ofPattern("M 月 d 日 HH:mm EEEE"))

    /**
     * conversion to string
     */
    private fun LocalDateTime.toStr() = this.format(timeFormatter)

    //endregion

}
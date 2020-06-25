package com.xuanyuetech.tocoach.data

import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

/**
 * the video class which can be shown in student archive and home event
 */
class Video() : StudentArchiveObject, HomeEventObject{

    //region properties

    var studentId = -1
    var id = -1
    var initTime = ""
    var updateTime = ""
    var title = ""
    var cloudUrl = ""
    var localUrl = ""
    var notes = ""
    var coverPath = ""
    var studentName = ""

    val maxTitleLength = 15

    //internal conversion usage
    private val timeFormatter = DateTimeFormatter.ofPattern("yyyy年 M月 d日 HH:mm EEEE")

    //endregion

    //region constructor

    /**
     * convert from DB
     */
    constructor(studentId : Int, id : Int, initTime : String, title : String, cloudUrl: String,
                localUrl : String, notes : String, coverUrl : String, updateTime : String, studentName : String)
            : this() {
        this.studentId = studentId
        this.id = id
        this.initTime = initTime
        this.title = title
        this.cloudUrl = cloudUrl
        this.localUrl = localUrl
        this.notes = notes
        this.coverPath = coverUrl
        this.updateTime = updateTime
        this.studentName = studentName
    }

    /**
     * new create video
     */
    constructor(studentId : Int, initTime : LocalDateTime, title : String, cloudUrl: String,
                localUrl : String,  notes : String, coverUrl : String, studentName : String) : this() {
        this.studentId = studentId
        this.initTime = initTime.format(timeFormatter)
        this.title = title
        this.cloudUrl = cloudUrl
        this.localUrl = localUrl
        this.notes = notes
        this.coverPath = coverUrl
        this.updateTime = initTime.format(timeFormatter)
        this.studentName = studentName
    }

    //endregion

    //region fun

    /**
     * update time setting //TODO: can be bind into whenever update the object
     */
    fun setUpdateTime(updateTime: LocalDateTime){
        this.updateTime = updateTime.format(timeFormatter)
    }

    //endregion

    //region studentArchiveObject interface

    override fun getArchiveTitle() : String{
        return title
    }

    override fun getArchiveSubtitle()  : String{
        return initTime
    }

    override fun getArchiveImagePath()  : String{
        return coverPath
    }

    override fun getArchiveUpdateTime(): LocalDateTime {
        return LocalDateTime.parse(updateTime, timeFormatter)
    }

    override fun getArchiveInitTime(): LocalDateTime {
        return LocalDateTime.parse(initTime, timeFormatter)
    }

    override fun getArchiveStudentName(): String {
        return studentName
    }

    //endregion

    //region HomeEventObject interface
    override fun getHomeEventTitle(): String {
        return title
    }

    override fun getHomeEventImagePath(): String {
        return coverPath
    }

    override fun getHomeEventType(): String {
        return HomeEvent().videoType()
    }

    override fun getHomeEventRefId(): String {
        return id.toString()
    }

    override fun getHomeEventInitTime(): LocalDateTime {
        return LocalDateTime.parse(initTime, timeFormatter)
    }

    override fun getStudentId(): String {
        return studentId.toString()
    }

    //endregion
}
package com.xuanyuetech.tocoach.data

import com.xuanyuetech.tocoach.R
import com.xuanyuetech.tocoach.util.ImageUtil
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

/**
 * Dairy data
 */
class Diary() : StudentArchiveObject, HomeEventObject{

    //region properties

    var id = -1
    var studentId = -1

    var title = ""
    var content = ""
    var updateTime = ""
    var initTime = ""
    var studentName = ""

    //internal conversion usage
    private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年 M月 d日 HH:mm EEEE")

    var maxTitleLength = 15

    //endregion

    //region constructor

    /**
     * new diary create
     */
    constructor(studentId : Int, localDateTime: LocalDateTime, studentName: String) : this(){
        this.studentId = studentId
        this.updateTime = localDateTime.format(timeFormatter)
        initTime = localDateTime.format(timeFormatter)
        this.studentName = studentName
    }

    /**
     * convert from DB
     */
    constructor(id : Int, studentId : Int, initTime : String, title : String, content: String, updateTime : String, studentName : String) : this() {
        this.id = id
        this.title = title
        this.content = content
        this.studentId = studentId
        this.updateTime = updateTime
        this.initTime = initTime
        this.studentName = studentName
    }

    //endregion

    //region fun

    /**
     * set update time
     */
    fun setUpdateTime(localDateTime: LocalDateTime){
        this.updateTime = localDateTime.format(timeFormatter)
    }

    //endregion

    //region StudentArchiveObject interface

    override fun getArchiveTitle(): String {
        return title
    }

    override fun getArchiveImagePath(): String {
        return ""
    }

    override fun getArchiveSubtitle(): String {
        return initTime
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
        return ImageUtil.getPathForDrawable(R.drawable.diary_cover)
    }

    override fun getHomeEventType(): String {
       return HomeEvent().diaryType()
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
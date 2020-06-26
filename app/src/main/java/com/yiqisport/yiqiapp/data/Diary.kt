package com.yiqisport.yiqiapp.data

import com.yiqisport.yiqiapp.R
import com.yiqisport.yiqiapp.util.ImageUtil
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

/**
 * Dairy data
 */
class Diary() : FolderArchiveObject, HomeEventObject{

    //region properties

    var id = -1
    var folderId = -1

    var title = ""
    var content = ""
    var updateTime = ""
    var initTime = ""
    var folderName = ""

    //internal conversion usage
    private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年 M月 d日 HH:mm EEEE")

    var maxTitleLength = 15

    //endregion

    //region constructor

    /**
     * new diary create
     */
    constructor(folderId : Int, localDateTime: LocalDateTime, folderName: String) : this(){
        this.folderId = folderId
        this.updateTime = localDateTime.format(timeFormatter)
        initTime = localDateTime.format(timeFormatter)
        this.folderName = folderName
    }

    /**
     * convert from DB
     */
    constructor(id : Int, folderId : Int, initTime : String, title : String, content: String, updateTime : String, folderName : String) : this() {
        this.id = id
        this.title = title
        this.content = content
        this.folderId = folderId
        this.updateTime = updateTime
        this.initTime = initTime
        this.folderName = folderName
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

    //region FolderArchiveObject interface

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

    override fun getArchiveFolderName(): String {
        return folderName
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

    override fun getFolderId(): String {
        return folderId.toString()
    }

    //endregion

}
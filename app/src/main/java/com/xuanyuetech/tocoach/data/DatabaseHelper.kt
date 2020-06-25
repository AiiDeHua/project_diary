package com.xuanyuetech.tocoach.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlin.collections.ArrayList

/**
 * database helper to store data in SQLite
 */
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    //region object

    companion object {

        // Database Version
        private const val DATABASE_VERSION = 2

        // Database Name
        private const val DATABASE_NAME = "folderManager.db"

        // User table name
        private const val TABLE_FOLDER = "folder"
        private const val TABLE_FOLDER_VIDEO = "folder_video"
        private const val TABLE_CALENDAR_EVENT = "calendar_event"
        private const val TABLE_HOME_EVENT = "home_event"
        private const val TABLE_DAIRY = "diary"

        // User Table Columns names for folder
        private const val COLUMN_FOLDER_ID = "folder_id"
        private const val COLUMN_FOLDER_NAME = "folder_name"
        private const val COLUMN_FOLDER_NOTES = "folder_notes"
        private const val COLUMN_FOLDER_PROFILE_IMAGE = "folder_profile_image"
        private const val COLUMN_FOLDER_BACKGROUND_IMAGE = "folder_background_image"

        //Columns for folder video
        private const val COLUMN_FOLDER_VIDEO_ID = "folder_video_id"
        private const val COLUMN_FOLDER_VIDEO_INIT_TIME = "folder_video_init_time"
        private const val COLUMN_FOLDER_VIDEO_UPDATE_TIME = "folder_video_update_time"
        private const val COLUMN_FOLDER_VIDEO_CLOUD_URL = "folder_video_cloud_url"
        private const val COLUMN_FOLDER_VIDEO_LOCAL_URL = "folder_video_local_url"
        private const val COLUMN_FOLDER_VIDEO_TITLE = "folder_video_title"
        private const val COLUMN_FOLDER_VIDEO_NOTES = "folder_video_notes"
        private const val COLUMN_FOLDER_VIDEO_COVER_URL = "folder_video_cover_url"
        private const val COLUMN_FOLDER_VIDEO_FOLDER_NAME = "folder_video_folder_name"

        //Columns for CalendarEvent
        private const val COLUMN_CALENDAR_EVENT_ID = "calender_event_id"
        private const val COLUMN_CALENDAR_EVENT_TITLE = "calendar_event_title"
        private const val COLUMN_CALENDAR_EVENT_ATTENDANTS = "calendar_event_attendants"
        private const val COLUMN_CALENDAR_EVENT_LOCATION = "calendar_event_location"
        private const val COLUMN_CALENDAR_EVENT_NOTES = "calendar_event_notes"
        private const val COLUMN_CALENDAR_EVENT_START_TIME = "calendar_event_start_time"
        private const val COLUMN_CALENDAR_EVENT_END_TIME = "calendar_event_end_time"
        private const val COLUMN_CALENDAR_EVENT_DAY_OF_WEEK = "calendar_event_day_of_week"
        private const val COLUMN_CALENDAR_EVENT_FREQUENCY = "calendar_event_frequency"
        private const val COLUMN_CALENDAR_EVENT_FREQUENCY_START = "calendar_event_frequency_start"
        private const val COLUMN_CALENDAR_EVENT_FREQUENCY_END = "calendar_event_frequency_end"

        //Columns for homeEvent
        private const val COLUMN_HOME_EVENT_ID = "home_event_id"
        private const val COLUMN_HOME_EVENT_TITLE = "home_event_title"
        private const val COLUMN_HOME_EVENT_NOTES = "home_event_notes"
        private const val COLUMN_HOME_EVENT_IMAGE_URL = "home_event_image_url"
        private const val COLUMN_HOME_EVENT_TYPE = "home_event_type"
        private const val COLUMN_HOME_EVENT_REF_ID = "home_event_ref_id"
        private const val COLUMN_HOME_EVENT_TIME = "home_event_time"

        //Columns for homeEvent
        private const val COLUMN_DAIRY_ID = "diary_id"
        private const val COLUMN_DAIRY_INIT_TIME = "diary_init_time"
        private const val COLUMN_DAIRY_TITLE = "diary_title"
        private const val COLUMN_DAIRY_CONTENT = "diary_content"
        private const val COLUMN_DAIRY_FOLDER_NAME = "folder_name"
        private const val COLUMN_DAIRY_UPDATE_TIME = "diary_update_time"

    }

    //endregion

    //region create table sql query

    private val createFolderTable = (
            "CREATE TABLE " + TABLE_FOLDER + "("
                    + COLUMN_FOLDER_ID.toIntPKAutoInc(true) + ","
                    + COLUMN_FOLDER_NAME.toTextNotNull(true) + ","
                    + COLUMN_FOLDER_PROFILE_IMAGE.toTextNotNull(false) + ","
                    + COLUMN_FOLDER_NOTES.toTextNotNull(false) + ","
                    + COLUMN_FOLDER_BACKGROUND_IMAGE.toTextNotNull(false) +  ")")

    private val createFolderVideoTable = (
            "CREATE TABLE " + TABLE_FOLDER_VIDEO + "("
                    + COLUMN_FOLDER_VIDEO_ID.toIntPKAutoInc(true) + ","
                    + COLUMN_FOLDER_VIDEO_INIT_TIME.toTextNotNull(true) + ","
                    + COLUMN_FOLDER_VIDEO_UPDATE_TIME.toTextNotNull(true) + ","
                    + COLUMN_FOLDER_VIDEO_TITLE.toTextNotNull(false) + ","
                    + COLUMN_FOLDER_ID + " INTEGER REFERENCES " + TABLE_FOLDER + "(" + COLUMN_FOLDER_ID + ") ON DELETE CASCADE,"
                    + COLUMN_FOLDER_VIDEO_CLOUD_URL.toTextNotNull(false) + ","
                    + COLUMN_FOLDER_VIDEO_LOCAL_URL.toTextNotNull(false) + ","
                    + COLUMN_FOLDER_VIDEO_NOTES.toTextNotNull(false) + ","
                    + COLUMN_FOLDER_VIDEO_COVER_URL.toTextNotNull(false) + ","
                    + COLUMN_FOLDER_VIDEO_FOLDER_NAME.toTextNotNull(false) +")")

    private val createCalendarEventTable = (
            "CREATE TABLE " + TABLE_CALENDAR_EVENT + "("
                    + COLUMN_CALENDAR_EVENT_ID.toIntPKAutoInc(true) + ","
                    + COLUMN_CALENDAR_EVENT_TITLE.toTextNotNull(true) + ","
                    + COLUMN_CALENDAR_EVENT_ATTENDANTS.toTextNotNull(false) + ","
                    + COLUMN_CALENDAR_EVENT_LOCATION.toTextNotNull(false) + ","
                    + COLUMN_CALENDAR_EVENT_NOTES.toTextNotNull(false) + ","
                    + COLUMN_CALENDAR_EVENT_START_TIME.toTextNotNull(true) + ","
                    + COLUMN_CALENDAR_EVENT_END_TIME.toTextNotNull(true) + ","
                    + COLUMN_CALENDAR_EVENT_DAY_OF_WEEK.toTextNotNull(false) + ","
                    + COLUMN_CALENDAR_EVENT_FREQUENCY.toIntNotNull(true) + ","
                    + COLUMN_CALENDAR_EVENT_FREQUENCY_START.toTextNotNull(true) + ","
                    + COLUMN_CALENDAR_EVENT_FREQUENCY_END.toTextNotNull(true) +")")

    private val createHomeEventTable = (
            "CREATE TABLE " + TABLE_HOME_EVENT + "("
                    + COLUMN_HOME_EVENT_ID.toIntPKAutoInc(true) + ","
                    + COLUMN_HOME_EVENT_TITLE.toTextNotNull(true) + ","
                    + COLUMN_HOME_EVENT_NOTES.toTextNotNull(true) + ","
                    + COLUMN_HOME_EVENT_IMAGE_URL.toTextNotNull(false) + ","
                    + COLUMN_HOME_EVENT_TYPE.toTextNotNull(true) + ","
                    + COLUMN_HOME_EVENT_REF_ID.toTextNotNull(true) + ","
                    + COLUMN_FOLDER_ID + " INTEGER REFERENCES " + TABLE_FOLDER + "(" + COLUMN_FOLDER_ID + ") ON DELETE CASCADE,"
                    + COLUMN_HOME_EVENT_TIME.toTextNotNull(true) + ")")

    private val createDairyTable = (
            "CREATE TABLE " + TABLE_DAIRY + "("
                    + COLUMN_DAIRY_ID.toIntPKAutoInc(true) + ","
                    + COLUMN_FOLDER_ID + " INTEGER REFERENCES " + TABLE_FOLDER + "(" + COLUMN_FOLDER_ID + ") ON DELETE CASCADE,"
                    + COLUMN_DAIRY_TITLE.toTextNotNull(false) + ","
                    + COLUMN_DAIRY_INIT_TIME.toTextNotNull(true) + ","
                    + COLUMN_DAIRY_UPDATE_TIME.toTextNotNull(true) + ","
                    + COLUMN_DAIRY_CONTENT.toTextNotNull(false) + ","
                    + COLUMN_DAIRY_FOLDER_NAME.toTextNotNull(true) + ")")

    //endregion

    //region drop table sql query
    private val dropFolderTable: String
        get() = "DROP TABLE IF EXISTS $TABLE_FOLDER"
    private val dropFolderVideoTable: String
        get() = "DROP TABLE IF EXISTS $TABLE_FOLDER_VIDEO"
    private val dropCalendarEventTable: String
        get() = "DROP TABLE IF EXISTS $TABLE_CALENDAR_EVENT"
    private val dropHomeEventTable: String
        get() = "DROP TABLE IF EXISTS $TABLE_HOME_EVENT"
    private val dropDairyTable: String
        get() = "DROP TABLE IF EXISTS $TABLE_DAIRY"

    //endregion

    //region override

    /**
     * create req tables
     */
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createFolderTable)
        db.execSQL(createFolderVideoTable)
        db.execSQL(createCalendarEventTable)
        db.execSQL(createHomeEventTable)
        db.execSQL(createDairyTable)
    }

    //TODO: NEED UPGRADE WITHOUT DROP OLD DATABASE
    /**
     * upgrade the existing tables
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

    /**
     * open db
     */
    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
        if (!db!!.isReadOnly) {
            //Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys = ON;")
        }
    }

    //endregion

    //region query function

    //region folder table

    /**
     *
     */
    fun getAllFolder(): ArrayList<Folder> {

        // array of columns to fetch
        val columns = arrayOf(
            COLUMN_FOLDER_ID,
            COLUMN_FOLDER_NAME,
            COLUMN_FOLDER_NOTES,
            COLUMN_FOLDER_PROFILE_IMAGE,
            COLUMN_FOLDER_BACKGROUND_IMAGE
        )

        // sorting orders
        val sortOrder = "$COLUMN_FOLDER_ID ASC"
        val folderList = ArrayList<Folder>()

        val db = this.readableDatabase

        // query the user table
        val cursor = db.query(
            TABLE_FOLDER, //Table to query
            columns,            //columns to return
            null,     //columns for the WHERE clause
            null,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            sortOrder //The sort order
        )

        if (cursor.moveToFirst()) {
            do {
                val foundFolder = Folder(
                    id = cursor.getDecodeString(COLUMN_FOLDER_ID).toInt(),
                    name = cursor.getDecodeString(COLUMN_FOLDER_NAME),
                    notes = cursor.getDecodeString(COLUMN_FOLDER_NOTES),
                    profileImagePath = cursor.getDecodeString(COLUMN_FOLDER_PROFILE_IMAGE),
                    backgroundImagePath = cursor.getDecodeString(COLUMN_FOLDER_BACKGROUND_IMAGE)
                )

                folderList.add(foundFolder)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return folderList
    }

    /**
     *
     */
    fun findFolderById(id: Int): Folder? {

        // array of columns to fetch
        val columns = arrayOf(
            COLUMN_FOLDER_ID,
            COLUMN_FOLDER_NAME,
            COLUMN_FOLDER_NOTES,
            COLUMN_FOLDER_PROFILE_IMAGE,
            COLUMN_FOLDER_BACKGROUND_IMAGE
        )

        var folder : Folder? = null

        val db = this.readableDatabase

        // query the user table
        val cursor = db.query(
            TABLE_FOLDER, //Table to query
            columns,            //columns to return
            "$COLUMN_FOLDER_ID = ? ",     //columns for the WHERE clause
            arrayOf(id.toString()),  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            null //The sort order
        )

        if (cursor.moveToFirst()) {
            do {
                val findFolder = Folder(
                    id = cursor.getDecodeString(COLUMN_FOLDER_ID).toInt(),
                    name = cursor.getDecodeString(COLUMN_FOLDER_NAME),
                    notes = cursor.getDecodeString(COLUMN_FOLDER_NOTES),
                    profileImagePath = cursor.getDecodeString(COLUMN_FOLDER_PROFILE_IMAGE),
                    backgroundImagePath = cursor.getDecodeString(COLUMN_FOLDER_BACKGROUND_IMAGE)
                )

                folder = findFolder

            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return folder
    }

    /**
     *
     */
    fun findHomeEventByRef(homeEventObject: HomeEventObject) : HomeEvent? =
        getAllHomeEvent().first { he ->
            he.type = homeEventObject.getHomeEventType()
            he.refId == homeEventObject.getHomeEventRefId()
        }

    //endregion

    //region video table

    /**
     *
     */
    fun getAllFolderVideo(): ArrayList<Video> {

        // array of columns to fetch
        val columns = arrayOf(
            COLUMN_FOLDER_VIDEO_ID,
            COLUMN_FOLDER_ID,
            COLUMN_FOLDER_VIDEO_INIT_TIME,
            COLUMN_FOLDER_VIDEO_UPDATE_TIME,
            COLUMN_FOLDER_VIDEO_TITLE,
            COLUMN_FOLDER_VIDEO_CLOUD_URL,
            COLUMN_FOLDER_VIDEO_LOCAL_URL,
            COLUMN_FOLDER_VIDEO_NOTES,
            COLUMN_FOLDER_VIDEO_COVER_URL,
            COLUMN_FOLDER_VIDEO_FOLDER_NAME
        )

        // sorting orders
        val sortOrder = "$COLUMN_FOLDER_VIDEO_ID DESC"
        val folderVideoList = ArrayList<Video>()

        val db = this.readableDatabase

        // query the user table
        val cursor = db.query(
            TABLE_FOLDER_VIDEO, //Table to query
            columns,            //columns to return
            null,
            null,
            null,      //group the rows
            null,       //filter by row groups
            sortOrder //The sort order
        )

        if (cursor.moveToFirst()) {
            do {
                val folderV = Video(
                    id = cursor.getDecodeString(COLUMN_FOLDER_VIDEO_ID).toInt(),
                    folderId = cursor.getDecodeString(COLUMN_FOLDER_ID).toInt(),
                    initTime = cursor.getDecodeString(COLUMN_FOLDER_VIDEO_INIT_TIME),
                    updateTime = cursor.getDecodeString(COLUMN_FOLDER_VIDEO_UPDATE_TIME),
                    title = cursor.getDecodeString(COLUMN_FOLDER_VIDEO_TITLE),
                    cloudUrl = cursor.getDecodeString(COLUMN_FOLDER_VIDEO_CLOUD_URL),
                    localUrl = cursor.getDecodeString(COLUMN_FOLDER_VIDEO_LOCAL_URL),
                    notes = cursor.getDecodeString(COLUMN_FOLDER_VIDEO_NOTES),
                    coverUrl = cursor.getDecodeString(COLUMN_FOLDER_VIDEO_COVER_URL),
                    folderName = cursor.getDecodeString(COLUMN_FOLDER_VIDEO_FOLDER_NAME)
                )

                folderVideoList.add(folderV)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return folderVideoList
    }

    /**
     *
     */
    fun getAllFolderVideoByFolderId(folderId: Int): ArrayList<Video> {

        // array of columns to fetch
        val columns = arrayOf(
            COLUMN_FOLDER_VIDEO_ID,
            COLUMN_FOLDER_ID,
            COLUMN_FOLDER_VIDEO_INIT_TIME,
            COLUMN_FOLDER_VIDEO_UPDATE_TIME,
            COLUMN_FOLDER_VIDEO_TITLE,
            COLUMN_FOLDER_VIDEO_CLOUD_URL,
            COLUMN_FOLDER_VIDEO_LOCAL_URL,
            COLUMN_FOLDER_VIDEO_NOTES,
            COLUMN_FOLDER_VIDEO_COVER_URL,
            COLUMN_FOLDER_VIDEO_FOLDER_NAME
        )

        // sorting orders
        val sortOrder = "$COLUMN_FOLDER_VIDEO_ID DESC"
        val folderVideoList = ArrayList<Video>()

        val db = this.readableDatabase

        // query the user table
        val cursor = db.query(
            TABLE_FOLDER_VIDEO, //Table to query
            columns,            //columns to return
            "$COLUMN_FOLDER_ID = ? ",     //columns for the WHERE clause
            arrayOf(folderId.toString()),  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            sortOrder //The sort order
        )

        if (cursor.moveToFirst()) {
            do {
                val folderV = Video(
                    id = cursor.getDecodeString(COLUMN_FOLDER_VIDEO_ID).toInt(),
                    folderId = cursor.getDecodeString(COLUMN_FOLDER_ID).toInt(),
                    initTime = cursor.getDecodeString(COLUMN_FOLDER_VIDEO_INIT_TIME),
                    updateTime = cursor.getDecodeString(COLUMN_FOLDER_VIDEO_UPDATE_TIME),
                    title = cursor.getDecodeString(COLUMN_FOLDER_VIDEO_TITLE),
                    cloudUrl = cursor.getDecodeString(COLUMN_FOLDER_VIDEO_CLOUD_URL),
                    localUrl = cursor.getDecodeString(COLUMN_FOLDER_VIDEO_LOCAL_URL),
                    notes = cursor.getDecodeString(COLUMN_FOLDER_VIDEO_NOTES),
                    coverUrl = cursor.getDecodeString(COLUMN_FOLDER_VIDEO_COVER_URL),
                    folderName = cursor.getDecodeString(COLUMN_FOLDER_VIDEO_FOLDER_NAME)

                )

                folderVideoList.add(folderV)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return folderVideoList
    }

    /**
     *
     */
    fun findVideoByIdFromID(id: Int): Video? {

        // array of columns to fetch
        val columns = arrayOf(
            COLUMN_FOLDER_VIDEO_ID,
            COLUMN_FOLDER_ID,
            COLUMN_FOLDER_VIDEO_INIT_TIME,
            COLUMN_FOLDER_VIDEO_UPDATE_TIME,
            COLUMN_FOLDER_VIDEO_TITLE,
            COLUMN_FOLDER_VIDEO_CLOUD_URL,
            COLUMN_FOLDER_VIDEO_LOCAL_URL,
            COLUMN_FOLDER_VIDEO_NOTES,
            COLUMN_FOLDER_VIDEO_COVER_URL,
            COLUMN_FOLDER_VIDEO_FOLDER_NAME
        )

        // sorting orders
        var video: Video? = null
        val db = this.readableDatabase

        // query the user table
        val cursor = db.query(
            TABLE_FOLDER_VIDEO, //Table to query
            columns,            //columns to return
            "$COLUMN_FOLDER_VIDEO_ID = ? ",     //columns for the WHERE clause
            arrayOf(id.toString()),  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            null //The sort order
        )

        if (cursor.moveToFirst()) {
            do {
                video = Video(
                    id = cursor.getDecodeString(COLUMN_FOLDER_VIDEO_ID).toInt(),
                    folderId = cursor.getDecodeString(COLUMN_FOLDER_ID).toInt(),
                    initTime = cursor.getDecodeString(COLUMN_FOLDER_VIDEO_INIT_TIME),
                    updateTime = cursor.getDecodeString(COLUMN_FOLDER_VIDEO_UPDATE_TIME),
                    title = cursor.getDecodeString(COLUMN_FOLDER_VIDEO_TITLE),
                    cloudUrl = cursor.getDecodeString(COLUMN_FOLDER_VIDEO_CLOUD_URL),
                    localUrl = cursor.getDecodeString(COLUMN_FOLDER_VIDEO_LOCAL_URL),
                    notes = cursor.getDecodeString(COLUMN_FOLDER_VIDEO_NOTES),
                    coverUrl = cursor.getDecodeString(COLUMN_FOLDER_VIDEO_COVER_URL),
                    folderName = cursor.getDecodeString(COLUMN_FOLDER_VIDEO_FOLDER_NAME)
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return video
    }

    //endregion

    //region calendarEvent table query

    /**
     * function to get all calendarEvent
     */
    fun getAllCalendarEvent(): ArrayList<CalendarEvent> {

        // array of columns to fetch
        val columns = arrayOf(
            COLUMN_CALENDAR_EVENT_ID,
            COLUMN_CALENDAR_EVENT_TITLE,
            COLUMN_CALENDAR_EVENT_ATTENDANTS,
            COLUMN_CALENDAR_EVENT_LOCATION,
            COLUMN_CALENDAR_EVENT_NOTES,
            COLUMN_CALENDAR_EVENT_START_TIME,
            COLUMN_CALENDAR_EVENT_END_TIME,
            COLUMN_CALENDAR_EVENT_DAY_OF_WEEK,
            COLUMN_CALENDAR_EVENT_FREQUENCY,
            COLUMN_CALENDAR_EVENT_FREQUENCY_START,
            COLUMN_CALENDAR_EVENT_FREQUENCY_END
        )

        // sorting orders
        val sortOrder = "$COLUMN_CALENDAR_EVENT_ID ASC"
        val calendarEventList = ArrayList<CalendarEvent>()

        val db = this.readableDatabase

        // query the user table
        val cursor = db.query(
            TABLE_CALENDAR_EVENT, //Table to query
            columns,            //columns to return
            null,     //columns for the WHERE clause
            null,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            sortOrder //The sort order
        )

        if (cursor.moveToFirst()) {
            do {
                val calendarEvent = CalendarEvent(
                    id = cursor.getDecodeString(COLUMN_CALENDAR_EVENT_ID).toInt(),
                    title = cursor.getDecodeString(COLUMN_CALENDAR_EVENT_TITLE),
                    attendants = cursor.getDecodeString(COLUMN_CALENDAR_EVENT_ATTENDANTS),
                    location = cursor.getDecodeString(COLUMN_CALENDAR_EVENT_LOCATION),
                    notes = cursor.getDecodeString(COLUMN_CALENDAR_EVENT_NOTES),
                    startTimeDBStr = cursor.getDecodeString(COLUMN_CALENDAR_EVENT_START_TIME),
                    endTimeDBStr = cursor.getDecodeString(COLUMN_CALENDAR_EVENT_END_TIME),
                    dayOfWeekDBStr = cursor.getDecodeString(COLUMN_CALENDAR_EVENT_DAY_OF_WEEK),
                    frequency = cursor.getDecodeString(COLUMN_CALENDAR_EVENT_FREQUENCY).toInt(),
                    eventStartDate = cursor.getDecodeString(COLUMN_CALENDAR_EVENT_FREQUENCY_START),
                    eventEndDate = cursor.getDecodeString(COLUMN_CALENDAR_EVENT_FREQUENCY_END)
                )
                calendarEventList.add(calendarEvent)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return calendarEventList
    }


    //endregion


    //region homeEvent table query

    /**
     * function to get all homeEvent
     */
    fun getAllHomeEvent(): ArrayList<HomeEvent> {

        // array of columns to fetch
        val columns = arrayOf(
            COLUMN_HOME_EVENT_ID,
            COLUMN_FOLDER_ID,
            COLUMN_HOME_EVENT_TITLE,
            COLUMN_HOME_EVENT_NOTES,
            COLUMN_HOME_EVENT_IMAGE_URL,
            COLUMN_HOME_EVENT_TYPE,
            COLUMN_HOME_EVENT_REF_ID,
            COLUMN_HOME_EVENT_TIME
        )

        // sorting orders
        val sortOrder = "$COLUMN_HOME_EVENT_ID DESC"
        val homeEventList = ArrayList<HomeEvent>()

        val db = this.readableDatabase

        // query the table
        val cursor = db.query(
            TABLE_HOME_EVENT, //Table to query
            columns,            //columns to return
            null,     //columns for the WHERE clause
            null,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            sortOrder //The sort order
        )

        if (cursor.moveToFirst()) {
            do {
                val homeEvent = HomeEvent(
                    id = cursor.getDecodeString(COLUMN_HOME_EVENT_ID).toInt(),
                    folderId = cursor.getDecodeString(COLUMN_FOLDER_ID),
                    title = cursor.getDecodeString(COLUMN_HOME_EVENT_TITLE),
                    notes = cursor.getDecodeString(COLUMN_HOME_EVENT_NOTES),
                    imageUrl = cursor.getDecodeString(COLUMN_HOME_EVENT_IMAGE_URL),
                    type = cursor.getDecodeString(COLUMN_HOME_EVENT_TYPE),
                    refId = cursor.getDecodeString(COLUMN_HOME_EVENT_REF_ID),
                    time = cursor.getDecodeString(COLUMN_HOME_EVENT_TIME)
                )
                homeEventList.add(homeEvent)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return homeEventList
    }

    //endregion

    //region video table

    /**
     *
     */
    fun getAllDairy(): ArrayList<Diary> {

        // array of columns to fetch
        val columns = arrayOf(
            COLUMN_DAIRY_ID,
            COLUMN_FOLDER_ID,
            COLUMN_DAIRY_INIT_TIME,
            COLUMN_DAIRY_UPDATE_TIME,
            COLUMN_DAIRY_TITLE,
            COLUMN_DAIRY_CONTENT,
            COLUMN_DAIRY_FOLDER_NAME
        )

        // sorting orders
        val sortOrder = "$COLUMN_DAIRY_ID DESC"
        val diaryList = ArrayList<Diary>()

        val db = this.readableDatabase

        // query the user table
        val cursor = db.query(
            TABLE_DAIRY, //Table to query
            columns,            //columns to return
            null,
            null,
            null,      //group the rows
            null,       //filter by row groups
            sortOrder //The sort order
        )

        if (cursor.moveToFirst()) {
            do {
                val diary = Diary(
                    id = cursor.getDecodeString(COLUMN_DAIRY_ID).toInt(),
                    folderId = cursor.getDecodeString(COLUMN_FOLDER_ID).toInt(),
                    initTime = cursor.getDecodeString(COLUMN_DAIRY_INIT_TIME),
                    updateTime = cursor.getDecodeString(COLUMN_DAIRY_UPDATE_TIME),
                    title = cursor.getDecodeString(COLUMN_DAIRY_TITLE),
                    content = cursor.getDecodeString(COLUMN_DAIRY_CONTENT),
                    folderName = cursor.getDecodeString(COLUMN_DAIRY_FOLDER_NAME)
                )

                diaryList.add(diary)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return diaryList
    }

    /**
     *
     */
    fun getAllDairyByFolderId(folderId: Int): ArrayList<Diary> {

        // array of columns to fetch
        val columns = arrayOf(
            COLUMN_DAIRY_ID,
            COLUMN_FOLDER_ID,
            COLUMN_DAIRY_INIT_TIME,
            COLUMN_DAIRY_UPDATE_TIME,
            COLUMN_DAIRY_TITLE,
            COLUMN_DAIRY_CONTENT,
            COLUMN_DAIRY_FOLDER_NAME
        )

        // sorting orders
        val sortOrder = "$COLUMN_DAIRY_ID DESC"
        val diaryList = ArrayList<Diary>()

        val db = this.readableDatabase

        // query the user table
        val cursor = db.query(
            TABLE_DAIRY, //Table to query
            columns,            //columns to return
            "$COLUMN_FOLDER_ID = ? ",     //columns for the WHERE clause
            arrayOf(folderId.toString()),  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            sortOrder //The sort order
        )

        if (cursor.moveToFirst()) {
            do {
                val diary = Diary(
                    id = cursor.getDecodeString(COLUMN_DAIRY_ID).toInt(),
                    folderId = cursor.getDecodeString(COLUMN_FOLDER_ID).toInt(),
                    initTime = cursor.getDecodeString(COLUMN_DAIRY_INIT_TIME),
                    updateTime = cursor.getDecodeString(COLUMN_DAIRY_UPDATE_TIME),
                    title = cursor.getDecodeString(COLUMN_DAIRY_TITLE),
                    content = cursor.getDecodeString(COLUMN_DAIRY_CONTENT),
                    folderName = cursor.getDecodeString(COLUMN_DAIRY_FOLDER_NAME)
                )

                diaryList.add(diary)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return diaryList
    }

    /**
     *
     */
    fun findDairyById(id: Int): Diary? {

        // array of columns to fetch
        val columns = arrayOf(
            COLUMN_DAIRY_ID,
            COLUMN_FOLDER_ID,
            COLUMN_DAIRY_INIT_TIME,
            COLUMN_DAIRY_UPDATE_TIME,
            COLUMN_DAIRY_TITLE,
            COLUMN_DAIRY_CONTENT,
            COLUMN_DAIRY_FOLDER_NAME
        )

        // sorting orders
        val sortOrder = "$COLUMN_DAIRY_ID ASC"
        var diary: Diary? = null
        val db = this.readableDatabase

        // query the user table
        val cursor = db.query(
            TABLE_DAIRY, //Table to query
            columns,            //columns to return
            "$COLUMN_DAIRY_ID = ? ",     //columns for the WHERE clause
            arrayOf(id.toString()),  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            sortOrder //The sort order
        )

        if (cursor.moveToFirst()) {
            do {
                diary = Diary(
                    id = cursor.getDecodeString(COLUMN_DAIRY_ID).toInt(),
                    folderId = cursor.getDecodeString(COLUMN_FOLDER_ID).toInt(),
                    initTime = cursor.getDecodeString(COLUMN_DAIRY_INIT_TIME),
                    updateTime = cursor.getDecodeString(COLUMN_DAIRY_UPDATE_TIME),
                    title = cursor.getDecodeString(COLUMN_DAIRY_TITLE),
                    content = cursor.getDecodeString(COLUMN_DAIRY_CONTENT),
                    folderName = cursor.getDecodeString(COLUMN_DAIRY_FOLDER_NAME)
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return diary
    }

    //endregion


    //endregion

    //region add record fun


    fun addFolder(folder: Folder) : Long {

        val db = this.writableDatabase

        val values = ContentValues()
        values.addEncodeString(COLUMN_FOLDER_NAME, folder.name)
        values.addEncodeString(COLUMN_FOLDER_NOTES, folder.notes)
        values.addEncodeString(COLUMN_FOLDER_PROFILE_IMAGE, folder.profileImagePath)
        values.addEncodeString(COLUMN_FOLDER_BACKGROUND_IMAGE, folder.backgroundImagePath)

        // Inserting Row
        val id = db.insert(TABLE_FOLDER, null, values)
        db.close()
        return id
    }

    fun addFolderVideo(video: Video) : Long {
        val db = this.writableDatabase

        val values = ContentValues()
        values.addEncodeString(COLUMN_FOLDER_ID, video.folderId)
        values.addEncodeString(COLUMN_FOLDER_VIDEO_INIT_TIME, video.initTime)
        values.addEncodeString(COLUMN_FOLDER_VIDEO_UPDATE_TIME, video.updateTime)
        values.addEncodeString(COLUMN_FOLDER_VIDEO_TITLE, video.title)
        values.addEncodeString(COLUMN_FOLDER_VIDEO_CLOUD_URL, video.cloudUrl)
        values.addEncodeString(COLUMN_FOLDER_VIDEO_LOCAL_URL, video.localUrl)
        values.addEncodeString(COLUMN_FOLDER_VIDEO_NOTES, video.notes)
        values.addEncodeString(COLUMN_FOLDER_VIDEO_COVER_URL, video.coverPath)
        values.addEncodeString(COLUMN_FOLDER_VIDEO_FOLDER_NAME, video.folderName)

        // Inserting Row
        val id = db.insert(TABLE_FOLDER_VIDEO, null, values)
        db.close()
        return id
    }

    fun addCalendarEvent(calendarEvent: CalendarEvent) : Long {
        val db = this.writableDatabase

        val values = ContentValues()
        values.addEncodeString(COLUMN_CALENDAR_EVENT_TITLE, calendarEvent.title)
        values.addEncodeString(COLUMN_CALENDAR_EVENT_ATTENDANTS, calendarEvent.attendants)
        values.addEncodeString(COLUMN_CALENDAR_EVENT_LOCATION, calendarEvent.location)
        values.addEncodeString(COLUMN_CALENDAR_EVENT_NOTES, calendarEvent.notes)
        values.addEncodeString(COLUMN_CALENDAR_EVENT_START_TIME, calendarEvent.startTime)
        values.addEncodeString(COLUMN_CALENDAR_EVENT_END_TIME, calendarEvent.endTime)
        values.addEncodeString(COLUMN_CALENDAR_EVENT_DAY_OF_WEEK, calendarEvent.dayOfWeekString())
        values.addEncodeString(COLUMN_CALENDAR_EVENT_FREQUENCY, calendarEvent.frequency)
        values.addEncodeString(COLUMN_CALENDAR_EVENT_FREQUENCY_START, calendarEvent.eventFreqStartDate)
        values.addEncodeString(COLUMN_CALENDAR_EVENT_FREQUENCY_END, calendarEvent.eventFreqEndDate)

        // Inserting Row
        val id = db.insert(TABLE_CALENDAR_EVENT, null, values)
        db.close()
        return id
    }

    fun addHomeEvent(homeEvent: HomeEvent) : Long {
        val db = this.writableDatabase

        val values = ContentValues()
        values.addEncodeString(COLUMN_HOME_EVENT_TITLE, homeEvent.title)
        values.addEncodeString(COLUMN_HOME_EVENT_NOTES, homeEvent.notes)
        values.addEncodeString(COLUMN_HOME_EVENT_IMAGE_URL, homeEvent.imagePath)
        values.addEncodeString(COLUMN_HOME_EVENT_TYPE, homeEvent.type)
        values.addEncodeString(COLUMN_HOME_EVENT_REF_ID, homeEvent.refId)
        values.addEncodeString(COLUMN_HOME_EVENT_TIME, homeEvent.time)
        values.addEncodeString(COLUMN_FOLDER_ID, homeEvent.folderId)

        // Inserting Row
        val id = db.insert(TABLE_HOME_EVENT, null, values)
        db.close()
        return id
    }

    fun addDairy(diary: Diary) : Long {
        val db = this.writableDatabase

        val values = ContentValues()
        values.addEncodeString(COLUMN_FOLDER_ID, diary.folderId)
        values.addEncodeString(COLUMN_DAIRY_INIT_TIME, diary.initTime)
        values.addEncodeString(COLUMN_DAIRY_UPDATE_TIME, diary.updateTime)
        values.addEncodeString(COLUMN_DAIRY_TITLE, diary.title)
        values.addEncodeString(COLUMN_DAIRY_CONTENT, diary.content)
        values.addEncodeString(COLUMN_DAIRY_FOLDER_NAME, diary.folderName)

        // Inserting Row
        val id = db.insert(TABLE_DAIRY, null, values)
        db.close()
        return id
    }

    //endregion

    //region update func

    /**
     * This method to update folder record
     *
     * @param folder
     */
    fun updateFolder(folder: Folder) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.addEncodeString(COLUMN_FOLDER_NAME, folder.name)
        values.addEncodeString(COLUMN_FOLDER_NOTES, folder.notes)
        values.addEncodeString(COLUMN_FOLDER_PROFILE_IMAGE, folder.profileImagePath)
        values.addEncodeString(COLUMN_FOLDER_BACKGROUND_IMAGE, folder.backgroundImagePath)

        // updating row
        db.update(
            TABLE_FOLDER, values, "$COLUMN_FOLDER_ID = ?",
            arrayOf(folder.id.toString())
        )
        db.close()
    }

    fun updateVideo(video: Video) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.addEncodeString(COLUMN_FOLDER_ID, video.folderId)
        values.addEncodeString(COLUMN_FOLDER_VIDEO_TITLE, video.title)
        values.addEncodeString(COLUMN_FOLDER_VIDEO_INIT_TIME, video.initTime)
        values.addEncodeString(COLUMN_FOLDER_VIDEO_UPDATE_TIME, video.updateTime)
        values.addEncodeString(COLUMN_FOLDER_VIDEO_CLOUD_URL, video.cloudUrl)
        values.addEncodeString(COLUMN_FOLDER_VIDEO_LOCAL_URL, video.localUrl)
        values.addEncodeString(COLUMN_FOLDER_VIDEO_NOTES, video.notes)
        values.addEncodeString(COLUMN_FOLDER_VIDEO_COVER_URL, video.coverPath)
        values.addEncodeString(COLUMN_FOLDER_VIDEO_FOLDER_NAME, video.folderName)

        // updating row
        db.update(
            TABLE_FOLDER_VIDEO, values, "$COLUMN_FOLDER_VIDEO_ID = ?",
            arrayOf(video.id.toString())
        )
        db.close()
    }

    fun updateCalendarEvent(calendarEvent: CalendarEvent) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.addEncodeString(COLUMN_CALENDAR_EVENT_TITLE, calendarEvent.title)
        values.addEncodeString(COLUMN_CALENDAR_EVENT_ATTENDANTS, calendarEvent.attendants)
        values.addEncodeString(COLUMN_CALENDAR_EVENT_LOCATION, calendarEvent.location)
        values.addEncodeString(COLUMN_CALENDAR_EVENT_NOTES, calendarEvent.notes)
        values.addEncodeString(COLUMN_CALENDAR_EVENT_START_TIME, calendarEvent.startTime)
        values.addEncodeString(COLUMN_CALENDAR_EVENT_END_TIME, calendarEvent.endTime)
        values.addEncodeString(COLUMN_CALENDAR_EVENT_DAY_OF_WEEK, calendarEvent.dayOfWeekString())
        values.addEncodeString(COLUMN_CALENDAR_EVENT_FREQUENCY, calendarEvent.frequency)
        values.addEncodeString(COLUMN_CALENDAR_EVENT_FREQUENCY_START, calendarEvent.eventFreqStartDate)
        values.addEncodeString(COLUMN_CALENDAR_EVENT_FREQUENCY_END, calendarEvent.eventFreqEndDate)

        db.update(
            TABLE_CALENDAR_EVENT, values, "$COLUMN_CALENDAR_EVENT_ID = ?",
            arrayOf(calendarEvent.id.toString())
        )
        db.close()
    }

    fun updateHomeEvent(homeEvent: HomeEvent) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.addEncodeString(COLUMN_HOME_EVENT_TITLE, homeEvent.title)
        values.addEncodeString(COLUMN_HOME_EVENT_NOTES, homeEvent.notes)
        values.addEncodeString(COLUMN_HOME_EVENT_IMAGE_URL, homeEvent.imagePath)
        values.addEncodeString(COLUMN_HOME_EVENT_TYPE, homeEvent.type)
        values.addEncodeString(COLUMN_HOME_EVENT_REF_ID, homeEvent.refId)
        values.addEncodeString(COLUMN_HOME_EVENT_TIME, homeEvent.time)
        values.addEncodeString(COLUMN_FOLDER_ID, homeEvent.folderId)

        db.update(
            TABLE_HOME_EVENT, values, "$COLUMN_HOME_EVENT_ID = ?",
            arrayOf(homeEvent.id.toString())
        )
        db.close()
    }

    fun updateDairy(diary: Diary) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.addEncodeString(COLUMN_FOLDER_ID, diary.folderId)
        values.addEncodeString(COLUMN_DAIRY_TITLE, diary.title)
        values.addEncodeString(COLUMN_DAIRY_INIT_TIME, diary.initTime)
        values.addEncodeString(COLUMN_DAIRY_UPDATE_TIME, diary.updateTime)
        values.addEncodeString(COLUMN_DAIRY_CONTENT, diary.content)
        values.addEncodeString(COLUMN_DAIRY_FOLDER_NAME, diary.folderName)

        // updating row
        db.update(
            TABLE_DAIRY, values, "$COLUMN_DAIRY_ID = ?",
            arrayOf(diary.id.toString())
        )
        db.close()
    }

    //endregion

    //region get last id fun for id generation

    /**
     *
     */
    @SuppressLint("Recycle")
    fun getLastFolderIdAuto(): Int {
        val db = this.writableDatabase
        val query = "SELECT * FROM SQLITE_SEQUENCE"
        val cursor: Cursor = db.rawQuery(query, null)
        var num = -1
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString((cursor.getColumnIndex("name"))) == TABLE_FOLDER) {
                    num = cursor.getString(cursor.getColumnIndex("seq")).toInt() + 1
                    break
                }
                else {
                    num = 1
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        return num
    }

    /**
     * This method to return the last folder id
     *
     */
    @SuppressLint("Recycle")
    fun getLastDairyIdAuto(): Int {
        val db = this.writableDatabase
        val query = "SELECT * FROM SQLITE_SEQUENCE"
        val cursor: Cursor = db.rawQuery(query, null)
        var num = -1
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString((cursor.getColumnIndex("name"))) == TABLE_DAIRY) {
                    num = cursor.getString(cursor.getColumnIndex("seq")).toInt() + 1
                    break
                }
                //no table folder created the first one start with one
                else {
                    num = 1
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        return num
    }

    //endregion

    //region delete fun

    /**
     * This method is to delete user record
     *
     * @param folder
     */
    fun deleteFolder(folder: Folder): Boolean {

        val db = this.writableDatabase
        // delete user record by id
        db.delete(
            TABLE_FOLDER, "$COLUMN_FOLDER_ID = ?",
            arrayOf(folder.id.toString())
        )
        db.close()

        return true
    }

    fun deleteFolderVideo(video: Video) {

        val db = this.writableDatabase
        db.delete(
            TABLE_FOLDER_VIDEO, "$COLUMN_FOLDER_VIDEO_ID = ?",
            arrayOf(video.id.toString())
        )
        db.close()
    }

    fun deleteCalendarEvent(calendarEvent: CalendarEvent) {
        val db = this.writableDatabase
        db.delete(
            TABLE_CALENDAR_EVENT, "$COLUMN_CALENDAR_EVENT_ID = ?",
            arrayOf(calendarEvent.id.toString())
        )
        db.close()
    }

    fun deleteHomeEvent(homeEvent: HomeEvent){
        val db = this.writableDatabase
        db.delete(
            TABLE_HOME_EVENT, "$COLUMN_HOME_EVENT_ID = ?",
            arrayOf(homeEvent.id.toString())
        )
        db.close()
    }

    fun deleteDairy(diary: Diary){
        val db = this.writableDatabase
        db.delete(
            TABLE_DAIRY, "$COLUMN_DAIRY_ID = ?",
            arrayOf(diary.id.toString())
        )
        db.close()
    }

    //endregion

    //region sql language converting fun

    private fun encodeToSqlStr(input: String): String {
        return input.replace(" ", "%20")
            .replace("/", "%2F")
            .replace("(", "%28")
            .replace(")", "%29")
    }

    private fun decodeToStdStr(sqlInput: String): String {
        return sqlInput.replace("%20", " ")
            .replace("%2F", "/")
            .replace("%28", "(")
            .replace("%29", ")")
    }

    private fun ContentValues.addEncodeString(columnName: String, columnValue: Int) =
        this.put(columnName, columnValue)

    private fun ContentValues.addEncodeString(columnName: String, columnValue: String) =
        this.put(columnName, encodeToSqlStr(columnValue))

    private fun Cursor.getDecodeString(columnName: String) =
        decodeToStdStr(this.getString(this.getColumnIndex(columnName)))

    private fun String.toIntPKAutoInc(isAutoInc: Boolean): String {
        return if (isAutoInc) "$this INTEGER PRIMARY KEY AUTOINCREMENT"
        else "$this INTEGER PRIMARY"
    }

    private fun String.toTextNotNull(isNotNull: Boolean): String {
        return if (isNotNull) "$this TEXT NOT NULL"
        else "$this TEXT"
    }

    private fun String.toIntNotNull(isNotNull: Boolean): String {
        return if (isNotNull) "$this INTEGER NOT NULL"
        else "$this INTEGER"
    }

    //endregion
}
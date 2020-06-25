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
        private const val DATABASE_NAME = "studentManager.db"

        // User table name
        private const val TABLE_STUDENT = "student"
        private const val TABLE_STUDENT_VIDEO = "student_video"
        private const val TABLE_CALENDAR_EVENT = "calendar_event"
        private const val TABLE_HOME_EVENT = "home_event"
        private const val TABLE_DAIRY = "diary"

        // User Table Columns names for student
        private const val COLUMN_STUDENT_ID = "student_id"
        private const val COLUMN_STUDENT_NAME = "student_name"
        private const val COLUMN_STUDENT_NOTES = "student_notes"
        private const val COLUMN_STUDENT_PROFILE_IMAGE = "student_profile_image"
        private const val COLUMN_STUDENT_BACKGROUND_IMAGE = "student_background_image"

        //Columns for student video
        private const val COLUMN_STUDENT_VIDEO_ID = "student_video_id"
        private const val COLUMN_STUDENT_VIDEO_INIT_TIME = "student_video_init_time"
        private const val COLUMN_STUDENT_VIDEO_UPDATE_TIME = "student_video_update_time"
        private const val COLUMN_STUDENT_VIDEO_CLOUD_URL = "student_video_cloud_url"
        private const val COLUMN_STUDENT_VIDEO_LOCAL_URL = "student_video_local_url"
        private const val COLUMN_STUDENT_VIDEO_TITLE = "student_video_title"
        private const val COLUMN_STUDENT_VIDEO_NOTES = "student_video_notes"
        private const val COLUMN_STUDENT_VIDEO_COVER_URL = "student_video_cover_url"
        private const val COLUMN_STUDENT_VIDEO_STUDENT_NAME = "student_video_student_name"

        //Columns for CalendarEvent
        private const val COLUMN_CALENDAR_EVENT_ID = "calender_event_id"
        private const val COLUMN_CALENDAR_EVENT_TITLE = "calendar_event_title"
        private const val COLUMN_CALENDAR_EVENT_STUDENTS = "calendar_event_students"
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
        private const val COLUMN_DAIRY_STUDENT_NAME = "student_name"
        private const val COLUMN_DAIRY_UPDATE_TIME = "diary_update_time"

    }

    //endregion

    //region create table sql query

    private val createStudentTable = (
            "CREATE TABLE " + TABLE_STUDENT + "("
                    + COLUMN_STUDENT_ID.toIntPKAutoInc(true) + ","
                    + COLUMN_STUDENT_NAME.toTextNotNull(true) + ","
                    + COLUMN_STUDENT_PROFILE_IMAGE.toTextNotNull(false) + ","
                    + COLUMN_STUDENT_NOTES.toTextNotNull(false) + ","
                    + COLUMN_STUDENT_BACKGROUND_IMAGE.toTextNotNull(false) +  ")")

    private val createStudentVideoTable = (
            "CREATE TABLE " + TABLE_STUDENT_VIDEO + "("
                    + COLUMN_STUDENT_VIDEO_ID.toIntPKAutoInc(true) + ","
                    + COLUMN_STUDENT_VIDEO_INIT_TIME.toTextNotNull(true) + ","
                    + COLUMN_STUDENT_VIDEO_UPDATE_TIME.toTextNotNull(true) + ","
                    + COLUMN_STUDENT_VIDEO_TITLE.toTextNotNull(false) + ","
                    + COLUMN_STUDENT_ID + " INTEGER REFERENCES " + TABLE_STUDENT + "(" + COLUMN_STUDENT_ID + ") ON DELETE CASCADE,"
                    + COLUMN_STUDENT_VIDEO_CLOUD_URL.toTextNotNull(false) + ","
                    + COLUMN_STUDENT_VIDEO_LOCAL_URL.toTextNotNull(false) + ","
                    + COLUMN_STUDENT_VIDEO_NOTES.toTextNotNull(false) + ","
                    + COLUMN_STUDENT_VIDEO_COVER_URL.toTextNotNull(false) + ","
                    + COLUMN_STUDENT_VIDEO_STUDENT_NAME.toTextNotNull(false) +")")

    private val createCalendarEventTable = (
            "CREATE TABLE " + TABLE_CALENDAR_EVENT + "("
                    + COLUMN_CALENDAR_EVENT_ID.toIntPKAutoInc(true) + ","
                    + COLUMN_CALENDAR_EVENT_TITLE.toTextNotNull(true) + ","
                    + COLUMN_CALENDAR_EVENT_STUDENTS.toTextNotNull(false) + ","
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
                    + COLUMN_STUDENT_ID + " INTEGER REFERENCES " + TABLE_STUDENT + "(" + COLUMN_STUDENT_ID + ") ON DELETE CASCADE,"
                    + COLUMN_HOME_EVENT_TIME.toTextNotNull(true) + ")")

    private val createDairyTable = (
            "CREATE TABLE " + TABLE_DAIRY + "("
                    + COLUMN_DAIRY_ID.toIntPKAutoInc(true) + ","
                    + COLUMN_STUDENT_ID + " INTEGER REFERENCES " + TABLE_STUDENT + "(" + COLUMN_STUDENT_ID + ") ON DELETE CASCADE,"
                    + COLUMN_DAIRY_TITLE.toTextNotNull(false) + ","
                    + COLUMN_DAIRY_INIT_TIME.toTextNotNull(true) + ","
                    + COLUMN_DAIRY_UPDATE_TIME.toTextNotNull(true) + ","
                    + COLUMN_DAIRY_CONTENT.toTextNotNull(false) + ","
                    + COLUMN_DAIRY_STUDENT_NAME.toTextNotNull(true) + ")")

    //endregion

    //region drop table sql query
    private val dropStudentTable: String
        get() = "DROP TABLE IF EXISTS $TABLE_STUDENT"
    private val dropStudentVideoTable: String
        get() = "DROP TABLE IF EXISTS $TABLE_STUDENT_VIDEO"
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
        db.execSQL(createStudentTable)
        db.execSQL(createStudentVideoTable)
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

    //region student table

    /**
     *
     */
    fun getAllStudent(): ArrayList<Student> {

        // array of columns to fetch
        val columns = arrayOf(
            COLUMN_STUDENT_ID,
            COLUMN_STUDENT_NAME,
            COLUMN_STUDENT_NOTES,
            COLUMN_STUDENT_PROFILE_IMAGE,
            COLUMN_STUDENT_BACKGROUND_IMAGE
        )

        // sorting orders
        val sortOrder = "$COLUMN_STUDENT_ID ASC"
        val studentList = ArrayList<Student>()

        val db = this.readableDatabase

        // query the user table
        val cursor = db.query(
            TABLE_STUDENT, //Table to query
            columns,            //columns to return
            null,     //columns for the WHERE clause
            null,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            sortOrder //The sort order
        )

        if (cursor.moveToFirst()) {
            do {
                val foundStudent = Student(
                    id = cursor.getDecodeString(COLUMN_STUDENT_ID).toInt(),
                    name = cursor.getDecodeString(COLUMN_STUDENT_NAME),
                    notes = cursor.getDecodeString(COLUMN_STUDENT_NOTES),
                    profileImagePath = cursor.getDecodeString(COLUMN_STUDENT_PROFILE_IMAGE),
                    backgroundImagePath = cursor.getDecodeString(COLUMN_STUDENT_BACKGROUND_IMAGE)
                )

                studentList.add(foundStudent)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return studentList
    }

    /**
     *
     */
    fun findStudentById(id: Int): Student? {

        // array of columns to fetch
        val columns = arrayOf(
            COLUMN_STUDENT_ID,
            COLUMN_STUDENT_NAME,
            COLUMN_STUDENT_NOTES,
            COLUMN_STUDENT_PROFILE_IMAGE,
            COLUMN_STUDENT_BACKGROUND_IMAGE
        )

        var student : Student? = null

        val db = this.readableDatabase

        // query the user table
        val cursor = db.query(
            TABLE_STUDENT, //Table to query
            columns,            //columns to return
            "$COLUMN_STUDENT_ID = ? ",     //columns for the WHERE clause
            arrayOf(id.toString()),  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            null //The sort order
        )

        if (cursor.moveToFirst()) {
            do {
                val findStudent = Student(
                    id = cursor.getDecodeString(COLUMN_STUDENT_ID).toInt(),
                    name = cursor.getDecodeString(COLUMN_STUDENT_NAME),
                    notes = cursor.getDecodeString(COLUMN_STUDENT_NOTES),
                    profileImagePath = cursor.getDecodeString(COLUMN_STUDENT_PROFILE_IMAGE),
                    backgroundImagePath = cursor.getDecodeString(COLUMN_STUDENT_BACKGROUND_IMAGE)
                )

                student = findStudent

            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return student
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
    fun getAllStudentVideo(): ArrayList<Video> {

        // array of columns to fetch
        val columns = arrayOf(
            COLUMN_STUDENT_VIDEO_ID,
            COLUMN_STUDENT_ID,
            COLUMN_STUDENT_VIDEO_INIT_TIME,
            COLUMN_STUDENT_VIDEO_UPDATE_TIME,
            COLUMN_STUDENT_VIDEO_TITLE,
            COLUMN_STUDENT_VIDEO_CLOUD_URL,
            COLUMN_STUDENT_VIDEO_LOCAL_URL,
            COLUMN_STUDENT_VIDEO_NOTES,
            COLUMN_STUDENT_VIDEO_COVER_URL,
            COLUMN_STUDENT_VIDEO_STUDENT_NAME
        )

        // sorting orders
        val sortOrder = "$COLUMN_STUDENT_VIDEO_ID DESC"
        val studentVideoList = ArrayList<Video>()

        val db = this.readableDatabase

        // query the user table
        val cursor = db.query(
            TABLE_STUDENT_VIDEO, //Table to query
            columns,            //columns to return
            null,
            null,
            null,      //group the rows
            null,       //filter by row groups
            sortOrder //The sort order
        )

        if (cursor.moveToFirst()) {
            do {
                val studentV = Video(
                    id = cursor.getDecodeString(COLUMN_STUDENT_VIDEO_ID).toInt(),
                    studentId = cursor.getDecodeString(COLUMN_STUDENT_ID).toInt(),
                    initTime = cursor.getDecodeString(COLUMN_STUDENT_VIDEO_INIT_TIME),
                    updateTime = cursor.getDecodeString(COLUMN_STUDENT_VIDEO_UPDATE_TIME),
                    title = cursor.getDecodeString(COLUMN_STUDENT_VIDEO_TITLE),
                    cloudUrl = cursor.getDecodeString(COLUMN_STUDENT_VIDEO_CLOUD_URL),
                    localUrl = cursor.getDecodeString(COLUMN_STUDENT_VIDEO_LOCAL_URL),
                    notes = cursor.getDecodeString(COLUMN_STUDENT_VIDEO_NOTES),
                    coverUrl = cursor.getDecodeString(COLUMN_STUDENT_VIDEO_COVER_URL),
                    studentName = cursor.getDecodeString(COLUMN_STUDENT_VIDEO_STUDENT_NAME)
                )

                studentVideoList.add(studentV)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return studentVideoList
    }

    /**
     *
     */
    fun getAllStudentVideoByStudentId(studentId: Int): ArrayList<Video> {

        // array of columns to fetch
        val columns = arrayOf(
            COLUMN_STUDENT_VIDEO_ID,
            COLUMN_STUDENT_ID,
            COLUMN_STUDENT_VIDEO_INIT_TIME,
            COLUMN_STUDENT_VIDEO_UPDATE_TIME,
            COLUMN_STUDENT_VIDEO_TITLE,
            COLUMN_STUDENT_VIDEO_CLOUD_URL,
            COLUMN_STUDENT_VIDEO_LOCAL_URL,
            COLUMN_STUDENT_VIDEO_NOTES,
            COLUMN_STUDENT_VIDEO_COVER_URL,
            COLUMN_STUDENT_VIDEO_STUDENT_NAME
        )

        // sorting orders
        val sortOrder = "$COLUMN_STUDENT_VIDEO_ID DESC"
        val studentVideoList = ArrayList<Video>()

        val db = this.readableDatabase

        // query the user table
        val cursor = db.query(
            TABLE_STUDENT_VIDEO, //Table to query
            columns,            //columns to return
            "$COLUMN_STUDENT_ID = ? ",     //columns for the WHERE clause
            arrayOf(studentId.toString()),  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            sortOrder //The sort order
        )

        if (cursor.moveToFirst()) {
            do {
                val studentV = Video(
                    id = cursor.getDecodeString(COLUMN_STUDENT_VIDEO_ID).toInt(),
                    studentId = cursor.getDecodeString(COLUMN_STUDENT_ID).toInt(),
                    initTime = cursor.getDecodeString(COLUMN_STUDENT_VIDEO_INIT_TIME),
                    updateTime = cursor.getDecodeString(COLUMN_STUDENT_VIDEO_UPDATE_TIME),
                    title = cursor.getDecodeString(COLUMN_STUDENT_VIDEO_TITLE),
                    cloudUrl = cursor.getDecodeString(COLUMN_STUDENT_VIDEO_CLOUD_URL),
                    localUrl = cursor.getDecodeString(COLUMN_STUDENT_VIDEO_LOCAL_URL),
                    notes = cursor.getDecodeString(COLUMN_STUDENT_VIDEO_NOTES),
                    coverUrl = cursor.getDecodeString(COLUMN_STUDENT_VIDEO_COVER_URL),
                    studentName = cursor.getDecodeString(COLUMN_STUDENT_VIDEO_STUDENT_NAME)

                )

                studentVideoList.add(studentV)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return studentVideoList
    }

    /**
     *
     */
    fun findVideoByIdFromID(id: Int): Video? {

        // array of columns to fetch
        val columns = arrayOf(
            COLUMN_STUDENT_VIDEO_ID,
            COLUMN_STUDENT_ID,
            COLUMN_STUDENT_VIDEO_INIT_TIME,
            COLUMN_STUDENT_VIDEO_UPDATE_TIME,
            COLUMN_STUDENT_VIDEO_TITLE,
            COLUMN_STUDENT_VIDEO_CLOUD_URL,
            COLUMN_STUDENT_VIDEO_LOCAL_URL,
            COLUMN_STUDENT_VIDEO_NOTES,
            COLUMN_STUDENT_VIDEO_COVER_URL,
            COLUMN_STUDENT_VIDEO_STUDENT_NAME
        )

        // sorting orders
        var video: Video? = null
        val db = this.readableDatabase

        // query the user table
        val cursor = db.query(
            TABLE_STUDENT_VIDEO, //Table to query
            columns,            //columns to return
            "$COLUMN_STUDENT_VIDEO_ID = ? ",     //columns for the WHERE clause
            arrayOf(id.toString()),  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            null //The sort order
        )

        if (cursor.moveToFirst()) {
            do {
                video = Video(
                    id = cursor.getDecodeString(COLUMN_STUDENT_VIDEO_ID).toInt(),
                    studentId = cursor.getDecodeString(COLUMN_STUDENT_ID).toInt(),
                    initTime = cursor.getDecodeString(COLUMN_STUDENT_VIDEO_INIT_TIME),
                    updateTime = cursor.getDecodeString(COLUMN_STUDENT_VIDEO_UPDATE_TIME),
                    title = cursor.getDecodeString(COLUMN_STUDENT_VIDEO_TITLE),
                    cloudUrl = cursor.getDecodeString(COLUMN_STUDENT_VIDEO_CLOUD_URL),
                    localUrl = cursor.getDecodeString(COLUMN_STUDENT_VIDEO_LOCAL_URL),
                    notes = cursor.getDecodeString(COLUMN_STUDENT_VIDEO_NOTES),
                    coverUrl = cursor.getDecodeString(COLUMN_STUDENT_VIDEO_COVER_URL),
                    studentName = cursor.getDecodeString(COLUMN_STUDENT_VIDEO_STUDENT_NAME)
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
            COLUMN_CALENDAR_EVENT_STUDENTS,
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
                    students = cursor.getDecodeString(COLUMN_CALENDAR_EVENT_STUDENTS),
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
            COLUMN_STUDENT_ID,
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
                    studentId = cursor.getDecodeString(COLUMN_STUDENT_ID),
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
            COLUMN_STUDENT_ID,
            COLUMN_DAIRY_INIT_TIME,
            COLUMN_DAIRY_UPDATE_TIME,
            COLUMN_DAIRY_TITLE,
            COLUMN_DAIRY_CONTENT,
            COLUMN_DAIRY_STUDENT_NAME
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
                    studentId = cursor.getDecodeString(COLUMN_STUDENT_ID).toInt(),
                    initTime = cursor.getDecodeString(COLUMN_DAIRY_INIT_TIME),
                    updateTime = cursor.getDecodeString(COLUMN_DAIRY_UPDATE_TIME),
                    title = cursor.getDecodeString(COLUMN_DAIRY_TITLE),
                    content = cursor.getDecodeString(COLUMN_DAIRY_CONTENT),
                    studentName = cursor.getDecodeString(COLUMN_DAIRY_STUDENT_NAME)
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
    fun getAllDairyByStudentId(studentId: Int): ArrayList<Diary> {

        // array of columns to fetch
        val columns = arrayOf(
            COLUMN_DAIRY_ID,
            COLUMN_STUDENT_ID,
            COLUMN_DAIRY_INIT_TIME,
            COLUMN_DAIRY_UPDATE_TIME,
            COLUMN_DAIRY_TITLE,
            COLUMN_DAIRY_CONTENT,
            COLUMN_DAIRY_STUDENT_NAME
        )

        // sorting orders
        val sortOrder = "$COLUMN_DAIRY_ID DESC"
        val diaryList = ArrayList<Diary>()

        val db = this.readableDatabase

        // query the user table
        val cursor = db.query(
            TABLE_DAIRY, //Table to query
            columns,            //columns to return
            "$COLUMN_STUDENT_ID = ? ",     //columns for the WHERE clause
            arrayOf(studentId.toString()),  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            sortOrder //The sort order
        )

        if (cursor.moveToFirst()) {
            do {
                val diary = Diary(
                    id = cursor.getDecodeString(COLUMN_DAIRY_ID).toInt(),
                    studentId = cursor.getDecodeString(COLUMN_STUDENT_ID).toInt(),
                    initTime = cursor.getDecodeString(COLUMN_DAIRY_INIT_TIME),
                    updateTime = cursor.getDecodeString(COLUMN_DAIRY_UPDATE_TIME),
                    title = cursor.getDecodeString(COLUMN_DAIRY_TITLE),
                    content = cursor.getDecodeString(COLUMN_DAIRY_CONTENT),
                    studentName = cursor.getDecodeString(COLUMN_DAIRY_STUDENT_NAME)
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
            COLUMN_STUDENT_ID,
            COLUMN_DAIRY_INIT_TIME,
            COLUMN_DAIRY_UPDATE_TIME,
            COLUMN_DAIRY_TITLE,
            COLUMN_DAIRY_CONTENT,
            COLUMN_DAIRY_STUDENT_NAME
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
                    studentId = cursor.getDecodeString(COLUMN_STUDENT_ID).toInt(),
                    initTime = cursor.getDecodeString(COLUMN_DAIRY_INIT_TIME),
                    updateTime = cursor.getDecodeString(COLUMN_DAIRY_UPDATE_TIME),
                    title = cursor.getDecodeString(COLUMN_DAIRY_TITLE),
                    content = cursor.getDecodeString(COLUMN_DAIRY_CONTENT),
                    studentName = cursor.getDecodeString(COLUMN_DAIRY_STUDENT_NAME)
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


    fun addStudent(student: Student) : Long {

        val db = this.writableDatabase

        val values = ContentValues()
        values.addEncodeString(COLUMN_STUDENT_NAME, student.name)
        values.addEncodeString(COLUMN_STUDENT_NOTES, student.notes)
        values.addEncodeString(COLUMN_STUDENT_PROFILE_IMAGE, student.profileImagePath)
        values.addEncodeString(COLUMN_STUDENT_BACKGROUND_IMAGE, student.backgroundImagePath)

        // Inserting Row
        val id = db.insert(TABLE_STUDENT, null, values)
        db.close()
        return id
    }

    fun addStudentVideo(video: Video) : Long {
        val db = this.writableDatabase

        val values = ContentValues()
        values.addEncodeString(COLUMN_STUDENT_ID, video.studentId)
        values.addEncodeString(COLUMN_STUDENT_VIDEO_INIT_TIME, video.initTime)
        values.addEncodeString(COLUMN_STUDENT_VIDEO_UPDATE_TIME, video.updateTime)
        values.addEncodeString(COLUMN_STUDENT_VIDEO_TITLE, video.title)
        values.addEncodeString(COLUMN_STUDENT_VIDEO_CLOUD_URL, video.cloudUrl)
        values.addEncodeString(COLUMN_STUDENT_VIDEO_LOCAL_URL, video.localUrl)
        values.addEncodeString(COLUMN_STUDENT_VIDEO_NOTES, video.notes)
        values.addEncodeString(COLUMN_STUDENT_VIDEO_COVER_URL, video.coverPath)
        values.addEncodeString(COLUMN_STUDENT_VIDEO_STUDENT_NAME, video.studentName)

        // Inserting Row
        val id = db.insert(TABLE_STUDENT_VIDEO, null, values)
        db.close()
        return id
    }

    fun addCalendarEvent(calendarEvent: CalendarEvent) : Long {
        val db = this.writableDatabase

        val values = ContentValues()
        values.addEncodeString(COLUMN_CALENDAR_EVENT_TITLE, calendarEvent.title)
        values.addEncodeString(COLUMN_CALENDAR_EVENT_STUDENTS, calendarEvent.students)
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
        values.addEncodeString(COLUMN_STUDENT_ID, homeEvent.studentId)

        // Inserting Row
        val id = db.insert(TABLE_HOME_EVENT, null, values)
        db.close()
        return id
    }

    fun addDairy(diary: Diary) : Long {
        val db = this.writableDatabase

        val values = ContentValues()
        values.addEncodeString(COLUMN_STUDENT_ID, diary.studentId)
        values.addEncodeString(COLUMN_DAIRY_INIT_TIME, diary.initTime)
        values.addEncodeString(COLUMN_DAIRY_UPDATE_TIME, diary.updateTime)
        values.addEncodeString(COLUMN_DAIRY_TITLE, diary.title)
        values.addEncodeString(COLUMN_DAIRY_CONTENT, diary.content)
        values.addEncodeString(COLUMN_DAIRY_STUDENT_NAME, diary.studentName)

        // Inserting Row
        val id = db.insert(TABLE_DAIRY, null, values)
        db.close()
        return id
    }

    //endregion

    //region update func

    /**
     * This method to update student record
     *
     * @param student
     */
    fun updateStudent(student: Student) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.addEncodeString(COLUMN_STUDENT_NAME, student.name)
        values.addEncodeString(COLUMN_STUDENT_NOTES, student.notes)
        values.addEncodeString(COLUMN_STUDENT_PROFILE_IMAGE, student.profileImagePath)
        values.addEncodeString(COLUMN_STUDENT_BACKGROUND_IMAGE, student.backgroundImagePath)

        // updating row
        db.update(
            TABLE_STUDENT, values, "$COLUMN_STUDENT_ID = ?",
            arrayOf(student.id.toString())
        )
        db.close()
    }

    fun updateVideo(video: Video) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.addEncodeString(COLUMN_STUDENT_ID, video.studentId)
        values.addEncodeString(COLUMN_STUDENT_VIDEO_TITLE, video.title)
        values.addEncodeString(COLUMN_STUDENT_VIDEO_INIT_TIME, video.initTime)
        values.addEncodeString(COLUMN_STUDENT_VIDEO_UPDATE_TIME, video.updateTime)
        values.addEncodeString(COLUMN_STUDENT_VIDEO_CLOUD_URL, video.cloudUrl)
        values.addEncodeString(COLUMN_STUDENT_VIDEO_LOCAL_URL, video.localUrl)
        values.addEncodeString(COLUMN_STUDENT_VIDEO_NOTES, video.notes)
        values.addEncodeString(COLUMN_STUDENT_VIDEO_COVER_URL, video.coverPath)
        values.addEncodeString(COLUMN_STUDENT_VIDEO_STUDENT_NAME, video.studentName)

        // updating row
        db.update(
            TABLE_STUDENT_VIDEO, values, "$COLUMN_STUDENT_VIDEO_ID = ?",
            arrayOf(video.id.toString())
        )
        db.close()
    }

    fun updateCalendarEvent(calendarEvent: CalendarEvent) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.addEncodeString(COLUMN_CALENDAR_EVENT_TITLE, calendarEvent.title)
        values.addEncodeString(COLUMN_CALENDAR_EVENT_STUDENTS, calendarEvent.students)
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
        values.addEncodeString(COLUMN_STUDENT_ID, homeEvent.studentId)

        db.update(
            TABLE_HOME_EVENT, values, "$COLUMN_HOME_EVENT_ID = ?",
            arrayOf(homeEvent.id.toString())
        )
        db.close()
    }

    fun updateDairy(diary: Diary) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.addEncodeString(COLUMN_STUDENT_ID, diary.studentId)
        values.addEncodeString(COLUMN_DAIRY_TITLE, diary.title)
        values.addEncodeString(COLUMN_DAIRY_INIT_TIME, diary.initTime)
        values.addEncodeString(COLUMN_DAIRY_UPDATE_TIME, diary.updateTime)
        values.addEncodeString(COLUMN_DAIRY_CONTENT, diary.content)
        values.addEncodeString(COLUMN_DAIRY_STUDENT_NAME, diary.studentName)

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
    fun getLastStudentIdAuto(): Int {
        val db = this.writableDatabase
        val query = "SELECT * FROM SQLITE_SEQUENCE"
        val cursor: Cursor = db.rawQuery(query, null)
        var num = -1
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString((cursor.getColumnIndex("name"))) == TABLE_STUDENT) {
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
     * This method to return the last student id
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
                //no table student created the first one start with one
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
     * @param student
     */
    fun deleteStudent(student: Student): Boolean {

        val db = this.writableDatabase
        // delete user record by id
        db.delete(
            TABLE_STUDENT, "$COLUMN_STUDENT_ID = ?",
            arrayOf(student.id.toString())
        )
        db.close()

        return true
    }

    fun deleteStudentVideo(video: Video) {

        val db = this.writableDatabase
        db.delete(
            TABLE_STUDENT_VIDEO, "$COLUMN_STUDENT_VIDEO_ID = ?",
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
package com.xuanyuetech.tocoach.data

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

/**
 * calendar event
 */
class CalendarEvent() {

    //region properties

    var id = -1

    var title = ""
    var students = ""
    var location = ""
    var notes = ""
    var startTime = ""
    var endTime = ""
    var eventFreqStartDate = ""
    var eventFreqEndDate = ""
    var dayOfWeek = mutableListOf<Int>() //1,2,3,4,5,6,7 //TODO: in future usage
    var frequency = 0 // 0:none, 1:weekly, 2:every two week, 3: every three week, 4: every four week

    //internal data conversion usage
    private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年 M月 d日 HH:mm EEEE")
    private val frequencyChoiceArray = arrayOf("不重复", "每周", "每两周", "每三周", "每四周")
    private val dayOfWeekNumToString =
        mapOf(1 to "一", 2 to "二", 3 to "三", 4 to "四", 5 to "五", 6 to "六", 7 to "日")
    private val dayOfWeekStringToNum =
        mapOf('一' to 1, '二' to 2, '三' to 3, '四' to 4, '五' to 5, '六' to 6, '日' to 7)

    val maxTitleLength = 15

    //endregion

    //region constructor

    constructor(initDate : LocalDate) : this(){
        //default start and end time
        setStartLocalDateTime(
            LocalDateTime.of(
                initDate.year,
                initDate.month,
                initDate.dayOfMonth,
                9,
                0)
        )
        setEndLocalDateTime(
            LocalDateTime.of(
                initDate.year,
                initDate.month,
                initDate.dayOfMonth,
                10,
                0)
        )

        //default frequency start and end date
        setEventFreqStartDate(
            LocalDateTime.of(
                initDate.year,
                initDate.month,
                initDate.dayOfMonth,
                0,
                0
            )
        )
        setEventFreqEndDate(
            LocalDateTime.of(
                initDate.year,
                initDate.monthValue,
                initDate.dayOfMonth,
                23,
                59
            )
        )

        //force to add the selected dayOfWeek
        dayOfWeek.add(initDate.dayOfWeek.value)
    }

    /**
     * constructor to convert db calendarEvent to the object
     */
    constructor( id: Int, title: String, students: String, location: String,
                 notes: String, startTimeDBStr: String, endTimeDBStr: String,
                 dayOfWeekDBStr: String, frequency: Int , eventStartDate : String = "",
                 eventEndDate : String = "") : this(){
        this.id = id
        this.title = title
        this.students = students
        this.location = location
        this.notes = notes
        this.setDayOfWeekFromString(dayOfWeekDBStr)
        this.frequency = frequency
        this.eventFreqStartDate = eventStartDate
        this.eventFreqEndDate = eventEndDate

        this.startTime = startTimeDBStr
        this.endTime = endTimeDBStr
    }

    //endregion

    //region fun

    /**
     * The string to be shown in the event dialog
     */
    fun frequencyString(): String {
        return frequencyChoiceArray[frequency]
    }

    /**
     * get start time in LocalDateTime
     */
    fun getStartLocalDateTime(): LocalDateTime = LocalDateTime.parse(startTime, timeFormatter)

    /**
     * get end time in LocalDateTime
     */
    fun getEndLocalDateTime(): LocalDateTime = LocalDateTime.parse(endTime, timeFormatter)

    /**
     * set event start time in LocalDateTime
     */
    fun setStartLocalDateTime(localDateTime: LocalDateTime) { startTime = localDateTime.format(timeFormatter)}

    /**
     * set event end time in LocalDateTime
     */
    fun setEndLocalDateTime(localDateTime: LocalDateTime) { endTime = localDateTime.format(timeFormatter)}

    /**
     * set freq start date in LocalDateTime
     */
    fun setEventFreqStartDate(localDateTime: LocalDateTime){
        eventFreqStartDate = localDateTime.format(timeFormatter)
    }

    /**
     * set freq end date in LocalDateTime
     */
    fun setEventFreqEndDate(localDateTime: LocalDateTime){
        eventFreqEndDate = localDateTime.format(timeFormatter)
    }

    /**
     * get freq start date in LocalDateTime
     */
    fun getEventFreqStartDate(): LocalDateTime = LocalDateTime.parse(eventFreqStartDate, timeFormatter)

    /**
     * get freq end date in LocalDateTime
     */
    fun getEventFreqEndDate(): LocalDateTime = LocalDateTime.parse(eventFreqEndDate, timeFormatter)


    /**
     * The string to be shown in the event dialog
     */
    fun dayOfWeekString(): String {
        var str = ""
        for (i in dayOfWeek) str += dayOfWeekNumToString[i]
        return str
    }

    /**
     * set day of week from string
     */
    private fun setDayOfWeekFromString(input: String) {
        dayOfWeek.clear()
        if (input.isBlank()) return
        for (char in input.toCharArray()) {
            dayOfWeek.add(dayOfWeekStringToNum[char] ?: 0 )
        }
    }

    //endregion
}


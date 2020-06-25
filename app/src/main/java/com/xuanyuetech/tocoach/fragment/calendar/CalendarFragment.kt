package com.xuanyuetech.tocoach.fragment.calendar

import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.*
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.xuanyuetech.tocoach.R
import com.xuanyuetech.tocoach.adapter.CalendarEventAdapter
import com.xuanyuetech.tocoach.adapter.setUpWith
import com.xuanyuetech.tocoach.data.CalendarEvent
import com.xuanyuetech.tocoach.data.DatabaseHelper
import com.xuanyuetech.tocoach.fragment.BasicFragment
import com.xuanyuetech.tocoach.util.isEqualDateOnly
import com.xuanyuetech.tocoach.util.setTextColorRes
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthScrollListener
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.yearMonth
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.ChronoUnit
import org.threeten.bp.temporal.WeekFields
import java.util.*
import kotlin.collections.ArrayList

/**
 * calendar fragment
 */
class CalendarFragment : BasicFragment(), EventDialogFragment.AddEventFragmentResultListener {

    //region properties

    private lateinit var databaseHelper : DatabaseHelper
    private lateinit var allEvents : ArrayList<CalendarEvent>

    private lateinit var selectedDate: LocalDate
    private lateinit var today : LocalDate

    //internal conversion formatter
    private val selectionFormatter = DateTimeFormatter.ofPattern("yyyy年 M月 d日 EEEE")

    //the total number of months shown in the calendar
    private val numMonthsBefore = 4
    private val numMonthsAfter = 6

    private lateinit var calendarView : CalendarView
    private lateinit var selectedDateView : TextView
    private lateinit var calendarEventAdapter : CalendarEventAdapter

    private val handler = Handler()

    //endregion

    //region onCreateView

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mView = inflater.inflate(R.layout.fragment_calendar, container, false)

        calendarView = mView.findViewById(R.id.calendarView)

        customizeToolbar()

        initView()

        initData()

        Thread {

            bindAdapter()

            initCalendar()

            setupButton()
        }.start()

        return mView
    }

    //endregion

    //region override

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_calendar, menu)
    }

    /**
     * option item selected
     */
    override fun onOptionsItemSelected(item: MenuItem) :Boolean {
        if(isValidClick()) {
            when (item.itemId) {
                R.id.menuItem_home_add_new_event -> {
                    createNewEvent()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * has option menu
     */
    override fun customizeToolbar(){
        setHasOptionsMenu(true)
    }

    /**
     * The function runs once finishing the event adding dialog
     */
    override fun finishAddEventDialogFrag(isSaveOrDelete: Int, calendarEvent: CalendarEvent) {
        when (isSaveOrDelete) {
            1 -> saveOrUpdateEvent(calendarEvent)
            -1 -> deleteEvent(calendarEvent)
            -2 -> stopEventFromSelectedLocalDate(calendarEvent)
        }
    }

    //endregion

    //region fun

    /**
     * init all views
     */
    fun initView(){
        selectedDateView = mView.findViewById(R.id.textView_selectedDate)

        mView.findViewById<ImageButton>(R.id.calendar_button_previous_date).setOnClickListener {
            val newDate = selectedDate.minusDays(1)
            if(newDate.monthValue != selectedDate.monthValue) calendarView.scrollToMonth(newDate.yearMonth)
            handler.post { selectDateInSameMonth(newDate)}
        }
        mView.findViewById<ImageButton>(R.id.calendar_button_next_date).setOnClickListener{
            val newDate = selectedDate.plusDays(1)
            if(newDate.monthValue != selectedDate.monthValue) calendarView.scrollToMonth(newDate.yearMonth)
            handler.post { selectDateInSameMonth(newDate)}
        }
    }

    /**
     * init data
     */
    fun initData(){
        databaseHelper = DatabaseHelper(activity!!)

        allEvents = ArrayList()
        allEvents.addAll(databaseHelper.getAllCalendarEvent())

        today = LocalDate.now()
        selectedDate = LocalDate.now().minusDays(1)

        selectedDateView.text = selectionFormatter.format(today)
    }

    /**
     * bind all button
     */
    private fun setupButton(){
        mView.findViewById<Button>(R.id.button_today).setOnClickListener {
            calendarView.scrollToMonth(today.yearMonth)
            selectDateInSameMonth(today)
        }
    }

    /**
     * init the CalendarView
     */
    private fun initCalendar(){

        //get the local day of week ordering
        //reset the week title header according to the local calendar config
        val daysOfWeek = daysOfWeekFromLocale()
        resetTheWeekTitle(daysOfWeek)

        val currentMonth = YearMonth.now()

        //the total months of calendar
        calendarView.setup(
            currentMonth.minusMonths((numMonthsBefore).toLong()),
            currentMonth.plusMonths((numMonthsAfter).toLong()),
            daysOfWeek.first()
        )
        calendarView.scrollToMonth(currentMonth)

        // Show today's events initially.
        selectDateInSameMonth(today)

        bindCalendar()
    }

    /**
     * Setup required components of calendars
     */
    private fun bindCalendar(){

        // the class to bind the view of day cell
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val textView = view.findViewById<TextView>(R.id.textView_calendarDay)
            val dotView = view.findViewById<View>(R.id.eventDotView_calendarDay)

            init {
                //selectDate once click the cell view
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        selectDateInSameMonth(day.date)
                    }
                }
            }
        }

        //define the date cell views
        calendarView.dayBinder = object : DayBinder<DayViewContainer> {

            override fun create(view: View) = DayViewContainer(view)

            override fun bind(container: DayViewContainer, day: CalendarDay) {

                container.day = day

                val textView = container.textView
                val dotView = container.dotView

                textView.text = day.date.dayOfMonth.toString()

                if (day.owner == DayOwner.THIS_MONTH) {

                    textView.visibility = View.VISIBLE

                    //different views
                    when (day.date) {
                        today -> {
                            //it is today
                            textView.setTextColorRes(R.color.colorWhite_100)
                            textView.setBackgroundResource(R.drawable.ic_circle_black)
                            dotView.visibility = View.INVISIBLE
                        }
                        selectedDate -> {
                            //it is selected date
                            textView.setTextColorRes(R.color.black)
                            textView.setBackgroundResource(R.drawable.ic_circle_orange)
                            dotView.visibility = View.INVISIBLE
                        }
                        else -> {
                            //it is regular date
                            textView.setTextColorRes(R.color.black)
                            textView.background = null
                            dotView.isVisible = hasEvent(day.date)
                        }
                    }
                } else {
                    //do not show other months
                    textView.visibility = View.INVISIBLE
                    dotView.visibility = View.INVISIBLE
                }
            }
        }

        //bind scroll action
        calendarView.monthScrollListener = scrollToMonthListener
    }

    private val scrollToMonthListener = object : MonthScrollListener{
        override fun invoke(it: CalendarMonth) {
            if(it.yearMonth == today.yearMonth){
                selectDateInSameMonth(today)
            }else{
                selectDateInSameMonth(it.yearMonth.atDay(1))
            }

            val monthText = mView.findViewById<TextView>(R.id.calendar_monthText)
            val monthFormatter = DateTimeFormatter.ofPattern("MMMM", Locale.getDefault())
            monthText.text = it.yearMonth.format(monthFormatter)
        }

    }

    /**
     * reset the header of the calendar based on the local language
     */
    private fun resetTheWeekTitle(daysOfWeek : Array<DayOfWeek>){
        mView.findViewById<LinearLayout>(R.id.legendLayout).children.forEachIndexed { index, view ->
            (view as TextView).apply {
                text = daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale.CHINESE)
            }
        }
    }

    /**
     * setUp events
     */
    private fun bindAdapter(){
        calendarEventAdapter = CalendarEventAdapter {

            if(isValidClick()){
                val sheet = EventDialogFragment(it, selectionFormatter.format(selectedDate))

                //set the result listener, it will run finishAddEventDialogFrag function once fragment dismissed
                sheet.setDialogResultListener(this)
                sheet.isCancelable = false
                sheet.show(activity!!.supportFragmentManager, "eventDialog")
            }
        }

        val eventsList = mView.findViewById<RecyclerView>(R.id.eventsList)
        eventsList.setUpWith(calendarEventAdapter)
    }

    /**
     * get the days of week based on the local info
     */
    private fun daysOfWeekFromLocale(): Array<DayOfWeek> {
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        var daysOfWeek = DayOfWeek.values()
        // Order `daysOfWeek` array so that firstDayOfWeek is at index 0.
        if (firstDayOfWeek != DayOfWeek.MONDAY) {
            val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
            val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
            daysOfWeek = rhs + lhs
        }

        return daysOfWeek
    }

    /**
     * select specific date
     */
    private fun selectDateInSameMonth(localDate: LocalDate) {
        if (selectedDate != localDate) {

                val oldDate = selectedDate

                selectedDate = localDate

                //changed the date view back
                calendarView.notifyDateChanged(oldDate)

                //update the view and the event list
                calendarView.notifyDateChanged(selectedDate)
                updateEventsListAndViewForSelectedDate(selectedDate)
            }

    }

    /**
     * update the events list
     */
    private fun updateEventsListAndViewForSelectedDate(localDate: LocalDate ) {
        calendarEventAdapter.events.clear()
        calendarEventAdapter.events.addAll(getEventAtDate(localDate))
        calendarEventAdapter.notifyDataSetChanged()
        selectedDateView.text = selectionFormatter.format(localDate)
    }

    /**
     * save the event or just update it
     */
    private fun saveOrUpdateEvent(calendarEvent : CalendarEvent) {

        handler.post{
            if(!allEvents.contains(calendarEvent)) {
                //if this is the new calendar event
                val newId = databaseHelper.addCalendarEvent(calendarEvent).toInt()
                calendarEvent.id = newId
                allEvents.add(calendarEvent)
            }else{
                //just update the calendar event
                databaseHelper.updateCalendarEvent(calendarEvent)
            }

            //refresh the event list
            updateEventsListAndViewForSelectedDate(selectedDate)

            //update if the calendar event has frequency
            if(calendarEvent.frequency != 0) calendarView.notifyCalendarChanged()
        }
    }

    /**
     * delete the event
     */
    private fun deleteEvent(calendarEvent: CalendarEvent) {
        handler.post{
            allEvents.remove(calendarEvent)
            databaseHelper.deleteCalendarEvent(calendarEvent)
            updateEventsListAndViewForSelectedDate(selectedDate)
            if(calendarEvent.frequency != 0) calendarView.notifyCalendarChanged()
        }
    }

    /**
     * delete the event from selected date
     */
    private fun stopEventFromSelectedLocalDate(calendarEvent: CalendarEvent){
        handler.post{
            calendarEvent.setEventFreqEndDate(LocalDateTime.of(selectedDate.minusDays(1), LocalTime.of(23,59)))
            databaseHelper.updateCalendarEvent(calendarEvent)
            updateEventsListAndViewForSelectedDate(selectedDate)
            if(calendarEvent.frequency != 0) calendarView.notifyCalendarChanged()
        }
    }

    /**
     * create new event and show the event editing fragment
     */
    private fun createNewEvent() {
        val newEvent = CalendarEvent(selectedDate)

        //crate the bottom sheet dialog to edit event
        val sheet = EventDialogFragment(newEvent, selectionFormatter.format(selectedDate))
        sheet.setDialogResultListener(this)
        sheet.isCancelable = false
        sheet.show(activity!!.supportFragmentManager, "eventDialog")
    }

    /**
     * Get all events for the particular date
     */
    private fun getEventAtDate(localDate : LocalDate) : MutableList<CalendarEvent>{
        val currCalendarEvents = mutableListOf<CalendarEvent>()

        for(event in allEvents){

            if(event.dayOfWeek.contains(localDate.dayOfWeek.value) && event.frequency != 0){
                //same dayOfWeek date

                val weeksBetween = ChronoUnit.WEEKS.between(localDate, event.getStartLocalDateTime())

                if(( weeksBetween % event.frequency).toInt() == 0
                    && LocalDateTime.of(localDate, LocalTime.NOON).isAfter(event.getEventFreqStartDate())
                    && LocalDateTime.of(localDate, LocalTime.NOON).isBefore(event.getEventFreqEndDate()))
                {
                    //is in the frequency
                    currCalendarEvents.add(event)
                }

            }else if(localDate.isEqualDateOnly(event.getStartLocalDateTime().toLocalDate())){
                //directly same date
                currCalendarEvents.add(event)
            }
        }

        //we want to sort by start time
        currCalendarEvents.sortBy { it.getStartLocalDateTime() }
        return currCalendarEvents
    }

    /**
     * Check if there is CalendarEvent on specific date, logic is cloned from getEventAtDate()
     */
    fun hasEvent(localDate : LocalDate) : Boolean{
        for(event in allEvents){
            if(event.dayOfWeek.contains(localDate.dayOfWeek.value) && event.frequency != 0){
                //same dayOfWeek date
                val weeksBetween = ChronoUnit.WEEKS.between(localDate, event.getStartLocalDateTime())
                if(( weeksBetween % event.frequency).toInt() == 0
                    && LocalDateTime.of(localDate, LocalTime.NOON).isAfter(event.getEventFreqStartDate())
                    && LocalDateTime.of(localDate, LocalTime.NOON).isBefore(event.getEventFreqEndDate()))
                {
                    //is in the frequency
                    return true
                }
            }else if(localDate.isEqualDateOnly(event.getStartLocalDateTime().toLocalDate())){
                //directly same date
                return true
            }
        }
        return false
    }

    //endregion
}
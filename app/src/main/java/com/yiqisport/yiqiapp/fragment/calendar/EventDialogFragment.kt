package com.yiqisport.yiqiapp.fragment.calendar

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import com.yiqisport.yiqiapp.R
import com.yiqisport.yiqiapp.data.CalendarEvent
import com.yiqisport.yiqiapp.util.*
import com.yiqisport.yiqiapp.util.getHourMinutesStr
import com.yiqisport.yiqiapp.util.setMaxLength
import com.yiqisport.yiqiapp.util.setOnClickable
import com.yiqisport.yiqiapp.util.setUnClickable
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.threeten.bp.LocalDateTime

/**
 * Event dialog fragment to edit the calendar event
 */
class EventDialogFragment(private val calendarEvent: CalendarEvent, private val pageTitle: String) :
    BottomSheetDialogFragment() {

    //region properties

    private var startCalendar = calendarEvent.getStartLocalDateTime()
    private var endCalendar = calendarEvent.getEndLocalDateTime()

    private lateinit var pageTitleText: TextView
    private lateinit var startTextView: TextView
    private lateinit var endTextView: TextView
    private lateinit var dayOfWeekText: TextView
    private lateinit var frequencyText: TextView
    private lateinit var eventFreqStartText: TextView
    private lateinit var eventFreqEndText: TextView

    private lateinit var titleEditText: EditText
    private lateinit var locationText: EditText
    private lateinit var foldersText: EditText
    private lateinit var notesText: EditText

    private lateinit var mView: View

    private var listener: AddEventFragmentResultListener? = null

    //endregion


    //region onCreateView

    /**
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        mView = inflater.inflate(R.layout.fragment_calendar_event, container, false)

        initViews()

        setupListener()

        initData()

        return mView
    }

    //endregion

    //region fun

    /**
     * init views
     */
    private fun initViews() {
        pageTitleText = mView.findViewById(R.id.textView_pageTitle)
        startTextView = mView.findViewById(R.id.textView_eventStartTime)
        endTextView = mView.findViewById(R.id.textView_eventEndTime)
        dayOfWeekText = mView.findViewById(R.id.textView_eventDayOfWeek)
        frequencyText = mView.findViewById(R.id.textView_eventFrequency)
        titleEditText = mView.findViewById(R.id.editText_event_title)
        titleEditText.setMaxLength(CalendarEvent().maxTitleLength)

        locationText = mView.findViewById(R.id.editText_event_location)
        foldersText = mView.findViewById(R.id.editText_event_people)
        notesText = mView.findViewById(R.id.editText_event_notes)
        eventFreqStartText = mView.findViewById(R.id.textView_eventFrequency_start)
        eventFreqEndText = mView.findViewById(R.id.textView_eventFrequency_end)
        refreshFreqSettings()
    }

    /**
     * bind listeners
     */
    private fun setupListener() {
        setupStartSettingListener()
        setupEndSettingListener()
        setupTitleListener()
        setupFrequencyListener()
        setupCancelAndAddListener()
        setupDeleteButtonListener()
        setupEventFreqStartListener()
        setupEventFreqEndListener()
    }

    /**
     * init data
     */
    private fun initData() {
        pageTitleText.text = pageTitle
        startTextView.text = startCalendar.getHourMinutesStr()
        endTextView.text = endCalendar.getHourMinutesStr()
        eventFreqStartText.text =
            calendarEvent.getEventFreqStartDate().format(dateWithDayOfWeekFormatter())
        eventFreqEndText.text =
            calendarEvent.getEventFreqEndDate().format(dateWithDayOfWeekFormatter())
        dayOfWeekText.text = calendarEvent.dayOfWeekString()
        frequencyText.text = calendarEvent.frequencyString()
        titleEditText.setText(calendarEvent.title)
        locationText.setText(calendarEvent.location)
        foldersText.setText(calendarEvent.attendants)
        notesText.setText(calendarEvent.notes)
    }

    /**
     * click start time line to pick a start time
     */
    private fun setupStartSettingListener() {
        mView.findViewById<ConstraintLayout>(R.id.event_start_setting)
            .setOnClickListener {

                val onTimeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    val newTime = LocalDateTime.of(
                        startCalendar.year,
                        startCalendar.monthValue,
                        startCalendar.dayOfMonth,
                        hour,
                        minute
                    )
                    calendarEvent.setStartLocalDateTime(newTime)
                    startCalendar = newTime
                    updateStartTime()
                }

                val timePickerTimeDialog = createTimePickerView(onTimeSetListener, startCalendar)

                timePickerTimeDialog.show()
            }
    }

    /**
     * click end time line to pick an end time
     */
    private fun setupEndSettingListener() {

        mView.findViewById<ConstraintLayout>(R.id.event_end_setting)
            .setOnClickListener {

                val onTimeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    val newTime = LocalDateTime.of(
                        endCalendar.year,
                        endCalendar.monthValue,
                        endCalendar.dayOfMonth,
                        hour,
                        minute
                    )
                    calendarEvent.setEndLocalDateTime(newTime)
                    endCalendar = newTime
                    updateEndTime()
                }

                val timePickerTimeDialog = createTimePickerView(onTimeSetListener, endCalendar)

                timePickerTimeDialog.show()
            }
    }

    /**
     * bind frequency listener
     */
    private fun setupFrequencyListener() {
        mView.findViewById<ConstraintLayout>(R.id.event_frequency_setting)
            .setOnClickListener {
                showFrequencyPickerView()
            }
    }

    /**
     * freq start date pick
     */
    private fun setupEventFreqStartListener() {
        mView.findViewById<ConstraintLayout>(R.id.event_freq_start_setting)
            .setOnClickListener {
                showDatePickerView(
                    DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                        val newDate = LocalDateTime.of(year, month + 1, dayOfMonth, 0, 0)
                        calendarEvent.setEventFreqStartDate(newDate)
                        eventFreqStartText.text = calendarEvent.getEventFreqStartDate()
                            .format(dateWithDayOfWeekFormatter())
                    },
                    calendarEvent.getEventFreqStartDate()
                )

            }
    }

    /**
     * freq end date pick
     */
    private fun setupEventFreqEndListener() {
        mView.findViewById<ConstraintLayout>(R.id.event_freq_end_setting)
            .setOnClickListener {
                showDatePickerView(
                    DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                        val newDate = LocalDateTime.of(year, month + 1, dayOfMonth, 23, 59)
                        calendarEvent.setEventFreqEndDate(newDate)
                        eventFreqEndText.text =
                            calendarEvent.getEventFreqEndDate().format(dateWithDayOfWeekFormatter())
                    },
                    calendarEvent.getEventFreqEndDate()
                )

            }
    }

    /**
     * cancel and add
     */
    private fun setupCancelAndAddListener() {

        val addButton = mView.findViewById<Button>(R.id.button_event_add)
        val cancelButton = mView.findViewById<Button>(R.id.button_event_cancel)

        cancelButton.setOnClickListener {
            listener?.finishAddEventDialogFrag(0, calendarEvent)
            dismiss()
        }

        addButton.setOnClickListener {
            calendarEvent.title = titleEditText.text.toString()
            calendarEvent.location = locationText.text.toString()
            calendarEvent.attendants = foldersText.text.toString()
            calendarEvent.notes = notesText.text.toString()
            if (isValidSave()) {
                listener?.finishAddEventDialogFrag(1, calendarEvent)
                dismiss()
            }
        }

    }

    /**
     * bind title text with confirm button
     */
    private fun setupTitleListener() {
        titleEditText.doOnTextChanged { text, _, _, _ ->
            val addButton = mView.findViewById<Button>(R.id.button_event_add)
            if (text != null && text.isNotBlank()) {
                addButton.setOnClickable(R.color.blue_flat_90)
            } else {
                addButton.setUnClickable(R.color.colorBlack_10)
            }
        }
    }

    /**
     * set up delete button
     */
    @SuppressLint("SetTextI18n", "InflateParams")
    private fun setupDeleteButtonListener() {
        val deleteButton = mView.findViewById<Button>(R.id.button_delete_event)
        if (calendarEvent.title.isBlank()) {
            deleteButton.visibility = GONE
        } else {

            deleteButton.setOnClickListener {

                if (calendarEvent.frequency == 0 ||
                    calendarEvent.getEventFreqStartDate().toLocalDate()
                        .isEqualDateOnly(calendarEvent.getEventFreqEndDate().toLocalDate())
                ) {
                    //no freq or freq is actually same start on end date
                    AlertDialog.Builder(context!!, R.style.CustomDialogTheme)
                        .setMessage("确定删除该课程?")
                        .setPositiveButton("删除") { _, _ ->
                            listener?.finishAddEventDialogFrag(-1, calendarEvent)
                            dismiss()
                        }
                        .setNegativeButton("取消", null)
                        .show()
                } else {
                    //has frequency, need show options
                    val builder = AlertDialog.Builder(context!!, R.style.CustomDialogTheme)
                    val inflater = activity!!.layoutInflater
                    val view: View =
                        inflater.inflate(R.layout.dialog_calendar_event_delete_option, null)

                    builder.setView(view)

                    builder
                        .setTitle("请选择停止或者删除")
                        .setPositiveButton("确认") { _, _ ->

                            when (view.findViewById<RadioGroup>(R.id.delete_options)!!.checkedRadioButtonId) {
                                R.id.delete_all -> {
                                    listener?.finishAddEventDialogFrag(-1, calendarEvent)
                                    dismiss()
                                }
                                R.id.delete_from_selected -> {
                                    listener?.finishAddEventDialogFrag(-2, calendarEvent)
                                    dismiss()
                                }
                            }
                        }
                        .setNeutralButton("取消") { _, _ -> }
                        .show()

                    view.findViewById<RadioButton>(R.id.delete_from_selected).text =
                        "自该日起停止课程\n-${pageTitleText.text}-"
                }
            }
        }
    }

    /**
     * update start time will check validation of end time
     */
    private fun updateStartTime() {
        startTextView.text = startCalendar.getHourMinutesStr()
        if (startCalendar >= endCalendar) {
            endCalendar = calendarEvent.getStartLocalDateTime()
            endCalendar = endCalendar.plusHours(1)
            calendarEvent.setEndLocalDateTime(endCalendar)
            endTextView.text = endCalendar.getHourMinutesStr()
        }
    }

    /**
     * update end time will check validation of start time
     */
    private fun updateEndTime() {
        endTextView.text = endCalendar.getHourMinutesStr()
        if (endCalendar <= startCalendar) {
            startCalendar = calendarEvent.getEndLocalDateTime()
            startCalendar = startCalendar.minusHours(1)
            calendarEvent.setStartLocalDateTime(startCalendar)
            startTextView.text = startCalendar.getHourMinutesStr()
        }
    }

    /**
     * create tune picker dialog for time picking
     */
    private fun createTimePickerView(
        clickListener: TimePickerDialog.OnTimeSetListener,
        localDateTime: LocalDateTime
    ): TimePickerDialog {

        return TimePickerDialog(
            context,
            R.style.TimePickerTheme,
            clickListener,
            localDateTime.hour,
            localDateTime.minute,
            false
        )
    }

    /**
     * freq choice dialog
     */
    @SuppressLint("InflateParams")
    private fun showFrequencyPickerView() {
        var radioButtonIds = arrayListOf<Int>()
        var frequencyDialog: AlertDialog? = null
        val builder = AlertDialog.Builder(context!!, R.style.CustomDialogTheme)
        val inflater = activity!!.layoutInflater
        val view: View = inflater.inflate(R.layout.dialog_calendar_event_freq_option, null)

        builder.setView(view)

        builder
            .setPositiveButton("确认") { _, _ ->

                val checkedButtonId =
                    frequencyDialog!!.findViewById<RadioGroup>(R.id.radioGroup_frequency)!!.checkedRadioButtonId
                calendarEvent.frequency = radioButtonIds.indexOf(checkedButtonId)

                frequencyText.text = calendarEvent.frequencyString()

                refreshFreqSettings()
            }
            .setNeutralButton("取消") { _, _ -> }

        frequencyDialog = builder.create()
        frequencyDialog.show()

        radioButtonIds = arrayListOf(
            R.id.frequency_0,
            R.id.frequency_1,
            R.id.frequency_2,
            R.id.frequency_3,
            R.id.frequency_4
        )

        val checkedRadioButtonId = radioButtonIds[calendarEvent.frequency]

        frequencyDialog.findViewById<RadioGroup>(R.id.radioGroup_frequency)!!.check(
            checkedRadioButtonId
        )
    }

    /**
     * date picker dialog
     */
    private fun showDatePickerView(
        onDateSetListener: DatePickerDialog.OnDateSetListener,
        localDateTime: LocalDateTime
    ) {
        val datePicker = DatePickerDialog(
            context!!,
            R.style.TimePickerTheme,
            onDateSetListener,
            localDateTime.year,
            localDateTime.monthValue - 1,
            localDateTime.dayOfMonth
        )
        datePicker.show()
    }

    /**
     * refresh freq settings
     */
    private fun refreshFreqSettings() {
        if (calendarEvent.frequency != 0) {
            mView.findViewById<LinearLayoutCompat>(R.id.freq_dates_setting_container).visibility =
                VISIBLE
        } else {
            mView.findViewById<LinearLayoutCompat>(R.id.freq_dates_setting_container).visibility =
                GONE
        }
    }

    /**
     * check validation of save
     */
    private fun isValidSave(): Boolean {
        if (calendarEvent.frequency != 0 &&
            calendarEvent.getEventFreqStartDate() > calendarEvent.getEventFreqEndDate()
        ) {
            AlertDialog.Builder(context!!, R.style.CustomDialogTheme)
                .setMessage("重复的开始日期不能晚于结束日期!")
                .setNeutralButton("确定") { _: DialogInterface, _: Int -> }
                .show()
            return false
        }

        return true
    }

    /**
     * dialog callback
     */
    fun setDialogResultListener(listener: AddEventFragmentResultListener) {
        this.listener = listener
    }

    //endregion

    //region listener interface for callback

    /**
     * the listener interface is to pass the result data to the calendar fragment
     */
    interface AddEventFragmentResultListener {
        //save = 1, deleteAll = -1, none = 0, deleteFromSelected = -2
        fun finishAddEventDialogFrag(isSaveOrDelete: Int, calendarEvent: CalendarEvent)
    }

    //endregion

    //TODO: FOR FUTURE USAGE TO SUPPORT DAY OF WEEK PICK
}


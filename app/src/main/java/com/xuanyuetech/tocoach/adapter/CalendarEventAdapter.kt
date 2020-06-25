package com.xuanyuetech.tocoach.adapter

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xuanyuetech.tocoach.R
import com.xuanyuetech.tocoach.data.CalendarEvent
import com.xuanyuetech.tocoach.util.getHourMinutesStr
import com.xuanyuetech.tocoach.util.inflate
import kotlinx.android.extensions.LayoutContainer
import kotlin.random.Random

/**
 * the adapter for calendar event list
 */
class CalendarEventAdapter(val onClick: (CalendarEvent) -> Unit) : RecyclerView.Adapter<CalendarEventAdapter.EventsViewHolder>() {

    val events = mutableListOf<CalendarEvent>()

    /**
     *
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        return EventsViewHolder(parent.inflate(R.layout.cardview_calendar_event))
    }

    /**
     *
     */
    override fun onBindViewHolder(viewHolder: EventsViewHolder, position: Int) {
        viewHolder.bind(events[position])
    }

    /**
     *
     */
    override fun getItemCount(): Int = events.size

    /**
     *
     */
    inner class EventsViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        init {
            itemView.setOnClickListener {
                onClick(events[adapterPosition])
            }
        }

        fun bind(event: CalendarEvent) {
            containerView.findViewById<TextView>(R.id.item_event_title).text = event.title

            //assign random color for the horizontal lane
            val rdn = Random
            containerView.findViewById<View>(R.id.item_event_horizontal_line).setBackgroundColor(
                Color.argb(255, rdn.nextInt(256), rdn.nextInt(256), rdn.nextInt(256)))

            containerView.findViewById<TextView>(R.id.item_event_startTime).text = event.getStartLocalDateTime().getHourMinutesStr()
            containerView.findViewById<TextView>(R.id.item_event_endTime).text = event.getEndLocalDateTime().getHourMinutesStr()
            containerView.findViewById<TextView>(R.id.item_event_detail).text = event.attendants

        }
    }

}

/**
 * infix function to set up the RecyclerView with adapter
 */
infix fun RecyclerView.setUpWith(calendarAdapter: CalendarEventAdapter) {
    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    adapter = calendarAdapter
    addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
}
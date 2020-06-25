package com.xuanyuetech.tocoach.util

import android.annotation.SuppressLint
import android.content.Context
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

/**
 * The file is included all internal functions which extents the class public methods
 */

internal fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

internal fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)

internal fun TextView.setTextColorRes(@ColorRes color: Int) = setTextColor(context.getColorCompat(color))

internal fun Button.setUnClickable(@ColorRes color: Int) {
    this.setTextColor(context.getColorCompat(color))
    this.isClickable = false
}

internal fun Button.setOnClickable(@ColorRes color: Int) {
    this.setTextColor(context.getColorCompat(color))
    this.isClickable = true
}

internal fun EditText.setMaxLength(maxLength : Int){
    val inputFilters: Array<InputFilter> =  Array(1) {InputFilter.LengthFilter(maxLength) }
    this.filters =  inputFilters
}

//Calendar or localDate related
internal fun LocalDate.isEqualDateOnly(otherLocalDate : LocalDate)
        = this.year == otherLocalDate.year && this.month == otherLocalDate.month
        && this.dayOfMonth == otherLocalDate.dayOfMonth

@SuppressLint("SimpleDateFormat")
internal fun LocalDateTime.getHourMinutesStr() : String {
    val selectionFormatter = DateTimeFormatter.ofPattern("h:mm a")
    return selectionFormatter.format(this)
}

internal fun dateWithDayOfWeekFormatter() = DateTimeFormatter.ofPattern("yyyy年 M月 d日 EEEE")
package com.yiqisport.yiqiapp.util.videoeditor

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class DisableScrollViewPager (context : Context, attrs : AttributeSet): ViewPager(context, attrs) {

    private var enabled: Boolean? = null

    init {
        enabled = false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        //disable the scrolling behavior
        return true
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        parent.requestDisallowInterceptTouchEvent(enabled!!)
        return enabled!! && super.onInterceptTouchEvent(event)
    }

}
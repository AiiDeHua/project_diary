package com.xuanyuetech.tocoach.util.videoeditor

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent

class ReversedSeekBar: androidx.appcompat.widget.AppCompatSeekBar{

    constructor(context: Context) : super(context){
    }

    constructor(context : Context, attributeSet: AttributeSet) : super(context, attributeSet) {
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyle : Int) : super(context, attributeSet, defStyle){

    }

    override fun onDraw(canvas: Canvas?) {
        val px = this.width / 2.0f
        val py = this.height / 2.0f

        canvas!!.scale((-1).toFloat(), 1F, px, py)

        super.onDraw(canvas)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.setLocation(this.width - event.x, event.y)
        return super.onTouchEvent(event)
    }
}
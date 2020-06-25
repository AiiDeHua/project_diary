package com.xuanyuetech.tocoach.fragment.video_editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.xuanyuetech.tocoach.R
import com.xuanyuetech.tocoach.fragment.BasicFragment
import com.xuanyuetech.tocoach.util.videoeditor.VideoEditorUtil
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar

/**
 * Video editor trim
 */
class VideoEditorTrimFragment(private var videoEndS:Float, private var rangeSeekBarListener: OnRangeChangedListener) : BasicFragment() {

    //region properties

    private lateinit var trimDone: Button
    lateinit var rangeSeekBar: RangeSeekBar
    private lateinit var itemClickListener:CustomOnFinishButtonClickListener
    lateinit var trimTextLeft:TextView
    lateinit var trimTextRight:TextView

    //endregion

    //region onCreateView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_video_editor_trim, container, false)
        trimDone = mView.findViewById(R.id.video_editor_trim_done)
        trimDone.setOnClickListener{
            itemClickListener.onFinishButtonClickListener()
        }
        rangeSeekBar = mView.findViewById(R.id.video_editor_trim_range_seek_bar)
        trimTextLeft = mView.findViewById(R.id.video_editor_trim_indicator_left)
        trimTextRight = mView.findViewById(R.id.video_editor_trim_indicator_right)
        rangeSeekBar.setRange(0f,videoEndS)
        rangeSeekBar.setProgress(0f,videoEndS)
        rangeSeekBar.setOnRangeChangedListener(rangeSeekBarListener)
        trimTextLeft.text = VideoEditorUtil.convertMsProgressToString(rangeSeekBar.leftSeekBar.progress)
        trimTextRight.text = VideoEditorUtil.convertMsProgressToString(rangeSeekBar.rightSeekBar.progress)
        return mView
    }

    //endregion

    //region fun

    /**
     * click finish button
     */
    fun setOnFinishButtonClickListener(finishButtonClickListener: CustomOnFinishButtonClickListener) {
        this.itemClickListener = finishButtonClickListener
    }

    /**
     * set rangeBar progress
     */
    fun setRangeBarThumb(left:Float,right:Float){
        rangeSeekBar.setProgress(left,right)
        trimTextLeft.text = VideoEditorUtil.convertMsProgressToString(left)
        trimTextRight.text = VideoEditorUtil.convertMsProgressToString(right)
    }

    //endregion

    //region interface

    interface CustomOnFinishButtonClickListener {
        fun onFinishButtonClickListener()
    }

    //endregion

}
package com.yiqisport.yiqiapp.fragment.video_editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView

import com.yiqisport.yiqiapp.R
import com.yiqisport.yiqiapp.fragment.BasicFragment
import com.yiqisport.yiqiapp.util.videoeditor.VideoEditorUtil
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar

/**
 * VideoTrimmerFragment for the speed change
 */
class VideoEditorSpeedFragment(private var videoEndS:Float, private val rangeSeekBarListener : OnRangeChangedListener) : BasicFragment() {

    //region properties

    private lateinit var speedDone:Button
    private lateinit var itemClickListener: CustomOnFinishButtonClickListener
    private lateinit var speedTypeChangeListener: SpeedTypeChangeListener
    lateinit var rangeSeekBar : RangeSeekBar
    lateinit var speedTypeRadioGroup:RadioGroup
    lateinit var speedTextLeft: TextView
    lateinit var speedTextRight: TextView
    //endregion

    //region override
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_video_editor_speed, container, false)

        initRangeSeeker()

        return mView
    }
    //endregion

    //region fun

    /**
     * init range seeker
     */
    private fun initRangeSeeker(){
        speedDone = mView.findViewById(R.id.video_editor_speed_done)
        speedDone.setOnClickListener{
            itemClickListener.onFinishButtonClickListener()
        }
        speedTypeRadioGroup = mView.findViewById(R.id.video_editor_speed_radio_group)
        speedTypeRadioGroup.setOnCheckedChangeListener {
                _, checkedId ->
            when(checkedId){
                R.id.video_editor_speed_radio_group_025 -> {
                    //                        speed = 0.25
                    speedTypeChangeListener.speedTypeChangeClickListener(0.25)
                }
                R.id.video_editor_speed_radio_group_05 -> {
                    //                        speed = 0.5
                    speedTypeChangeListener.speedTypeChangeClickListener(0.5)
                }
            }
        }
        rangeSeekBar = mView.findViewById(R.id.video_editor_speed_range_seek_bar)
        rangeSeekBar.setRange(0f,videoEndS)
        rangeSeekBar.setProgress(0f,videoEndS)
        rangeSeekBar.setOnRangeChangedListener(rangeSeekBarListener)
        speedTextLeft = mView.findViewById(R.id.video_editor_speed_indicator_left)
        speedTextRight = mView.findViewById(R.id.video_editor_speed_indicator_right)
        speedTextLeft.text = VideoEditorUtil.convertMsProgressToString(rangeSeekBar.leftSeekBar.progress)
        speedTextRight.text = VideoEditorUtil.convertMsProgressToString(rangeSeekBar.rightSeekBar.progress)
    }

    /**
     * set range progress
     */
    fun setRangeBarThumb(left:Float,right:Float){
        rangeSeekBar.setProgress(left,right)
        speedTextLeft.text = VideoEditorUtil.convertMsProgressToString(left)
        speedTextRight.text = VideoEditorUtil.convertMsProgressToString(right)
    }

    /**
     * set finish button listener
     */
    fun setOnFinishButtonClickListener(itemClickListener: CustomOnFinishButtonClickListener) {
        this.itemClickListener = itemClickListener
    }

    /**
     * set speed type change listener
     */
    fun setSpeedTypeChangeListener(itemClickListener: SpeedTypeChangeListener) {
        this.speedTypeChangeListener = itemClickListener
    }

    //endregion

    //region interfaces

    interface CustomOnFinishButtonClickListener {
        fun onFinishButtonClickListener()
    }
    interface SpeedTypeChangeListener {
        fun speedTypeChangeClickListener(speed:Double)
    }
    //endregion

}

package com.yiqisport.yiqiapp.util.videoeditor

import com.jaygoo.widget.OnRangeChangedListener

class RangeBar() {

    //type
    //1 trim
    //2 speed
    //3 graffiti
    var controllerType = -1
    //rangeSeekBar 大小
    var startRange = -1f
    var endRange = -1f
    //rangeSeekBar 两个指针位置
    var startThumb = -1f
    var endThumb = -1f
    //rangeseekbar变换监听器
    lateinit var rangeChangedListener:OnRangeChangedListener
    var labelText = ""

    var rangeSeekBarHashCode = -1

    constructor(type:Int,startRange:Float,endRange:Float,rangeChangedListener: OnRangeChangedListener) : this() {
        this.controllerType = type
        this.startRange = startRange
        this.endRange = endRange
        startThumb = startRange
        endThumb = endRange
        this.rangeChangedListener = rangeChangedListener
        if(type == 1){
            labelText = "裁剪区段"
        }else if(type == 2){
            labelText = "变速特效"
        }else if(type == 3){
            labelText = "涂鸦和文字特效"
        }
    }

    fun rangeThumbChange(left:Float, right:Float){
        startThumb = left
        endThumb = right
    }
}
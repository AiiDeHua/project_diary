package com.yiqisport.yiqiapp.util.videoeditor

import android.util.Log
import com.qiniu.pili.droid.shortvideo.PLPaintView

class VideoEditorTimeRange{

    var typeRangeBar:Int = -1
    var left:Float = -1.0f
    var right:Float = -1.0f
    var indexInItsTypeList = -1
    /**
     * param for speed
     */
    var speedType = 1.0 //default speed

    /**
     * param for graffiti
     */
    lateinit var paintView : PLPaintView
    lateinit var stickerTextViewList:ArrayList<StickerTextView>
    lateinit var stickerImageViewList:ArrayList<StickerImageView>

    //空白构造器
    constructor()

    //整合用构造器
    //添加一个rangebar时 知晓在分类中是第几位 （2 ，0）---> 速度rangelist第1位
    constructor(typeRangeBar: Int,typePosition: Int){
        this.typeRangeBar = typeRangeBar
        this.indexInItsTypeList = typePosition
    }

    //速度用构造器
    constructor(typeRangeBar:Int,//type range bar 1 for trim, 2 for speed, 3 for graffiti
                left:Float, //left range for preview
                right:Float ,//right range for preview
                speedType:Double
    ){
        if(typeRangeBar == 1 || typeRangeBar == 3)
            Log.d("nmd","wrong constructor2")
        this.typeRangeBar = typeRangeBar
        this.left = left
        this.right = right
        this.speedType = speedType
    }

    //速度合并浏览用构造器
    constructor(left:Float, //left range for preview
                right:Float, //right range for preview
                speedType: Double
    ){
        this.left = left
        this.right = right
        this.speedType = speedType
    }

    //绘图用构造器
    constructor(typeRangeBar:Int,//type range bar 1 for trim, 2 for speed, 3 for graffiti
                left:Float, //left range for preview
                right:Float, //right range for preview
                paintView: PLPaintView
    ){
        if(typeRangeBar == 1 || typeRangeBar == 2)
            Log.d("nmd","wrong constructor3")
        this.typeRangeBar = typeRangeBar
        this.left = left
        this.right = right
        this.paintView = paintView
        stickerTextViewList = ArrayList()
        stickerImageViewList = ArrayList()
    }

    fun addText(text:StickerTextView){
        stickerTextViewList.add(text)
    }
    fun addImage(image:StickerImageView){
        stickerImageViewList.add(image)
    }

    fun removeText(text:StickerTextView){
        stickerTextViewList.remove(text)
    }
    fun removeImage(image:StickerImageView){
        stickerImageViewList.remove(image)
    }

    fun resetLeftRight(left: Float,right: Float){
        this.left = left
        this.right = right
    }

    override fun toString(): String {
        return "typeRangeBar = $typeRangeBar, left = $left, right = $right, typePosition = $indexInItsTypeList,speedType = $speedType "
    }
}
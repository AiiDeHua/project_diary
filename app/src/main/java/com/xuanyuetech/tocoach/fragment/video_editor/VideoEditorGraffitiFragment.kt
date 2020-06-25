package com.xuanyuetech.tocoach.fragment.video_editor

import android.app.AlertDialog
import android.graphics.drawable.Drawable
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.widget.NestedScrollView
import com.xuanyuetech.tocoach.R
import com.xuanyuetech.tocoach.fragment.BasicFragment
import com.xuanyuetech.tocoach.util.setMaxLength
import com.xuanyuetech.tocoach.util.videoeditor.*
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.qiniu.pili.droid.shortvideo.PLImageView
import com.qiniu.pili.droid.shortvideo.PLPaintView
import com.qiniu.pili.droid.shortvideo.PLShortVideoEditor
import com.qiniu.pili.droid.shortvideo.PLTextView

/**
 * Video editor graffiti fragment
 */
class VideoEditorGraffitiFragment(
    private var videoEndS: Float,
    private val previewView: GLSurfaceView,
    private val videoEditorController: PLShortVideoEditor,
    private val rangeSeekBarListener: OnRangeChangedListener
) :
    BasicFragment() {

    //region properties

    private lateinit var paintSelectorPanel: PaintSelectorPanel
    private lateinit var textSelectorPanel: TextSelectorPanel
    lateinit var stickerTextViewList:ArrayList<StickerTextView>
    private lateinit var imageSelectorPanel: ImageSelectorPanel
    lateinit var stickerImageViewList:ArrayList<StickerImageView>
    lateinit var paintView: PLPaintView
    private var mCurView: View? = null
    lateinit var graffitiTextLeft: TextView
    lateinit var graffitiTextRight: TextView

    private lateinit var graffitiDone: Button
    private lateinit var containerRangeSeeker: LinearLayoutCompat
    private lateinit var nestedScrollView: NestedScrollView
    private lateinit var textGraffiti: TextView
    private lateinit var textStickerText: TextView
    private lateinit var textStickerImage: TextView
    lateinit var rangeSeekBar: RangeSeekBar
    private lateinit var graffitiCreateListener: CustomOnFinishedButtonClickListener
    private lateinit var textCreateListener: CustomOnTextCreateListener
    private lateinit var imageCreateListener: CustomOnImageCreateListener
    private lateinit var stickerDeleteListener: CustomOnStickerDeleteListener


    //endregion

    //region onCreateView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_video_editor_graffiti, container, false)

        initViews()

        bindViewOptionButtonListener()

        initRangeSeeker()

        initTextSelectPanel()

        initImageSelectorPanel()

        return mView
    }

    //endregion

    //region methods

    /**
     * init views
     */
    private fun initViews() {
        nestedScrollView = mView.findViewById(R.id.video_editor_graffiti_nestedscrollview)
        paintSelectorPanel = mView.findViewById(R.id.paint_selector_panel)
        textSelectorPanel = mView.findViewById(R.id.video_editor_text_selector_panel)
        imageSelectorPanel = mView.findViewById(R.id.video_editor_image_selector_panel)
        graffitiDone = mView.findViewById(R.id.video_editor_graffiti_done)

        graffitiDone.setOnClickListener {
            paintView.setPaintEnable(false)
            graffitiCreateListener.onGraffitiFinishedListener()
        }

        textGraffiti = mView.findViewById(R.id.video_editor_graffiti_text_graffiti)
        textStickerText = mView.findViewById(R.id.video_editor_graffiti_text_sticker_text)
        textStickerImage = mView.findViewById(R.id.video_editor_graffiti_text_sticker_image)
        rangeSeekBar = mView.findViewById(R.id.video_editor_graffiti_range_seek_bar)
        containerRangeSeeker = mView.findViewById(R.id.container_rangeSeeker)

        //pick textGraffiti
        textGraffiti.setBackgroundColor(context!!.getColor(R.color.orange))
        textGraffiti.setTextColor(context!!.getColor(R.color.black))
        graffitiTextLeft = mView.findViewById(R.id.video_editor_graffiti_indicator_left)
        graffitiTextRight = mView.findViewById(R.id.video_editor_graffiti_indicator_right)

    }

    /**
     * pick graffiti
     */
    private fun pickGraffiti(){
        textStickerText.selectItem(false)
        textGraffiti.selectItem(true)
        textStickerImage.selectItem(false)
    }

    /**
     * bind button listeners
     */
    private fun bindViewOptionButtonListener(){

        //pick graffiti setting
        textGraffiti.setOnClickListener {

            onClickShowGraffiti()
            pickGraffiti()
            setUnSelected()
        }

        //set text sticker
        textStickerText.setOnClickListener {
            onClickShowText()

            textStickerText.selectItem(true)
            textGraffiti.selectItem(false)
            textStickerImage.selectItem(false)
        }

        //set image sticker
        textStickerImage.setOnClickListener {
            onClickShowImages()

            textStickerText.selectItem(false)
            textGraffiti.selectItem(false)
            textStickerImage.selectItem(true)

        }
    }

    /**
     * select/unSelect the view
     */
    private fun TextView.selectItem(isSelect : Boolean){
        if(isSelect){
            this.setBackgroundColor(context!!.getColor(R.color.orange))
            this.setTextColor(context!!.getColor(R.color.black))
        }else{
            this.setBackgroundResource(R.color.transparent)
            this.setTextColor(context!!.getColor(R.color.white))
        }
    }

    /**
     * init text selected panel
     */
    private fun initTextSelectPanel() {
        stickerTextViewList = ArrayList()
        textSelectorPanel.setOnTextSelectorListener { textView -> addText(textView!!) }
    }

    /**
     * init image sticker panel
     */
    private fun initImageSelectorPanel() {
        stickerImageViewList = ArrayList()
        imageSelectorPanel.setOnImageSelectedListener { drawable -> addImageView(drawable!!) }
    }

    /**
     *
     */
    private fun showPanel( panel: View )
    {
        when (panel) {
            is TextSelectorPanel -> {
                panel.visibility = View.VISIBLE
                imageSelectorPanel.visibility = View.GONE
                paintSelectorPanel.visibility = View.GONE
                paintView.setPaintEnable(false)
            }
            is PaintSelectorPanel -> {
                paintView.setPaintEnable(true)
                panel.visibility = View.VISIBLE
                textSelectorPanel.visibility = View.GONE
                imageSelectorPanel.visibility = View.GONE

            }
            is ImageSelectorPanel -> {
                panel.visibility = View.VISIBLE
                textSelectorPanel.visibility = View.GONE
                paintSelectorPanel.visibility = View.GONE
                paintView.setPaintEnable(false)

            }
        }
    }

    /**
     * 显示文字贴图面板
     */
    private fun onClickShowText() {
        showPanel(textSelectorPanel)
    }

    /**
     * 显示贴图面板
     */
    private fun onClickShowImages() {
        showPanel(imageSelectorPanel)
    }

    /**
     * 显示绘图面板
     */
    private fun onClickShowGraffiti() {
        showPanel(paintSelectorPanel)
    }

    /**
     * 添加文字贴图
     */
    private fun addText(selectText: StrokedTextView) {
        //可能贴图要用到 保存当前时间并隐藏frameList改动
        setUnSelected()
        val stickerTextView: StickerTextView = View.inflate(
            context,
            R.layout.component_video_editor_graffiti_sticker_text_view,
            null
        ) as StickerTextView
        stickerTextView.setText(selectText.text.toString())
        stickerTextView.setTextColor(selectText.currentTextColor)
        stickerTextView.typeface = selectText.typeface
        stickerTextView.setShadowLayer(
            selectText.shadowRadius,
            selectText.shadowDx,
            selectText.shadowDy,
            selectText.shadowColor
        )
        videoEditorController.addTextView(stickerTextView)
        stickerTextView.setOnStickerOperateListener(StickerOperateListener(stickerTextView))
        textCreateListener.onTextCreateListener(stickerTextView)
        setOnSelected(stickerTextView)
    }

    /**
     * 添加图片贴图
     */
    private fun addImageView(drawable: Drawable) {
        setUnSelected()
        val stickerImageView: StickerImageView = View.inflate(
            context,
            R.layout.component_video_editor_graffiti_sticker_image_view,
            null
        ) as StickerImageView
        stickerImageView.setImageDrawable(drawable)
        videoEditorController.addImageView(stickerImageView)
        stickerImageView.setOnStickerOperateListener(StickerOperateListener(stickerImageView))
        imageCreateListener.onImageCreateListener(stickerImageView)
        setOnSelected(stickerImageView)
    }

    /**
     * 贴图操作监听
     */
    private inner class StickerOperateListener constructor(private val mView: View) :
        OnStickerOperateListener {

        /**
         * 当点击删除贴图
         */
        override fun onDeleteClicked() {
            stickerDeleteListener.onStickerDeleteListener(mView)
            if (mView is StickerTextView) {
                stickerTextViewList.remove(mView)
                videoEditorController.removeTextView(mView as PLTextView)
            } else {
                stickerImageViewList.remove(mView)
                videoEditorController.removeImageView(mView as PLImageView)
            }
            mCurView = null
        }

        /**
         * 当点击贴图编辑
         */
        override fun onEditClicked() {
            if (mView is StickerTextView) {
                createTextDialog(mView)
            }
        }

        /**
         * 当贴图被选中
         */
        override fun onStickerSelected() {
            if(mCurView != mView){
                setUnSelected()
                mCurView = mView
                //我这里手动加上的 demo中为啥不需要呢
                setOnSelected(mCurView!!)
            }
        }

    }

    /**
     * 取消选中
     */
    fun setUnSelected() {
        if (mCurView != null) {
            mCurView!!.isSelected = false
            mCurView = null
        }
    }

    /**
     * 创建文字编辑弹窗并显示
     */
    private fun createTextDialog(textView: PLTextView) {

        val edit = EditText(context)
        edit.setMaxLength(10)
        edit.gravity = Gravity.CENTER
        edit.text = textView.text
        AlertDialog.Builder(context,R.style.CustomDialogTheme)
            .setView(edit)
            .setTitle("请输入文字")
            .setPositiveButton("确定") {
                    dialog, _ ->
                dialog.dismiss()
                (textView as StickerTextView).setText(edit.text.toString())
            }
            .setNegativeButton("取消") { dialog, _ -> dialog.cancel() }
            .create()
            .show()
    }

    /**
     * 显示编辑框
     */
    private fun setOnSelected(view: View) {
        mCurView = view
        mCurView!!.isSelected = true
    }

    /**
     * init range seeker
     */
    private fun initRangeSeeker() {
        rangeSeekBar.setRange(0f, videoEndS)
        rangeSeekBar.setProgress(0f, videoEndS)
        rangeSeekBar.setOnRangeChangedListener(rangeSeekBarListener)
        graffitiTextLeft.text = VideoEditorUtil.convertMsProgressToString(rangeSeekBar.leftSeekBar.progress)
        graffitiTextRight.text = VideoEditorUtil.convertMsProgressToString(rangeSeekBar.rightSeekBar.progress)
    }

    /**
     * init paint selector panel
     */
    private fun initPaintSelectorPanel() {
        paintSelectorPanel.setup()
        paintSelectorPanel.setOnPaintSelectorListener(object :
            PaintSelectorPanel.OnPaintSelectorListener {
            override fun onPaintColorSelected(color: Int) {
                paintView.setPaintColor(color)
            }

            override fun onPaintSizeSelected(size: Int) {
                paintView.setPaintSize(size)
            }

            override fun onPaintUndoSelected() {
                paintView.undo()
            }

            override fun onPaintClearSelected() {
                paintView.clear()
            }
        })
    }

    /**
     * add paint graffiti
     */
    fun addPaint() {
        paintView = PLPaintView(context, previewView.width, previewView.height)
        initPaintSelectorPanel()
        videoEditorController.addPaintView(paintView)
    }

    /**
     * reset the views
     */
    fun resetView() {
        pickGraffiti()
        nestedScrollView.scrollTo(0, 0)
        showPanel(paintSelectorPanel)
    }

    /**
     * set the range bar progress
     */
    fun setRangeBarThumb(left: Float, right: Float) {
        rangeSeekBar.setProgress(left, right)
        graffitiTextLeft.text = VideoEditorUtil.convertMsProgressToString(left)
        graffitiTextRight.text = VideoEditorUtil.convertMsProgressToString(right)
    }

    /**
     * finish button listener
     */
    fun setOnFinishButtonClickListener(itemClickListener: CustomOnFinishedButtonClickListener) {
        this.graffitiCreateListener = itemClickListener
    }

    /**
     * create text sticker
     */
    fun setOnTextCreateListener(itemClickListener: CustomOnTextCreateListener) {
        this.textCreateListener = itemClickListener
    }

    /**
     * create image sticker
     */
    fun setOnImageCreateListener(itemClickListener: CustomOnImageCreateListener) {
        this.imageCreateListener = itemClickListener
    }

    /**
     * delete sticker
     */
    fun setOnStickerDeleteListener(itemClickListener: CustomOnStickerDeleteListener) {
        this.stickerDeleteListener = itemClickListener
    }

    //endregion

    //region interfaces

    interface CustomOnFinishedButtonClickListener {
        fun onGraffitiFinishedListener()
    }

    interface CustomOnTextCreateListener{
        fun onTextCreateListener(text:StickerTextView)
    }

    interface CustomOnImageCreateListener{
        fun onImageCreateListener(image:StickerImageView)
    }

    interface CustomOnStickerDeleteListener{
        fun onStickerDeleteListener(view: View)
    }

    //endregion

}

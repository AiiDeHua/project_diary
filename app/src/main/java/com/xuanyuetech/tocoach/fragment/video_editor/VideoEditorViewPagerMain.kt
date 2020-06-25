package com.xuanyuetech.tocoach.fragment.video_editor

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView

import com.xuanyuetech.tocoach.R
import com.xuanyuetech.tocoach.adapter.VideoEditorRangeBarAdapter
import com.xuanyuetech.tocoach.adapter.setUpWith
import com.xuanyuetech.tocoach.util.videoeditor.RangeBar
import com.xuanyuetech.tocoach.fragment.BasicFragment

/**
 * view pager main to hold all rangeBar
 */
class VideoEditorViewPagerMain : BasicFragment() {

    //region properties

    lateinit var rangeBarList:ArrayList<RangeBar>
    lateinit var videoEditorRangeBarAdapter:VideoEditorRangeBarAdapter

    private lateinit var recyclerView: RecyclerView

    private lateinit var itemEditClickListener:CustomOnItemEditClickListener
    private lateinit var itemDeleteListener:CustomOnItemDeleteClickListener

    private val handler = Handler()

    //endregion

    //region onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_video_editor_view_pager_main, container, false)

        initVideoFrameList()

        return mView
    }

    /**
     * init video frame list
     */
    private fun initVideoFrameList(){
        rangeBarList = ArrayList()
        //recyclerview related
        recyclerView = mView.findViewById(R.id.video_editor_recyclerview)
        videoEditorRangeBarAdapter = VideoEditorRangeBarAdapter(rangeBarList)
        videoEditorRangeBarAdapter.setOnDeleteItemClickListener(object :
            VideoEditorRangeBarAdapter.CustomOnDeleteItemClickListener {
            override fun onItemDeleteClickListener(deleteItemPosition: Int) {
                itemDeleteListener.onDeleteItemListener(deleteItemPosition)
            }
        })
        videoEditorRangeBarAdapter.setOnEditItemClickListener(
            object: VideoEditorRangeBarAdapter.CustomOnEditItemClickListener{
                override fun onItemEditClickListener(editItemClickedPosition: Int) {
                    itemEditClickListener.onItemEditClickListener(editItemClickedPosition)
                }
            }
        )
        videoEditorRangeBarAdapter.setOnRenameItemClickListener(object : VideoEditorRangeBarAdapter.CustomOnRenameItemListener{
            override fun onRenameItemListener(renameItemPosition: Int) {
                showRenameDialog(renameItemPosition)
            }

        })

        recyclerView.setUpWith(videoEditorRangeBarAdapter)


    }

    /**
     * show rangeBar rename dialog
     */
    fun showRenameDialog(position: Int) {
        val edit = EditText(context)
        edit.gravity = Gravity.CENTER
        edit.setText(rangeBarList[position].labelText)
        val dialogBuilder = AlertDialog.Builder(context!!, R.style.CustomDialogTheme)
            .setTitle("重命名特效区段")
            .setView(edit)
            .setNegativeButton("取消") { dialog, _ -> dialog.cancel() }
            .setPositiveButton("确定") { dialog, _ ->
                dialog.dismiss()
                rangeBarList[position].labelText = edit.text.toString()
                videoEditorRangeBarAdapter.notifyItemChanged(position)
            }
            .create()
        dialogBuilder.show()
    }

    /**
     * notice thumb change will refresh the position item view
     */
    fun notifyRangBarThumbChange(index:Int){
        handler.post {
            videoEditorRangeBarAdapter.notifyItemChanged(index)
        }
    }

    /**
     * rangeBar delete
     */
    fun notifyRangBarDelete(){
        handler.post {
            videoEditorRangeBarAdapter.notifyDataSetChanged()
        }
    }


    fun setOnItemEditClickListener(itemEditClickListener: CustomOnItemEditClickListener) {
        this.itemEditClickListener = itemEditClickListener
    }
    fun setOnItemDeleteListener(itemDeleteListener: CustomOnItemDeleteClickListener) {
        this.itemDeleteListener = itemDeleteListener
    }

    //endregion

    //region interfaces

    interface CustomOnItemEditClickListener {
        fun onItemEditClickListener(clickedPosition: Int)
    }
    interface CustomOnItemDeleteClickListener {
        fun onDeleteItemListener(deletedPosition: Int)
    }
    //endregion

}

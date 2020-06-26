package com.yiqisport.yiqiapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yiqisport.yiqiapp.R
import com.yiqisport.yiqiapp.util.videoeditor.RangeBar
import com.jaygoo.widget.RangeSeekBar
import kotlin.collections.ArrayList

/**
 * The adapter for the video editor rangeBar list usage
 */
class VideoEditorRangeBarAdapter(var rangeBarList:ArrayList<RangeBar>) : RecyclerView.Adapter<VideoEditorRangeBarAdapter.ViewHolder>() {

    private var deleteItemClickListener: CustomOnDeleteItemClickListener? = null
    private var editItemClickListener: CustomOnEditItemClickListener? = null
    private var renameItemListener: CustomOnRenameItemListener? = null
    private var isExpand = false

    /**
     * inflate item view
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cardview_video_editor_view_pager_main, parent, false)
        )
    }

    /**
     * bind view
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //properties
        val item = rangeBarList[position]
        holder.expandContainer.visibility = View.GONE
        holder.expandArrow.setImageResource(R.drawable.ic_keyboard_arrow_right_white_24dp)
        holder.rangeSeekBar.isEnabled = false
        holder.labelTextView.text = item.labelText

        //rangeBar's hashcode
        rangeBarList[position].rangeSeekBarHashCode = holder.rangeSeekBar.hashCode()

        //set bar colors
        when (item.controllerType) {
            1 -> {
                holder.rangeSeekBar.progressColor = ResourcesCompat.getColor(holder.itemView.resources, R.color.color_videoEditor_trimmer, null)
            }
            2 -> {
                holder.rangeSeekBar.progressColor = ResourcesCompat.getColor(holder.itemView.resources, R.color.color_videoEditor_speed, null)
            }
            3 -> {
                holder.rangeSeekBar.progressColor = ResourcesCompat.getColor(holder.itemView.resources, R.color.color_videoEditor_graffiti, null)
            }
        }

        //set total range and progress
        holder.rangeSeekBar.setRange(item.startRange,item.endRange)
        holder.rangeSeekBar.setProgress(item.startThumb,item.endThumb)
        //bind tools listeners
        holder.rangeSeekBar.setOnRangeChangedListener(item.rangeChangedListener)
        holder.deleteBtn.setOnClickListener{
            deleteItemClickListener!!.onItemDeleteClickListener(position)
        }
        holder.editBtn.setOnClickListener{
            editItemClickListener!!.onItemEditClickListener(position)
        }
        holder.labelTextView.setOnClickListener {
            renameItemListener!!.onRenameItemListener(position)
        }
        holder.expandArrowContainer.setOnClickListener{
            if(!isExpand){
                holder.expandContainer.visibility = View.VISIBLE
                holder.expandArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp)
                holder.rangeSeekBar.isEnabled = true
                isExpand = true
            }else{
                holder.expandContainer.visibility = View.GONE
                holder.expandArrow.setImageResource(R.drawable.ic_keyboard_arrow_right_white_24dp)
                holder.rangeSeekBar.isEnabled = false
                isExpand = false
            }
        }
//        holder.expandArrow.setOnClickListener{
//
//        }

    }

    /**
     * add context
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var rangeSeekBar: RangeSeekBar = view.findViewById(R.id.video_editor_card_view_range_seek_bar)
        var deleteBtn: ImageButton = view.findViewById(R.id.video_editor_card_view_btn_delete)
        var editBtn: ImageButton = view.findViewById(R.id.video_editor_card_view_btn_edit)
        var expandContainer: LinearLayoutCompat = view.findViewById(R.id.video_editor_card_view_expand_container)
        var expandArrow: ImageButton = view.findViewById(R.id.video_editor_card_view_right_arrow)
        var expandArrowContainer : ConstraintLayout = view.findViewById(R.id.video_editor_card_view_right_arrow_container)
        var labelTextView: TextView = view.findViewById(R.id.video_editor_card_view_label)
    }

    /**
     * add item listener
     */
    fun setOnDeleteItemClickListener(itemClickListener: CustomOnDeleteItemClickListener) {
        this.deleteItemClickListener = itemClickListener
    }
    fun setOnEditItemClickListener(itemClickListener: CustomOnEditItemClickListener) {
        this.editItemClickListener = itemClickListener
    }

    fun setOnRenameItemClickListener(itemClickListener: CustomOnRenameItemListener) {
        this.renameItemListener = itemClickListener
    }

    /**
     * delete the bar
     */
    interface CustomOnDeleteItemClickListener {
        fun onItemDeleteClickListener(deleteItemPosition: Int)
    }

    /**
     * edit the bar
     */
    interface CustomOnEditItemClickListener {
        fun onItemEditClickListener(editItemClickedPosition: Int)
    }

    /**
     * rename the bar
     */
    interface CustomOnRenameItemListener {
        fun onRenameItemListener(renameItemPosition: Int)
    }

    /**
     *
     */
    override fun getItemCount(): Int {
        return rangeBarList.size
    }

}

/**
 * infix function to set up the RecyclerView with adapter
 */
infix fun RecyclerView.setUpWith(videoEditorRangeBarAdapter: VideoEditorRangeBarAdapter) {
    isNestedScrollingEnabled = true
    layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
    adapter = videoEditorRangeBarAdapter
}



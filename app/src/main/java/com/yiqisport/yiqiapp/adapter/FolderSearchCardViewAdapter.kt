package com.yiqisport.yiqiapp.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yiqisport.yiqiapp.R
import com.yiqisport.yiqiapp.data.FolderArchiveObject

/**
 * Adapter for folder archive cardView
 */
class FolderArchiveCardViewAdapter(private var folderArchiveObjectList : List<FolderArchiveObject>) : RecyclerView.Adapter<FolderArchiveCardViewAdapter.ViewHolder>(){

    private var itemClickListener: CostomOnItemClickListener? = null

    /**
     * inflate item view
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_archive_list_object, parent, false)
        )
    }

    /**
     * item count
     */
    override fun getItemCount(): Int {
        return folderArchiveObjectList.size
    }

    /**
     * bind view
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = folderArchiveObjectList[position]

        if(item.getArchiveImagePath().isNotBlank()){
            holder.imageView.setImageURI(Uri.parse(item.getArchiveImagePath()))
        }else{
            holder.imageView.setImageResource(R.drawable.diary_cover)
        }

        holder.textViewTitle.text = item.getArchiveTitle()
        holder.textViewSubtitle.text = item.getArchiveSubtitle()
        holder.textViewFolderName.text = item.getArchiveFolderName()

        holder.itemView.setOnClickListener {
            itemClickListener!!.onItemClickListener(position)
        }
    }

    /**
     * add context
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView = view.findViewById(R.id.cardView_image)
        var textViewTitle: TextView = view.findViewById(R.id.cardView_title)
        var textViewSubtitle: TextView = view.findViewById(R.id.cardView_subtitle)
        var textViewFolderName: TextView = view.findViewById(R.id.cardView_folderName)
    }

    /**
     * add item listener
     */
    fun setOnItemClickListener(itemClickListener: CostomOnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    /**
     * on item click listener interface
     */
    interface CostomOnItemClickListener {
        fun onItemClickListener(position: Int)
    }

}

/**
 * infix function to set up the RecyclerView with adapter
 */
infix fun RecyclerView.setUpWith(folderArchiveCardViewAdapter : FolderArchiveCardViewAdapter) {
    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    adapter = folderArchiveCardViewAdapter
}
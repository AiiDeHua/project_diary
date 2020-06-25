package com.xuanyuetech.tocoach.adapter

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.xuanyuetech.tocoach.R
import com.xuanyuetech.tocoach.data.Folder


/**
 * Adapter for folder card view
 */
class FolderCardViewAdapter(private val listFolders: ArrayList<Folder>) : RecyclerView.Adapter<FolderCardViewAdapter.ViewHolder>(){

    //private val items = DataSource.items
    private var itemClickListener: CustomOnItemClickListener? = null

    /**
     * inflate item view
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.cardview_folder,
                parent,
                false
            )
        )
    }

    /**
     * item count
     */
    override fun getItemCount(): Int {
        return listFolders.size
    }

    /**
     * bind view
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        
        val imgUriStr = listFolders[position].profileImagePath

        holder.imageViewCardViewFolder.setImageResource(R.drawable.profile_default)
        if(imgUriStr.isNotBlank()) holder.imageViewCardViewFolder.setImageURI(Uri.parse(imgUriStr))

        holder.textViewFolderCardViewName.text = listFolders[position].name
        holder.textViewFolderCardViewNote.text = listFolders[position].notes

        holder.itemView.setOnClickListener {
            itemClickListener!!.onItemClickListener(position)
        }
    }

    /**
     * add context
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var imageViewCardViewFolder:ImageView = view.findViewById<View>(R.id.folder_frag_cardView_img) as ImageView
        var textViewFolderCardViewName:TextView = view.findViewById<View>(R.id.folder_frag_cardView_name) as TextView
        var textViewFolderCardViewNote:TextView = view.findViewById<View>(R.id.folder_frag_cardView_detail) as TextView

    }

    /**
     * add item listener
     */
    fun setOnItemClickListener(itemClickListener: CustomOnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    /**
     * on item click listener interface
     */
    interface CustomOnItemClickListener {
        fun onItemClickListener(position: Int)
    }

}

/**
 * infix function to set up the RecyclerView with adapter
 */
infix fun RecyclerView.setUpWith(folderCardViewAdapter: FolderCardViewAdapter) {
    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    adapter = folderCardViewAdapter
}

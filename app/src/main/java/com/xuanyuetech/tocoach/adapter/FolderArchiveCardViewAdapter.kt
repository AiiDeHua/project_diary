package com.xuanyuetech.tocoach.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xuanyuetech.tocoach.R
import com.xuanyuetech.tocoach.data.FolderArchiveObject

/**
 * Adapter for folder archive cardView
 */
class FolderListCardViewAdapter(private var folderArchiveObjectList : List<FolderArchiveObject>) : RecyclerView.Adapter<FolderListCardViewAdapter.ViewHolder>(),
    Filterable {

    private var itemClickListener: CustomOnItemClickListener? = null
    var filterFolderArchiveObjectList:List<FolderArchiveObject> = folderArchiveObjectList

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
        return filterFolderArchiveObjectList.size
    }

    /**
     * bind view
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filterFolderArchiveObjectList[position]

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
    fun setOnItemClickListener(itemClickListener: CustomOnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    /**
     * on item click listener interface
     */
    interface CustomOnItemClickListener {
        fun onItemClickListener(position: Int)
    }

    override fun getFilter(): Filter {
        return object :Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val charString: String = constraint.toString()

                filterFolderArchiveObjectList =
                    if (charString.isEmpty()) {
                        //没有过滤的内容，则使用源数据
                        folderArchiveObjectList
                    } else {
                        val filteredList: ArrayList<FolderArchiveObject> = ArrayList()
                        for (str in folderArchiveObjectList) {
                            if (str.getArchiveTitle().contains(charString)) {
                                filteredList.add(str)
                            }
                        }
                        filteredList
                    }

                val filterResults = FilterResults()
                filterResults.values = filterFolderArchiveObjectList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterFolderArchiveObjectList = results!!.values as ArrayList<FolderArchiveObject>
                notifyDataSetChanged()
            }
        }
    }

}

/**
 * infix function to set up the RecyclerView with adapter
 */
infix fun RecyclerView.setUpWith(folderListCardViewAdapter: FolderListCardViewAdapter) {
    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    adapter = folderListCardViewAdapter
}
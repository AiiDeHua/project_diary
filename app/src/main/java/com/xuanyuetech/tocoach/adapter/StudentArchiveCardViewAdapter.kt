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
import com.xuanyuetech.tocoach.data.StudentArchiveObject

/**
 * Adapter for student archive cardView
 */
class StudentListCardViewAdapter(private var studentArchiveObjectList : List<StudentArchiveObject>) : RecyclerView.Adapter<StudentListCardViewAdapter.ViewHolder>(),
    Filterable {

    private var itemClickListener: CustomOnItemClickListener? = null
    var filterStudentArchiveObjectList:List<StudentArchiveObject> = studentArchiveObjectList

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
        return filterStudentArchiveObjectList.size
    }

    /**
     * bind view
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filterStudentArchiveObjectList[position]

        if(item.getArchiveImagePath().isNotBlank()){
            holder.imageView.setImageURI(Uri.parse(item.getArchiveImagePath()))
        }else{
            holder.imageView.setImageResource(R.drawable.diary_cover)
        }

        holder.textViewTitle.text = item.getArchiveTitle()
        holder.textViewSubtitle.text = item.getArchiveSubtitle()
        holder.textViewStudentName.text = item.getArchiveStudentName()

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
        var textViewStudentName: TextView = view.findViewById(R.id.cardView_studentName)
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

                filterStudentArchiveObjectList =
                    if (charString.isEmpty()) {
                        //没有过滤的内容，则使用源数据
                        studentArchiveObjectList
                    } else {
                        val filteredList: ArrayList<StudentArchiveObject> = ArrayList()
                        for (str in studentArchiveObjectList) {
                            if (str.getArchiveTitle().contains(charString)) {
                                filteredList.add(str)
                            }
                        }
                        filteredList
                    }

                val filterResults = FilterResults()
                filterResults.values = filterStudentArchiveObjectList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterStudentArchiveObjectList = results!!.values as ArrayList<StudentArchiveObject>
                notifyDataSetChanged()
            }
        }
    }

}

/**
 * infix function to set up the RecyclerView with adapter
 */
infix fun RecyclerView.setUpWith(studentListCardViewAdapter: StudentListCardViewAdapter) {
    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    adapter = studentListCardViewAdapter
}
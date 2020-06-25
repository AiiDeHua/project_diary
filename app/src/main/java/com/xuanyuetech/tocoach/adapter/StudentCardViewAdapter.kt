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
import com.xuanyuetech.tocoach.data.Student


/**
 * Adapter for student card view
 */
class StudentCardViewAdapter(private val listStudents: ArrayList<Student>) : RecyclerView.Adapter<StudentCardViewAdapter.ViewHolder>(){

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
        return listStudents.size
    }

    /**
     * bind view
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        
        val imgUriStr = listStudents[position].profileImagePath

        holder.imageViewCardViewStudent.setImageResource(R.drawable.profile_default)
        if(imgUriStr.isNotBlank()) holder.imageViewCardViewStudent.setImageURI(Uri.parse(imgUriStr))

        holder.textViewStudentCardViewName.text = listStudents[position].name
        holder.textViewStudentCardViewNote.text = listStudents[position].notes

        holder.itemView.setOnClickListener {
            itemClickListener!!.onItemClickListener(position)
        }
    }

    /**
     * add context
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var imageViewCardViewStudent:ImageView = view.findViewById<View>(R.id.folder_frag_cardView_img) as ImageView
        var textViewStudentCardViewName:TextView = view.findViewById<View>(R.id.folder_frag_cardView_name) as TextView
        var textViewStudentCardViewNote:TextView = view.findViewById<View>(R.id.folder_frag_cardView_detail) as TextView

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
infix fun RecyclerView.setUpWith(studentCardViewAdapter: StudentCardViewAdapter) {
    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    adapter = studentCardViewAdapter
}

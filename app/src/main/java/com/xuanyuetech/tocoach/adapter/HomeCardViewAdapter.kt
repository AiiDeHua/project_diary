package com.xuanyuetech.tocoach.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xuanyuetech.tocoach.R
import com.xuanyuetech.tocoach.data.DatabaseHelper
import com.xuanyuetech.tocoach.data.HomeEvent

class HomeCardViewAdapter(private val homeEvents: List<HomeEvent>, val context : Context) : RecyclerView.Adapter<HomeCardViewAdapter.ViewHolder>(){

    private var itemClickListener: costumOnItemClickListener? = null

    /**
     * inflate item view
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.cardview_home_event,
                parent,
                false
            )
        )
    }

    /**
     * item count
     */
    override fun getItemCount(): Int {
        return homeEvents.size
    }

    /**
     * bind view
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titleView.text = homeEvents[position].title
        holder.notesView.text = homeEvents[position].notes

        if( homeEvents[position].type == HomeEvent().videoType()){
            holder.imageView.setImageURI(Uri.parse(homeEvents[position].imagePath))
        }else{
            holder.imageView.setImageResource(R.drawable.diary_cover)
        }

        holder.studentView.text = DatabaseHelper(context).findStudentById(homeEvents[position].studentId.toInt())!!.name

        holder.itemView.setOnClickListener {
            itemClickListener!!.onItemClickListener(position)
        }

    }

    /**
     * add context
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.homeEvent_imageView)
        val titleView: TextView = view.findViewById(R.id.homeEvent_title)
        val notesView: TextView = view.findViewById(R.id.homeEvent_notes)
        val studentView: TextView = view.findViewById(R.id.homeEvent_folder)
    }

    /**
     * add item listener
     */
    fun setOnItemClickListener(itemClickListener: costumOnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    /**
     * on item click listener interface
     */
    interface costumOnItemClickListener {
        fun onItemClickListener(position: Int)
    }

}

/**
 * infix function to set up the RecyclerView with adapter
 */
infix fun RecyclerView.setUpWith(homeCardViewAdapter: HomeCardViewAdapter) {
    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    adapter = homeCardViewAdapter
}
package com.xuanyuetech.tocoach.fragment.students

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xuanyuetech.tocoach.R
import com.xuanyuetech.tocoach.adapter.StudentCardViewAdapter
import com.xuanyuetech.tocoach.adapter.setUpWith
import com.xuanyuetech.tocoach.data.DatabaseHelper
import com.xuanyuetech.tocoach.data.Student
import com.xuanyuetech.tocoach.fragment.BasicFragment
import com.xuanyuetech.tocoach.util.ActivityUtil

/**
 * students fragment
 */
class StudentsFragment : BasicFragment() {

    //region properties

    private lateinit var listStudents: ArrayList<Student>
    private var studentCardViewAdapter: StudentCardViewAdapter? = null
    private lateinit var databaseHelper: DatabaseHelper
    private val handler = Handler()

    private lateinit var sizeLabelView : TextView

    //endregion

    //region onCreateView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mView =  inflater.inflate(R.layout.fragment_students, container, false)

        databaseHelper = DatabaseHelper(activity!!)

        initView()

        initData()

        refreshStudents()

        return mView
    }

    //endregion

    //region override

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_students, menu)
    }

    override fun onOptionsItemSelected(item:MenuItem) :Boolean {
        if(isValidClick()) {
            when(item.itemId){
                R.id.menuItem_home_add_new_student->{
                    ActivityUtil.startCreateNewStudent(this)
                }
                R.id.menuItem_search -> {
                    ActivityUtil.startSearch(this)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun customizeToolbar(){
        setHasOptionsMenu(true)
    }

    //endregion

    //region fun

    /**
     *
     */
    private fun initData() {
        listStudents = ArrayList()
    }

    private fun initView(){
        sizeLabelView = mView.findViewById(R.id.textView_student_label)
    }

    /**
     * Setup the CardView
     */
    private fun bindAdapter(){
        studentCardViewAdapter = StudentCardViewAdapter(listStudents)
        studentCardViewAdapter!!.setOnItemClickListener(
            object : StudentCardViewAdapter.CustomOnItemClickListener {
                override fun onItemClickListener(position: Int) {
                    if(isValidClick()){
                        ActivityUtil.startStudent(this@StudentsFragment, listStudents[position].id)
                    }
                }
            })

        //set up the adapter for the recycler
        val recyclerView : RecyclerView = mView.findViewById(R.id.recycler_view)

        recyclerView.setUpWith(studentCardViewAdapter!!)
    }

    /**
     * refresh all student list
     */
    fun refreshStudents(){
        handler.post{
            if(studentCardViewAdapter == null) bindAdapter()
            listStudents.clear()
            listStudents.addAll(databaseHelper.getAllStudent().sortedBy { it.name })
            studentCardViewAdapter!!.notifyDataSetChanged()
            refreshStudentNumTextView()
        }
    }

    /**
     * refresh student number text view
     */
    @SuppressLint("SetTextI18n")
    private fun refreshStudentNumTextView(){
        if(listStudents.size <= 0 ) sizeLabelView.text = "赶紧点击右上角\"+\"添加新的学员吧！"
        else{sizeLabelView.text = "已有 ${listStudents.size} 位学员"}
    }


    //endregion

}
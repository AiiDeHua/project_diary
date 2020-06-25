package com.xuanyuetech.tocoach.fragment.students.student

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import com.xuanyuetech.tocoach.R
import androidx.appcompat.app.AlertDialog
import com.xuanyuetech.tocoach.data.DatabaseHelper
import com.xuanyuetech.tocoach.data.DataHelper
import com.xuanyuetech.tocoach.data.GlobalVariable
import com.xuanyuetech.tocoach.data.Student
import com.xuanyuetech.tocoach.fragment.BasicFragment
import com.xuanyuetech.tocoach.util.ActivityUtil
import de.hdodenhof.circleimageview.CircleImageView

/**
 * student profile
 */
class StudentProfileFragment : BasicFragment() {

    //region properties
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var student:Student

    private lateinit var studentNameView : TextView
    private lateinit var studentNotesView : TextView
    private lateinit var studentIdView : TextView
    private lateinit var studentImageView : CircleImageView

    //endregion

    //region onCreateView

    /**
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mView = inflater.inflate(R.layout.fragment_student_profile, container, false)

        initViews()

        initData()

        setHasOptionsMenu(true)

        return mView
    }

    //endregion

    //region override

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_student_profile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) :Boolean {
        when(item.itemId){
            R.id.menuItem_studentProfile_edit ->{
                //edit the profile
                ActivityUtil.startStudentProfileEdit(this, student.id)
            }
            R.id.menuItem_studentProfile_delete ->{
                deleteStudent()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun customizeToolbar() {
        super.customizeToolbar()
        setUpToolBarTitle("学员信息")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        refreshView()
        activity!!.setResult(GlobalVariable().RESULT_NEED_REFRESH_STUDENT_LIST_OR_HOME_EVENT_OR_ARCHIVE_LIST)
    }

    //endregion

    //endregion fun

    /**
     * init views
     */
    private fun initViews(){
        studentNameView = mView.findViewById(R.id.textView_name)
        studentNotesView = mView.findViewById(R.id.textView_notes)
        studentIdView = mView.findViewById(R.id.textView_id)
        studentImageView = mView.findViewById(R.id.imageView_studentImage)
    }

    /**
     * init data
     */
    @SuppressLint("SetTextI18n")
    private fun initData(){
        databaseHelper = DatabaseHelper(activity!!)
        student = databaseHelper.findStudentById(ActivityUtil.getStudentIdFromIntent(activity!!.intent))!!
        studentNameView.text = student.name
        studentNotesView.text = student.notes

        if(student.profileImagePath.isNotBlank()) studentImageView.setImageURI(Uri.parse(student.profileImagePath))
        else studentImageView.setImageResource(R.drawable.profile_default)

        studentIdView.text = "student${student.id}"
    }

    /**
     *
     */
    @SuppressLint("SetTextI18n")
    private fun refreshView(){
        student = databaseHelper.findStudentById(student.id)!!
        studentNameView.text = student.name
        studentNotesView.text = student.notes

        if(student.profileImagePath.isNotBlank()) studentImageView.setImageURI(Uri.parse(student.profileImagePath))
        else studentImageView.setImageResource(R.drawable.profile_default)

        studentIdView.text = "student${student.id}"
    }

    /**
     * delete student action
     */
    private fun deleteStudent(){
        val dialogBuilder = AlertDialog.Builder(activity!!,R.style.CustomDialogTheme)
            .setCancelable(true)
            .setTitle("警告")
            .setMessage("你确定要删除当前学员吗?")
            .setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("确定"){
                dialog, _ ->dialog.dismiss()

                DataHelper().deleteStudent(student, databaseHelper)
                Toast.makeText(context,"成功删除!",Toast.LENGTH_SHORT).show()
                activity!!.setResult(globalVariable.RESULT_DELETE_STUDENT)
                activity!!.finish()
            }

        dialogBuilder.show()
    }


    //endregion

}
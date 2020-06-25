package com.xuanyuetech.tocoach.fragment.students.new_student

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.github.dhaval2404.imagepicker.ImagePicker
import com.xuanyuetech.tocoach.R
import com.xuanyuetech.tocoach.data.DataHelper
import com.xuanyuetech.tocoach.data.DatabaseHelper
import com.xuanyuetech.tocoach.data.GlobalVariable
import com.xuanyuetech.tocoach.data.Student
import com.xuanyuetech.tocoach.fragment.BasicFragment
import com.xuanyuetech.tocoach.util.*
import de.hdodenhof.circleimageview.CircleImageView

/**
 * new student fragment
 */
class NewStudentFragment : BasicFragment() {

    //region properties
    private lateinit var databaseHelper: DatabaseHelper

    private var imageUri : Uri? = null
    private lateinit var textInputEditTextName : EditText
    private lateinit var textInputEditTextNotes : EditText
    private lateinit var imageView : CircleImageView

    private val handler = Handler()

    //endregion

    //region onCreateView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_new_student, container, false)

        initView()

        databaseHelper = DatabaseHelper(activity!!)

        setUpToolBarTitle(resources.getString(R.string.newStudentActivity_newStudentFrag_title))

        initData()

        initListeners()

        return mView
    }
    //endregion

    //region fun

    /**
     * init views
     */
    private fun initView(){
        textInputEditTextName = mView.findViewById(R.id.editText_student_name)
        textInputEditTextNotes = mView.findViewById(R.id.editText_student_note)
        textInputEditTextName.setMaxLength(Student().maxNameLength)
        imageView = mView.findViewById(R.id.imageView_studentImage)
    }

    /**
     * init data
     */
    private fun initData(){
        mView.findViewById<EditText>(R.id.editText_student_note).setText("新的视频夹")
    }

    /**
     * bind listeners
     */
    private fun initListeners() {

        //confirm button
        val buttonAdd = mView.findViewById<Button>(R.id.btn_add_new_student)
        buttonAdd.setOnClickListener{

            val student = createStudent()
            if(!student.name.isBlank()){
                DataHelper().addStudent(student, databaseHelper)
                Toast.makeText(context,"成功创建新的学员!",Toast.LENGTH_SHORT).show()
                activity?.setResult(GlobalVariable().RESULT_NEED_REFRESH_STUDENT_LIST_OR_HOME_EVENT_OR_ARCHIVE_LIST)
                activity?.finish()
            }else{
                //invalid name
                MessageHelper.noticeDialog(context!!, "请输入名字")
            }
        }

        //student image view
        val studentImageView = mView.findViewById<CircleImageView>(R.id.imageView_studentImage)
        studentImageView.setOnClickListener{
            if(isValidClick()){
                ImagePicker.with(this)
                    .crop(1f,1f)	    			//Crop image(Optional), Check Customization for more option
                    .compress(256)			//Final image size will be less than 0.512 MB(Optional)
                    .maxResultSize(150, 150)	//Final image resolution will be less than 300 x 300(Optional)
                    .saveDir(FilePathHelper(context!!).appTempPath)
                    .start()
            }
        }
    }

    /**
     * create the student
     */
    private fun createStudent() : Student{
        val studentName = textInputEditTextName.text.toString()
        val studentNotes = textInputEditTextNotes.text.toString()
        val studentId = databaseHelper.getLastStudentIdAuto()

        val studentImagePath = ImageUtil.saveStudentProfileImage(imageUri, studentId, context!!)

        return Student(
            studentId,
            studentName,
            studentNotes,
            studentImagePath,
            backgroundImagePath = ""
        )
    }

    //endregion

    //region override

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            imageUri = data!!.data
            imageView.setImageURI(imageUri)
        }
        else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(context!!, "缺少授权！", Toast.LENGTH_SHORT).show()
        }
    }

    //endregion

}

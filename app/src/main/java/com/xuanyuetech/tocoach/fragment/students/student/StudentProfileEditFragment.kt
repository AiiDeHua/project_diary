package com.xuanyuetech.tocoach.fragment.students.student

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import com.xuanyuetech.tocoach.R
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import com.github.dhaval2404.imagepicker.ImagePicker
import com.xuanyuetech.tocoach.data.DatabaseHelper
import com.xuanyuetech.tocoach.data.DataHelper
import com.xuanyuetech.tocoach.data.Student
import com.xuanyuetech.tocoach.fragment.BasicFragment
import com.xuanyuetech.tocoach.util.*
import com.xuanyuetech.tocoach.util.setMaxLength
import de.hdodenhof.circleimageview.CircleImageView

/**
 * student profile fragment
 */
class StudentProfileEditFragment : BasicFragment() {

    //region properties
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var student : Student

    private lateinit var profileView : CircleImageView
    private lateinit var nameView : TextView

    private lateinit var itemProfileImage : ConstraintLayout
    private lateinit var itemName : ConstraintLayout
    private lateinit var itemNotes : ConstraintLayout
    private lateinit var itemBackgroundPic : ConstraintLayout

    private var galleryIntentFor = GalleryIntentFor.Profile

    enum class GalleryIntentFor {
        Profile, Background
    }

    private var updatedName = ""
    private var updatedNotes = ""

    //endregion

    //region onCreateView

    /**
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mView = inflater.inflate(R.layout.fragment_student_profile_edit, container, false)

        databaseHelper = DatabaseHelper(activity!!)

        initViews()

        initData()

        bindListener()

        setHasOptionsMenu(true)

        return mView
    }

    //endregion

    //endregion fun

    /**
     * init views
     */
    private fun initViews(){
        profileView = mView.findViewById(R.id.imageView_studentImage)
        nameView = mView.findViewById(R.id.textView_name)

        itemProfileImage = mView.findViewById(R.id.item_edit_image)
        itemName = mView.findViewById(R.id.item_edit_name)
        itemNotes = mView.findViewById(R.id.item_edit_notes)
        itemBackgroundPic = mView.findViewById(R.id.item_edit_background_image)
    }

    /**
     * init data
     */
    private fun initData(){
        student = DatabaseHelper(activity!!).findStudentById(ActivityUtil.getStudentIdFromIntent(activity!!.intent))!!
        nameView.text = student.name

        if(student.profileImagePath.isNotBlank()) profileView.setImageURI(Uri.parse(student.profileImagePath))
        else profileView.setImageResource(R.drawable.profile_default)

    }

    /**
     * bind listener
     */
    private fun bindListener(){

        //profile image
        itemProfileImage.setOnClickListener {
            if(isValidClick()){
                galleryIntentFor = GalleryIntentFor.Profile
                ImagePicker.with(this)
                    .crop(1f,1f)	    			//Crop image(Optional), Check Customization for more option
                    .compress(256)			//Final image size will be less than 0.512 MB(Optional)
                    .saveDir(FilePathHelper(context!!).appTempPath)
                    .start()
            }
        }

        //background image
        itemBackgroundPic.setOnClickListener{
            if(isValidClick()){
                galleryIntentFor = GalleryIntentFor.Background
                ImagePicker.with(this)
                    .crop(16f,9f)	    			//Crop image(Optional), Check Customization for more option
                    .compress(512)			//Final image size will be less than 0.512 MB(Optional)
                    .saveDir(FilePathHelper(context!!).appTempPath)
                    .start()
            }
        }

        //name
        itemName.setOnClickListener{
            if(isValidClick()) {
                val builder = buildDialogBuilder()
                builder
                    .setPositiveButton("确定") { dialogInterface: DialogInterface, _: Int ->

                        if(updatedName.isBlank()) MessageHelper.noticeDialog(context!!, "姓名不能为空")
                        else{
                            nameView.text = updatedName
                            student.name = updatedName
                            DataHelper().updateStudent(student, databaseHelper)
                            dialogInterface.dismiss()
                        }
                    }
                    .setNegativeButton("取消") { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                    }

                val dialog = builder.create()
                dialog.show()
                dialog.setCanceledOnTouchOutside(false)
                dialog.findViewById<TextView>(R.id.title).text = "姓名"

                val content = dialog.findViewById<EditText>(R.id.content)

                content.setText(student.name)
                content.setMaxLength(Student().maxNameLength)
                content.addTextChangedListener {
                    updatedName = dialog.findViewById<EditText>(R.id.content).text.toString()
                }
            }
        }

        //notes
        itemNotes.setOnClickListener{
            if(isValidClick()) {
                val builder = buildDialogBuilder()
                builder
                    .setPositiveButton("确定") { dialogInterface: DialogInterface, _: Int ->
                        student.notes = updatedNotes
                        DataHelper().updateStudent(student, databaseHelper)
                        dialogInterface.dismiss()
                    }
                    .setNegativeButton("取消") { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                    }

                val dialog = builder.create()
                dialog.show()
                dialog.setCanceledOnTouchOutside(false)
                dialog.findViewById<TextView>(R.id.title).text = "备注"

                val content = dialog.findViewById<EditText>(R.id.content)
                content.findViewById<EditText>(R.id.content).setText(student.notes)
                content.addTextChangedListener {
                    updatedNotes = dialog.findViewById<EditText>(R.id.content).text.toString()
                }
            }
        }
    }

    /**
     * each edit dialog builder
     */
    @SuppressLint("InflateParams")
    private fun buildDialogBuilder() : AlertDialog.Builder{
        val inflater = activity!!.layoutInflater
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity, R.style.CustomDialogTheme)
        val view: View = inflater.inflate(R.layout.dialog_student_profile_edit_item, null)
        builder.setView(view)

        return builder
    }

    //region override

    override fun customizeToolbar() {
        super.customizeToolbar()
        val toolbar = mView.findViewById<Toolbar>(R.id.toolbar)
        setUpToolBarTitle("信息编辑")
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val uri = data!!.data
            when (galleryIntentFor) {

                GalleryIntentFor.Profile -> {
                    //get image for profile
                    student.profileImagePath =
                        ImageUtil.saveStudentProfileImage(uri, student.id, context!!)
                    profileView.setImageURI(uri)
                    DataHelper().updateStudent(student, databaseHelper)
                }
                GalleryIntentFor.Background -> {
                    //get image for background
                    student.backgroundImagePath =
                        ImageUtil.saveStudentBackgroundImage(uri, student.id, context!!)
                    DataHelper().updateStudent(student, databaseHelper)
                }
            }
        }   else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(context, "缺少授权", Toast.LENGTH_SHORT).show()
        }
    }

    //endregion


}
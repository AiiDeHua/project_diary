package com.yiqisport.yiqiapp.fragment.folders.folder

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import com.yiqisport.yiqiapp.R
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import com.github.dhaval2404.imagepicker.ImagePicker
import com.yiqisport.yiqiapp.data.DatabaseHelper
import com.yiqisport.yiqiapp.data.DataHelper
import com.yiqisport.yiqiapp.data.Folder
import com.yiqisport.yiqiapp.fragment.BasicFragment
import com.yiqisport.yiqiapp.util.*
import com.yiqisport.yiqiapp.util.setMaxLength
import de.hdodenhof.circleimageview.CircleImageView

/**
 * folder profile fragment
 */
class FolderProfileEditFragment : BasicFragment() {

    //region properties
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var folder : Folder

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

        mView = inflater.inflate(R.layout.fragment_folder_profile_edit, container, false)

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
        profileView = mView.findViewById(R.id.imageView_folderImage)
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
        folder = DatabaseHelper(activity!!).findFolderById(ActivityUtil.getFolderIdFromIntent(activity!!.intent))!!
        nameView.text = folder.name

        if(folder.profileImagePath.isNotBlank()) profileView.setImageURI(Uri.parse(folder.profileImagePath))
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

                        if(updatedName.isBlank()) MessageHelper.noticeDialog(context!!, "名称不能为空")
                        else{
                            nameView.text = updatedName
                            folder.name = updatedName
                            DataHelper().updateFolder(folder, databaseHelper)
                            dialogInterface.dismiss()
                        }
                    }
                    .setNegativeButton("取消") { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                    }

                val dialog = builder.create()
                dialog.show()
                dialog.setCanceledOnTouchOutside(false)
                dialog.findViewById<TextView>(R.id.title).text = "名称"

                val content = dialog.findViewById<EditText>(R.id.content)

                content.setText(folder.name)
                content.setMaxLength(Folder().maxNameLength)
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
                        folder.notes = updatedNotes
                        DataHelper().updateFolder(folder, databaseHelper)
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
                content.findViewById<EditText>(R.id.content).setText(folder.notes)
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
        val view: View = inflater.inflate(R.layout.dialog_folder_detail_edit_item, null)
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
                    folder.profileImagePath =
                        ImageUtil.saveFolderProfileImage(uri, folder.id, context!!)
                    profileView.setImageURI(uri)
                    DataHelper().updateFolder(folder, databaseHelper)
                }
                GalleryIntentFor.Background -> {
                    //get image for background
                    folder.backgroundImagePath =
                        ImageUtil.saveFolderBackgroundImage(uri, folder.id, context!!)
                    DataHelper().updateFolder(folder, databaseHelper)
                }
            }
        }   else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(context, "缺少授权", Toast.LENGTH_SHORT).show()
        }
    }

    //endregion


}
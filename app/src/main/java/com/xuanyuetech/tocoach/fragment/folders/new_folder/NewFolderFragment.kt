package com.xuanyuetech.tocoach.fragment.folders.new_folder

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
import com.xuanyuetech.tocoach.data.Folder
import com.xuanyuetech.tocoach.fragment.BasicFragment
import com.xuanyuetech.tocoach.util.*
import de.hdodenhof.circleimageview.CircleImageView

/**
 * new folder fragment
 */
class NewFolderFragment : BasicFragment() {

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
        mView = inflater.inflate(R.layout.fragment_new_folder, container, false)

        initView()

        databaseHelper = DatabaseHelper(activity!!)

        setUpToolBarTitle(resources.getString(R.string.newFolderActivity_newFolderFrag_title))

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
        textInputEditTextName = mView.findViewById(R.id.editText_folder_name)
        textInputEditTextNotes = mView.findViewById(R.id.editText_folder_note)
        textInputEditTextName.setMaxLength(Folder().maxNameLength)
        imageView = mView.findViewById(R.id.imageView_folderImage)
    }

    /**
     * init data
     */
    private fun initData(){}

    /**
     * bind listeners
     */
    private fun initListeners() {

        //confirm button
        val buttonAdd = mView.findViewById<Button>(R.id.btn_add_new_folder)
        buttonAdd.setOnClickListener{

            val folder = createFolder()
            if(!folder.name.isBlank()){
                DataHelper().addFolder(folder, databaseHelper)
                Toast.makeText(context,"成功创建新的日志库!",Toast.LENGTH_SHORT).show()
                activity?.setResult(GlobalVariable().RESULT_NEED_REFRESH_FOLDER_LIST_OR_HOME_EVENT_OR_ARCHIVE_LIST)
                activity?.finish()
            }else{
                //invalid name
                MessageHelper.noticeDialog(context!!, "请输入名字")
            }
        }

        //folder image view
        val folderImageView = mView.findViewById<CircleImageView>(R.id.imageView_folderImage)
        folderImageView.setOnClickListener{
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
     * create the folder
     */
    private fun createFolder() : Folder{
        val folderName = textInputEditTextName.text.toString()
        val folderNotes = textInputEditTextNotes.text.toString()
        val folderId = databaseHelper.getLastFolderIdAuto()

        val folderImagePath = ImageUtil.saveFolderProfileImage(imageUri, folderId, context!!)

        return Folder(
            folderId,
            folderName,
            folderNotes,
            folderImagePath,
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

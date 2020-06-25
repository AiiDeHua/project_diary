package com.xuanyuetech.tocoach.fragment.folders.folder

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
import com.xuanyuetech.tocoach.data.Folder
import com.xuanyuetech.tocoach.fragment.BasicFragment
import com.xuanyuetech.tocoach.util.ActivityUtil
import de.hdodenhof.circleimageview.CircleImageView

/**
 * folder profile
 */
class FolderProfileFragment : BasicFragment() {

    //region properties
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var folder:Folder

    private lateinit var folderNameView : TextView
    private lateinit var folderNotesView : TextView
    private lateinit var folderIdView : TextView
    private lateinit var folderImageView : CircleImageView

    //endregion

    //region onCreateView

    /**
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mView = inflater.inflate(R.layout.fragment_folder_profile, container, false)

        initViews()

        initData()

        setHasOptionsMenu(true)

        return mView
    }

    //endregion

    //region override

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_folder_profile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) :Boolean {
        when(item.itemId){
            R.id.menuItem_folderProfile_edit ->{
                //edit the profile
                ActivityUtil.startFolderProfileEdit(this, folder.id)
            }
            R.id.menuItem_folderProfile_delete ->{
                deleteFolder()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun customizeToolbar() {
        super.customizeToolbar()
        setUpToolBarTitle("日志库信息")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        refreshView()
        activity!!.setResult(GlobalVariable().RESULT_NEED_REFRESH_FOLDER_LIST_OR_HOME_EVENT_OR_ARCHIVE_LIST)
    }

    //endregion

    //endregion fun

    /**
     * init views
     */
    private fun initViews(){
        folderNameView = mView.findViewById(R.id.textView_name)
        folderNotesView = mView.findViewById(R.id.textView_notes)
        folderIdView = mView.findViewById(R.id.textView_id)
        folderImageView = mView.findViewById(R.id.imageView_folderImage)
    }

    /**
     * init data
     */
    @SuppressLint("SetTextI18n")
    private fun initData(){
        databaseHelper = DatabaseHelper(activity!!)
        folder = databaseHelper.findFolderById(ActivityUtil.getFolderIdFromIntent(activity!!.intent))!!
        folderNameView.text = folder.name
        folderNotesView.text = folder.notes

        if(folder.profileImagePath.isNotBlank()) folderImageView.setImageURI(Uri.parse(folder.profileImagePath))
        else folderImageView.setImageResource(R.drawable.profile_default)

        folderIdView.text = "folder${folder.id}"
    }

    /**
     *
     */
    @SuppressLint("SetTextI18n")
    private fun refreshView(){
        folder = databaseHelper.findFolderById(folder.id)!!
        folderNameView.text = folder.name
        folderNotesView.text = folder.notes

        if(folder.profileImagePath.isNotBlank()) folderImageView.setImageURI(Uri.parse(folder.profileImagePath))
        else folderImageView.setImageResource(R.drawable.profile_default)

        folderIdView.text = "folder${folder.id}"
    }

    /**
     * delete folder action
     */
    private fun deleteFolder(){
        val dialogBuilder = AlertDialog.Builder(activity!!,R.style.CustomDialogTheme)
            .setCancelable(true)
            .setTitle("警告")
            .setMessage("你确定要删除当前日志库吗?")
            .setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("确定"){
                dialog, _ ->dialog.dismiss()

                DataHelper().deleteFolder(folder, databaseHelper)
                Toast.makeText(context,"成功删除!",Toast.LENGTH_SHORT).show()
                activity!!.setResult(globalVariable.RESULT_DELETE_FOLDER)
                activity!!.finish()
            }

        dialogBuilder.show()
    }


    //endregion

}
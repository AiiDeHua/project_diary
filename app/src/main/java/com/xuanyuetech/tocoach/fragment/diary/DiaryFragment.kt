package com.xuanyuetech.tocoach.fragment.diary

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.dhaval2404.imagepicker.ImagePicker
import com.xuanyuetech.tocoach.R
import com.xuanyuetech.tocoach.data.*
import com.xuanyuetech.tocoach.fragment.BasicFragment
import com.xuanyuetech.tocoach.util.FilePathHelper
import com.xuanyuetech.tocoach.util.MessageHelper
import com.xuanyuetech.tocoach.util.setMaxLength
import jp.wasabeef.richeditor.RichEditor
import org.threeten.bp.LocalDateTime
import java.lang.Exception

/**
 * Dairy fragment
 */
class DiaryFragment : BasicFragment() {

    //region properties

    private lateinit var databaseHelper: DatabaseHelper

    private lateinit var mEditor : RichEditor
    private lateinit var titleText : EditText
    private lateinit var folderView : TextView
    private lateinit var timeView : TextView
    private lateinit var deleteMenuItem : MenuItem

    private var folderId = -1
    private var diaryId = -1
    private var diary : Diary? = null
    private var folder : Folder? = null

    //the var to check diary's dirty
    private lateinit var initTitle : String
    private lateinit var initContent : String

    //var to check whether or not set the result
    private var isSaved = false

    private var handler = Handler()

    //endregion

    //region onCreateView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        databaseHelper = DatabaseHelper(activity!!)

        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_diary, container, false)

        initView()

        handler.post {
            initData()
            setupEditorToolbar()
        }

        return mView
    }

    //endregion

    //region fun

    /**
     * init view
     */
    private fun initView(){
        mEditor = mView.findViewById(R.id.editor)
        mEditor.setPadding(15,15,15,20)

        titleText = mView.findViewById(R.id.titleText)
        timeView = mView.findViewById(R.id.timeView)
        folderView = mView.findViewById(R.id.folderView)

        //limit title length
        titleText.setMaxLength(Diary().maxTitleLength)

    }

    /**
     * editor toolbar set up
     */
    private fun setupEditorToolbar(){

        mView.findViewById<ImageButton>(R.id.action_undo).setOnClickListener { mEditor.undo() }

        mView.findViewById<ImageButton>(R.id.action_redo).setOnClickListener { mEditor.redo() }

        mView.findViewById<ImageButton>(R.id.action_bold).setOnClickListener { mEditor.setBold() }

        mView.findViewById<ImageButton>(R.id.action_heading1).setOnClickListener { mEditor.setHeading(1) }

        mView.findViewById<ImageButton>(R.id.action_heading2).setOnClickListener { mEditor.setHeading(2) }

        mView.findViewById<ImageButton>(R.id.action_normal).setOnClickListener { mEditor.setHeading(4) }

        mView.findViewById<ImageButton>(R.id.action_indent).setOnClickListener { mEditor.setIndent() }

        mView.findViewById<ImageButton>(R.id.action_insert_bullets).setOnClickListener { mEditor.setBullets() }

        mView.findViewById<ImageButton>(R.id.action_insert_image).setOnClickListener {
            val diaryDir =
                if(diaryId == -1) FilePathHelper(context!!).diaryDirPath(databaseHelper.getLastDairyIdAuto())
                else FilePathHelper(context!!).diaryDirPath(diaryId)

            ImagePicker.with(this)
                .crop()
                .compress(1560)
                .maxResultSize(300, 700)
                .saveDir(diaryDir)
                .start()
        }

        mView.findViewById<ImageButton>(R.id.action_insert_numbers).setOnClickListener { mEditor.setNumbers() }

        mView.findViewById<ImageButton>(R.id.action_italic).setOnClickListener { mEditor.setItalic() }

        mView.findViewById<ImageButton>(R.id.action_outdent).setOnClickListener { mEditor.setOutdent() }

        mView.findViewById<ImageButton>(R.id.action_underline).setOnClickListener { mEditor.setUnderline() }

        mView.findViewById<ImageButton>(R.id.action_align_left).setOnClickListener { mEditor.setAlignLeft() }

        mView.findViewById<ImageButton>(R.id.action_align_center).setOnClickListener { mEditor.setAlignCenter() }

        mView.findViewById<ImageButton>(R.id.action_align_right).setOnClickListener { mEditor.setAlignRight() }

        //editor's toolbar only visible when it is focusing on the editor
        mView.findViewById<ConstraintLayout>(R.id.richText_toolbar).visibility = View.GONE
        mEditor.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus){
                mView.findViewById<ConstraintLayout>(R.id.richText_toolbar).visibility = View.VISIBLE
            }else{
                mView.findViewById<ConstraintLayout>(R.id.richText_toolbar).visibility = View.GONE
            }
        }
    }

    /**
     * init data, it might be the new diary with only folderId, or the existing diary
     */
    private fun initData(){
        folderId = arguments!!.getInt("folder_id")
        diaryId = arguments!!.getInt("diary_id")

        when {
            diaryId > 0 -> {
                //if there is diary, apply the data
                diary = databaseHelper.findDairyById(diaryId)!!

                timeView.text = diary!!.initTime

                titleText.setText(diary!!.title)

                mEditor.html = diary!!.content

                folder = databaseHelper.findFolderById(diary!!.folderId)

                initContent = diary!!.content
                initTitle = diary!!.title
            }
            folderId > 0 -> {
                //if there is no diary, no time show
                timeView.text = ""
                folder = databaseHelper.findFolderById(folderId)

                initContent = ""
                initTitle = ""
            }
            else -> {
                activity!!.finish()
            }
        }

        folderView.text = folder!!.name
    }

    //endregion

    //region override

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_diary, menu)
        deleteMenuItem = menu.findItem(R.id.menuItem_diary_delete)

        //only shows when there is diary
        if(diary == null) deleteMenuItem.isVisible = false
    }

    override fun customizeToolbar(){
        setUpToolBarTitle("文本日志")
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.menuItem_diary_save -> {

                if(titleText.text.toString().isBlank()) {
                    //check title's validation
                    MessageHelper.noticeDialog(context!!, "请输入标题")
                    return true
                }

                val currentTime = LocalDateTime.now()

                if(diary == null){
                    //diary is new
                    val newDairy = Diary(folderId, currentTime, folder!!.name)

                    try{
                        //since content has issue with null html, have to use try/catch
                        //TODO: customized the package to support null html
                        newDairy.content = mEditor.html
                    }catch (e : Exception){
                    }

                    newDairy.title = titleText.text.toString()

                    val diaryId = DataHelper().addDairy(newDairy, databaseHelper)

                    diary = databaseHelper.findDairyById(diaryId.toInt())

                }else{
                    //update the existing diary
                    diary!!.setUpdateTime(currentTime)

                    try{
                        diary!!.content = mEditor.html
                    }catch (e : Exception){
                    }

                    diary!!.title = titleText.text.toString()

                    DataHelper().updateDairy(diary!!, databaseHelper)
                }

                //init time
                timeView.text = diary!!.initTime

                Toast.makeText(context,"成功保存!",Toast.LENGTH_SHORT).show()

                activity?.setResult(GlobalVariable().RESULT_NEED_REFRESH_FOLDER_LIST_OR_HOME_EVENT_OR_ARCHIVE_LIST)

                isSaved = true

                //show delete diary choice
                deleteMenuItem.isVisible = true

                initTitle = diary!!.title
                initContent = diary!!.content

                true
            }

            R.id.menuItem_diary_delete -> {

                val builder = AlertDialog.Builder(context!!, R.style.CustomDialogTheme )
                builder
                    .setTitle("警告")
                    .setMessage("确定要删除当前文本日志吗")
                    .setPositiveButton("确定") { dialog, _ ->
                        DataHelper().deleteDairy(diary!!, databaseHelper)

                        dialog.dismiss()

                        Toast.makeText(context,"成功删除!",Toast.LENGTH_SHORT).show()
                        activity?.setResult(GlobalVariable().RESULT_NEED_REFRESH_FOLDER_LIST_OR_HOME_EVENT_OR_ARCHIVE_LIST)
                        activity?.finish()
                    }
                    .setNegativeButton("取消", null)

                builder.show()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            //get the image
            val uri = data!!.data
            handler.post {
                mEditor.insertImage(uri!!.path, uri.path)
            }
        }else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(context, "缺少授权", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed(): Boolean {

        var checkContent = ""

        try{
            checkContent = mEditor.html
        }catch (e : Exception){}

        if(initTitle != titleText.text.toString() || initContent != checkContent) {

            val builder = AlertDialog.Builder(context!!, R.style.CustomDialogTheme )
            builder
                .setTitle("退出")
                .setMessage("确定要放弃未保存的编辑吗")
                .setPositiveButton("确定") { dialog, _ ->
                    dialog.dismiss()
                    if(isSaved) activity?.setResult(GlobalVariable().RESULT_NEED_REFRESH_FOLDER_LIST_OR_HOME_EVENT_OR_ARCHIVE_LIST)
                    activity!!.finish()
                }
                .setNegativeButton("取消", null)

            builder.show()

            return true
        }

        return super.onBackPressed()
    }

    //endregion

}

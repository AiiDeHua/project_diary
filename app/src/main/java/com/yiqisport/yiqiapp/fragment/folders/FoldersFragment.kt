package com.yiqisport.yiqiapp.fragment.folders

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yiqisport.yiqiapp.R
import com.yiqisport.yiqiapp.adapter.FolderCardViewAdapter
import com.yiqisport.yiqiapp.adapter.setUpWith
import com.yiqisport.yiqiapp.data.DatabaseHelper
import com.yiqisport.yiqiapp.data.Folder
import com.yiqisport.yiqiapp.fragment.BasicFragment
import com.yiqisport.yiqiapp.util.ActivityUtil

/**
 * folders fragment
 */
class FoldersFragment : BasicFragment() {

    //region properties

    private lateinit var listFolders: ArrayList<Folder>
    private var folderCardViewAdapter: FolderCardViewAdapter? = null
    private lateinit var databaseHelper: DatabaseHelper
    private val handler = Handler()

    private lateinit var sizeLabelView : TextView

    //endregion

    //region onCreateView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mView =  inflater.inflate(R.layout.fragment_folders, container, false)

        databaseHelper = DatabaseHelper(activity!!)

        initView()

        initData()

        refreshFolders()

        return mView
    }

    //endregion

    //region override

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_folders, menu)
    }

    override fun onOptionsItemSelected(item:MenuItem) :Boolean {
        if(isValidClick()) {
            when(item.itemId){
                R.id.menuItem_home_add_new_folder->{
                    ActivityUtil.startCreateNewFolder(this)
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
        listFolders = ArrayList()
    }

    private fun initView(){
        sizeLabelView = mView.findViewById(R.id.textView_folder_label)
    }

    /**
     * Setup the CardView
     */
    private fun bindAdapter(){
        folderCardViewAdapter = FolderCardViewAdapter(listFolders)
        folderCardViewAdapter!!.setOnItemClickListener(
            object : FolderCardViewAdapter.CustomOnItemClickListener {
                override fun onItemClickListener(position: Int) {
                    if(isValidClick()){
                        ActivityUtil.startFolder(this@FoldersFragment, listFolders[position].id)
                    }
                }
            })

        //set up the adapter for the recycler
        val recyclerView : RecyclerView = mView.findViewById(R.id.recycler_view)

        recyclerView.setUpWith(folderCardViewAdapter!!)
    }

    /**
     * refresh all folder list
     */
    fun refreshFolders(){
        handler.post{
            if(folderCardViewAdapter == null) bindAdapter()
            listFolders.clear()
            listFolders.addAll(databaseHelper.getAllFolder().sortedBy { it.name })
            folderCardViewAdapter!!.notifyDataSetChanged()
            refreshFolderNumTextView()
        }
    }

    /**
     * refresh folder number text view
     */
    @SuppressLint("SetTextI18n")
    private fun refreshFolderNumTextView(){
        if(listFolders.size <= 0 ) sizeLabelView.text = "赶紧点击右上角\"+\"添加新的日志库吧！"
        else{sizeLabelView.text = "已有 ${listFolders.size} 个日志库"}
    }


    //endregion

}
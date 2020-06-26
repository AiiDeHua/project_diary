package com.yiqisport.yiqiapp.activity

import android.os.Bundle
import android.view.Window
import com.yiqisport.yiqiapp.R
import com.yiqisport.yiqiapp.fragment.folders.folder.FolderArchiveFragment

/**
 * Folder activity to hold all Folder's related stuff
 */
class FolderActivity: BasicActivity() {

    //region onCreate

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE) // 隐藏标题 //这行代码一定要在setContentView之前，不然会闪退
        setContentView(R.layout.activity_archive)

        val ts = supportFragmentManager.beginTransaction()
        ts.add(
            R.id.nav_host_fragment_folder,
            FolderArchiveFragment()
        )
        ts.commit()

    }

    //endregion

}
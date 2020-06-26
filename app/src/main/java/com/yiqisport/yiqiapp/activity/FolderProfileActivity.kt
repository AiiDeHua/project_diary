package com.yiqisport.yiqiapp.activity

import android.os.Bundle
import com.yiqisport.yiqiapp.R
import com.yiqisport.yiqiapp.fragment.folders.folder.FolderProfileFragment

/**
 * new folder activity
 */
class FolderProfileActivity : BasicActivity() {

    //region onCreate

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archive_detail)

        initToolbar()

        initFragment()
    }

    //endregion

    //region fun

    /**
     * Set up toolbar
     */
    private fun initToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setToolbarToActionbar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    /**
     * start the fragment
     */
    private fun initFragment(){
        val ts = supportFragmentManager.beginTransaction()
        ts.add(
            R.id.nav_host_fragment_archive_detail,
            FolderProfileFragment()
        )
        ts.commit()
    }

    //endregion

}
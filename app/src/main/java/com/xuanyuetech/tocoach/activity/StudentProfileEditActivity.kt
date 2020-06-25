package com.xuanyuetech.tocoach.activity

import android.os.Bundle
import com.xuanyuetech.tocoach.R
import com.xuanyuetech.tocoach.fragment.students.student.StudentProfileEditFragment

/**
 * new student activity
 */
class StudentProfileEditActivity : BasicActivity() {

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
            StudentProfileEditFragment()
        )
        ts.commit()
    }

    //endregion

}
package com.xuanyuetech.tocoach.activity

import android.app.Activity
import android.os.Bundle
import com.xuanyuetech.tocoach.R
import com.xuanyuetech.tocoach.fragment.students.new_student.NewStudentFragment

/**
 * new student activity
 */
class NewFolderActivity : BasicActivity() {

    //region onCreate

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_folder)

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
        //transfer the video editor fragment
        val ts = supportFragmentManager.beginTransaction()
        ts.add(
            R.id.nav_host_fragment_new_student,
            NewStudentFragment()
        )
        ts.commit()
    }

    //endregion

    //region override
    /**
     * back pressed is meaning canceling adding
     */
    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
    }

    //endregion

}
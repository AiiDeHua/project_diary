package com.xuanyuetech.tocoach.activity

import android.os.Bundle
import android.view.Window
import com.xuanyuetech.tocoach.R
import com.xuanyuetech.tocoach.fragment.students.student.StudentArchiveFragment

/**
 * Student activity to hold all Student's related stuff
 */
class StudentActivity: BasicActivity() {

    //region onCreate

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE) // 隐藏标题 //这行代码一定要在setContentView之前，不然会闪退
        setContentView(R.layout.activity_archive)

        val ts = supportFragmentManager.beginTransaction()
        ts.add(
            R.id.nav_host_fragment_student,
            StudentArchiveFragment()
        )
        ts.commit()

    }

    //endregion

}
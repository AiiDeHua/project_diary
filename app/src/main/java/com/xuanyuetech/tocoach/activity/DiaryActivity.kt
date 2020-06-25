package com.xuanyuetech.tocoach.activity

import android.app.Activity
import android.os.Bundle
import com.xuanyuetech.tocoach.R
import com.xuanyuetech.tocoach.fragment.diary.DiaryFragment
import com.xuanyuetech.tocoach.util.ActivityUtil

/**
 * diary activity
 */
class DiaryActivity : BasicActivity() {

    //region properties

    private var studentId = -1
    private var diaryId = -1

    //endregion

    //region onCreate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getStudentData()

        setContentView(R.layout.activity_diary)

        setSwipeBackEnable(false)

        initToolbar()

        initFragment()
    }

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

        val bundle = Bundle()
        bundle.putInt("student_id",studentId)
        bundle.putInt("diary_id",diaryId)

        val diaryFragment = DiaryFragment()
        diaryFragment.arguments = bundle

        val ts = supportFragmentManager.beginTransaction()

        ts.add(
            R.id.nav_host_fragment_diary,
            diaryFragment
        )
        ts.commit()
    }


    /**
     *
     */
    private fun getStudentData(){
        studentId = ActivityUtil.getStudentIdFromIntent(intent)
        diaryId = ActivityUtil.getDairyIdFromIntent(intent)
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

package com.yiqisport.yiqiapp.activity

import android.app.Activity
import android.os.Bundle
import com.yiqisport.yiqiapp.R
import com.yiqisport.yiqiapp.fragment.diary.DiaryFragment
import com.yiqisport.yiqiapp.util.ActivityUtil

/**
 * diary activity
 */
class DiaryActivity : BasicActivity() {

    //region properties

    private var folderId = -1
    private var diaryId = -1

    //endregion

    //region onCreate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getFolderData()

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
        bundle.putInt("folder_id",folderId)
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
    private fun getFolderData(){
        folderId = ActivityUtil.getFolderIdFromIntent(intent)
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

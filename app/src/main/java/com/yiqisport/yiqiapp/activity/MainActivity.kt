package com.yiqisport.yiqiapp.activity

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jakewharton.threetenabp.AndroidThreeTen
import com.yiqisport.yiqiapp.R
import com.yiqisport.yiqiapp.data.*
import com.yiqisport.yiqiapp.fragment.BasicFragment
import com.yiqisport.yiqiapp.fragment.account.AccountFragment
import com.yiqisport.yiqiapp.fragment.calendar.CalendarFragment
import com.yiqisport.yiqiapp.fragment.home.HomeFragment
import com.yiqisport.yiqiapp.fragment.folders.FoldersFragment
import com.yiqisport.yiqiapp.util.*
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

/**
 * main start activity
 */
class MainActivity : BasicActivity() {

    //region properties
    private lateinit var homeFragment : HomeFragment
    private lateinit var foldersFragment : FoldersFragment
    private lateinit var calendarFragment : CalendarFragment
    private lateinit var accountFragment : AccountFragment
    private lateinit var activeFrag : BasicFragment

    lateinit var string: String

    //endregion

    //region onCreate

    /**
     * Main init func
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.setDefaultUncaughtExceptionHandler(
            CrashHandler(
                this
            )
        )

        setSwipeBackEnable(false)

        //requirement for the calendar
        AndroidThreeTen.init(this)

        setContentView(R.layout.activity_main)

        checkAgreementActivity()

        createExample()

        initToolbar()

        initBottomBar()
    }

    //endregion

    //region fun

    /**
     * Set up toolbar
     */
    private fun initToolbar(){
        //do not want back arrow to be showed
        setToolbarToActionbar(findViewById(R.id.toolbar), null ,
            isDisplayHomeAsUpEnabled = false,
            isDisplayShowHomeEnabled = false
        )
    }

    /**
     * Check whether the user agree or not with the privacy agreement
     */
    private fun checkAgreementActivity(){
        val prefs =
            getDefaultSharedPreferences(baseContext)
        val previouslyAgreed: Boolean =
            prefs.getBoolean(getString(R.string.pref_previously_privacy_agreement), false)

        if(!previouslyAgreed){
            val welcomeIntent = Intent(this, WelcomeActivity::class.java)
            this.startActivity(welcomeIntent)
        }

    }

    /**
     * Create the example if this is the first time open the app
     */
    private fun createExample(){
        val databaseHelper = DatabaseHelper(this)
        val prefs =
            getDefaultSharedPreferences(baseContext)
        val previouslyStarted: Boolean =
            prefs.getBoolean(getString(R.string.pref_previously_started), false)


        if (!previouslyStarted) {
            //change the shared string to be TRUE
            val edit: SharedPreferences.Editor = prefs.edit()
            edit.putBoolean(getString(R.string.pref_previously_started), java.lang.Boolean.TRUE)
            edit.apply()

            //folder example
            val profileImagePath = PathUtil.getPathFromRaw(R.raw.example_logo_small)
            val profileBackgroundPath = PathUtil.getPathFromRaw(R.raw.example_logo_background)
            val savedProfileImagePath = ImageUtil.saveFolderProfileImage(Uri.parse(profileImagePath), 1, this)
            val savedProfileBackgroundPath = ImageUtil.saveFolderBackgroundImage(Uri.parse(profileBackgroundPath), 1, this)

            //diary example
            val diaryImagePath = PathUtil.getPathFromRaw(R.raw.diary_example_image)
            val savedDairyImagePath = FilePathHelper(this).diaryDirPath(1) + "exampleImage.jpg"
            ImageUtil.reduceSizeOfImageAndSave(Uri.parse(diaryImagePath), savedDairyImagePath, this)
            var diaryContent: String = ReaderUtil.inputStreamConvector(resources.openRawResource(R.raw.diary_example))
            diaryContent = diaryContent.replace("exampleImagePath", savedDairyImagePath)

            //video example
            val videoCoverPath = PathUtil.getPathFromRaw(R.raw.example_video_cover)
            val resourceId = this.resources.getIdentifier("example_video","raw",this.packageName)
            val videoPath = "rawresource:///$resourceId"

            val folder = Folder()
            folder.name = "小明(案例)"
            folder.notes = "案例日志库"
            folder.profileImagePath = savedProfileImagePath
            folder.backgroundImagePath = savedProfileBackgroundPath
            DataHelper().addFolder(folder,databaseHelper)

            val diary = Diary(folder.id, LocalDateTime.now(), folder.name)
            diary.title = "文本日志"
            diary.content = diaryContent
            DataHelper().addDairy(diary,databaseHelper)

            val video = Video(
                folderId = folder.id,
                initTime = LocalDateTime.now(),
                title = "视频日志",
                cloudUrl = "",
                localUrl = videoPath,
                notes = "1. 视频日志提供了裁剪、分段变速（0.25倍速、0.5倍速）、自由涂鸦、文字、贴图来帮助运动员编辑想要的短视频片段。\n\n" +
                        "2. 视频日志的视频不能再次修改。\n\n" +
                        "3. 视频的长度只允许在5秒到5分钟之间。\n\n" +
                        "4. 编辑完的视频可以自由分享。 \n\n" +
                        "5. 所有视频或文本资料都只存在于本地，且未分享的视频在相册中不可见。\n\n" +
                        "6. 我们正在研发多视频拼接、云端存储等更多的功能，敬请期待",
                coverUrl = videoCoverPath,
                folderName = folder.name
                )
            DataHelper().addVideo(video, databaseHelper)

            //calendarEvent
            val calendarEvent = CalendarEvent(LocalDate.now().plusDays(1))
            calendarEvent.title = "课程示例"
            calendarEvent.attendants = "点击右上角“+”添加课程!"
            calendarEvent.notes = "赶紧试试添加课程吧！"
            databaseHelper.addCalendarEvent(calendarEvent)
        }
    }

    //endregion

    //region navigation

    /**
     * Set up the navigation
     * Because we do not want the fragment recreate every time switching fragment
     * We cannot use the default navigation controller
     */
    private fun initBottomBar() {
        homeFragment = HomeFragment()
        foldersFragment = FoldersFragment()
        calendarFragment = CalendarFragment()
        accountFragment = AccountFragment()
        activeFrag = foldersFragment

        supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment, homeFragment).hide(homeFragment).commit()
        supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment, foldersFragment).hide(homeFragment).commit()
        supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment, calendarFragment).hide(calendarFragment).commit()
        supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment, accountFragment).hide(accountFragment).commit()

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomBar_home)
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
        bottomNavigation.selectedItemId = R.id.nav_main_folders
    }

    //override the default listener
    private val navigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {
        when(it.itemId){
            R.id.nav_main_home -> {
                supportFragmentManager.beginTransaction().hide(activeFrag).show(homeFragment).commit()
                activeFrag = homeFragment
                findViewById<TextView>(R.id.toolbar_title).text = getString(R.string.mainActivity_homeFrag_title)
                true
            }
            R.id.nav_main_folders->{
                supportFragmentManager.beginTransaction().hide(activeFrag).show(foldersFragment).commit()
                activeFrag = foldersFragment
                findViewById<TextView>(R.id.toolbar_title).text = getString(R.string.mainActivity_foldersFrag_title)
                true
            }
            R.id.nav_main_calendar->{
                supportFragmentManager.beginTransaction().hide(activeFrag).show(calendarFragment).commit()
                activeFrag = calendarFragment
                findViewById<TextView>(R.id.toolbar_title).text = getString(R.string.mainActivity_calendarFrag_title)
                true
            }
            R.id.nav_main_account->{
                supportFragmentManager.beginTransaction().hide(activeFrag).show(accountFragment).commit()
                activeFrag = accountFragment
                findViewById<TextView>(R.id.toolbar_title).text = getString(R.string.mainActivity_accountFrag_title)
                true
            }
            else -> false
        }
    }

    /**
     * response to intent results if any
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == GlobalVariable().RESULT_NEED_REFRESH_FOLDER_LIST_OR_HOME_EVENT_OR_ARCHIVE_LIST){
            homeFragment.refreshViews()
            foldersFragment.refreshFolders()
        }
    }

    /**
     * do not want anim once start the main activity
     */
    override fun setUpTransitionAnim(savedInstanceState: Bundle?) {}

    //endregion

}

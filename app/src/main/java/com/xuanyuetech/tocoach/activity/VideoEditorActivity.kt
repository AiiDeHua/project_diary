package com.xuanyuetech.tocoach.activity

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.xuanyuetech.tocoach.R
import com.xuanyuetech.tocoach.data.DatabaseHelper
import com.xuanyuetech.tocoach.fragment.video_editor.VideoEditorFragment
import com.xuanyuetech.tocoach.util.ActivityUtil

/**
 * Video Editor Activity
 */
class VideoEditorActivity : BasicActivity(){

    //region properties

    private lateinit var inputVideoUri: Uri
    private lateinit var databaseHelper: DatabaseHelper
    private var folderId = -1

    //endregion

    //region onCreate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(!checkInternet()){
            Toast.makeText(this, "请检查网络", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setSwipeBackEnable(false)

        setContentView(R.layout.activity_video_editor)

        databaseHelper = DatabaseHelper(this)

        initData()

        initFragment()

    }
    //endregion

    //region fun

    private fun checkInternet() : Boolean{
        var result = false
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }
                }
            }
        }
        return result
    }

    /**
     *
     */
    private fun initData(){
        folderId = ActivityUtil.getFolderIdFromIntent(intent)

        try {
            inputVideoUri = ActivityUtil.getVideoUriFromIntent(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "错误", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
    }


    /**
     * start the fragment
     */
    private fun initFragment(){

        val bundle = Bundle()
        bundle.putInt("folder_id",folderId)

        val videoEditorFragment = VideoEditorFragment(inputVideoUri)
        videoEditorFragment.arguments = bundle

        //transfer the video editor fragment
        val ts = supportFragmentManager.beginTransaction()
        ts.add(
            R.id.nav_host_fragment_video_editor,
            videoEditorFragment
        )
        ts.commit()
    }

    //endregion

    //region override

    /**
     * toolbar menu setting
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_video_editor, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * VideoEditor should be in dark theme
     */
    override fun initSystemUI() {
        super.initSystemUI()

        window.statusBarColor = Color.BLACK

        //set icon colors for status bar
        window.decorView.systemUiVisibility = 0

        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
    }
    
    //endregion

}
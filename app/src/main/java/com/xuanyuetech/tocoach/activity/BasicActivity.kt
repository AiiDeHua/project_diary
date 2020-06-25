package com.xuanyuetech.tocoach.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.xuanyuetech.tocoach.R
import com.xuanyuetech.tocoach.fragment.BasicFragment
import me.imid.swipebacklayout.lib.SwipeBackLayout
import me.imid.swipebacklayout.lib.app.SwipeBackActivity


/**
 * basic class for all activity
 * it standardizes all animation and basic setup
 */
@SuppressLint("Registered")
open class BasicActivity : SwipeBackActivity() {

    //region properties
    private var onStartCount = 0

    //the variable to prevent mis-double click action
    private var mLastClickTime: Long = 0
    private lateinit var mSwipeBackLayout: SwipeBackLayout

    //endregion

    //region override

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initSystemUI()

        setUpSwipeAnim()

        setUpTransitionAnim(savedInstanceState)
    }


    override fun onStart() {
        super.onStart()
        if (onStartCount > 1) {
            overridePendingTransition(
                R.anim.anim_no,
                R.anim.anim_slide_out_to_right
            )
        } else if (onStartCount == 1) {
            onStartCount++
        }
    }

    override fun onBackPressed() {

        val fragmentList: List<*> = supportFragmentManager.fragments
        var handled = false
        for (f in fragmentList) {
            if (f is BasicFragment) {
                handled = f.onBackPressed()
                if (handled) {
                    break
                }
            }
        }
        if (!handled) {
            super.onBackPressed()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0,R.anim.anim_slide_out_to_right)
    }

    //endregion

    //region fun


    /**
     * setUp transition animation
     */
    open fun setUpTransitionAnim(savedInstanceState : Bundle?){
        onStartCount = 1
        if (savedInstanceState == null) // 1st time
        {
            this.overridePendingTransition(
                R.anim.anim_slide_in_to_left,
                R.anim.anim_no)
        } else // already created so reverse animation
        {
            onStartCount = 2
        }
    }

    /**
     * setup swipe animation
     */
    private fun setUpSwipeAnim(){
        mSwipeBackLayout = swipeBackLayout
        //设置滑动方向，可设置EDGE_LEFT, EDGE_RIGHT, EDGE_ALL, EDGE_BOTTOM
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        //mSwipeBackLayout.setEdgeSize(200);//滑动删除的效果只能从边界滑动才有效果，如果要扩大touch的范围，可以调用这个方法
    }

    /**
     * Set up the toolbar to action bar
     */
    fun setToolbarToActionbar(toolbar: Toolbar,
                              homeAsUpIndicatorID : Int? = null,
                              isDisplayHomeAsUpEnabled: Boolean = true,
                              isDisplayShowHomeEnabled: Boolean = true){
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(isDisplayHomeAsUpEnabled)
        supportActionBar?.setDisplayShowHomeEnabled(isDisplayShowHomeEnabled)
        if (homeAsUpIndicatorID != null) supportActionBar?.setHomeAsUpIndicator(homeAsUpIndicatorID)
    }

    /**
     * check if mis-double click
     */
    fun isValidClick() : Boolean{
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1500){
            return false
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        return true
    }

    /**
     * Set up the system general UI control
     */
    open fun initSystemUI(){
        //set icon colors for status bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
    }

    //endregion

}


package com.xuanyuetech.tocoach.fragment

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.xuanyuetech.tocoach.R
import com.xuanyuetech.tocoach.data.GlobalVariable


/**
 * basic fragment
 */
open class BasicFragment : Fragment() {


    //region properties
    var globalVariable = GlobalVariable()
    lateinit var mView : View

    //the variable to prevent mis-double click action
    private var mLastClickTime: Long = 0

    //endregion

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        customizeToolbar()
    }

    //endregion

    //region open fun

    /**
     * generic back pressed function for customized hardware back function from Fragment
     */
    open fun onBackPressed() : Boolean{
        return false
    }

    /**
     * function for customize toolbar
     */
    open fun customizeToolbar(){
    }

    //endregion

    //region public function

    /**
     * change toolbar title
     */
    fun setUpToolBarTitle(title : String) {
        activity!!.findViewById<TextView>(R.id.toolbar_title)?.text = title
        mView.findViewById<TextView>(R.id.toolbar_title)?.text = title
        // activity!!.title = title
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

    fun hideKeyBoard(){
        val imm =
        activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity!!.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    //endregion

    //region override

    override fun onDestroy() {
        hideKeyBoard()
        super.onDestroy()
    }

    override fun onPause() {
        hideKeyBoard()
        super.onPause()
    }
    //endregion


}

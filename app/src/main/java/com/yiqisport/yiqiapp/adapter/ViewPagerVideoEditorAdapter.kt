package com.yiqisport.yiqiapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * adapter for video editor viewPager
 */
class ViewPagerVideoEditorAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val mFragmentList:ArrayList<Fragment>? = ArrayList()

    fun addFragment(fragment:Fragment) {
        mFragmentList!!.add(fragment)
    }

    override fun getItem(position: Int): Fragment {
        return mFragmentList!![position]
    }

    override fun getCount(): Int {
       return mFragmentList!!.size
    }

}
package com.yiqisport.yiqiapp.fragment.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yiqisport.yiqiapp.R
import com.yiqisport.yiqiapp.adapter.HomeCardViewAdapter
import com.yiqisport.yiqiapp.adapter.setUpWith
import com.yiqisport.yiqiapp.data.DatabaseHelper
import com.yiqisport.yiqiapp.data.HomeEvent
import com.yiqisport.yiqiapp.fragment.BasicFragment
import com.yiqisport.yiqiapp.util.ActivityUtil

/**
 * home fragment
 */
class HomeFragment : BasicFragment() {

    //region properties

    private var homeCardViewAdapter: HomeCardViewAdapter? = null
    private lateinit var homeEvents : ArrayList<HomeEvent>
    private lateinit var databaseHelper : DatabaseHelper
    private lateinit var endTextView : TextView
    private var handler = Handler()

    private val maxListLength = 15

    //endregion

    //region onCreateView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mView = inflater.inflate(R.layout.fragment_home, container, false)

        initView()

        initData()

        refreshViews()

        return mView
    }

    //endregion

    //region fun

    /**
     * init data
     */
    private fun initData(){
        databaseHelper = DatabaseHelper(activity!!)
        homeEvents = ArrayList()
    }

    /**
     * init view
     */
    private fun initView(){
        endTextView = mView.findViewById(R.id.textView_list_end)
    }

    /**
     * setUp events
     */
    private fun bindAdapter(){
        homeCardViewAdapter = HomeCardViewAdapter(homeEvents, context!!)

        homeCardViewAdapter!!.setOnItemClickListener(
            object : HomeCardViewAdapter.costumOnItemClickListener {

                override fun onItemClickListener(position: Int) {

                    //need check if mis-double click happens
                    if(isValidClick()) {
                        if (homeEvents[position].type == HomeEvent().videoType()) {
                            //open video
                            ActivityUtil.startVideoPlayer(
                                this@HomeFragment,
                                homeEvents[position].refId.toInt()
                            )
                        } else if (homeEvents[position].type == HomeEvent().diaryType()) {
                            //open diary
                            ActivityUtil.startDairy(
                                this@HomeFragment,
                                homeEvents[position].refId.toInt(),
                                -1
                            )
                        }
                    }
                }

            })

        val eventsList = mView.findViewById<RecyclerView>(R.id.recyclerView_home)
        eventsList.setUpWith(homeCardViewAdapter!!)
    }

    /**
     * refresh views
     */
    @SuppressLint("SetTextI18n")
    fun refreshViews(){
        handler.post{
            if(homeCardViewAdapter == null ) bindAdapter()

            homeEvents.clear()

            //homeEvent is already sorted by initTime (id)
            homeEvents.addAll(databaseHelper.getAllHomeEvent().take(maxListLength))

            homeCardViewAdapter!!.notifyDataSetChanged()

            when(homeEvents.size){
                0 -> endTextView.text = "赶紧给日志库添加新的视频和文本日志吧！"
                maxListLength -> endTextView.text = "... ...\n... ..."
                else -> endTextView.text = ""
            }
        }
    }
}
package com.xuanyuetech.tocoach.fragment.account

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.xuanyuetech.tocoach.R
import com.xuanyuetech.tocoach.fragment.BasicFragment
import com.xuanyuetech.tocoach.util.ReaderUtil

class AccountFragment : BasicFragment() {


    //region onCreateView
    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mView = inflater.inflate(R.layout.fragment_account, container, false)

        val tvUser = mView.findViewById<TextView>(R.id.account_user_policy)
        tvUser.paint.flags = Paint.UNDERLINE_TEXT_FLAG
        tvUser.setOnClickListener{
            val builder = buildDialogBuilder("用户协议",ReaderUtil.inputStreamConvector(resources.openRawResource(R.raw.user_agreement)))
            builder.setPositiveButton("确定") { dialog, _ -> dialog!!.dismiss() }
            builder.show()
        }

        val tvPrivacy = mView.findViewById<TextView>(R.id.account_privacy_policy)
        tvPrivacy.paint.flags = Paint.UNDERLINE_TEXT_FLAG
        tvPrivacy.setOnClickListener{
            val builder = buildDialogBuilder("隐私政策",ReaderUtil.inputStreamConvector(resources.openRawResource(R.raw.privacy_agreement)))
            builder.setPositiveButton("确定") { dialog, _ -> dialog!!.dismiss() }
            builder.show()
        }

        return mView
    }
    //endregion

    @SuppressLint("InflateParams")
    private fun buildDialogBuilder(title : String, content : String) : AlertDialog.Builder{
        val inflater = activity!!.layoutInflater
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity, R.style.CustomDialogTheme)
        val view: View = inflater.inflate(R.layout.dialog_privacy_policy, null)
        view.findViewById<TextView>(R.id.dialog_privacy_policy_title).text = title
        view.findViewById<TextView>(R.id.dialog_privacy_policy_msg).text = content
        builder.setView(view)
        return builder
    }

}
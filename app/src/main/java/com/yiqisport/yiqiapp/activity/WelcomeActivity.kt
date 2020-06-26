package com.yiqisport.yiqiapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.Button
import android.widget.TextView
import androidx.preference.PreferenceManager
import com.yiqisport.yiqiapp.R
import com.yiqisport.yiqiapp.util.ReaderUtil
import kotlin.system.exitProcess


class WelcomeActivity : BasicActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_welcome)

        setSwipeBackEnable(false)

        iniViews()
    }

    /**
     * init views
     */
    @SuppressLint("SetTextI18n")
    private fun iniViews(){

        val privacyLink = findViewById<TextView>(R.id.textView_privacy_agreement_link)

        privacyLink.movementMethod = LinkMovementMethod.getInstance()
        privacyLink.setOnClickListener{
            val browserIntent = Intent(Intent.ACTION_VIEW)
            browserIntent.data = Uri.parse(getString(R.string.privacy_agreement_link))
            startActivity(browserIntent)
        }

        val userAgreementLink = findViewById<TextView>(R.id.textView_user_agreement_link)

        userAgreementLink.movementMethod = LinkMovementMethod.getInstance()
        userAgreementLink.setOnClickListener{
            val browserIntent = Intent(Intent.ACTION_VIEW)
            browserIntent.data = Uri.parse(getString(R.string.user_agreement_link))
            startActivity(browserIntent)
        }

        findViewById<TextView>(R.id.textView_user_and_privacy_agreement).text =
            ReaderUtil.inputStreamConvector(resources.openRawResource(R.raw.user_agreement)) +"\n\n" +
            ReaderUtil.inputStreamConvector(resources.openRawResource(R.raw.privacy_agreement))

        findViewById<Button>(R.id.button_agree).setOnClickListener {
            val prefs =
                PreferenceManager.getDefaultSharedPreferences(baseContext)
            val edit: SharedPreferences.Editor = prefs.edit()
            edit.putBoolean(getString(R.string.pref_previously_privacy_agreement), java.lang.Boolean.TRUE)
            edit.apply()
            finish()
        }

        findViewById<Button>(R.id.button_refuse).setOnClickListener {
            finish()
            exitProcess(0)
        }
    }

    /**
     * do not want anim
     */
    override fun setUpTransitionAnim(savedInstanceState: Bundle?) {}

    override fun onBackPressed() {
        finish()
        exitProcess(0)
    }
}


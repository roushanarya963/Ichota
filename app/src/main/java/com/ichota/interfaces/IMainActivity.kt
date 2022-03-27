package com.ichota.interfaces

import android.content.Context
import android.view.View
import com.ichota.preferences.PreferenceHelper

interface IMainActivity {
    fun showMessage(message: String)
    fun showProgress(isShowing: Boolean)
    fun getPreference(): PreferenceHelper
    fun getContext(): Context
    fun hideSoftKeyboard(view: View)
    fun showSoftKeyboard(view: View)
}
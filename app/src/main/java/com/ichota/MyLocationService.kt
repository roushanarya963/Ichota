package com.ichota

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MyLocationService : BroadcastReceiver() {

    companion object{
        val ACTION_PROCESS_UPDATE="com.ichota.UPDATE_LOCATION"
    }

    override fun onReceive(context: Context, intent: Intent) {

    }
}
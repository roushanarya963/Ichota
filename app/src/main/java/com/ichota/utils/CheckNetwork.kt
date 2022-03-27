package com.ichota.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkRequest

class CheckNetwork(val context: Context) {

    private fun registerNetworkCallbacks() {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkRequestBuilder = NetworkRequest.Builder()
        connectivityManager.registerDefaultNetworkCallback(object :
            ConnectivityManager.NetworkCallback() {

        })
    }
}
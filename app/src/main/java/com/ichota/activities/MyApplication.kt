package com.ichota.activities

import android.annotation.SuppressLint
import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.ichota.preferences.PreferenceHelper
import com.google.firebase.FirebaseApp

import com.google.firebase.FirebaseOptions




private const val TAG = "MyApplication"

class MyApplication : Application() {


    private var helper: PreferenceHelper? = null
    // private var mFusedLocationProviderClient: FusedLocationProviderClient? = null


   // @SuppressLint("HardwareIds", "MissingPermission")
    override fun onCreate() {
        super.onCreate()

       /* val options = FirebaseOptions.Builder()
            .setApplicationId("1:131360871416:android:82efbd9f41ac6e34ed7c0d") // Required for Analytics.
            .setProjectId("ichota-bf783") // Required for Firebase Installations.
            .setApiKey("AIzaSyCCgEwRIXrJQBORqHk-2uQKqjds4q25elY") // Required for Auth.
            .build()
        FirebaseApp.initializeApp(this, options, "Ichota")*/

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        helper = PreferenceHelper.getPreferences(this)
        /* mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
         if (Global.hasPermissions(
                 this,
                 arrayOf(
                     Manifest.permission.ACCESS_FINE_LOCATION,
                     Manifest.permission.ACCESS_COARSE_LOCATION
                 )
             )
         ) {
             Log.d(TAG, "onCreate:Appp location")
             mFusedLocationProviderClient?.lastLocation?.addOnSuccessListener {
                 helper?.saveStringValue(PrefKeys.KEY_LATITUDE, it.latitude.toString())
                 helper?.saveStringValue(PrefKeys.KEY_LONGITUDE, it.longitude.toString())


             }
         }*/

        // initPlacesApi()


    }

    /*   private fun initPlacesApi() {
           val apiKey = getString(R.string.api_key)

           if (!Places.isInitialized()){
               Places.initialize(applicationContext,apiKey)
           }
           val client : PlacesClient = Places.createClient(this)
       }*/


}
package com.ichota.activities

import android.content.Context
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


internal class  MyLocationListener(
   private val context: Context,
   private val callback : (Location) -> Unit

){
    fun start(){

    }

    fun stop(){

    }

}


class MyActivity : AppCompatActivity() {

    private lateinit var myLocationListener : MyLocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myLocationListener = MyLocationListener(this){

        }

    }

    public override fun onStart() {
        super.onStart()
        myLocationListener.start()
        // manage other components that need to respond
        // to the activity lifecycle
    }

    public override fun onStop() {
        super.onStop()
        myLocationListener.stop()
        // manage other components that need to respond
        // to the activity lifecycle
    }


}
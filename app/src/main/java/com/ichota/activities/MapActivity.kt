package com.ichota.activities

import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.ichota.R
import com.ichota.databinding.ActivityMapBinding

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    private lateinit var binding: ActivityMapBinding
    private var mLat: Double = 0.0
    private var mLon : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = Color.TRANSPARENT
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        )
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent?.let {
            mLat = it.getDoubleExtra(KEY_LATITUDE,0.0)
            mLon = it.getDoubleExtra(KEY_LONGITUDE,0.0)
        }
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
        binding.fabBack.setOnClickListener { finish() }


    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        mMap?.setMinZoomPreference(13F)
        val currentLocation = LatLng(mLat, mLon)
       // val markerOptions = MarkerOptions()
       // markerOptions.position(LatLng(mLat,mLon))
        val circleOptions = CircleOptions()
        circleOptions.center(currentLocation)
        circleOptions.fillColor(ContextCompat.getColor(this, R.color.colorMapCircle))
        circleOptions.strokeColor(ContextCompat.getColor(this, R.color.colorMapCircle))
        circleOptions.strokeWidth(0f)
        circleOptions.radius(1200.0)
        mMap?.addCircle(circleOptions)
       // mMap?.addMarker(markerOptions)
        mMap?.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))


    }

    companion object{
        const val KEY_LATITUDE = "key_latitude"
        const val KEY_LONGITUDE = "key_longitude"
    }
}
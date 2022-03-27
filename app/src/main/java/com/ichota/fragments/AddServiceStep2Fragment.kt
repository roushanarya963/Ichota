package com.ichota.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.ichota.R
import com.ichota.adapter.BoxSizeAdapter
import com.ichota.databinding.FragmentAddServiceStep2Binding
import com.ichota.interfaces.IMainActivity
import com.ichota.preferences.PrefKeys
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.viewModel.AddServiceViewModel
import com.ichota.viewModel.AuthViewModel


private const val TAG = "AddServiceStep2Fragment"
private const val REQUEST_CHECK_SETTINGS = 101
private const val AUTOCOMPLETE_REQUEST_CODE = 1
class AddServiceStep2Fragment : Fragment(), OnMapReadyCallback, BoxSizeAdapter.BoxSizeInterface {
    private lateinit var binding: FragmentAddServiceStep2Binding
    private val addServiceViewModel: AddServiceViewModel by activityViewModels()
    private var mMap: GoogleMap? = null
    private var mIMainActivity: IMainActivity? = null
    private lateinit var helper: PreferenceHelper
    private var authViewModel:AuthViewModel?=null
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mLat: Double = 0.0
    private var mLon: Double = 0.0
    private var mBoxSizeAdapter: BoxSizeAdapter? = null
    private lateinit var locationCallback: LocationCallback
    private var locationRequest:LocationRequest?=null
    var intent1: Intent? = null
    private var requestingLocationUpdates : Boolean =true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        helper = PreferenceHelper.getPreferences(requireContext())
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mBoxSizeAdapter = BoxSizeAdapter()
        mBoxSizeAdapter?.onBoxSelectListener(this)

        createLocationRequest()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {

                Log.d(TAG, "onLocationResult: $locationResult ")
                locationResult ?: return
                for (location in locationResult.locations){
                    setupMap(location.latitude, location.longitude)
                    stopLocationUpdates()
                    requestingLocationUpdates=false
                }
            }
        }

      //  getCurrentLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (Global.hasPermissions(
                requireContext(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        ) {
            mFusedLocationProviderClient?.lastLocation?.addOnCompleteListener {

                if (!it.isSuccessful) {
                    Log.d(TAG, "Fetching current location data failed")
                    return@addOnCompleteListener
                }
                val latLon = it.result
                mLat = latLon.latitude
                mLon = latLon.longitude

                helper.saveStringValue(PrefKeys.KEY_DEFAULT_LOCATION_LAT,mLat.toString())
                helper.saveStringValue(PrefKeys.KEY_DEFAULT_LOCATION_LON,mLon.toString())

                binding.tvLocation.text = Global.getCompleteAddress(requireContext(), mLat.toString(), mLon.toString())

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddServiceStep2Binding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
        setupListeners()
        setupObserver()
        Log.d(TAG, "onViewCreated: ${addServiceViewModel.getPostData.toString()}")
    }



    private fun stopLocationUpdates() {
        mFusedLocationProviderClient?.removeLocationUpdates(locationCallback)
    }

    fun createLocationRequest() {

        locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(requireContext())

        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->

        }



        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(requireActivity(),
                        REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }

    }


    private fun setupObserver() {
        addServiceViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showProgress(it)

        }
        addServiceViewModel.getMessageObserver.observe(viewLifecycleOwner) {
           mIMainActivity?.showMessage(it)
        }

        addServiceViewModel.getAddServiceObserver.observe(viewLifecycleOwner) {
            addServiceViewModel.removeAllImages()

            Handler().postDelayed({
                val vibrationEffect=requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrationEffect.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
                }
                showSuccessDialog()
            },300)
           // showSuccessDialog()
        }

    }

    private fun setupListeners() {
        binding.fabBack.setOnClickListener {
            it.findNavController().navigateUp()
        }

        binding.btPostService.setOnClickListener {
            addServiceViewModel.postService()
            // findNavController().navigate(R.id.action_nav_add_post_step_2_fragment_to_nav_product_search_fragment)
        }

        binding.tvLocation.setOnClickListener {
            val apiKey = getString(R.string.api_key)

            if (!Places.isInitialized()) {
                Places.initialize(requireContext(), apiKey)
            }
            val fields = listOf(Place.Field.LAT_LNG, Place.Field.ADDRESS)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(requireContext())
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

    }

    override fun onResume() {
        super.onResume()
        if(requestingLocationUpdates) startLocationUpdates()
        binding.mapView.onResume()

    }

    private fun startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mFusedLocationProviderClient?.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

    }


    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
        binding.mapView.onPause()

    }


    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        mMap?.uiSettings?.isMapToolbarEnabled = false
        setupMap(mLat, mLon)
        mMap?.setOnMapClickListener {

        }
    }

    private fun setupMap(lat: Double, lon: Double) {

        Log.d(TAG, "onMapReady: Lat => $lat  and Lon => $lon")
        binding.tvLocation.text = Global.getCompleteAddress(requireContext(), lat.toString(), lon.toString())
        if (lat == 0.0 && lon == 0.0) {
            Log.d(TAG, "onMapReady: Pref Lat => $lat  and Lon => $lon")

            /* var latitude = helper.getStringValue(PrefKeys.KEY_DEFAULT_LOCATION_LAT)?.toDouble()
             var longitude = helper.getStringValue(PrefKeys.KEY_DEFAULT_LOCATION_LON)?.toDouble()

             Log.d(TAG, "onMapReady: Pref Lat => $latitude  and Lon => $longitude")

              val currentLocation = longitude?.let { longitude ->
                 latitude?.let {
                         latitude -> LatLng(latitude, longitude)
                 }
             }*/

            val currentLocation = lat?.let { latitude ->
                lon?.let {
                        longitute -> LatLng(latitude, longitute)
                }
            }

            addServiceViewModel.getPostData.latitude = lat.toString()
            addServiceViewModel.getPostData.longitude = lon.toString()
            mMap?.clear()
            val marker = MarkerOptions()
            marker.position(currentLocation)
            mMap?.addMarker(marker)
            mMap?.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))

        } else {
            val currentLocation = LatLng(lat, lon)
            addServiceViewModel.getPostData.latitude = lat.toString()
            addServiceViewModel.getPostData.longitude = lon.toString()
            mMap?.clear()
            val marker = MarkerOptions()
            marker.position(currentLocation)
            mMap?.addMarker(marker)
            mMap?.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
        }

    }

    private fun showSuccessDialog() {

        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_posted_successfully)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        val tvmessage=dialog.findViewById<TextView>(R.id.tv_thank_you_description)
            tvmessage.setText(R.string.yourservicehasbeenlistedsuccessfully)
        val btOkay = dialog.findViewById<Button>(R.id.bt_okay)
        btOkay.setOnClickListener {
            dialog.dismiss()
            findNavController().popBackStack(R.id.nav_graph_home, false)
        }
        dialog.show()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                val place: Place = Autocomplete.getPlaceFromIntent(data ?: return)
                binding.tvLocation.text = place.address
                setupMap(place.latLng?.latitude ?: 0.0, place.latLng?.longitude ?: 0.0)

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data ?: return)
                Log.d(
                    TAG,
                    "onActivityResult: Erorr in fetching places data ${status.statusMessage}"
                )
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIMainActivity = context as IMainActivity
    }

    override fun onDetach() {
        super.onDetach()
        mIMainActivity = null
    }

    override fun onBoxSizeSelect(dimension: String) {
        addServiceViewModel.getPostData.boxSize = dimension
    }

}
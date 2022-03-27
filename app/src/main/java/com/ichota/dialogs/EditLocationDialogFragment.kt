package com.ichota.dialogs

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ichota.R
import com.ichota.databinding.DialogFragmentEditLocationBinding
import com.ichota.interfaces.IMainActivity
import com.ichota.preferences.PrefKeys
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.Global
import com.ichota.viewModel.LocationViewModel


private const val TAG = "EditLocationDialogFragment"
private const val AUTOCOMPLETE_REQUEST_CODE = 1

class EditLocationDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: DialogFragmentEditLocationBinding
    private val locationViewModel: LocationViewModel by viewModels()
    private lateinit var helper: PreferenceHelper
    private var mCallback: LocationSelection? = null
    private val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    private var mLat: Double = 0.0
    private var mLon: Double = 0.0
    private lateinit var locationManager: LocationManager
    var gpsStatus = false
    var intent1: Intent? = null

    private var mIMainActivity: IMainActivity? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        helper = PreferenceHelper.getPreferences(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFragmentEditLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        setupObserver()
        setupListeners()
    }


    private fun initData() {

       try {
           mLat = (helper.getStringValue(PrefKeys.KEY_LATITUDE) ?: "0.0").toDouble()
           mLon = (helper.getStringValue(PrefKeys.KEY_LONGITUDE) ?: "0.0").toDouble()
           setupAddressData(mLat, mLon)
       } catch (e:NumberFormatException){
           mLat = 0.0
           mLon = 0.0
       }

    }

    private fun setupObserver() {

        locationViewModel.getAddressObserver.observe(viewLifecycleOwner) {
            setupAddressData(
                it.latLong.lattitude.toDouble(),
                it.latLong.longitude.toDouble()
            )
        }
        locationViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            Global.showMessage(binding.root, it)
        }
        locationViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
            Log.d("TAG", "setupObserver:$it")

        }

    }

    @SuppressLint("MissingPermission")
    private fun setupListeners() {
        binding.fabBack.setOnClickListener { dismiss() }

        binding.btWithinLocation.setOnClickListener {

            locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

            if (gpsStatus) {

                binding.progressBar.visibility = View.VISIBLE
                binding.ivPin.visibility=View.GONE

                if (Global.hasPermissions(requireContext(), permissions)) {

                    /*LocationServices.getFusedLocationProviderClient(requireContext()).lastLocation.addOnSuccessListener {
                        mLat = it.latitude
                        mLon = it.longitude

                        setupAddressData(mLat, mLon)
                        binding.progressBar.visibility = View.GONE
                        binding.ivPin.visibility=View.VISIBLE
                    }*/

                    Handler(Looper.getMainLooper()).postDelayed({
                        LocationServices.getFusedLocationProviderClient(requireContext()).lastLocation.addOnSuccessListener {
                            mLat = it.latitude
                            mLon = it.longitude
                            setupAddressData(mLat, mLon)
                            binding.progressBar.visibility = View.GONE
                            binding.ivPin.visibility=View.VISIBLE
                        }
                    }, 3000)

                }

            } else {

                val builder = MaterialAlertDialogBuilder(requireContext())
                builder.setTitle("Alert!")
                builder.setMessage("Are you want to enable the GPS?.")
                builder.setPositiveButton("YES"){ dialog,which->
                    intent1 = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent1)
                }
                builder.setNegativeButton("No"){dialog,which->

                    builder.setCancelable(true)
                }
                val dialog = builder.create()
                dialog.show()

            }

/*
            binding.progressBar.visibility = View.VISIBLE
            if (Global.hasPermissions(
                    requireContext(),
                    permissions
                )
            ) {

                LocationServices.getFusedLocationProviderClient(requireContext()).lastLocation.addOnSuccessListener {
                    mLat = it.latitude
                    mLon = it.longitude
                    setupAddressData(it.latitude, it.longitude)
                    binding.progressBar.visibility = View.GONE
                }
            }
*/

        }

        binding.btSaveAddress.setOnClickListener {
            Log.d("TAG", "setupListeners: BUTTON_APPLY_CLICKED")
            helper.saveStringValue(PrefKeys.KEY_LATITUDE, mLat.toString())
            helper.saveStringValue(PrefKeys.KEY_LONGITUDE, mLon.toString())
            mCallback?.onLocationSelect()
            dismiss()

        }
        
        binding.rbZipCode.setOnEditorActionListener { textView, i, keyEvent ->

            if (i == EditorInfo.IME_ACTION_SEARCH){
                mIMainActivity?.hideSoftKeyboard(binding.etPinCode)
                val zipcode = binding.etPinCode.text.toString().trim()
                locationViewModel.getAddressFromZipCode(zipcode)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        binding.etPinCode.setOnEditorActionListener {  textview, i, keyEvent->

            if (i==EditorInfo.IME_ACTION_SEARCH){
                mIMainActivity?.hideSoftKeyboard(binding.etPinCode)
                val zipcode = binding.etPinCode.text.toString().trim()
                locationViewModel.getAddressFromZipCode(zipcode)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false

        }

        binding.rbAddress.setOnClickListener {

                val apiKey = getString(R.string.api_key)
                 binding.rbZipCode.isChecked=true
                if (!Places.isInitialized()) {
                    Places.initialize(requireContext(), apiKey)
                }
                val fields = listOf(Place.Field.LAT_LNG, Place.Field.ADDRESS)
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(requireContext())
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)

        }

    }


    private fun setupAddressData(lat: Double, lon: Double) {

        try {

            mLat = lat
            mLon = lon

            binding.tvAddress.text = Global.getCompleteAddress(
                requireContext(),
                mLat.toString(),
                mLon.toString()
            )

            binding.etPinCode.setText(
                Global.getZipCode(
                    requireContext(),
                    lat.toString(),
                    lon.toString()
                )
            )

        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }

    }

    @SuppressLint("LongLogTag")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val place: Place = Autocomplete.getPlaceFromIntent(data ?: return)
                binding.tvAddress.text = place.address
                Log.d(TAG, "Fetch selected places => ${place.latLng}")

                setupAddressData(
                    place.latLng?.latitude ?: 0.0,
                    place.latLng?.longitude ?: 0.0
                )

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data ?: return)

                Log.d(TAG, "Unable to get Places data status is => ${status.statusMessage}")
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = EditLocationDialogFragment()

    }


    fun onLocationSelectedListener(callback: LocationSelection) {
        this.mCallback = callback

    }

    interface LocationSelection {
        fun onLocationSelect()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIMainActivity = context as IMainActivity
    }

    override fun onDetach() {
        super.onDetach()
        mIMainActivity = null
    }

}
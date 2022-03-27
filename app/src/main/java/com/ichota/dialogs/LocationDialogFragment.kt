package com.ichota.dialogs

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ichota.R
import com.ichota.databinding.DialogFragmentLocationBinding
import com.ichota.preferences.PrefKeys
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.Global


private const val TAG = "LocationDialogFragment"

class LocationDialogFragment : BottomSheetDialogFragment(),
    EditLocationDialogFragment.LocationSelection {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: DialogFragmentLocationBinding
    internal lateinit var helper: PreferenceHelper
    private var mCallback : ILocationDialogFragment? =null
    var intent1: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        helper = PreferenceHelper.getPreferences(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        setupListener()
    }



    private fun initData() {

        binding.tvCurrentLocation.text = Global.getCompleteAddress(
            requireContext(),
            helper.getStringValue(PrefKeys.KEY_LATITUDE) ?: "0.0",
            helper.getStringValue(PrefKeys.KEY_LONGITUDE) ?: "0.0"
        )
        binding.sliderDistance.value = helper.getInt(PrefKeys.KEY_SEARCH_DISTANCE, 5).toFloat()

        binding.tvDistanceInitialValue.text=
            helper.getInt(PrefKeys.KEY_SEARCH_DISTANCE, 5).toFloat().toString()

    }

    private fun setupListener() {

        binding.btEdit.setOnClickListener {
            val editLocationDialogFragment = EditLocationDialogFragment.newInstance()
            editLocationDialogFragment.onLocationSelectedListener(this)
            editLocationDialogFragment.show(requireActivity().supportFragmentManager, TAG)
        }

        binding.btApply.setOnClickListener {
            helper.saveInt(PrefKeys.KEY_SEARCH_DISTANCE, binding.sliderDistance.value.toInt())
            dismiss()
        }

        binding.btCancel.setOnClickListener { dismiss() }

        binding.btReset.setOnClickListener {

            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.alert))
                    .setMessage(getString(R.string.messageAllowLocationPermission))
                    .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                        intent1 = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent1)
                    }

            }else{
                getCurrentLocation()
                initData()
            }
           /* getCurrentLocation()
            initData()*/
        }

        binding.tvCurrentLocation.setOnClickListener {
            binding.btEdit.callOnClick()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            LocationDialogFragment()

    }

    override fun onLocationSelect() {
        initData()
    }

  private  fun getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.alert))
                .setMessage(getString(R.string.messageAllowLocationPermission))
                .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                    dialog.dismiss()
                }
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener {
            try {
                helper.saveStringValue(PrefKeys.KEY_LATITUDE, it.latitude.toString())
                helper.saveStringValue(PrefKeys.KEY_LONGITUDE, it.longitude.toString())
            } catch (e: Exception) {

            }

        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        mCallback?.onDialogDismiss()
    }

    fun setOnLocationDialogListeners(callback : ILocationDialogFragment){
        mCallback = callback
    }

    interface ILocationDialogFragment{
        fun onDialogDismiss()
    }
}
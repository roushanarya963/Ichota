package com.ichota.publicProfile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ichota.NavGraphDirections
import com.ichota.R
import com.ichota.databinding.FragmentPublicProfileSubBinding
import com.ichota.model.User

private const val TAG = "PublicProfileSub"
private const val KEY_USER_DATA = "key_user_data"

class PublicProfileSubFragment : Fragment()/*,OnMapReadyCallback*/ {
    private lateinit var binding: FragmentPublicProfileSubBinding
   // private var mMap: GoogleMap? = null
    private var mUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPublicProfileSubBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //binding.mapView.onCreate(savedInstanceState)
        //binding.mapView.getMapAsync(this)
        setupUserdata()
        setupListeners()
    }


    private fun setupUserdata() {

        Log.d(TAG, "setupUserdata: $mUser")

        binding.cbEmailVerify.isChecked = mUser?.emailStatus.equals("1")
        binding.cbInstaVerify.isChecked = mUser?.instagramStatus.equals("1")
        binding.cbPhoneVerify.isChecked = mUser?.phoneNumStatus.equals("1")
        binding.cbAccountVerify.isChecked = mUser?.secureStatus.equals("1")
        binding.cbFacebookVerify.isChecked = mUser?.facebookStatus.equals("1")

        binding.tvEmailStatus.text =
            if (mUser?.emailStatus.equals("0")) getString(R.string.notVerified) else getString(R.string.verified)

        binding.tvInstaStatus.text =
            if (mUser?.emailStatus.equals("0")) getString(R.string.notVerified) else getString(R.string.verified)

        binding.tvPhoneStatus.text =
            if (mUser?.emailStatus.equals("0")) getString(R.string.notVerified) else getString(R.string.verified)

        binding.tvAccountStatus.text =
            if (mUser?.emailStatus.equals("0")) getString(R.string.notVerified) else getString(R.string.verified)

        binding.tvFacebookStatus.text =
            if (mUser?.emailStatus.equals("0")) getString(R.string.notVerified) else getString(R.string.verified)

        binding.tvFollowers.text = "${mUser?.followerCount} users"

        binding.tvFollowing.text = "${mUser?.followingCount} users"
    }

    private fun setupListeners() {

        binding.tvFollowers.setOnClickListener {
            moveToMyNetwork()
        }

        binding.tvFollowing.setOnClickListener {
            moveToMyNetwork()

        }
    }

    private fun moveToMyNetwork() {
        val direction =
            NavGraphDirections.actionGlobalNavMyNetworkPagerFragment(
                mUser?.userId ?: ""
            )
        findNavController().navigate(direction)
    }

    override fun onResume() {
        super.onResume()
        // binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        //binding.mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
       // binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
       // binding.mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
       // binding.mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        //binding.mapView.onDestroy()


    }


   /* override fun onMapReady(googleMap: GoogleMap?) {
        Log.d(TAG, "onMapReady: ")
        mMap = googleMap
        mMap?.setMinZoomPreference(12F)
        val currentLocation =
            LatLng(

                mUser?.latitude?.toDouble() ?: 0.0,
                mUser?.longitude?.toDouble() ?: 0.0
            )
        val marker = MarkerOptions()
        marker.position(currentLocation)
        mMap?.addMarker(marker)
        mMap?.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))

    }*/


    companion object {
        fun newInstance(user: User) = PublicProfileSubFragment().apply {
            mUser = user
        }
    }
}

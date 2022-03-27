package com.ichota.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ichota.R
import com.ichota.databinding.ActivityMainBinding
import com.ichota.interfaces.IMainActivity
import com.ichota.preferences.PrefKeys
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.viewModel.AuthViewModel
import com.ichota.viewModel.ProfileViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val RC_PERMISSIONS = 101
private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), IMainActivity {

    private lateinit var binding: ActivityMainBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private var navController : NavController? =null
    private  var helper: PreferenceHelper?=null
    private var authViewModel:AuthViewModel ? = null
    private var count: Int =1

    private val permissions = arrayOf(

        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
     //   Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        init()
        setupObserver()


    }



    private fun init() {

        GlobalScope.launch {
            helper = PreferenceHelper.getPreferences(this@MainActivity)
            checkForPermission()
        }
            setupNavigation()
          //  setupObserver()

    }

    override fun onResume() {
        super.onResume()
        profileViewModel.getUserProfile(helper?.getCurrentUser()?.userId ?: "")
        authViewModel?.unReadChatCount(helper?.getCurrentUser()?.userId!!)
        authViewModel?.toTalUnseenNotificatio(helper?.getCurrentUser()?.userId.toString())
    }

    private fun checkForPermission() {
        if (!Global.hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, RC_PERMISSIONS)
        }
    }

    private fun setupObserver() {

        profileViewModel.getUserObserver.observe(this) {
            if (it.isNotEmpty()){
                helper?.saveCurrentUser(it[0])
            }
        }

        authViewModel?.getUnSeenNotificationCount?.observe(this){

            if(it.success== Constants.RESPONSE_SUCCESS){
                helper?.saveInt(PrefKeys.kEY_UNREADNOTIFICATION_COUNT,it.data.totalUnseenNotification.toInt())
                getUnSeenNotificationCount(it.data.totalUnseenNotification.toInt())
            }
            Log.d(TAG, "setupObserver: $it")
        }

        authViewModel?.getUnReadChatCount?.observe(this){
            if(it.success==Constants.RESPONSE_SUCCESS){
                helper?.saveInt(PrefKeys.kEY_UNREADNOTIFICATION_COUNT,it.data.count.toInt())
                upUnReadChatCount(it.data.count.toInt())
            }
        }

    }


    private fun setupNavigation() {
        val navHostFragment: NavHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        NavigationUI.setupWithNavController(binding.bottomNavigation, navHostFragment.navController)
        navController = navHostFragment.navController
        navController?.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.nav_graph_home  -> showBottomBar(true)
                R.id.nav_graph_message -> showBottomBar(true)
                R.id.nav_post_options_fragment -> showBottomBar(true)
                R.id.nav_notification_fragment -> showBottomBar(true)
                R.id.nav_account_fragment -> showBottomBar(true)
                else -> showBottomBar(false)
            }
        }
        
    }

    private fun upUnReadChatCount(i: Int?) {
        val navBar  = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        if (i != null) {
            navBar.getOrCreateBadge(R.id.nav_graph_message).number = i
        }
    }

    private fun getUnSeenNotificationCount(i: Int?) {
        val navBar  = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        if (i != null) {
            navBar.getOrCreateBadge(R.id.nav_notification_fragment).number = i
        }
    }


  private  fun showBottomBar(show: Boolean) {
        binding.bottomNavigation.visibility = if (show) View.VISIBLE else View.GONE
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == RC_PERMISSIONS && grantResults.isNotEmpty()) {
            val isLocationAllowed = grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (isLocationAllowed) {
                LocationServices.getFusedLocationProviderClient(this).lastLocation.addOnSuccessListener {
                    Log.d(TAG, "onRequestPermissionsResult: ")
                    try {
                        if (helper?.getStringValue(PrefKeys.KEY_LATITUDE)
                                .isNullOrEmpty() || helper?.getStringValue(PrefKeys.KEY_LONGITUDE)
                                .isNullOrEmpty()
                        ){
                            helper?.saveStringValue(PrefKeys.KEY_LATITUDE, it.latitude.toString())
                            helper?.saveStringValue(PrefKeys.KEY_LONGITUDE, it.longitude.toString())
                        }

                    } catch (e: Exception) {

                        if (helper?.getStringValue(PrefKeys.KEY_LATITUDE)
                                .isNullOrEmpty() || helper?.getStringValue(PrefKeys.KEY_LONGITUDE)
                                .isNullOrEmpty()
                        ){
                            helper?.saveStringValue(PrefKeys.KEY_LATITUDE, "0")
                            helper?.saveStringValue(PrefKeys.KEY_LONGITUDE, "0")
                        }

                    }
                }

            } else {
                ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    RC_PERMISSIONS
                )
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun showMessage(message: String) {
        Global.showMessage(binding.root, message)
    }

    override fun showProgress(isShowing: Boolean) {
        if (isShowing)
            binding.progressBar.root.visibility = View.VISIBLE
        else
            binding.progressBar.root.visibility = View.GONE
    }

    override fun getPreference(): PreferenceHelper {
        return helper!!
    }

    override fun getContext(): Context {
        return this
    }

    override fun hideSoftKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun showSoftKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

}


package com.ichota.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.ichota.NavGraphDirections
import com.ichota.R
import com.ichota.adapter.SalesServicesPagerAdapter
import com.ichota.databinding.FragmentHomeBinding
import com.ichota.dialogs.LocationDialogFragment
import com.ichota.interfaces.IMainActivity
import com.ichota.preferences.PrefKeys
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.Global

private const val TAB_CATEGORY = 1
private const val TAB_SALE = 2


private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private var mIMainActivity: IMainActivity? = null
    private var isUserLogIn = false
    private var connectivityManager: ConnectivityManager? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isUserLogIn = PreferenceHelper.getPreferences(requireContext()).getBoolean(PrefKeys.KEY_IS_USER_LOG_IN)
        connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ServiceCast")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tabHome.animation = AnimationUtils.loadAnimation(requireContext(), R.anim.anim_home_tab)

        if (checkForInternet(requireContext())) {
            //   Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
            binding.emptyFileSearchInternet.root.visibility=View.GONE
            initHomePager()
        } else {
            //  Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show()
            binding.emptyFileSearchInternet.root.visibility=View.VISIBLE
        }
       // initHomePager()
        setupListeners()
    }

    private fun checkForInternet(requireContext: Context): Boolean {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val network = connectivityManager?.activeNetwork ?: return false
            val activeNetwork = connectivityManager?.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        }else{
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager?.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }

    }

    private fun initHomePager() {

        binding.homePager.adapter = SalesServicesPagerAdapter(this)

       /* if (isUserLogIn==false){
            Global.showLoginAlertMessage(requireActivity())
        }else{
            TabLayoutMediator(binding.tabHome,binding.homePager){tab,position ->
                tab.text = if (position == 0) getString(R.string.sales) else getString(R.string.services)
            }.attach()
        }*/

        TabLayoutMediator(binding.tabHome,binding.homePager){tab,position ->
            tab.text = if (position == 0) getString(R.string.sales) else getString(R.string.services)
        }.attach()

    }

    private fun setupListeners() {

      /*  binding.ivSearch.setOnClickListener {
            it.findNavController().navigate(R.id.action_nav_graph_home_to_productSearchFragment)
        }*/

        binding.etSearch.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                val directions=  NavGraphDirections.actionGlobalNavProductSearchFragment(
                    binding.etSearch.text.toString().trim()
                )
                findNavController().navigate(directions)
            }
            true
        }

        binding.ivLocation.setOnClickListener {
            LocationDialogFragment.newInstance().show(requireActivity().supportFragmentManager, TAG)
        }

    }

    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIMainActivity = context as IMainActivity
    }

    override fun onDetach() {
        super.onDetach()
        mIMainActivity?.showProgress(false)
    }
}
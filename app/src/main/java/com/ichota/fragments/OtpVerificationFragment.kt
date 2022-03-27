package com.ichota.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI.navigateUp
import com.ichota.NavGraphDirections
import com.ichota.R
import com.ichota.auth.LoginActivity
import com.ichota.databinding.FragmentOtpVertificationBinding
import com.ichota.interfaces.IMainActivity
import com.ichota.preferences.PrefKeys
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.viewModel.AuthViewModel
import com.ichota.viewModel.ProfileViewModel
import kotlin.system.exitProcess


private const val TAG = "OtpVerificationFragment"

class OtpVerificationFragment : Fragment() {
    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var binding: FragmentOtpVertificationBinding
    private val navArgs: OtpVerificationFragmentArgs by navArgs()
    private var mIMainActivity: IMainActivity? = null
    private var mOTP : String? = null
    private val authViewModel: AuthViewModel by viewModels()
    private  var otp : String ? = null
    private var helper: PreferenceHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        helper=PreferenceHelper.getPreferences(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOtpVertificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
        setupListener()
    }

    private fun setupObserver() {

        authViewModel.getProgress.observe(viewLifecycleOwner){
            mIMainActivity?.showProgress(it)
        }

        authViewModel.getMessage.observe(viewLifecycleOwner){
            mIMainActivity?.showMessage(it)
        }

        authViewModel.getVerifyOtpObserver.observe(viewLifecycleOwner){
          //  mIMainActivity?.getPreference()?.saveCurrentUser(it)
           // findNavController().popBackStack(R.id.nav_account_fragment,false)

            if (it.success == Constants.RESPONSE_SUCCESS) {

                //  Global.showMessage(binding.root, it.messageText)

               var directions=NavGraphDirections.actionGlobalNavAccountFragment()
                findNavController().navigate(directions)

            }

        }
    }

    private fun setupListener() {


        binding.fabBack.setOnClickListener {
       // var directions=OtpVerificationFragmentDirections.actionGlobalNavAccountFragment()
          //  it.findNavController().navigateUp()
            it.findNavController().popBackStack()
        }

        binding.otpView.setOtpCompletionListener {
            Log.d(TAG, "setupListener: $it")
            mOTP  = it
        }

        binding.btValidate.setOnClickListener {

            if (mOTP.isNullOrEmpty()){
                Global.showMessage(binding.root,getString(R.string.messageOptRequired))
                return@setOnClickListener
            }

            otp = helper?.getStringValue(PrefKeys.KEY_EDIT_MOBILE_NUMBER_OTP)

            if(otp!=null){

                if(mOTP==otp){
                    Toast.makeText(requireContext(),"Phone Number verification Success", Toast.LENGTH_LONG).show()
                    helper?.saveStringValue(PrefKeys.KEY_SET_PHONE_NUMBER,navArgs.value)
                    var directions=NavGraphDirections.actionGlobalNavAccountFragment()
                    findNavController().navigate(directions)
                }else{
                    Toast.makeText(requireContext(),getString(R.string.pleaseentercorrectpin), Toast.LENGTH_LONG).show()
                }
            }
        }


    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = OtpVerificationFragment()
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






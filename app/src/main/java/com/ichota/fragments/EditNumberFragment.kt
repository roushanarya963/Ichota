package com.ichota.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ichota.NavGraphDirections
import com.ichota.R
import com.ichota.activities.OtpVerification
import com.ichota.databinding.FragmentEditNumberBinding
import com.ichota.preferences.PrefKeys
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.viewModel.AuthViewModel
import kotlin.time.Duration.Companion.days


private const val TAG = "EditNumberFragment"
class EditNumberFragment : Fragment() {
    private lateinit var binding : FragmentEditNumberBinding
    private val navArgs : EditNumberFragmentArgs by navArgs()
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var mProgressDialog: Dialog
    private var helper:PreferenceHelper?=null
    private var phone: String?=null

    private lateinit var intent : Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditNumberBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        helper=PreferenceHelper.getPreferences(requireContext())
        mProgressDialog = Global.getProgressDialog(requireActivity())
        binding.etNumber.setText(navArgs.number)
        setupListener()
        setupObservers()
    }

    private fun setupObservers() {

        authViewModel.getMessage.observe(requireActivity()) {
            Global.showMessage(binding.root, it)
        }

        authViewModel.getProgress.observe(requireActivity()) {
            if (it) mProgressDialog.show() else mProgressDialog.dismiss()
        }

        authViewModel.getVerifyOtpObserver.observe(requireActivity()) {

            if (it.success == Constants.RESPONSE_SUCCESS) {
                it.data?.otp
                helper?.saveStringValue(PrefKeys.KEY_EDIT_MOBILE_NUMBER_OTP,
                    it.data?.otp.toString()
                )
                var direction = NavGraphDirections.actionGlobalOtpVerificationFragment(phone.toString())
                findNavController().navigate(direction)
            }

        }

    }

    private fun setupListener() {

        binding.fabBack.setOnClickListener {
            it.findNavController().navigateUp()
        }

        binding.btSave.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
         phone = binding.etNumber.text.toString().trim()
        helper?.saveStringValue(PrefKeys.KEY_EDIT_MOBILE_NUMBER, phone!!)
        if (phone!!.isEmpty()){
            Global.showMessage(binding.root,getString(R.string.messageNumberRequired))
            return
        }else if(phone!!.length<10 && phone!!.length>0){
            Global.showMessage(binding.root,getString(R.string.numbershouldbetendigits))
        }else if(phone=="0000000000"){
            Global.showMessage(binding.root,getString(R.string.numbershouldcontainzeroonly))
        } else{
            authViewModel.verifyMobile("+91"+phone)
        }

        //authViewModel.verifyMobile("+91"+phone)

    }

    companion object {
        @JvmStatic
        fun newInstance() = EditNumberFragment()

    }

}



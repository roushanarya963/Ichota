package com.ichota.profile

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hbb20.CountryCodePicker
import com.ichota.NavGraphDirections
import com.ichota.R
import com.ichota.databinding.FragUserProfileSubBinding
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.viewModel.ProfileViewModel

private const val TAG = "UserProfileSubFragment"

class UserProfileSubFragment : Fragment() {
    private lateinit var binding: FragUserProfileSubBinding
    private var updateContactDialog: Dialog? = null
    private lateinit var helper: PreferenceHelper
    private val profileViewModel: ProfileViewModel by viewModels()
    private var progressDialog: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        helper = PreferenceHelper.getPreferences(requireContext())
        progressDialog = Global.getProgressDialog(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragUserProfileSubBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //binding.mapView.onCreate(savedInstanceState)
        // binding.mapView.getMapAsync(this)
        setupObserver()
        setupListeners()
        setupUserdata()


    }

    private fun setupObserver() {
        profileViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            Global.showMessage(binding.root, it)

        }
        profileViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            if (it) progressDialog?.show() else progressDialog?.dismiss()
        }

        profileViewModel.getUserObserver.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()){
                helper.saveCurrentUser(it[0])

            }
            setupUserdata()

        }


        profileViewModel.getEmergencyContactObserver.observe(viewLifecycleOwner) {
            if (it.success == Constants.RESPONSE_SUCCESS) {
                binding.switchEmergency.isChecked = true
                updateContactDialog?.dismiss()
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.success))
                    .setMessage(it.message)
                    .setPositiveButton(getString(R.string.ok)){dialog,which ->

                        dialog.dismiss()
                        profileViewModel.getUserProfile(
                            helper.getCurrentUser()?.userId ?: ""
                        )
                    }.show()

            }

        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUserdata() {
        val mUser = helper.getCurrentUser()
        Log.d(TAG, "setupUserdata: $mUser")
        mUser?.let {

            binding.cbEmailVerify.isChecked = it.emailStatus == "1"
            binding.cbInstaVerify.isChecked = it.instagramStatus == "1"
            binding.cbPhoneVerify.isChecked = it.phoneNumStatus == "1"
            binding.cbAccountVerify.isChecked = it.secureStatus == "1"
            binding.cbFacebookVerify.isChecked = it.facebookStatus == "1"
            binding.switchEmergency.isChecked = it.emergencyContact.isNotEmpty()&&it.emergencyContact != "0"

            binding.cbPaymentVenmo.isChecked = it.emailStatus == "1"
            binding.cbPaymentZelle.isChecked = it.instagramStatus == "1"
            binding.cbPaymentPaypal.isChecked = it.phoneNumStatus == "1"
            binding.cbPaymentStripe.isChecked = it.secureStatus == "1"
            binding.cbPaymentApplePay.isChecked = it.facebookStatus == "1"
            binding.cbPaymentGooglePay.isChecked = it.emergencyContact == "1"

            binding.tvEmailStatus.text = getVerificationStatus(it.emailStatus)
            binding.tvInstaStatus.text = getVerificationStatus(it.instagramStatus)
            binding.tvPhoneStatus.text = getVerificationStatus(it.phoneNumStatus)
            binding.tvAccountStatus.text = getVerificationStatus(it.secureStatus)
            binding.tvFacebookStatus.text = getVerificationStatus(it.facebookStatus)

            val followers = "${it.followerCount} users"
            val followings = "${it.followingCount} users"
            binding.btFollowers.text = followers
            binding.btFollowing.text = followings

        }


        binding.root.setOnScrollChangeListener { view, scrollX, scrollY, oldScrollX, oldScrollY ->

            Log.d(TAG, "check: i =$scrollX   scrollY= $scrollY   i3= $oldScrollX   oldScrollY= $oldScrollY")
            if (scrollY >= oldScrollY){
                Log.d(TAG, "check1: Scroll Up    " )
            }
            else{
                Log.d(TAG, "check1: Scroll Down")
            }
        }


        binding.root.setOnTouchListener { view, motionEvent ->
            var touchPosition: Float = 0f
            var releasePosition: Float = 0f

            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                touchPosition = motionEvent.y

            }

            if (motionEvent.action == MotionEvent.ACTION_UP) {
                releasePosition = motionEvent.y
            }


            if (touchPosition - releasePosition > 0){
                Log.d(TAG, "setupUserdata: Scroll down")
            }
            else{
                Log.d(TAG, "setupUserdata: Scroll up")
            }

            return@setOnTouchListener false

        }


    }

    private fun getVerificationStatus(status: String?): String {
        return if (status == "0")
            getString(R.string.notVerified)
        else getString(R.string.verified)

    }

    private fun setupListeners() {

        binding.tvEmergencyContactHeader.setOnClickListener {
            if (binding.switchEmergency.isChecked) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.info))
                    .setMessage(getString(R.string.messageEmergencyContactUpdatedAlready,helper.getCurrentUser()?.emergencyContact))
                    .setPositiveButton(getString(R.string.update)) { dialog, which ->
                        dialog.dismiss()
                        updateEmergencyContact()
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            } else {
                updateEmergencyContact()
            }
        }

        binding.btFollowing.setOnClickListener {
            val direction = NavGraphDirections.actionGlobalNavMyNetworkPagerFragment(helper.getCurrentUser()?.userId?:"")
            it.findNavController().navigate(direction)
        }
        binding.btFollowers.setOnClickListener {
            val direction = NavGraphDirections.actionGlobalNavMyNetworkPagerFragment(helper.getCurrentUser()?.userId?:"")
            it.findNavController().navigate(direction)
        }

        binding.btAccountVerification.setOnClickListener {

            val directions = NavGraphDirections.actionGlobalNavAccountVerificationFragment()
            it.findNavController().navigate(directions)

           /* it.findNavController()
                .navigate(R.id.action_nav_graph_profile_to_accountVerificationFragment)*/
        }

    }

    override fun onStart() {
        super.onStart()
        profileViewModel.getUserProfile(
            helper.getCurrentUser()?.userId ?: ""
        )
    }


    private fun updateEmergencyContact() {
        updateContactDialog = Dialog(requireContext())
        updateContactDialog?.setContentView(R.layout.dialog_contact_information)
        updateContactDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        updateContactDialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        updateContactDialog?.show()

        val btCancel = updateContactDialog?.findViewById<Button>(R.id.bt_cancel)
        val btSave = updateContactDialog?.findViewById<Button>(R.id.bt_save)
        val etEmergencyContact =
            updateContactDialog?.findViewById<EditText>(R.id.et_emergency_contact_number)
        val ccp = updateContactDialog?.findViewById<CountryCodePicker>(R.id.ccp)

        btCancel?.setOnClickListener {
            binding.switchEmergency.isChecked = false
            updateContactDialog?.dismiss()
        }
        btSave?.setOnClickListener {
            val number = etEmergencyContact?.text.toString().trim()
            if (!TextUtils.isEmpty(number)) {
                val contact = "${ccp?.selectedCountryCodeWithPlus}${
                    etEmergencyContact?.text?.toString()?.trim()
                }"
                Log.d(TAG, "onViewCreated: fbnaboanbnao;")
                profileViewModel.updateEmergencyContact(
                    helper.getCurrentUser()?.userId ?: "",
                    contact
                )
            } else {
                Global.showMessage(binding.root, getString(R.string.messageNumberRequired))
            }
        }

    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = UserProfileSubFragment()
    }

}


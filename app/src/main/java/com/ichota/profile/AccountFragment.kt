package com.ichota.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.facebook.*
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ichota.NavGraphDirections
import com.ichota.activities.MainActivity
import com.ichota.R
import com.ichota.auth.LoginActivity
import com.ichota.databinding.FragmentAccountBinding
import com.ichota.dialogs.AddAddressDialogFragment
import com.ichota.dialogs.AddEmergencyContactDialogFragment
import com.ichota.dialogs.LocationDialogFragment
import com.ichota.interfaces.IMainActivity
import com.ichota.model.User
import com.ichota.preferences.PrefKeys
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.Global
import com.ichota.viewModel.AuthViewModel
import com.ichota.viewModel.ProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


private const val TAG = "AccountFragment"
private const val EMAIL = "email"

// private const val TEST_LINK = "https://www.freeprivacypolicy.com/blog/privacy-policy-url/"

private const val TEST_LINK = "https://ichotaa.appdeft.biz/privacy_policy.php"
class AccountFragment : Fragment(), LocationDialogFragment.ILocationDialogFragment {

    private lateinit var binding: FragmentAccountBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private var mUser: User? = null
    private var mIMainActivity: IMainActivity? = null
    private var mCallbackManager: CallbackManager? = null
    private var helper:PreferenceHelper? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        helper=PreferenceHelper.getPreferences(requireContext())
      //  setupProfileData()

        if(helper?.getStringValue(PrefKeys.KEY_ADD_ADDRESS)!=null){
            binding.btUserLocation.text=helper?.getStringValue(PrefKeys.KEY_ADD_ADDRESS)

        }else{
            binding.btUserLocation.text = Global.getCompleteAddress(
                requireContext(),
                mIMainActivity?.getPreference()?.getStringValue(PrefKeys.KEY_LATITUDE) ?: "0.0",
                mIMainActivity?.getPreference()?.getStringValue(PrefKeys.KEY_LONGITUDE) ?: "0.0"
            )
        }

        setupListeners()
        setupObservers()
        setupFaceBookSignIn()
    }

    private fun setupObservers() {
        authViewModel.getProgress.observe(viewLifecycleOwner) {
            mIMainActivity?.showProgress(it)
        }

        authViewModel.getMessage.observe(viewLifecycleOwner) {
            mIMainActivity?.showMessage(it)
        }

        authViewModel.getUpdateSocialObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.getPreference()?.saveCurrentUser(it.data[0])
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.success))
                .setMessage(getString(R.string.messageFacebookConnect))
                .setPositiveButton(getString(R.string.close)){dialog,which ->
                    dialog.dismiss()
                }
                .show()
        }

    }


    private fun setupProfileData() {

        mUser = mIMainActivity?.getPreference()?.getCurrentUser()
        Glide.with(requireActivity())
            .load(Global.getImageUrl(mUser?.userImage))
            .placeholder(R.drawable.img_user_placeholder)
            .error(R.drawable.img_user_placeholder)
            .into(binding.ivUser)



        binding.btAddEmergencyContact.setOnClickListener {

           AddEmergencyContactDialogFragment.newInstance().show(requireActivity().supportFragmentManager, TAG)

        }

        binding.tvUserName.text = mUser?.name ?: ""
        val bio = Global.formatItemListingDate(mUser?.createDtm ?: "")

        binding.tvUserJoinedDate.text = bio
        binding.btUserName.text = mUser?.name
        
        var usermobilenumber=mUser?.mobile

        Log.d(TAG, "setupProfileData: ${mUser?.mobile}")

        binding.btUserEmail.text = mUser?.email

       // val mobilenumber=SpannableStringBuilder(mUser?.mobile) //91+8709264807

       /* mobilenumber.setSpan(
            ForegroundColorSpan(Color.BLACK),
            3,
            9,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )*/

        if(helper?.getStringValue(PrefKeys.KEY_SET_PHONE_NUMBER)==null){
            binding.btUserNum.text = mUser?.mobile
        }else{
            binding.btUserNum.text=helper?.getStringValue(PrefKeys.KEY_SET_PHONE_NUMBER)
        }

        if(helper?.getStringValue(PrefKeys.KEY_ADD_ADDRESS)!=null){
            binding.btUserLocation.text=helper?.getStringValue(PrefKeys.KEY_ADD_ADDRESS)
        }else{
            binding.btUserLocation.text = Global.getCompleteAddress(
                requireContext(),
                mIMainActivity?.getPreference()?.getStringValue(PrefKeys.KEY_LATITUDE) ?: "0.0",
                mIMainActivity?.getPreference()?.getStringValue(PrefKeys.KEY_LONGITUDE) ?: "0.0"
            )
        }

        /*binding.btUserLocation.text = Global.getCompleteAddress(
            requireContext(),
            mIMainActivity?.getPreference()?.getStringValue(PrefKeys.KEY_LATITUDE) ?: "0.0",
            mIMainActivity?.getPreference()?.getStringValue(PrefKeys.KEY_LONGITUDE) ?: "0.0"
        )*/


    }


    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        setupProfileData()

        binding.authRoot.root.visibility =
            if (mIMainActivity?.getPreference()
                    ?.getBoolean(PrefKeys.KEY_IS_USER_LOG_IN) == false
            ) View.VISIBLE else View.GONE

    }


    private fun setupListeners() {

        binding.clProfile.setOnClickListener {

            /*val direction = AccountFragmentDirections.actionAccountFragmentToNavGraphProfile()
            it.findNavController().navigate(direction)

            profileViewModel.getUserObserver.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    mIMainActivity?.getPreference()?.saveCurrentUser(it[0])

                }
            }*/
            val direction = NavGraphDirections.actionGlobalNavPublicProfileFragment(mIMainActivity?.getPreference()?.getCurrentUser()?.userId?:"")
            it.findNavController().navigate(direction)

            profileViewModel.getUserObserver.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    mIMainActivity?.getPreference()?.saveCurrentUser(it[0])

                }
            }


        }

        binding.btUserName.setOnClickListener {
            val name = binding.btUserName.text.toString().trim()
            val direction = AccountFragmentDirections.actionNavAccountFragmentToEditNameFragment(name)
            it.findNavController().navigate(direction)
        }

        binding.btUserNum.setOnClickListener {
            val number = binding.btUserNum.text.toString().trim()
            val direction = AccountFragmentDirections.actionNavAccountFragmentToEditNumberFragment(number)
            it.findNavController().navigate(direction)
        }

        binding.btUserEmail.setOnClickListener {
            val email = binding.btUserEmail.text.toString().trim()
            val directions = AccountFragmentDirections.actionNavAccountFragmentToEditEmailFragment(email)
            it.findNavController().navigate(directions)
        }

        binding.btPaymentMethod.setOnClickListener {
            it.findNavController().navigate(R.id.action_nav_account_fragment_to_nav_payment_methods_fragment)
        }

        binding.btPublicProfile.setOnClickListener {
            val direction = NavGraphDirections.actionGlobalNavPublicProfileFragment(mIMainActivity?.getPreference()?.getCurrentUser()?.userId?:"")
            it.findNavController().navigate(direction)

            profileViewModel.getUserObserver.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    mIMainActivity?.getPreference()?.saveCurrentUser(it[0])

                }
            }
        }


        binding.btAccountVerification.setOnClickListener {

            val direction =  NavGraphDirections.actionGlobalNavAccountVerificationFragment()

          //  val directions = AccountFragmentDirections.actionNavAccountFragmentToNavAccountVerificationFragment()

            it.findNavController().navigate(direction)
        }

        binding.btConnectFacebook.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf(EMAIL))
        }

        binding.authRoot.btMoveToLogin.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                moveToLogin()
            }
        }

        binding.btChangePassword.setOnClickListener {
            val direction =
                AccountFragmentDirections.actionNavAccountFragmentToNavChangePasswordFragment()
            it.findNavController().navigate(direction)
        }

        binding.btPurchaseSale.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                it.findNavController()
                    .navigate(R.id.action_nav_account_fragment_to_myListingFragment)
            }
        }
        binding.btNotification.setOnClickListener {
            val direction =
                AccountFragmentDirections.actionNavAccountFragmentToNavNotificationFragment()
            GlobalScope.launch(Dispatchers.Main) {
                it.findNavController()
                    .navigate(direction)

            }
        }

        binding.btFavourite.setOnClickListener {
            val direction =
                AccountFragmentDirections.actionNavAccountFragmentToNavFavouriteFragment()
            it.findNavController()
                .navigate(direction)
        }


        binding.btHelp.setOnClickListener {
            moveToBrowser(TEST_LINK)
        }

        binding.btContactSupport.setOnClickListener {
            moveToBrowser(TEST_LINK)
        }
        binding.btCommunityForums.setOnClickListener {
            moveToBrowser(TEST_LINK)
        }

        binding.btUserLocation.setOnClickListener {

            AddAddressDialogFragment.newInstance().show(requireActivity().supportFragmentManager, TAG)

           // AddEmergencyContactDialogFragment.newInstance().show(requireActivity().supportFragmentManager, TAG)

          /*val locationDialogFragment =
              LocationDialogFragment.newInstance()
            locationDialogFragment.setOnLocationDialogListeners(this)
                  locationDialogFragment.show(requireActivity().supportFragmentManager,
                TAG
            )*/
        }

        binding.btLogout.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.alert))
                .setMessage(getString(R.string.messageLogout))
                .setPositiveButton(getString(R.string.cancel)) { dialog, which ->
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.logout)) { dialog, which ->
                    mIMainActivity?.getPreference()?.saveBoolean(PrefKeys.KEY_IS_USER_LOG_IN, false)
                    dialog.dismiss()
                    moveToHome()
                }.show()


        }
    }

    private fun setupFaceBookSignIn() {
        mCallbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().loginBehavior = LoginBehavior.WEB_ONLY
        LoginManager.getInstance()
            .registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    result?.let {
                        val request = GraphRequest.newMeRequest(
                            it.accessToken
                        ) { jsonObject, response ->
                            jsonObject?.let { res ->
                                try {
                                    Log.d(TAG, "onSuccess: $jsonObject")
                                    val facebookUserId = res.getString("id")
                                    disconnectFromFacebook()
                                    authViewModel.updateSocial(
                                        mUser?.userId ?: "",
                                        facebookUserId,
                                        ""
                                    )
                                } catch (e: Exception) {
                                    Global.showMessage(binding.root, e.localizedMessage)

                                }

                            }

                        }

                        val params = Bundle()
                        params.putString("fields", "id, name, email, gender, birthday")
                        request.parameters = params
                        request.executeAsync()

                    }


                    /*  helper?.saveBoolean(PrefKeys.KEY_IS_USER_LOG_IN, true)
                      Toast.makeText(this@LoginActivity, "Success", Toast.LENGTH_LONG).show()
                      moveTo(MainActivity())*/

                }

                override fun onCancel() {
                    Toast.makeText(requireContext(), "cancel", Toast.LENGTH_LONG).show()

                }

                override fun onError(error: FacebookException?) {
                    Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_LONG).show()

                }

            })


    }

    fun disconnectFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return  // already logged out
        }
        GraphRequest(
            AccessToken.getCurrentAccessToken(),
            "/me/permissions/",
            null,
            HttpMethod.DELETE
        ) { LoginManager.getInstance().logOut() }
            .executeAsync()
    }


    @SuppressLint("LogConditional")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mCallbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun moveToBrowser(link: String) {
        val browserIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(link)
        )
        startActivity(browserIntent)

    }

    private fun moveToLogin() {
        Intent(requireActivity(), LoginActivity::class.java).also {
            startActivity(it)
            requireActivity().finishAffinity()
        }
    }


    private fun moveToHome() {
        Intent(requireActivity(), MainActivity::class.java).also {
            startActivity(it)
            requireActivity().finishAffinity()
        }
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = AccountFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIMainActivity = context as IMainActivity
    }

    override fun onDetach() {
        super.onDetach()
        mIMainActivity = null
    }

    override fun onDialogDismiss() {
       /* binding.btUserLocation.text = Global.getCompleteAddress(
            requireContext(),
            mIMainActivity?.getPreference()?.getStringValue(PrefKeys.KEY_LATITUDE) ?: "0.0",
            mIMainActivity?.getPreference()?.getStringValue(PrefKeys.KEY_LONGITUDE) ?: "0.0"
        )*/
    }
}
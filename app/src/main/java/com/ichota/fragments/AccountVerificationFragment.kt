package com.ichota.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.facebook.*
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.ichota.NavGraphDirections
import com.ichota.R
import com.ichota.activities.OtpVerification
import com.ichota.databinding.FragmentAccountVerificationBinding
import com.ichota.interfaces.IMainActivity
import com.ichota.model.User
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.viewModel.AuthViewModel
import com.ichota.viewModel.ProfileViewModel
import okhttp3.MultipartBody
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*


private const val TAG = "AccountVerifiFragment"
private const val EMAIL = "email"
class AccountVerificationFragment : Fragment() {
    private lateinit var binding: FragmentAccountVerificationBinding
    private var mIMainActivity: IMainActivity? = null
    private val profileViewModel: ProfileViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private var chooseOptDia: Dialog? = null
    private var mCurrentPhotoPath: String? = null
    private var mImagePart: MultipartBody.Part? = null
    private var imageKey: String = ""
    private var mUser: User? = null
    private var mCallbackManager: CallbackManager? = null
    private var intent : Intent ? = null

    private val TEST_LINK = "https://ichotaa.appdeft.biz/terms.php"
    private val TEST_LINK_PRIVACY_POLICY = "https://ichotaa.appdeft.biz/privacy_policy.php"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUser = mIMainActivity?.getPreference()?.getCurrentUser()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupData()
        setupFaceBookSignIn()
        setupObserver()
        setupTermsSpan()
        setupListeners()
    }

    private fun setupObserver() {
        profileViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showMessage(it)
        }
        profileViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showProgress(it)
        }

        authViewModel.getProgress.observe(viewLifecycleOwner) {
            mIMainActivity?.showProgress(it)
        }

        authViewModel.getMessage.observe(viewLifecycleOwner) {
            mIMainActivity?.showMessage(it)
        }
        profileViewModel.getProfileUpdateObserver.observe(viewLifecycleOwner) {

            if (it.success == Constants.RESPONSE_SUCCESS) {
                mUser?.userImage = it.data[0].userImg
                mUser?.let { user ->
                    mIMainActivity?.getPreference()?.saveCurrentUser(user)
                    setupButtonTint("1", binding.btUploadProfileImage)
                    binding.btUploadProfileImage.text = getString(R.string.uploaded)
                }
            }

        }


        authViewModel.getVerifyEmailObserver.observe(viewLifecycleOwner) {
            MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.Success))
                    .setMessage(it.message)
                    .setPositiveButton(getString(R.string.okay)) { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
        }

        authViewModel.getUpdateSocialObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.getPreference()?.saveCurrentUser(it.data[0])
            setupButtonTint("1",binding.btConnectFacebook)
            binding.btConnectFacebook.text = getString(R.string.connected)
        }
    }



    private fun setupData() {
        mUser?.let {
            val profileStatus = if (it.userImage.isEmpty()) "0" else "1"
            binding.btUploadProfileImage.text =
                    if (profileStatus == "0")
                        getString(R.string.upload)
                    else
                        getString(R.string.uploaded)
            binding.btVerifyEmail.text =
                    if (it.emailStatus == "0")
                        getString(R.string.verify)
                    else
                        getString(R.string.verified)

            binding.btVerifyPhone.text =
                    if (it.phoneNumStatus == "0")
                        getString(R.string.verify)
                    else
                        getString(R.string.verified)

            binding.btConnectFacebook.text =
                    if (it.facebookStatus == "0")
                        getString(R.string.connect)
                    else
                        getString(R.string.connected)

            setupButtonTint(profileStatus, binding.btUploadProfileImage)
            setupButtonTint(it.emailStatus, binding.btVerifyEmail)
            setupButtonTint(it.phoneNumStatus, binding.btVerifyPhone)
            setupButtonTint(it.facebookStatus, binding.btConnectFacebook)


        }

    }

    private fun setupButtonTint(status: String?, button: Button) {
        if (status == "0") {
            button.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorGrey600)
        } else if (status == "1") {
            button.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)

        }


    }

    private fun setupTermsSpan() {
        val ss = SpannableString(getString(R.string.accountVerificationTermsText))
        ss.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                moveToBrowser(TEST_LINK)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
            }
        }, 58, 77, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        ss.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                moveToBrowser(TEST_LINK_PRIVACY_POLICY)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)

            }
        }, 81, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvTermsPrivacy.text = ss
        binding.tvTermsPrivacy.movementMethod = LinkMovementMethod.getInstance()
        binding.tvTermsPrivacy.highlightColor = Color.TRANSPARENT
    }

    private fun moveToBrowser(link: String) {
        val browserIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(link)
        )
        startActivity(browserIntent)

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

    private fun setupListeners() {
        binding.fabBack.setOnClickListener {
            it.findNavController().popBackStack()
        }

        binding.btUploadProfileImage.setOnClickListener {
            if (binding.btUploadProfileImage.text == getString(R.string.uploaded)) {
                Global.showMessage(binding.root, getString(R.string.messageProfilePictureUploaded))
                return@setOnClickListener
            }
        }

        binding.btVerifyEmail.setOnClickListener {
            if (binding.btVerifyEmail.text == getString(R.string.verified)) {
                Global.showMessage(binding.root, getString(R.string.messageEmailVerified))
                return@setOnClickListener
            }

            authViewModel.verifyEmail(mUser?.email?:"")
        }

        binding.btConnectFacebook.setOnClickListener {
            if (binding.btConnectFacebook.text == getString(R.string.connected)){
                Global.showMessage(binding.root,getString(R.string.messageAlreadyConnectedToFacebook))
                return@setOnClickListener
            }
                LoginManager.getInstance().logInWithReadPermissions(this, listOf(EMAIL))
        }


        binding.btVerifyPhone.setOnClickListener {

            if(binding.btVerifyPhone.text== getString(R.string.verified)){
                Global.showMessage(binding.root,getString(R.string.messagealreadyverifiedphonenumber))
                return@setOnClickListener
            }
             authViewModel.verifyMobile(mUser?.mobile?:"")

        //  val direction = AccountVerificationFragmentDirections.actionNavAccountVerificationFragmentToAddPhoneNumberBottomSheetDialogFragment()
         /* val direction = NavGraphDirections.actionGlobalOtpVerificationFragment()
           findNavController().navigate(direction)*/

            intent = Intent(requireContext(), OtpVerification::class.java)
            requireContext().startActivity(intent)
        }


    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = AccountVerificationFragment()

    }

    private fun chooseOptionsDialog() {
        chooseOptDia = Dialog(requireActivity())
        chooseOptDia?.setCancelable(true)
        chooseOptDia?.setContentView(R.layout.dialog_choose_img_options)

        chooseOptDia?.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        )

        chooseOptDia?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val params = chooseOptDia?.window?.attributes
        params?.gravity = Gravity.CENTER_VERTICAL


        val fabGallery: FloatingActionButton =
                (chooseOptDia ?: return).findViewById(R.id.fab_gallery)
        val fabCamera: FloatingActionButton = chooseOptDia!!.findViewById(R.id.fab_camera)


        fabCamera.setOnClickListener { v: View? -> chooseFromCamera() }
        fabGallery.setOnClickListener { v: View? -> chooseFromGallery() }
        chooseOptDia?.show()


    }

    private fun chooseFromCamera() {
        if (!Global.hasFeatureCamera(requireActivity())) {
            Global.showMessage(binding.root, "Unable to detect camera", Snackbar.LENGTH_LONG)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                !Global.hasPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA))
        ) {
            requestPermissions(
                    arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), Global.RC_CAMERA
            )
        } else {
            openCamera()
        }
    }

    private fun chooseFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Global.hasPermissions(
                        requireActivity(),
                        arrayOf(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                )
        ) {
            requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Global.RC_GALLERY
            )
        } else {
            openGallery()
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent()
        cameraIntent.action = MediaStore.ACTION_IMAGE_CAPTURE
        if (cameraIntent.resolveActivity(requireActivity().packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = Global.createImgFile(requireActivity())
                mCurrentPhotoPath = photoFile.absolutePath
            } catch (e: IOException) {
                Log.d(TAG, "openCamera: unable to create path ")
            }
            if (photoFile != null) {
                val photoUri = FileProvider.getUriForFile(
                        requireActivity(),
                        "com.ichota.fileprovider",
                        photoFile
                )
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            }
            startActivityForResult(cameraIntent, Global.RC_CAMERA)
        } else {
            Global.showMessage(
                    binding.root,
                    "Could not found any application to capture Photo",
                    Snackbar.LENGTH_LONG
            )
        }

        if (chooseOptDia != null) chooseOptDia!!.dismiss()
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(
                Intent.createChooser(intent, "Choose picture from"),
                Global.RC_GALLERY
        )
        if (chooseOptDia != null) (chooseOptDia ?: return).dismiss()
    }


    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        if (chooseOptDia != null) (chooseOptDia ?: return).dismiss()
        if (requestCode == Global.RC_CAMERA && grantResults.isNotEmpty()) {
            val isAllowed = grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (!isAllowed && Global.requiredRational(
                            requireActivity(),
                            Manifest.permission.CAMERA
                    )
            ) {
                accessPermissionReason(
                        Manifest.permission.CAMERA,
                        getString(R.string.cameraPermissionReason)
                )
            } else if (!isAllowed && !Global.requiredRational(
                            requireActivity(),
                            Manifest.permission.CAMERA
                    )
            ) {
                showManuallyAlert()
            } else {
                openCamera()
            }
        } else if (requestCode == Global.RC_GALLERY && grantResults.isNotEmpty()) {
            val isAllowed = grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (!isAllowed && Global.requiredRational(
                            requireActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
            ) {
                accessPermissionReason(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        getString(R.string.galleryPermissionReason)
                )
            } else if (!isAllowed && !Global.requiredRational(
                            requireActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
            ) {
                showManuallyAlert()
            } else {
                openGallery()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun accessPermissionReason(permission: String, reason: String?) {
        val reasonDialog = Dialog(requireActivity())
        reasonDialog.setContentView(R.layout.dia_permission_reason)
        reasonDialog.setCancelable(false)

        reasonDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        reasonDialog.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        )
        val params = reasonDialog.window?.attributes
        params?.gravity = Gravity.CENTER_VERTICAL

        val tvPermissionReason = reasonDialog.findViewById<TextView>(R.id.tv_permission_reason)
        val tvRetry = reasonDialog.findViewById<TextView>(R.id.bt_retry)
        val tvIamSure = reasonDialog.findViewById<TextView>(R.id.bt_iam_sure)
        tvPermissionReason.text = reason
        tvIamSure.setOnClickListener { reasonDialog.dismiss() }
        tvRetry.setOnClickListener {
            reasonDialog.dismiss()
            if (permission == Manifest.permission.CAMERA) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), Global.RC_CAMERA)
            } else if (permission == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        Global.RC_GALLERY
                )
            }
        }
        reasonDialog.show()
    }

    private fun showManuallyAlert() {
        AlertDialog.Builder(requireActivity())
                .setTitle("Permission Alert")
                .setMessage("We need Permission to access this functionality\nPlease enable it manually from settings")
                .setPositiveButton("Enable") { dialog: DialogInterface?, which: Int -> enableManually() }
                .setNegativeButton("cancel") { dialog: DialogInterface, which: Int -> dialog.dismiss() }
                .show()
    }

    private fun enableManually() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    @SuppressLint("LogConditional")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult: requestCode = $requestCode")
        if (requestCode == Global.RC_CAMERA) {
            Log.d(TAG, "onActivityResult: mCurrentPhotoPath = $mCurrentPhotoPath")
            // startEditing(mCurrentPhotoPath!!)
            mCurrentPhotoPath?.let {

                val bitmap = BitmapFactory.decodeFile(File(it).absolutePath)


                profileViewModel.updateProfileImage(

                        mUser?.userId ?: "",

                        Global.prepareFilePart("user_image", mCurrentPhotoPath ?: "",requireContext())

                )
                Log.d(TAG, "onActivityResult: bitmap = $bitmap")
            }


        } else if (requestCode == Global.RC_GALLERY) {
            if (data != null) {
                Log.d(TAG, "onActivityResult: gallery img = " + data.data)
                try {
                    val path = Global.getRealPathFromURI(requireActivity(), data.data ?: return)
                    val `is` =
                            requireActivity().contentResolver.openInputStream(data.data ?: return)
                    val bitmap = BitmapFactory.decodeStream(`is`)
                    profileViewModel.updateProfileImage(
                            mUser?.userId ?: "",
                            Global.prepareFilePart("user_image", path ?: "")
                    )


                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
        else
        {
            mCallbackManager?.onActivityResult(requestCode, resultCode, data)
        }
        super.onActivityResult(requestCode, resultCode, data)
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIMainActivity = context as IMainActivity
    }

    override fun onDetach() {
        super.onDetach()
        mIMainActivity = null
    }
}
package com.ichota.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.hbb20.CountryCodePicker
import com.ichota.NavGraphDirections
import com.ichota.R
import com.ichota.adapter.ProfilePagerAdapter
import com.ichota.databinding.FragmentProfileBinding
import com.ichota.fragments.UserReviewFragment
import com.ichota.interfaces.IMainActivity
import com.ichota.model.HomeTabs
import com.ichota.model.User
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.viewModel.ProfileViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*


private const val TAG = "ProfileFragment"

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    private val profileViewModel: ProfileViewModel by viewModels()
    private var mUser: User? = null
    private var chooseOptDia: Dialog? = null
    private var mCurrentPhotoPath: String? = null
    private var profilePagerAdapter: ProfilePagerAdapter? = null
    private var mImagePart: MultipartBody.Part? = null
    private var imageKey: String = ""
    private var mIMainActivity: IMainActivity? = null
    private var mTopMargin: Int = 180
    private var mShareLink: String? = null
    private var updateContactDialog: Dialog? = null
    private var followers : String? = null
    private var followings : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUser = mIMainActivity?.getPreference()?.getCurrentUser()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //  setMargins(binding.tabProfile,0,mTopMargin,0,0)
        mShareLink = "www.ichota.com/userprofile/${mUser?.userId}"

        val tabs = arrayOf(
            HomeTabs(UserMarketplaceFragment.newInstance(mUser?.userId ?: ""), ""),
            HomeTabs(UserReviewFragment.newInstance(mUser?.userId ?: ""), "")
        )

        profilePagerAdapter = ProfilePagerAdapter(this, tabs)
        binding.pagerProfile.adapter = profilePagerAdapter
        TabLayoutMediator(binding.tabProfile, binding.pagerProfile) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.marketplace)
                1 -> getString(R.string.reviews)
                else -> getString(R.string.marketplace)
            }
        }.attach()
        setupProfileData()
        setupObserver()
        setupListeners()
    }

    private fun setupObserver() {
        profileViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showMessage(it)
        }
        profileViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showProgress(it)

        }

        profileViewModel.getUserObserver.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                mIMainActivity?.getPreference()?.saveCurrentUser(it[0])

            }
            setupProfileData()

        }


        profileViewModel.getProfileUpdateObserver.observe(viewLifecycleOwner) {
            if (it.success == Constants.RESPONSE_SUCCESS && it.data.isNotEmpty()) {
                mUser?.userImage = it.data[0].userImg
                mUser?.let { user ->
                    Glide.with(requireContext())
                        .load(Global.getImageUrl(user.userImage))
                        .placeholder(R.drawable.img_user_placeholder)
                        .error(R.drawable.img_user_placeholder)
                        .into(binding.ivUser)
                    mIMainActivity?.getPreference()?.saveCurrentUser(user)
                }
            }
        }
        profileViewModel.getEmergencyContactObserver.observe(viewLifecycleOwner) {
            if (it.success == Constants.RESPONSE_SUCCESS) {
                binding.ivSwitchEmergencyContact.setImageResource(R.drawable.ic_switch_off)
                updateContactDialog?.dismiss()
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.success))
                    .setMessage(it.message)
                    .setPositiveButton(getString(R.string.ok)) { dialog, which ->

                        dialog.dismiss()
                        profileViewModel.getUserProfile(
                            mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: ""
                        )

                    }.show()
            }

        }


    }

    private fun setupProfileData() {


        Glide.with(requireActivity())
            .load(Global.getImageUrl(mUser?.userImage))
            .placeholder(R.drawable.img_user_placeholder)
            .error(R.drawable.img_user_placeholder)
            .into(binding.ivUser)

        binding.tvUserName.text = mUser?.name ?:" "

        val bio = Global.formatItemListingDate(mUser?.createDtm ?: "")
        binding.tvJoinedDate.text = bio

        try {
            binding.ratingBar.rating = mUser?.rating?.toFloat() ?: 1F
        } catch (e: NumberFormatException) {
           // binding.ratingBar.rating = 1F
        }
        if (mUser?.rating.isNullOrEmpty()) {
            binding.tvRatingCount.text = getString(R.string.stars, "1")
        } else {
            binding.tvRatingCount.text = getString(R.string.stars, mUser?.rating)
        }

        mUser?.let {

            binding.cbEmailVerify.isChecked = it.emailStatus == "1"
            binding.cbPhoneVerify.isChecked = it.phoneNumStatus == "1"
            binding.cbFacebookVerify.isChecked = it.facebookStatus == "1"

            binding.cbPaymentVenmo.isChecked = it.venmoStatus == "1"
            binding.cbPaymentPaypal.isChecked = it.paypalStatus == "1"
            binding.cbPaymentCashapp.isChecked = it.cashAppStatus == "1"
            binding.cbPaymentApplePay.isChecked = it.applePayStatus == "1"

            setPaymentMethodsTitle(getString(R.string.venmo),binding.tvVenmo,it.venmoStatus)
            setPaymentMethodsTitle(getString(R.string.paypal),binding.tvPaypal,it.paypalStatus)
            setPaymentMethodsTitle(getString(R.string.cashapp),binding.tvCashapp,it.cashAppStatus)
            setPaymentMethodsTitle(getString(R.string.applePay),binding.tvApplePay,it.applePayStatus)


            binding.ivSwitchEmergencyContact.setImageResource(
                if (it.emergencyContact != "0" && it.emergencyContact.isNotEmpty())
                    R.drawable.ic_switch_checked
                else R.drawable.ic_switch_off
            )

            setVerifyStatus(binding.tvEmailVerifyStatus, it.emailStatus)
            setVerifyStatus(binding.tvPhoneVerifyStatus, it.phoneNumStatus)
            setVerifyStatus(binding.tvFacebookVerifyStatus, it.facebookStatus)


             followers = it.followerCount
             followings = it.followingCount
            binding.tvFollowerCount.text = followers
            binding.tvFollowingCount.text = followings

        }
    }



    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            it.findNavController().popBackStack()
        }
        binding.ivUser.setOnClickListener {
            chooseOptionsDialog()
        }

        binding.tvFollowingCount.setOnClickListener {
            val direction = NavGraphDirections.actionGlobalNavMyNetworkPagerFragment(
                mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: ""
            )
            it.findNavController().navigate(direction)
        }

        binding.tvFollowingHeader.setOnClickListener {
            val direction = NavGraphDirections.actionGlobalNavMyNetworkPagerFragment(
                mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: ""
            )
            it.findNavController().navigate(direction)
        }
        binding.tvFollowerCount.setOnClickListener {
            val direction = NavGraphDirections.actionGlobalNavMyNetworkPagerFragment(
                mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: ""
            )
            it.findNavController().navigate(direction)
        }
        binding.tvFollowerHeader.setOnClickListener {
            val direction = NavGraphDirections.actionGlobalNavMyNetworkPagerFragment(
                mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: ""
            )
            it.findNavController().navigate(direction)
        }

        binding.ivMore.setOnClickListener {

            if (mShareLink.isNullOrEmpty() || mUser?.userId == null) return@setOnClickListener
            Global.shareLink(requireContext(), mShareLink!!)

        }

        binding.ivSwitchEmergencyContact.setOnClickListener {
            if (mUser?.emergencyContact != "0" && mUser?.emergencyContact?.isNotEmpty() == true) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.info))
                    .setMessage(
                        getString(
                            R.string.messageEmergencyContactUpdatedAlready,
                            mIMainActivity?.getPreference()?.getCurrentUser()?.emergencyContact
                        )
                    )
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
            binding.ivSwitchEmergencyContact.setImageResource(R.drawable.ic_switch_off)
            updateContactDialog?.dismiss()
        }
        btSave?.setOnClickListener {
            val number = etEmergencyContact?.text.toString().trim()
            if (!TextUtils.isEmpty(number) && number != "0") {
                val contact = "${ccp?.selectedCountryCodeWithPlus}${
                    number
                }"
                Log.d(TAG, "onViewCreated: fbnaboanbnao;")
                profileViewModel.updateEmergencyContact(
                    mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: "",
                    contact
                )
            } else {
                Global.showMessage(binding.root, getString(R.string.messageNumberRequired))
            }
        }
    }

    private fun setVerifyStatus(view: TextView, status: String?) {
        if (status == "0") {
            view.text = getString(R.string.notVerified)
            view.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorTextPrimary))
        } else {
            view.text = getString(R.string.verified)
            view.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        }
    }

    private fun setPaymentMethodsTitle(title:String, view:TextView, status:String?){
        view.text = title
        if (status == "0") {
            view.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorTextPrimary))
        } else {
            view.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        }
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


        val fabGallery: FloatingActionButton = (chooseOptDia ?: return).findViewById(R.id.fab_gallery)
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
        if (requestCode == Global.RC_CAMERA && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: mCurrentPhotoPath = $mCurrentPhotoPath")
            // startEditing(mCurrentPhotoPath!!)
            mCurrentPhotoPath?.let {
                val bitmap = BitmapFactory.decodeFile(File(it).absolutePath)
                uploadPhoto(bitmap)
                binding.ivUser.setImageBitmap(bitmap)

                profileViewModel.updateProfileImage(

                    mUser?.userId ?: "",

                    Global.prepareFilePart("user_image", mCurrentPhotoPath ?: "")

                )
                Log.d(TAG, "onActivityResult: bitmap = $bitmap")
            } ?: Global.showMessage(binding.root, "unable to capture image")


        } else if (requestCode == Global.RC_GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                Log.d(TAG, "onActivityResult: gallery img = " + data.data)
                try {
                    val path = Global.getRealPathFromURI(requireActivity(), data.data ?: return)
                    val `is` =
                        requireActivity().contentResolver.openInputStream(data.data ?: return)
                    val bitmap = BitmapFactory.decodeStream(`is`)
                    uploadPhoto(bitmap)
                    binding.ivUser.setImageBitmap(bitmap)

                    profileViewModel.updateProfileImage(
                        mUser?.userId ?: "",
                        Global.prepareFilePart("user_image", path ?: "")
                    )


                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun uploadPhoto(bitmap: Bitmap) {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream)
        val byteArr = outputStream.toByteArray()
        val requestBody = byteArr.toRequestBody("file/*".toMediaTypeOrNull(), 0, byteArr.size)
        val random = Random()
        mImagePart = MultipartBody.Part.createFormData(
            imageKey, "Calender_${random.nextInt(1000)}.jpg", requestBody
        )


    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) = ProfileFragment()
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
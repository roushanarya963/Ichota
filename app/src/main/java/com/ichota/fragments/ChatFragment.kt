package com.ichota.fragments

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
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.ichota.NavGraphDirections
import com.ichota.activities.FullImageActivity
import com.ichota.R
import com.ichota.adapter.ChatAdapter
import com.ichota.databinding.FragmentChatBinding
import com.ichota.interfaces.IMainActivity
import com.ichota.model.*
import com.ichota.preferences.PrefKeys
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.viewModel.AuthViewModel
import com.ichota.viewModel.ChatViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

private const val TAG = "ChatActivity"
private const val KEY_CHAT_DATA = "chat_data"
private const val KEY_MESSAGE_TYPE = "key_message_type"


class ChatFragment : Fragment(), ChatAdapter.ChatMessageClickInterface {

    private lateinit var binding: FragmentChatBinding
    private val chatViewModel: ChatViewModel by viewModels()
    private var helper: PreferenceHelper? = null
    private var mChatAdapter: ChatAdapter? = null
    private var mChatDialog: ChatDialog? = null
    private val navArgspost: PostDetailFragmentArgs by navArgs()
    private var mCurrentUserId: String = ""
    private var authViewModel:AuthViewModel?=null
    private var mreciverId: String = ""
    private var chooseOptDia: Dialog? = null
    private var mCurrentPhotoPath: String? = null
    private var mImagePart: MultipartBody.Part? = null
    private var handler: Handler? = null
    private var mIMainActivity: IMainActivity? = null
    private val navArgs: ChatFragmentArgs by navArgs()
    private var mPostDetail: PostDetail? = null
    private var mPostServiceDetail: ServicePostDetail? = null
    private var postDetailServiceType: String = ""

    private val runnable = object : Runnable {
        override fun run() {
            getChat()
            handler?.postDelayed(this, 5000)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mChatDialog = navArgs.chatData
        postDetailServiceType = navArgs.postType
        mreciverId = mChatDialog?.receiverId.toString()

        helper = PreferenceHelper.getPreferences(requireContext())

        mCurrentUserId = helper?.getCurrentUser()?.userId ?: ""
        mChatAdapter = ChatAdapter(this)

        val messageType = navArgs.messageType
        if (messageType == MessageType.TYPE_OFFER) {
            Handler(Looper.getMainLooper()).postDelayed({
                mChatDialog?.let { dialog ->
                    chatViewModel.insertChat(
                        mCurrentUserId,
                        dialog.receiverId ?: return@let,
                        "0",
                        dialog.message ?: return@let,
                        dialog.productId ?: return@let,
                        navArgs.postType,
                        MessageType.TYPE_OFFER,
                        "",
                        "",
                        null,
                        ) }


            }, 200)

        } else {
            getChat()
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        setNotificationUnReadCountObserver()
        setupObserver()
        setupListeners()
    }

    private fun setNotificationUnReadCountObserver() {
        authViewModel?.getUnReadChatCount?.observe(viewLifecycleOwner){
             if(it.success==Constants.RESPONSE_SUCCESS){
             helper?.saveInt(PrefKeys.KEY_UNREADCHAT_COUNT,it.data.count.toInt())
             }
        }

    }

    override fun onStart() {
        super.onStart()

         mChatDialog?.let { dialog ->
             dialog.productId?.let { it1 ->
                 (if (dialog.receiverId == mCurrentUserId) dialog.userId else dialog.receiverId)?.let { it2 ->
                     chatViewModel.readChat(
                         mCurrentUserId,
                         it1,
                         it2,
                         navArgs.postType
                     )
                 }
             }
         }

        chatViewModel.changeUserActiveStatus(
            mCurrentUserId, "1"
        )
    }

    override fun onResume() {
        super.onResume()
        handler = Handler(Looper.getMainLooper())
        handler?.postDelayed(runnable, 5000)
    }

    override fun onPause() {
        super.onPause()
        handler?.removeCallbacksAndMessages(null)
        handler = null
    }

    private fun setupObserver() {

        chatViewModel.getInsertChatResObserver.observe(viewLifecycleOwner) {
            if (it.success == Constants.RESPONSE_SUCCESS) {
                Log.d(TAG, "setupObserver: insert_chat $mChatDialog")
                authViewModel?.unReadChatCount(mIMainActivity?.getPreference()?.getCurrentUser()?.userId?:"")
                getChat()
            } else {
                Global.showMessage(binding.root, it.message)
            }
        }

        chatViewModel.getViewChatObserver.observe(viewLifecycleOwner) {

            if (binding.progressBar.visibility == View.VISIBLE) binding.progressBar.visibility =
                View.GONE
            binding.ivNoMessages.visibility =
                if (it.data.chatList.isEmpty()) View.VISIBLE else View.GONE
            mPostDetail = if (it.data.postDetails.isNotEmpty()) it.data.postDetails[0] else null

            mChatAdapter?.setData(it.data.chatList)

            binding.tvAvgRating.text = it.data.usersStatus[0].userprofileavgrating
            // binding.tvCntRating.text=it.data.usersStatus[0].userprofileratingcount
            binding.tvCntRating.text =
                "(" + it.data.usersStatus[0].userprofileratingcount + ")".trim()

            binding.rvChat.smoothScrollToPosition(
                (mChatAdapter ?: return@observe).itemCount
            )

            binding.tvOnlineStatus.text =
                if (it.data.usersStatus[0].receiverStatus == "1")
                    "Online"
                else
                    "Offline"


            binding.ivOnlineIndicator.backgroundTintList =
                if (it.data.usersStatus[0].receiverStatus == "1")
                    ContextCompat.getColorStateList(requireContext(), R.color.colorActiveGreen)
                else
                    ContextCompat.getColorStateList(requireContext(), R.color.colorTextSecondary)
        }

        chatViewModel.getSaveChatImageObserver.observe(viewLifecycleOwner) {
            getChat()
        }
    }


    private fun getChat() {
        mChatDialog?.let { chat ->
            (if (chat.receiverId == mCurrentUserId) chat.userId else chat.receiverId)?.let {
                chat.productId?.let { it1 ->
                    chatViewModel.viewChat(
                        mCurrentUserId,
                        it,
                        it1,
                        navArgs.postType
                    )
                }
            }
        }
    }

    private fun initData() {
        val currentUserId = helper?.getCurrentUser()?.userId



        mChatDialog?.let {
            binding.tvUserName.text =
                if (currentUserId == it.receiverId) it.userName else it.userName

            Glide.with(this)
                .load(
                    Global.getImageUrl(
                        if (currentUserId == it.receiverId) it.userImage1 else it.userImage1
                    )
                )
                .error(R.drawable.img_user_placeholder)
                .placeholder(R.drawable.img_user_placeholder)
                .into(binding.ivUser)

            Glide.with(this)
                .load(Global.getImageUrl(it.productCoverImage))
                .error(R.drawable.img_placeholder)
                .placeholder(R.drawable.img_placeholder)
                .into(binding.ivProduct)


            if (it.productPrice != null) {
                binding.txtactlprc.text = String.format("$%,d", it.productPrice.toString().split(".")[0].toLong())
            } else {
                binding.txtactlprc.text = String.format("$%,d", 0)
            }

            // binding.txtactlprc.text = String.format("$%,d", it.productPrice.toString().split(".")[0].toLong())

            binding.rvChat.adapter = mChatAdapter

        }

    }

    private fun setupListeners() {

        binding.fabBack.setOnClickListener {
            it.findNavController().navigateUp()
        }

        binding.fabSend.setOnClickListener {
            sendTextMessage()
        }
        binding.fabLocation.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.alert))
                .setMessage(getString(R.string.messageShareLocation))
                .setPositiveButton(getString(R.string.cancel)) { dialog, which ->
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.shareLocation)) { dialog, which ->
                     mChatDialog?.let { dialog->
                         (if (dialog.receiverId == mCurrentUserId) dialog.userId else dialog.receiverId)?.let { it1 ->
                             dialog.productId?.let { it2 ->
                                 chatViewModel.insertChat(
                                     mCurrentUserId,
                                     it1,
                                     "1",
                                     "",
                                     it2,
                                     mChatDialog?.postType?:return@let,
                                     MessageType.TYPE_LOCATION,
                                     helper?.getStringValue(PrefKeys.KEY_LATITUDE) ?: "",
                                     helper?.getStringValue(PrefKeys.KEY_LONGITUDE) ?: "",
                                     null
                                 )
                             }
                         }
                     }

                }
                .show()
        }

        val i = 36

        when {

        }


        binding.fabAttachment.setOnClickListener { chooseOptionsDialog() }

        binding.ivUser.setOnClickListener {
            val direction =
                NavGraphDirections.actionGlobalNavPublicProfileFragment(
                    mreciverId
                )
            it.findNavController().navigate(direction)
        }

        binding.ivProduct.setOnClickListener {

            val directions = NavGraphDirections.actionGlobalNavPostDetailFragment(
                navArgs.postType,
                mPostDetail?.id.toString(),
                mPostDetail?.userId.toString()
            )
            findNavController().navigate(directions)

        }
    }

    private fun sendTextMessage() {

        Handler(Looper.getMainLooper()).post {
            mIMainActivity?.hideSoftKeyboard(binding.root)
        }
        val message = binding.etMessage.text.toString().trim()

        if (message.isEmpty()) {
            Global.showMessage(binding.root, "Please type something")
            return
        }

        if (isContainPhoneNumber(message)) {
            showContainContactInfoAlert()
            return
        }

        binding.etMessage.text.clear()

           mChatDialog?.let {
               (if (it.receiverId == mCurrentUserId) it.userId else it.receiverId)?.let { receiverId ->
                   it.productId?.let { productId ->
                       chatViewModel.insertChat(
                           mCurrentUserId,
                           receiverId,
                           "0",
                           message,
                           productId,
                           it.postType?:return@let ,
                           MessageType.TYPE_MESSAGE,
                           "",
                           "",
                           null

                       )
                   }
               }
           }

    }

    private fun showContainContactInfoAlert() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.safetyTip))
            .setMessage(getString(R.string.messageShareContactSafetyTip))
            .setPositiveButton(getString(R.string.learnMore)) { dialog, which ->
                dialog.dismiss()
                Global.openBrowser(requireContext(), "https://www.appdeft.biz/")
            }
            .setNegativeButton(getString(R.string.dismiss)) { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    companion object {

    }

    override fun onStop() {

        mChatDialog?.let {
            it.productId?.let { it1 ->
                (if (it.receiverId == mCurrentUserId) it.userId else it.receiverId)?.let { it2 ->
                    chatViewModel.readChat(
                        mCurrentUserId,
                        it1,
                        it2,
                        navArgs.postType
                    )
                }
            }
        }
        chatViewModel.changeUserActiveStatus(
            mCurrentUserId, "0"
        )
        super.onStop()
    }

    private fun chooseOptionsDialog() {

        chooseOptDia = Dialog(requireContext())
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

        fabGallery.setOnClickListener {
            chooseOptDia?.dismiss()
            openGallery()
            //  chooseFromGallery()
        }

        chooseOptDia?.show()


    }

    private fun chooseFromCamera() {
        if (!Global.hasFeatureCamera(requireContext())) {
            Global.showMessage(binding.root, "Unable to detect camera", Snackbar.LENGTH_LONG)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            !Global.hasPermissions(requireContext(), arrayOf(android.Manifest.permission.CAMERA))
        ) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), Global.RC_CAMERA
            )
        } else {
            openCamera()
        }
    }

    private fun chooseFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Global.hasPermissions(
                requireContext(),
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        ) {
            requestPermissions(
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
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
                photoFile = Global.createImgFile(requireContext())
                mCurrentPhotoPath = photoFile.absolutePath
            } catch (e: IOException) {
                Log.d(TAG, "openCamera: unable to create path ")
            }
            if (photoFile != null) {
                val photoUri = FileProvider.getUriForFile(
                    requireContext(),
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
                    android.Manifest.permission.CAMERA
                )
            ) {
                accessPermissionReason(
                    android.Manifest.permission.CAMERA,
                    getString(R.string.cameraPermissionReason)
                )
            } else if (!isAllowed && !Global.requiredRational(
                    requireActivity(),
                    android.Manifest.permission.CAMERA
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
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                accessPermissionReason(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    getString(R.string.galleryPermissionReason)
                )
            } else if (!isAllowed && !Global.requiredRational(
                    requireActivity(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
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
        val reasonDialog = Dialog(requireContext())
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
            if (permission == android.Manifest.permission.CAMERA) {
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA), Global.RC_CAMERA)
            } else if (permission == android.Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Global.RC_GALLERY
                )
            }
        }
        reasonDialog.show()
    }

    private fun showManuallyAlert() {

       /* AlertDialog.Builder(requireContext())
            .setTitle("Permission Alert")
            .setMessage("We need Permission to access this functionality\nPlease enable it manually from settings")
            .setPositiveButton("Enable") { dialog: DialogInterface?, which: Int -> enableManually() }
            .setNegativeButton("cancel") { dialog: DialogInterface, which: Int -> dialog.dismiss() }
            .show()*/

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Permission Alert")
            .setMessage("We need Permission to access this functionality\nPlease enable it manually from settings")
            .setPositiveButton("Enable") {dialog,_->
                dialog.dismiss()
                enableManually()
            }
            .setNegativeButton("cancel") {dialog,_ ->
                dialog.dismiss()
            }
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
            mCurrentPhotoPath?.let {
                val bitmap = BitmapFactory.decodeFile(File(it).absolutePath)
                uploadPhoto(bitmap)

            } ?: Global.showMessage(binding.root, "unable to capture image")


        } else if (requestCode == Global.RC_GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                Log.d(TAG, "onActivityResult: gallery img = " + data.data)
                try {
                    val path = Global.getRealPathFromURI(requireContext(), data.data ?: return)
                    val `is` =
                        requireActivity().contentResolver.openInputStream(data.data ?: return)
                    val bitmap = BitmapFactory.decodeStream(`is`)
                    uploadPhoto(bitmap)
                    // binding.ivUser.setImageBitmap(bitmap)

                    Log.d(TAG, "onActivityResult: $mChatDialog")


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
            "image", "Calender_${random.nextInt(1000)}.jpg", requestBody
        )

         mChatDialog?.let { it ->
             Log.d(TAG, "uploadPhoto: $it")

             (if (it.receiverId == mCurrentUserId) it.userId else it.receiverId)?.let { it1 ->
                 it.productId?.let { it2 ->
                     chatViewModel.insertChat(
                         mCurrentUserId,
                         it1,
                         "",
                         "",
                         it2,
                         navArgs.postType,
                         MessageType.TYPE_IMAGE,
                         "",
                         "",
                         mImagePart,

                     )
                 }
             }

         }

    }

    private fun isContainPhoneNumber(source: String): Boolean {


        val patterns = ("^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
                + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"
                + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$")

        val pattern = Pattern.compile(patterns)
        val sourceArray = source.split(" ")
        for (value in sourceArray) {
            val matcher = pattern.matcher(value)
            if (matcher.matches()) {
                return true
            }
        }
        return false

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIMainActivity = context as IMainActivity
    }

    override fun onDetach() {
        super.onDetach()
        //  mIMainActivity = null
        mIMainActivity?.showProgress(false)
    }

    override fun onImageClick(img: String) {
        val images = ArrayList<String>()
        images.add(img)
        Intent(requireContext(), FullImageActivity::class.java).apply {
            putExtra("images", images)
            startActivity(this)
        }
    }

    override fun onProfilePictureClick(userId: String) {
        val direction = ChatFragmentDirections.actionGlobalNavPublicProfileFragment(userId)
        findNavController().navigate(direction)
    }

    override fun onUserProfilePictureClick(userId: String) {
        val directions = ChatFragmentDirections.actionGlobalNavGraphProfile(userId)
        findNavController().navigate(directions)
    }

    override fun onLocationClick(message: Message) {
        mPostDetail?.let {
            val direction = ChatFragmentDirections.actionNavChatFragmentToLocationDetailFragment(
                message,
                it.postType!!, it.id!!, it.userId!!
            )
            findNavController().navigate(direction)
        }
    }

}






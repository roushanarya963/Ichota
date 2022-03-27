package com.ichota.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.ichota.R
import com.ichota.adapter.ImagesAdapter
import com.ichota.databinding.FragmentAddServiceBinding
import com.ichota.dialogs.CategoryDialogFragment
import com.ichota.model.ServiceCategory
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.Global
import com.ichota.viewModel.AddServiceViewModel
import com.ichota.viewModel.ServiceViewModel
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.model.ImagePickerConfig
import com.nguyenhoanglam.imagepicker.model.RootDirectory

import com.nguyenhoanglam.imagepicker.ui.imagepicker.registerImagePicker
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

private const val TAG = "AddServiceFragment"

class AddServiceFragment : Fragment(), ImagesAdapter.ImageClickInterface,
    CategoryDialogFragment.CategorySelectedInterface {
    private lateinit var binding: FragmentAddServiceBinding
    private var helper: PreferenceHelper? = null
    private val addServiceViewModel: AddServiceViewModel by activityViewModels()
    private val serviceViewModel: ServiceViewModel by viewModels()
    private var chooseOptDia: Dialog? = null
    private var mCurrentPhotoPath: String? = null
    private var mImagesAdapter: ImagesAdapter? = null
    private var mCategoryDialog: CategoryDialogFragment? = null
    private var categories = ArrayList<Any>()
    private var mSelectedCategory: ServiceCategory? = null
    private var maxImageSelectionCount : Int = 6
    private var intent1 : Intent ?= null


    private val launcher = registerImagePicker { images ->
        // Selected images are ready to use
        if (images.isNotEmpty()) {
            try {
                // Do stuff with image's path or id. For example:
                for (image in images) {
                    Log.d(TAG, "images = ${image.uri}:  path = ${image.uri.path}")
                    addServiceViewModel.addImage(image.uri.toString())
                }
            } catch (e: Exception) {

            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        helper = PreferenceHelper.getPreferences(requireContext())
        mCategoryDialog = CategoryDialogFragment.newInstance()
        mCategoryDialog?.onCategorySelectedListener(this)
        mImagesAdapter = ImagesAdapter()
        mImagesAdapter?.onImageClickListener(this)
        addServiceViewModel.removeAllImages()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAddServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Handler().postDelayed({ serviceViewModel.getServiceCategories() }, 300)
        binding.rvPostImgs.adapter = mImagesAdapter

        setupObserver()
        setupListeners()
    }

    private fun setupObserver() {
        addServiceViewModel.getLiveDataImages.observe(viewLifecycleOwner) {
            Log.d(TAG, "setupObserver: Images $it")
            mImagesAdapter?.setData(it)
        }

        addServiceViewModel.getCoverImageObserver.observe(viewLifecycleOwner) {
            Glide.with(requireActivity())
                .load(it)
                .placeholder(R.drawable.app_logo)
                .error(R.drawable.app_logo)
                .into(binding.ivCoverPic)
        }


        //sale category
        serviceViewModel.getServiceCategoriesObserver.observe(viewLifecycleOwner) {
            categories.addAll(it)
        }


    }

    override fun onResume() {
        super.onResume()
        mSelectedCategory?.let {
            binding.btCategory.text = it.name
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun setupListeners() {
        binding.fabBin.setOnClickListener {


            if (addServiceViewModel.getServiceImages.isEmpty()) return@setOnClickListener

            MaterialAlertDialogBuilder(requireContext(),R.style.MaterialAlertDialog_App)
                .setTitle(getString(R.string.alert))
                .setMessage(getString(R.string.messageDeleteAllImages))
                .setPositiveButton(getString(R.string.cancel)){dialog,which->
                    dialog.dismiss()

                }.setNegativeButton(getString(R.string.deleteAll)){dialog,which->
                    binding.ivCoverPic.setImageResource(R.drawable.img_placeholder)
                    addServiceViewModel.removeAllImages()
                }.show()






        }

        binding.btContinue.setOnClickListener {

           // validatePostData()
            val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as? LocationManager

            Log.d(TAG, "locationManager:$locationManager ")
            if(locationManager!!.isLocationEnabled){
                validatePostData()
            }else{
                intent1 = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent1)
            }

        }
        binding.btCategory.setOnClickListener {
            mCategoryDialog?.setCategoryData(categories)
            mCategoryDialog?.show(requireActivity().supportFragmentManager, TAG)
        }
        binding.fabAddImg.setOnClickListener {
            if (mImagesAdapter!!.itemCount >= 6) {
                Global.showMessage(binding.root, "Maximum limit reached", Snackbar.LENGTH_LONG)
                return@setOnClickListener
            }
            chooseOptionsDialog()
        }

        binding.fabBack.setOnClickListener { requireActivity().onBackPressed() }


    }

    private fun validatePostData() {
        when {
            mImagesAdapter?.itemCount == 0 -> {
                Global.showMessage(binding.root, getString(R.string.messageUploadImages))
            }
            TextUtils.isEmpty(binding.etServiceTitle.text.toString().trim()) -> {
                Global.showMessage(binding.root, getString(R.string.messageProductNameRequired))
            }
            TextUtils.isEmpty(binding.etServiceDescription.text.toString().trim()) -> {
                Global.showMessage(binding.root, getString(R.string.messageDescriptionRequired))
            }
            TextUtils.isEmpty(binding.etServicePrice.text.toString().trim()) -> {
                Global.showMessage(binding.root, getString(R.string.messagePriceRequired))
            }
            mSelectedCategory == null -> {
                Global.showMessage(binding.root, getString(R.string.messageSelectCategory))
            }
            else -> {
                addServiceViewModel.getPostData.userId =
                    helper?.getCurrentUser()?.userId ?: ""

                addServiceViewModel.getPostData.productName =
                    binding.etServiceTitle.text.toString().trim()

                addServiceViewModel.getPostData.productDescription =
                    binding.etServiceDescription.text.toString().trim()

                addServiceViewModel.getPostData.productPrice = binding.etServicePrice.text.toString().trim()

                addServiceViewModel.getPostData.category = mSelectedCategory?.name ?: ""

                addServiceViewModel.getPostData.categoryId = mSelectedCategory?.id ?: ""

                Log.d(TAG, "validatePostData: ${addServiceViewModel.getPostData.toString()}")

                findNavController().navigate(R.id.action_nav_add_service_fragment_to_nav_add_service_step_2_fragment)

            }
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


        val fabGallery: FloatingActionButton = chooseOptDia!!.findViewById(R.id.fab_gallery)
        val fabCamera: FloatingActionButton = chooseOptDia!!.findViewById(R.id.fab_camera)


        fabCamera.setOnClickListener { v: View? -> chooseFromCamera() }
        fabGallery.setOnClickListener { v: View? -> chooseFromGallery() }

        fabGallery.setOnClickListener {
            chooseOptDia?.dismiss()
            openGallery()
            //  chooseFromGallery()
        }
        chooseOptDia?.show()

      //  chooseOptDia?.show()


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
        
       /* val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(
            Intent.createChooser(intent, "Choose picture from"),
            Global.RC_GALLERY
        )*/*/

        maxImageSelectionCount = 6 - mImagesAdapter!!.itemCount
        launcher.launch(
            ImagePickerConfig(
                isFolderMode = true,
                folderTitle = "Album",
                rootDirectory = RootDirectory.DOWNLOADS,
                isMultipleMode = true,
                maxSize = maxImageSelectionCount,
                limitMessage = "You can select up to $maxImageSelectionCount images"
            )
        )
       /* ImagePicker.with(this)
            .setFolderMode(true)
            .setFolderTitle("Album")
            .setRootDirectoryName(Config.ROOT_DIR_DCIM)
            .setDirectoryName("Image Picker")
            .setMultipleMode(true)
            .setShowNumberIndicator(true)
            .setMaxSize(maxImageSelectionCount)
            .setLimitMessage("You can select up to $maxImageSelectionCount images")
            //.setSelectedImages(images)
            .setRequestCode(Global.RC_GALLERY)
            .start()*/

        if (chooseOptDia != null) chooseOptDia!!.dismiss()

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (chooseOptDia != null) chooseOptDia!!.dismiss()
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

       /* AlertDialog.Builder(requireActivity())
            .setTitle("Permission Alert")
            .setMessage("We need Permission to access this functionality\nPlease enable it manually from settings")
            .setPositiveButton("Enable") { dialog: DialogInterface?, which: Int -> enableManually() }
            .setNegativeButton("cancel") { dialog: DialogInterface, which: Int -> dialog.dismiss() }
            .show()*/
    }

  private fun enableManually() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult: requestCode = $requestCode")
        if (requestCode == Global.RC_CAMERA && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: mCurrentPhotoPath = $mCurrentPhotoPath")
            // startEditing(mCurrentPhotoPath!!)
            val bitmap = BitmapFactory.decodeFile(File(mCurrentPhotoPath).absolutePath)
            Log.d(TAG, "onActivityResult: bitmap = $bitmap")
            addServiceViewModel.addImage(mCurrentPhotoPath ?: "")

        } /*else if (ImagePicker.shouldHandleResult(requestCode, resultCode, data, Global.RC_GALLERY)) {
            try {

                val images: ArrayList<Image> = ImagePicker.getImages(data)
                // Do stuff with image's path or id. For example:
                for (image in images) {
                    addServiceViewModel.addImage(image.path)
                }
            } catch (e: Exception) {
            }
        }*/
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onImageClick(imagePath: String,position : Int) {
        addServiceViewModel.setCoverImage(imagePath,position)
    }

    override fun onDeleteImage(position: Int) {
        addServiceViewModel.removeImage(position)
    }

    override fun getSelectedCategory(category: Any) {
        mSelectedCategory = category as ServiceCategory
        binding.btCategory.text = category.name
    }


}
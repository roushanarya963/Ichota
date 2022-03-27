package com.ichota.fragments

import android.Manifest
import android.app.Activity
import android.app.Activity.LOCATION_SERVICE
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Context
import android.content.Context.LOCATION_SERVICE
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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import com.google.android.gms.location.LocationRequest
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.ichota.NavGraphDirections
import com.ichota.activities.AddCarDetailActivity
import com.ichota.R
import com.ichota.adapter.ConditionsAdapter
import com.ichota.adapter.ImagesAdapter
import com.ichota.databinding.FragmentAddPostBinding
import com.ichota.dialogs.CategoryDialogFragment
import com.ichota.model.SaleCategory
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.BuyingOptions
import com.ichota.utils.Global
import com.ichota.viewModel.AddPostViewModel
import com.ichota.viewModel.SaleViewModel
import com.nguyenhoanglam.imagepicker.model.ImagePickerConfig
import com.nguyenhoanglam.imagepicker.model.RootDirectory
import com.nguyenhoanglam.imagepicker.ui.imagepicker.registerImagePicker
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


private const val TAG = "AddPostFragment"
private const val RC_ADD_CAR_DETAIL = 111

private const val START_DATE = 0
private const val END_DATE = 1

class AddPostFragment : Fragment(), ImagesAdapter.ImageClickInterface,
    CategoryDialogFragment.CategorySelectedInterface, ConditionsAdapter.ConditionSelectInterface {
    private lateinit var binding: FragmentAddPostBinding
    private var helper: PreferenceHelper? = null
    private val addPostViewModel: AddPostViewModel by activityViewModels()
    private val saleViewModel: SaleViewModel by viewModels()
    private var chooseOptDia: Dialog? = null
    private var mCurrentPhotoPath: String? = null
    private var mImagesAdapter: ImagesAdapter? = null
    private var mCategoryDialog: CategoryDialogFragment? = null
    private var categories = ArrayList<Any>()
    private var mSelectedCategory: SaleCategory? = null
    private var mBuyingOptions: String = BuyingOptions.PICKUP.name
    private var mCondition: String = "New"
    private var startDateTimeInMillis: Long = 0
    private var endDateTimeInMillis: Long = 0
    private var maxImageSelectionCount : Int = 6
    private var intent1 : Intent ?= null

    private val launcher = registerImagePicker { images ->
        // Selected images are ready to use
        if (images.isNotEmpty()) {
            try {
                // Do stuff with image's path or id. For example:
                for (image in images) {
                    Log.d(TAG, "images = ${image.uri}:  path = ${image.uri.path}")
                    addPostViewModel.addImage(image.uri.toString())
                }
            } catch (e: Exception) {

            }
        }
    }


    private val storagePermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if(it){
            openGallery()
        }else{
            showManuallyAlert()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddPostFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        helper = PreferenceHelper.getPreferences(requireContext())
        mCategoryDialog = CategoryDialogFragment.newInstance()
        mCategoryDialog?.onCategorySelectedListener(this)
        mImagesAdapter = ImagesAdapter()
        mImagesAdapter?.onImageClickListener(this)
        addPostViewModel.removeAllImages()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Handler().postDelayed({ saleViewModel.getSaleCategories() }, 300)
        binding.rvPostImgs.adapter = mImagesAdapter
        binding.rvConditions.adapter = ConditionsAdapter(this)
        setupObserver()
        setupListeners()

    }

    override fun onResume() {
        super.onResume()
        mSelectedCategory?.let {
            binding.btCategory.text = it.name
        }
    }

    private fun setupObserver() {

        addPostViewModel.getLiveDataImages.observe(viewLifecycleOwner) {
            mImagesAdapter?.setData(it)
        }

        addPostViewModel.getCoverImageObserver.observe(viewLifecycleOwner) {
            Glide.with(requireActivity())
                .load(it)
                .placeholder(R.drawable.app_logo)
                .error(R.drawable.app_logo)
                .into(binding.ivCoverPic)
        }


        //sale category
        saleViewModel.getSaleResObserver.observe(viewLifecycleOwner) {
            categories.addAll(it)
        }

    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun setupListeners() {
        binding.fabBack.setOnClickListener { requireActivity().onBackPressed() }

        binding.fabBin.setOnClickListener {

            if (addPostViewModel.getPostImage.isEmpty()) return@setOnClickListener

            MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_App)
                .setTitle(getString(R.string.alert))
                .setMessage(getString(R.string.messageDeleteAllImages))
                .setPositiveButton(getString(R.string.cancel)) { dialog, which ->
                    dialog.dismiss()

                }.setNegativeButton(getString(R.string.deleteAll)) { dialog, which ->
                    binding.ivCoverPic.setImageResource(R.drawable.img_placeholder)
                    addPostViewModel.removeAllImages()
                }.show()

        }

        binding.btContinue.setOnClickListener {
          //  val activity: Activity? = this.requireActivity()
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
            mCategoryDialog?.show(
                requireActivity().supportFragmentManager,
                TAG
            )
        }

        binding.fabAddImg.setOnClickListener {
            if ((mImagesAdapter ?: return@setOnClickListener).itemCount >= 6) {
                Global.showMessage(binding.root, "Maximum limit reached", Snackbar.LENGTH_LONG)
                return@setOnClickListener
            }
            chooseOptionsDialog()
        }


        binding.rgBuyingOptions.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.rb_bid -> {
                    binding.gpBidTime.visibility = View.VISIBLE
                    binding.gpFirmOnPrice.visibility = View.GONE
                    mBuyingOptions = BuyingOptions.BID.name

                }
                R.id.rb_pickup -> {
                    binding.gpBidTime.visibility = View.GONE
                    binding.gpFirmOnPrice.visibility = View.VISIBLE
                    mBuyingOptions = BuyingOptions.PICKUP.name
                    binding.tvStartDateTime.text = ""
                    binding.tvEndDateTime.text = ""
                    startDateTimeInMillis = 0
                    endDateTimeInMillis = 0
                }
            }
        }

        binding.tvStartDateTime.setOnClickListener {
            selectDate(START_DATE)
        }

        binding.tvEndDateTime.setOnClickListener {
            selectDate(END_DATE)
        }

    }

    private fun validatePostData() {

        when {
            mImagesAdapter?.itemCount == 0 -> {
                Global.showMessage(binding.root, getString(R.string.messageUploadImages))
            }
            TextUtils.isEmpty(binding.etPostTitle.text.toString().trim()) -> {
                Global.showMessage(binding.root, getString(R.string.messageProductNameRequired))
            }
            TextUtils.isEmpty(binding.etPostDescription.text.toString().trim()) -> {
                Global.showMessage(binding.root, getString(R.string.messageDescriptionRequired))
            }
            TextUtils.isEmpty(binding.etPostPrice.text.toString().trim()) -> {
                Global.showMessage(binding.root, getString(R.string.messagePriceRequired))
            }
            mSelectedCategory == null -> {
                Global.showMessage(binding.root, getString(R.string.messageSelectCategory))

            }
            else -> {
                if (mBuyingOptions == BuyingOptions.BID.name && (startDateTimeInMillis == 0L || endDateTimeInMillis == 0L)) {
                    Global.showMessage(binding.root, getString(R.string.messageSelectBidTime))
                    return
                }

                if(endDateTimeInMillis < startDateTimeInMillis){
                    Global.showMessage(binding.root,getString(R.string.messageEndBidTimeMustBeHigher))
                    return
                }

                addPostViewModel.getPostData.userId =
                    helper?.getCurrentUser()?.userId ?: ""

                addPostViewModel.getPostData.productName =
                    binding.etPostTitle.text.toString().trim()

                addPostViewModel.getPostData.productDescription =
                    binding.etPostDescription.text.toString().trim()

                addPostViewModel.getPostData.productPrice =
                    binding.etPostPrice.text.toString().trim()

                addPostViewModel.getPostData.category = mSelectedCategory?.name ?: ""

                addPostViewModel.getPostData.categoryId = mSelectedCategory?.categoryId ?: ""

                addPostViewModel.getPostData.firmOnPrice =
                    if (binding.switchFirmOnPrice.isChecked) "1" else "0"

                addPostViewModel.getPostData.buying_option = mBuyingOptions

                addPostViewModel.getPostData.condition = mCondition

                Log.d(TAG, "validatePostData: ${addPostViewModel.getPostData.toString()}")

              //  findNavController().navigate(R.id.action_nav_graph_add_post_to_nav_add_post_step_2_fragment)
                val direction=AddPostFragmentDirections.actionNavGraphAddPostToNavAddPostStep2Fragment()
                findNavController().navigate(direction)

            }
        }

    }

  private  fun chooseOptionsDialog() {

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


        fabCamera.setOnClickListener {
            chooseOptDia?.dismiss()
            chooseFromCamera()
        }

        fabGallery.setOnClickListener {
            chooseOptDia?.dismiss()
            openGallery()
          //  chooseFromGallery()
        }
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
        storagePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)

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
        /*val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        intent.data = MediaStore.Images.Media.INTERNAL_CONTENT_URI
        startActivityForResult(intent, Global.RC_GALLERY);*/*/
        maxImageSelectionCount = 6-mImagesAdapter!!.itemCount

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
        }


        else if (requestCode == Global.RC_GALLERY && grantResults.isNotEmpty()) {
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
            addPostViewModel.addImage(mCurrentPhotoPath ?: "")


        } else if (requestCode == RC_ADD_CAR_DETAIL) {

            if (resultCode == RESULT_OK) {

                Log.d(TAG, "onActivityResult:  ${data?.getStringExtra("brandName")} ")

                addPostViewModel.getPostData.brandName = data?.getStringExtra("brandName") ?: ""
                addPostViewModel.getPostData.year = data?.getStringExtra("year") ?: ""
                addPostViewModel.getPostData.fuelType = data?.getStringExtra("fuel") ?: ""
                addPostViewModel.getPostData.transmissionType = data?.getStringExtra("transmission") ?: ""
                addPostViewModel.getPostData.kmDriven = data?.getStringExtra("kmDriven") ?: ""
                addPostViewModel.getPostData.noOfOwners = data?.getStringExtra("numOwner") ?: ""
                addPostViewModel.getPostData.carTitle = data?.getStringExtra("adTitle") ?: ""
                addPostViewModel.getPostData.cardAdditionalInfo = data?.getStringExtra("adDescription") ?: ""
                addPostViewModel.getPostData.vinNumber = data?.getStringExtra("vinNumber") ?: ""


            } else {
                mSelectedCategory = null
                binding.btCategory.text = "Select Category"

            }


        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onImageClick(imagePath: String, position: Int) {
        addPostViewModel.setCoverImage(imagePath, position)
    }

    override fun onDeleteImage(position: Int) {
        addPostViewModel.removeImage(position)
    }

    override fun getSelectedCategory(category: Any) {

        if (category is SaleCategory) {

            mSelectedCategory = category
            binding.btCategory.text = category.name
            if (category.name.equals("Cars & Automobile", true)) {
                Intent(requireContext(), AddCarDetailActivity::class.java).also {
                    startActivityForResult(it, RC_ADD_CAR_DETAIL)
                }
            }
        }

    }

   private fun selectDate(dateType: Int) {

       val defaultDate : Date = if(startDateTimeInMillis == 0L){
           Date()
       }else{
           Date(startDateTimeInMillis+(2*24*60*60*1000))
       }

       SingleDateAndTimePickerDialog.Builder(requireContext())
           .bottomSheet()
           .curved()
           .minutesStep(15)
           .mustBeOnFuture()
           .defaultDate(defaultDate)

           .mainColor(ContextCompat.getColor(requireContext(),R.color.colorPrimary))
           .titleTextColor(ContextCompat.getColor(requireContext(),R.color.colorWhite))
           //.displayHours(false)
           //.displayMinutes(false)
           //.todayText("aujourd'hui")
           .title(getString(R.string.selectTime))
           .listener {

             val result =   SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(it)
               if (dateType == START_DATE){
                   binding.tvStartDateTime.text = result
                   addPostViewModel.getPostData.bidStartTime = result
                   startDateTimeInMillis = it.time
               }else{
                   binding.tvEndDateTime.text = result
                   addPostViewModel.getPostData.bidEndTime = result
                   endDateTimeInMillis = it.time
               }
               Log.d(TAG, "selectDate: $result  long value is = ${it.time}")

           }.display()






      /*  val resultBuilder = StringBuilder()
        val constraintBuilder =
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.selectDate))
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setCalendarConstraints(constraintBuilder.build())
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())

            .build()
        datePicker.addOnPositiveButtonClickListener {
            Log.d(TAG, "selectDate: $it")

            val selectedDate = Date(it)
            val result = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(selectedDate)
            Log.d(TAG, "selectDate: $result")
            resultBuilder.append(result)
            resultBuilder.append(" ")


            val rightNow = Calendar.getInstance()
            val timePicker = MaterialTimePicker
                .Builder()
                .setHour(rightNow.get(Calendar.HOUR_OF_DAY))
                .setMinute(rightNow.get(Calendar.MINUTE))
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .setTitleText("Select Time")
                .build()
            timePicker.addOnPositiveButtonClickListener {
                val hour = timePicker.hour
                val minutes = timePicker.minute
                Log.d(TAG, "selectDate: Hours = $hour  and  minutes = $minutes")

                resultBuilder.append("$hour:$minutes:00")

                val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                val date = sdf.parse(resultBuilder.toString())

                date?.let { d ->
                    if (dateType == START_DATE) {
                        binding.tvStartDateTime.text = resultBuilder.toString()
                        addPostViewModel.getPostData.bidStartTime = resultBuilder.toString()
                        startDateTimeInMillis = d.time
                    } else {
                        endDateTimeInMillis = d.time
                        when {
                            startDateTimeInMillis == endDateTimeInMillis -> {
                                Global.showMessage(
                                    binding.root,
                                    "Time must not be same",
                                    Snackbar.LENGTH_LONG
                                )
                                endDateTimeInMillis = 0
                            }

                            endDateTimeInMillis < startDateTimeInMillis -> {
                                Global.showMessage(
                                    binding.root,
                                    "End Time must  be higher",
                                    Snackbar.LENGTH_LONG
                                )
                                endDateTimeInMillis = 0
                            }
                            else -> {
                                binding.tvEndDateTime.text = resultBuilder.toString()
                                addPostViewModel.getPostData.bidEndTime = resultBuilder.toString()
                            }


                        }
                    }

                }


            }
            timePicker.show(childFragmentManager, TAG)


        }
        datePicker.show(childFragmentManager, TAG)
*/

    }

    override fun onConditionSelect(condition: String) {
        mCondition = condition
    }


}
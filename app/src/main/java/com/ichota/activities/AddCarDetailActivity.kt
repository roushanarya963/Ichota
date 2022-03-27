package com.ichota.activities

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.ichota.R
import com.ichota.adapter.ConditionsAdapter
import com.ichota.adapter.ImagesAdapter
import com.ichota.adapter.VehicleTypeAdapter
import com.ichota.databinding.ActivityAddCarDetailBinding
import com.ichota.dialogs.CategoryDialogFragment
import com.ichota.utils.BuyingOptions
import com.ichota.utils.Global
import com.ichota.viewModel.AddPostViewModel
import com.nguyenhoanglam.imagepicker.model.ImagePickerConfig
import com.nguyenhoanglam.imagepicker.model.RootDirectory
import com.nguyenhoanglam.imagepicker.ui.imagepicker.registerImagePicker
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "AddCarDetailActivity"
private const val RC_ADD_CAR_DETAIL = 111
class AddCarDetailActivity : AppCompatActivity(),ImagesAdapter.ImageClickInterface ,ConditionsAdapter.ConditionSelectInterface,
    CategoryDialogFragment.CategorySelectedInterface {

    private lateinit var binding: ActivityAddCarDetailBinding
    private var mBuyingOptions: String = BuyingOptions.PICKUP.name
    private var startDateTimeInMillis: Long = 0
    private var endDateTimeInMillis: Long = 0
   // private val addPostViewModel: AddPostViewModel by activityViewModels()
    private val addPostViewModel : AddPostViewModel by viewModels()
    private var mImagesAdapter: ImagesAdapter? = null
    private var chooseOptDia: Dialog? = null
    private var maxImageSelectionCount : Int = 6
    private var mCurrentPhotoPath: String? = null
    private var intent1 : Intent ?= null
    private val brands = arrayOf(
        "Toyota",
        "Honda",
        "Chevrolet",
        "Ford",
        "Mercedes-Benz",
        "Jeep",
        "BMW",
        "Porsche",
        "Subaru",
        "Nissan",
        "Cadillac",
        "Volkswagen",
        "Lexus",
        "Audi",
        "Ferrari",
        "Volvo",
        "Jaguar",
        "GMC",
        "Buick",
        "Acura",
        "Bentley",
        "Dodge",
        "Hyundai",
        "Lincoln",
        "Mazda",
        "Land Rover",
        "Tesla",
        "Ram Trucks",
        "Kia",
        "Chrysler",
        "Pontiac",
        "Infiniti",
        "Mitsubishi",
        "Oldsmobile",
        "Maserati",
        "Aston Martin",
        "Bugatti",
        "Fiat",
        "Mini",
        "Alfa Romeo",
        "Saab",
        "Genesis",
        "Suzuki",
        "Studebaker",
        "Renault",
        "Peugeot",
        "Daewoo",
        "Hudson",
        "Citroen",
        "MG"
    )
    private val fuelTypes = arrayOf("CNG & Hybrids", "Diesel", "Electric", "LPG", "Gas")
    private val transmissions = arrayOf("Automatic", "Manual")
    private val numOwners = arrayOf("1st", "2nd", "3rd", "4th", "4+")

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

    private fun showManuallyAlert() {

        MaterialAlertDialogBuilder(this)
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
        val uri = Uri.fromParts("package", this.packageName, null)
        intent.data = uri
        startActivity(intent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCarDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mImagesAdapter = ImagesAdapter()
        binding.rvPostImgs.adapter = mImagesAdapter
        addPostViewModel.removeAllImages()
        mImagesAdapter?.onImageClickListener(this)
        populateYear()
        initView()
        setupListeners()
        setupObserver()
    }

    private fun setupObserver() {

        addPostViewModel.getLiveDataImages.observe(this) {
            mImagesAdapter?.setData(it)
        }

        addPostViewModel.getCoverImageObserver.observe(this) {

        }
        //sale category

    }

    private fun populateYear() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = ArrayList<String>()
        for (i in currentYear downTo 1901) {
            years.add(i.toString())
        }

        val yearAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,years)
        binding.etYear.setAdapter(yearAdapter)


    }


    private fun initView() {

        val vehicleTypeAdapter = VehicleTypeAdapter()
        val brandsAdapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,brands)
        val fuelAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fuelTypes)
        val transmissionAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, transmissions)
        val numOwnerAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, numOwners)

        binding.rvCarType.setHasFixedSize(true)
        binding.rvCarType.adapter = vehicleTypeAdapter
        binding.etFuel.setAdapter(fuelAdapter)
        binding.etTransmission.setAdapter(transmissionAdapter)
        binding.etNunOwners.setAdapter(numOwnerAdapter)
        binding.etBrand.setAdapter(brandsAdapter)

    }

    private fun setupListeners() {


        binding.topAppBar.setNavigationOnClickListener { onBackPressed() }

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

        binding.btContinue.setOnClickListener {
            validatePostData()
        }

        binding.fabAddImg.setOnClickListener {
            if ((mImagesAdapter ?: return@setOnClickListener).itemCount >= 6) {
                Global.showMessage(binding.root, "Maximum limit reached", Snackbar.LENGTH_LONG)
                return@setOnClickListener
            }
            chooseOptionsDialog()
        }

       /* binding.btContinue.setOnClickListener {

            val brandName = binding.etBrand.text.toString().trim()
            val year = binding.etYear.text.toString().trim()
            val fuel = binding.etFuel.text.toString().trim()
            val transmission = binding.etTransmission.text.toString().trim()
            val kmDriven = binding.etKmDriven.text.toString().trim()
            val noOfOwners = binding.etNunOwners.text.toString().trim()
            val adTitle = binding.etAdTitle.text.toString().trim()
            val adDescription = binding.etDescription.text.toString().trim()
            val vinNumber = binding.etVinNumber.text.toString().trim()

            if (brandName.isEmpty()) {
                Global.showMessage(binding.root, "Brand name required")
                return@setOnClickListener
            }

            if (year.isEmpty()) {
                Global.showMessage(binding.root, "Please provide the year of registration")
                return@setOnClickListener
            }

            if (fuel.isEmpty()) {
                Global.showMessage(binding.root, "Fuel type required")
                return@setOnClickListener
            }

            if (transmission.isEmpty()) {
                Global.showMessage(binding.root, "Transmission type required")
                return@setOnClickListener
            }

            if (kmDriven.isEmpty()) {
                Global.showMessage(binding.root, "Km driven value is required")
                return@setOnClickListener
            }

            if (noOfOwners.isEmpty()) {
                Global.showMessage(binding.root, "No. of owners required")
                return@setOnClickListener
            }

            if (adTitle.isEmpty()) {
                Global.showMessage(binding.root, "Ad title required")
                return@setOnClickListener

            }

            if (vinNumber.isEmpty()) {
                Global.showMessage(binding.root, "Vin Number required")
                return@setOnClickListener

            }

            if (adDescription.isEmpty()) {
                Global.showMessage(binding.root, "Ad description required")
                return@setOnClickListener
            } else {
                Log.d(
                    TAG, "setupListeners: " +
                            "$brandName\n$year\n$fuel\n$transmission\n$kmDriven\n$noOfOwners\n$adTitle\n$adDescription\n"

                )

                val result = Intent()
                result.putExtra("brandName", brandName)
                result.putExtra("year", year)
                result.putExtra("fuel", fuel)
                result.putExtra("transmission", transmission)
                result.putExtra("kmDriven", kmDriven)
                result.putExtra("numOwner", noOfOwners)
                result.putExtra("adTitle", adTitle)
                result.putExtra("adDescription", adDescription)
                result.putExtra("vinNumber",vinNumber)
                setResult(RESULT_OK, result)
                this.finish()
            }

        }*/


    }

    private  fun chooseOptionsDialog() {

        chooseOptDia = Dialog(this)
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

    private fun chooseFromCamera() {
        if (!Global.hasFeatureCamera(this)) {
            Global.showMessage(binding.root, "Unable to detect camera", Snackbar.LENGTH_LONG)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            !Global.hasPermissions(this, arrayOf(Manifest.permission.CAMERA))
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

    private fun openCamera() {

        val cameraIntent = Intent()
        cameraIntent.action = MediaStore.ACTION_IMAGE_CAPTURE
        if (cameraIntent.resolveActivity(this.packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = Global.createImgFile(this)
                mCurrentPhotoPath = photoFile.absolutePath
            } catch (e: IOException) {
                Log.d(TAG, "openCamera: unable to create path ")
            }
            if (photoFile != null) {
                val photoUri = FileProvider.getUriForFile(
                    this,
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

    private fun validatePostData() {

    }

    override fun onBackPressed() {
        setResult(RESULT_CANCELED)
        this.finish()
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
               /* mSelectedCategory = null
                binding.btCategory.text = "Select Category"*/
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onImageClick(imagePath: String, position: Int) {
       // addPostViewModel.setCoverImage(imagePath, position)
    }

    override fun onDeleteImage(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onConditionSelect(condition: String) {

    }

    override fun getSelectedCategory(cateId: Any) {
        TODO("Not yet implemented")
    }
}
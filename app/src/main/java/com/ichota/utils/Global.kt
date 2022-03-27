package com.ichota.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Environment.getExternalStoragePublicDirectory
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.ichota.R
import com.ichota.auth.LoginActivity
import com.ichota.network.APIFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.http.Multipart
import java.io.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


object Global {


    const val RC_CAMERA = 101
    const val RC_GALLERY = 102

    fun showMessage(rootView: View, message: String, length: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(rootView, message, length).show()
    }

    fun hasInternet(context: Context): Boolean {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetworks = connectivityManager.activeNetwork ?: return false
            val activeNetworkCapabilities =
                connectivityManager.getNetworkCapabilities(activeNetworks) ?: return false
            return when {
                activeNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                activeNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false

            }

        } else {
            return connectivityManager.activeNetworkInfo?.isConnectedOrConnecting == true
        }
    }


    fun getTimeDifference(starSource: String?, endSource: String?): String {

        Log.d(
            "TAG",
            "getTimeDifference: startSource = $starSource =========  endSoure = $endSource"
        )


        if (starSource.isNullOrEmpty() || endSource.isNullOrEmpty()) return "No Time Available"
        val startDate = starSource
        val sdfStart = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val sdfEnd = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val dateStart = sdfStart.parse(starSource)
        val dateEnd = sdfEnd.parse(endSource)

        val timeDifference = (dateEnd?.time ?: 0) - (dateStart?.time ?: 0)
        val numDays = (timeDifference / (1000 * 60 * 60 * 24)).toInt()
        val hours = (timeDifference / (1000 * 60 * 60)).toInt()
        val minute = (timeDifference / (1000 * 60)).toInt()
        val seconds = (timeDifference / 1000).toInt()

        Log.d(
            "TAG",
            "getTimeDifference: timeDifference = $timeDifference , numDays  =$numDays ,hours = $hours ,minutes = $minute , seconds = $seconds"
        )

        return when {
            hours > 47 -> "${formatItemListingDate(starSource)}"
            hours > 23 -> "Yesterday"
            minute > 59 -> "$hours hours ago"
            seconds > 59 -> "$minute minutes ago"
            else -> {
                "Just now"
            }
        }

    }


    fun showLoginAlertMessage(context: Activity) {
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.login)
            .setMessage(R.string.messageLoginRequired)
            .setPositiveButton(R.string.login) { dialog, _ ->
                dialog.dismiss()
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
                context.finishAffinity()

            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    fun hasFeatureCamera(context: Context): Boolean {
        val pm = context.packageManager
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        var allGranted = false

        for (permission in permissions) {
            allGranted = (ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED)

        }

        return allGranted
    }

    fun requiredRational(activity: Activity?, permissions: String?): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permissions!!)
    }

    @Throws(IOException::class)
    fun createImgFile(context: Context): File {
        val timeStemp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imgFileName = "JPEG_" + timeStemp + "_"

        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            imgFileName,  //prefix
            ".jpg",  //suffix
            storageDir //directory
        )

    }

    fun getRealPathFromURI(context: Context, contentURI: Uri): String? {

        val result: String?

        //  val filePathColumn = arrayOf(MediaStore.Images.ImageColumns.DATA)
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? =
            context.contentResolver.query(contentURI, filePathColumn, null, null, null)
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex(filePathColumn[0])
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }


    @SuppressLint("Range")
    fun getPathFromUri(context: Context, contentUri: Uri): String? {
        var imgPath: String? = null
        val file = File(contentUri.path)
        val filePath = file.path.split(":")
        val imageId = filePath[filePath.size - 1]
        val cursor = context.contentResolver.query(
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null, MediaStore.Images.Media._ID + "= ?", arrayOf(imageId), null
        )
        cursor?.let {
            it.moveToFirst()
            imgPath = it.getString(it.getColumnIndex(MediaStore.Images.Media.DATA))
            it.close()
        }

        return imgPath
    }

    fun prepareFilePart(partName: String, uri: String,context: Context?=null): MultipartBody.Part? {
        try {
            val inputStream = context?.contentResolver?.openInputStream(Uri.parse(uri))
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG,60,outputStream)
            val byteArray = outputStream.toByteArray()
            val requestBody = byteArray.toRequestBody("file/*".toMediaTypeOrNull(),0,byteArray.size)
            return  MultipartBody.Part.createFormData(partName,"ichota_${Random().nextInt(1000)}.jpg",requestBody)

        }catch (e:Exception){
            return null

        }

      //  val file = File(filePath)
      //  val requestBody = saveBitmapToFile(file).asRequestBody("multipart/form-data".toMediaTypeOrNull())
      //  return MultipartBody.Part.createFormData(partName, file.name, requestBody)
    }


    private fun saveBitmapToFile(file: File): File {
        return try {

            // BitmapFactory options to downsize the image
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            o.inSampleSize = 6
            // factor of downsizing the image
            var inputStream = FileInputStream(file)
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o)
            inputStream.close()

            // The new size we want to scale to
            val REQUIRED_SIZE = 75

            // Find the correct scale value. It should be the power of 2.
            var scale = 1
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                o.outHeight / scale / 2 >= REQUIRED_SIZE
            ) {
                scale *= 2
            }
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            inputStream = FileInputStream(file)
            val selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2)
            inputStream.close()

            // here i override the original image file
            file.createNewFile()
            val outputStream = FileOutputStream(file)
            selectedBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            file
        } catch (e: Exception) {
            File("")
        }
    }

    fun getProgressDialogFullScreen(context: Context): Dialog {
        val progressDialog = Dialog(context, R.style.customDialogAnimation)
        progressDialog.setCancelable(false)
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.setContentView(R.layout.layout_progress_full_screen)
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        return progressDialog

    }

    fun getProgressDialog(context: Context): Dialog {
        val progressDialog = Dialog(context, R.style.customDialogAnimation)
        progressDialog.setCancelable(false)
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.setContentView(R.layout.layout_progress)
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        return progressDialog

    }


    //2021-02-25 11:17:04
    fun formatItemListingDate(source: String): String {
        var sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())
        return try {
            val sourceDate = sdf.parse(source)
            sdf = SimpleDateFormat("MMM dd, yyy", Locale.getDefault())
            sdf.format(sourceDate ?: Date())
        } catch (e: ParseException) {
            ""

        }
    }

    fun getCompleteAddress(
        context: Context,
        latitude: String,
        longitude: String
    ): String {


        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses: List<Address> =
                geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1)
            if (addresses.isEmpty()) return context.getString(R.string.noAddressAvailable)

            // val address = addresses[0]

            val address = addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

            val city = addresses[0].locality
            val state = addresses[0].adminArea

            val country = addresses[0].countryName
            val postalCode = addresses[0].postalCode
            val knownName = addresses[0].featureName


            /*val strReturnedAddress = StringBuilder("")

            for (i in 0..address.maxAddressLineIndex) {
                strReturnedAddress.append(address.getAddressLine(i)).append(" ")
            }*/
            // return strReturnedAddress.toString().trim()
            // return "$address,$city,$state,$country,$postalCode,$knownName"


            return "$city, $state"
        } catch (e: IOException) {
            return context.getString(R.string.noAddressAvailable)

        } catch (e: NumberFormatException) {
            return context.getString(R.string.noAddressAvailable)
        } catch (e: IllegalArgumentException) {
            return context.getString(R.string.noAddressAvailable)
        }


    }

    fun getAddress(
        context: Context,
        latitude: String,
        longitude: String
    ): Address? {


        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses: List<Address> =
                geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1)
            if (addresses.isEmpty()) return null
            return addresses[0]


        } catch (e: IOException) {
            return null

        } catch (e: NumberFormatException) {
            return null
        }


    }

    fun getZipCode(
        context: Context,
        latitude: String,
        longitude: String
    ): String {


        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses: List<Address> = geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1)
            if (addresses.isEmpty()) return ""
            val address = addresses[0]
            return address.postalCode
        } catch (e: IOException) {
            return ""

        } catch (e: NumberFormatException) {
            return ""
        }


    }

    fun shareLink(context: Context, link: String?) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = ("text/plain")
            putExtra(Intent.EXTRA_SUBJECT, "Sharing link")
            putExtra(Intent.EXTRA_TEXT, link)

        }
        context.startActivity(Intent.createChooser(intent, "Share using"))
    }

    fun getImageUrl(source: String?): String {
        if (source == null) return ""
        return if (source.startsWith("http")) {
            source
        } else
            APIFactory.BASE_URL_IMAGE + source
    }

    fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)

    }


    val Int.toPx
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            Resources.getSystem().displayMetrics
        )


    fun getPxFromDp(valueInDp: Int): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            valueInDp.toFloat(),
            Resources.getSystem().displayMetrics
        )
    }

    fun isOutDated(sourceDate: String): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(sourceDate)
        return Date().after(date)

    }

    fun getTimeDifference(sourceDate: String): Long {

        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = sdf.parse(sourceDate)
            date.time - Date().time

        } catch (e: Exception) {
            0
        }


    }

    fun openBrowser(context: Context, url: String) {
        val browserIntent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(url)
        }
        context.startActivity(browserIntent)

    }


}
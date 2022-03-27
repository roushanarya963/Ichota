package com.ichota.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.location.Location
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ichota.dialogs.SafetyToolkitBottomSheetDialog
import com.ichota.preferences.PrefKeys
import com.google.android.gms.maps.model.LatLng
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

import com.google.android.gms.maps.model.PolylineOptions
import com.ichota.NavGraphDirections
import com.ichota.R
import com.ichota.activities.FullImageActivity
import com.ichota.activities.SafetyCenterActivity
import com.ichota.databinding.FragmentLocationDetailBinding
import com.ichota.interfaces.IMainActivity
import com.ichota.utils.DataParser
import com.ichota.utils.Global
import com.ichota.viewModel.ProfileViewModel

import org.json.JSONObject
import android.graphics.Bitmap

import android.app.Activity
import android.os.Handler

import android.util.DisplayMetrics
import android.widget.ImageView
import com.ichota.model.ChatDialog
import com.ichota.model.Message
import com.ichota.model.MessageType
import com.ichota.model.PostDetail
import com.ichota.preferences.PreferenceHelper
import com.ichota.viewModel.ChatViewModel

import de.hdodenhof.circleimageview.CircleImageView
import java.util.regex.Pattern


private const val TAG = "LocationDetailActivity"
private const val INTERVAL = 1000 * 10L
private const val FASTEST_INTERVAL = 1000 * 5L
private const val CAMERA_ZOOM_LEVEL = 14.3f

class LocationDetailFragment : Fragment(), OnMapReadyCallback,
    SafetyToolkitBottomSheetDialog.ISafetyToolkitSheet {

    private lateinit var binding: FragmentLocationDetailBinding
    private var mIMainActivity: IMainActivity? = null
    private var mLocationRequest: LocationRequest? = null
    private var helper: PreferenceHelper? = null
    private var mCurrentUserId: String = ""
    private var remoteCurrentUserId: String = ""
    private var mChatDialog: ChatDialog? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val chatViewModel: ChatViewModel by viewModels()
    private var mCallback: SafetyToolkitBottomSheetDialog.ISafetyToolkitSheet? = null
    private var mSentReceivedId: Message? = null


    private val navArgs: LocationDetailFragmentArgs by navArgs()
    private val profileViewModel: ProfileViewModel by viewModels()
    private var mLatCurrentUser: Double = 0.0
    private var mLngCurrentUser: Double = 0.0
    private var mLatSeller: Double = 0.0
    private var mLngSeller: Double = 0.0
    private var mUserId: String? = null
    private var mContactNumber: String? = null
    private var mUserImage: String = ""
    private var mproductId: String = ""
    private var mUserName: String = ""
    private var mMap: GoogleMap? = null
    private var isLocationShareByRemoteUser: Boolean = false


    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)

            locationResult ?: return
            Log.d(TAG, "onLocationResult: ${locationResult.locations}")
            for (location in locationResult.locations) {
                mLatCurrentUser = location.latitude
                mLngCurrentUser = location.longitude
            }
        }

        override fun onLocationAvailability(p0: LocationAvailability?) {
            super.onLocationAvailability(p0)
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        helper = PreferenceHelper.getPreferences(requireContext())
        mCurrentUserId = helper?.getCurrentUser()?.userId ?: ""

        mSentReceivedId = navArgs.message

        createLocationRequest()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val supportMapFragment: SupportMapFragment? =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        supportMapFragment?.getMapAsync(this)

        if (!isGooglePlayServiceAvailable()) {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(getString(R.string.messageGooglePlayServicesNotAvailable))
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    dialog.dismiss()
                    findNavController().navigateUp()
                }.show()

            return
        }

        showSafetyTooltip()
        isLocationShareByRemoteUser = navArgs.message?.senderId == navArgs.postedByUserId

        if (isLocationShareByRemoteUser) {
            getLastKnownLocation()
        }


        if(navArgs.message?.senderId== mIMainActivity?.getPreference()?.getCurrentUser()?.userId!!){
            binding.clSeller.visibility=View.GONE
        }else{
            profileViewModel.getUserProfile(navArgs.message?.senderId?:"")
            try {

               /* mUserImage = Global.getImageUrl(navArgs.message?.senderImage)
                mUserName= navArgs.message?.senderName.toString()*/

                setupObserver()
            }catch (e: Exception){

            }

           /* mUserImage = Global.getImageUrl(navArgs.message.senderImage)
            mUserName= navArgs.message.senderName.toString()
            setupObserver(mUserImage,mUserName)*/
        }
        setupListener()
    }

    private fun getLastKnownLocation() {

        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            showLocationAlert()
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->

            Log.d(TAG, "getLastKnownLocation: $location")
            location?.let {
                mLatCurrentUser = it.latitude
                mLngCurrentUser = it.longitude
                drawRoute()
                mMap?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            it.latitude,
                            it.longitude
                        ), CAMERA_ZOOM_LEVEL
                    )
                )
            }
        }

    }


    private fun createLocationRequest() {
        mLocationRequest = LocationRequest.create().apply {
            interval = INTERVAL
            fastestInterval = FASTEST_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }


    private fun isGooglePlayServiceAvailable(): Boolean {
        val status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(requireContext())
        if (ConnectionResult.SUCCESS == status) {
            return true
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, requireActivity(), 0).show()
            return false
        }
    }


    private fun setupObserver() {

        Glide.with(this)
            .load(this.mUserImage)
            .into(binding.ivSeller)
        binding.tvSellerName.text = mUserName

        profileViewModel.getUserObserver.observe(viewLifecycleOwner) {

            try {
                val user = it[0]
                this.mUserImage = Global.getImageUrl(user.userImage)

              //  mUserImage = Global.getImageUrl(navArgs.message.senderImage)

                Glide.with(this)
                    .load(this.mUserImage)
                    .error(R.drawable.img_placeholder)
                    .placeholder(R.drawable.img_placeholder)
                    .into(binding.ivSeller)

                binding.tvSellerName.text = user.name
              //  binding.tvSellerName.text = navArgs.message.senderName
                mContactNumber = user.mobile

            } catch (e: Exception) {

            }
        }
    }


    private fun startLocationUpdate() {

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            startLocationUpdate()
            return
        }
        fusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.getMainLooper()
        )
    }


    private fun showSafetyTooltip() {
        val safetyToolkitDialog = SafetyToolkitBottomSheetDialog()
        safetyToolkitDialog.setOnSafetyToolkitClickListener(this)
        safetyToolkitDialog.show(childFragmentManager, TAG)
    }


    private fun showShareLiveLocationTooltip() {

        /*val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_tooltip_live_location)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val params = WindowManager.LayoutParams()
        params.copyFrom(dialog.window?.attributes)
        params.gravity = Gravity.BOTTOM
        params.y = binding.fabCurrentLocation.height
        params.x = 150
        dialog.window?.attributes = params
        dialog.show()*/

        val inflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_tooltip_live_location, null)
        val popupWindow = PopupWindow(view, 700, ConstraintLayout.LayoutParams.WRAP_CONTENT, true)
        popupWindow.showAsDropDown(binding.fabSafetyTooltip, -150, -50, Gravity.END)

    }

    private fun setupListener() {


        binding.fabBack.setOnClickListener {
            it.findNavController().navigateUp()
        }


        binding.ivMessage.setOnClickListener { binding.fabBack.callOnClick() }


        binding.fabSafetyTooltip.setOnClickListener {
            showSafetyTooltip()
        }

        binding.fabCurrentLocation.setOnClickListener {

               drawRoute()

            Log.d(TAG, "setupListener: CUrrent lat lng $mLatCurrentUser\n$mLngCurrentUser")

            mMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        mLatCurrentUser,
                        mLngCurrentUser
                    ), CAMERA_ZOOM_LEVEL
                )
            )
        }




        binding.fabSend.setOnClickListener {
            sendTextMessage()
        }

        binding.fabDirection.setOnClickListener {
            drawRoute()
        }

        binding.fabSafetyTooltip.setOnClickListener {
            showSafetyTooltip()
        }

        binding.fabWifi.setOnClickListener {
            showShareLiveLocationTooltip()
        }

        binding.ivSeller.setOnClickListener {
            Intent(requireContext(), FullImageActivity::class.java).apply {
                putStringArrayListExtra("images", arrayListOf(mUserImage))
                startActivity(this)
            }

        }


        binding.ivPhone.setOnClickListener {
            if (mContactNumber.isNullOrEmpty()) {
                showContactMissingAlert()
                return@setOnClickListener
            }
            val callIntent = Intent().apply {
                action = Intent.ACTION_DIAL
                data = Uri.parse("tel:$mContactNumber")

            }
            startActivity(callIntent)
        }
    }

    private fun sendTextMessage() {


      Handler(Looper.getMainLooper()).post{
          mIMainActivity?.hideSoftKeyboard(binding.root)
      }

        val message=binding.etMessage.text.toString().trim()
        if(message.isEmpty()){
            Global.showMessage(binding.root,"Please type something..")
            return
        }
        if(isContainPhoneNumber(message)){
            showContainContactInfoAlert()
            return
        }

      val msg =  binding.etMessage.text.toString()

      val receiverId=navArgs.message?.receiverId.toString()
      val productid=navArgs.postId.toString()
      var senderId=navArgs.message?.senderId.toString()
        if(senderId==mCurrentUserId){
            remoteCurrentUserId=receiverId
        }else{
            remoteCurrentUserId=senderId
        }
        binding.etMessage.text.clear()
        chatViewModel.insertChat(
            mCurrentUserId,
            remoteCurrentUserId,
            msg,
            productid,
            MessageType.TYPE_MESSAGE,
            navArgs.postType,
            "",
            "",
            "",
            null

        )

    }

    private fun showContainContactInfoAlert() {
        TODO("Not yet implemented")
    }

    private fun isContainPhoneNumber(source: String): Boolean {

        val patterns = ("^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
                + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"
                + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$")
        val pattern= Pattern.compile(patterns)
        val sourceArray=source.split("")
        for(value in sourceArray){
            val matcher=pattern.matcher(value)
            if(matcher.matches()){
                return true
            }
        }
        return false
    }

    private fun showContactMissingAlert() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.alert))
            .setMessage(getString(R.string.messageContactMissing))
            .setPositiveButton(getString(R.string.dismiss)) { dialog, which ->
                dialog.dismiss()
            }.show()
    }


    override fun onResume() {
        super.onResume()
        startLocationUpdate()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdate()
    }

    private fun stopLocationUpdate() {
        fusedLocationClient.removeLocationUpdates(mLocationCallback)
    }


    override fun onMapReady(map: GoogleMap) {
        mMap = map
        mMap?.setOnMapClickListener(GoogleMap.OnMapClickListener {
           Handler(Looper.getMainLooper()).post{
               mIMainActivity?.hideSoftKeyboard(binding.root)
           }
        })
        setupMap()
    }

    private fun setupMap() {


        if (isLocationShareByRemoteUser) {

            try {
                mLatCurrentUser =
                    mIMainActivity?.getPreference()?.getStringValue(PrefKeys.KEY_LATITUDE)!!
                        .toDouble()
                mLngCurrentUser =
                    mIMainActivity?.getPreference()?.getStringValue(PrefKeys.KEY_LONGITUDE)!!
                        .toDouble()

                mLatSeller = navArgs.message?.lat!!.toDouble()
                mLngSeller = navArgs.message?.lng!!.toDouble()

            } catch (e: Exception) {

            }
            drawRoute()


        } else {

            try {
                mLatCurrentUser = navArgs.message?.lat!!.toDouble()
                mLngCurrentUser = navArgs.message?.lng!!.toDouble()
            } catch (e: Exception) {

            }
            val origin = LatLng(mLatCurrentUser, mLngCurrentUser)

            val marker = MarkerOptions()
                .position(origin)
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        createCustomMarker(
                            requireContext(),
                            mIMainActivity?.getPreference()?.getCurrentUser()?.userImage!!,
                        ) ?: return
                    )
                )

            mMap?.addMarker(marker)
            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, CAMERA_ZOOM_LEVEL))

        }


        /* if (ActivityCompat.checkSelfPermission(
                 requireContext(),
                 Manifest.permission.ACCESS_FINE_LOCATION
             ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                 requireContext(),
                 Manifest.permission.ACCESS_COARSE_LOCATION
             ) != PackageManager.PERMISSION_GRANTED
         ) {
             return
         }*/mMap

        // mMap?.isMyLocationEnabled = true
        mMap?.uiSettings?.isMyLocationButtonEnabled = false

        mMap?.setOnMyLocationButtonClickListener {
            return@setOnMyLocationButtonClickListener true
        }

    }


    private fun createCustomMarker(context: Context, userImage: String): Bitmap? {
        val marker: View =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.layout_custom_marker,
                null
            )
        val markerImage = marker.findViewById<ImageView>(R.id.iv_user) as CircleImageView
        Glide.with(requireActivity())
            .load(Global.getImageUrl(userImage))
            .placeholder(R.drawable.img_placeholder)
            .error(R.drawable.img_placeholder)
            .into(markerImage)
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        marker.layoutParams = ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT)
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        marker.buildDrawingCache()

        val bitmap = Bitmap.createBitmap(
            marker.measuredWidth,
            marker.measuredHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        marker.draw(canvas)
        return bitmap

    }

    private fun drawRoute() {

        val origin = LatLng(mLatCurrentUser, mLngCurrentUser)
        val destination = LatLng(mLatSeller, mLngSeller)
        val url = getDirectionsUrl(origin, destination)
        val downloadTask = DownloadTask(url)

        // Start downloading json data from Google Directions API
        // Start downloading json data from Google Directions API
        downloadTask.execute()
    }

    private fun getDirectionsUrl(origin: LatLng, dest: LatLng): String {

        Log.d(TAG, "getDirectionsUrl: origin = $origin\ndestination = $dest")

        // Origin of route
        val str_origin = "origin=" + origin.latitude + "," + origin.longitude

        // Destination of route
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude

        // Sensor enabled

        val sensor = "sensor=false"
        val mode = "mode=driving"

        // Building the parameters to the web service
        val key = "key=" + getString(R.string.api_key)

        // Building the parameters to the web service

        // Building the parameters to the web service
        // val parameters = "$str_origin&amp;$str_dest&amp;$key"

        val parameters = "$str_origin&$str_dest&$sensor&$mode&$key"

        // Output format
        val output = "json"

        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters"
    }

    private fun showLocationAlert() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Alert")
            .setTitle("Location permission is required please enable it from app settings")
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    @Throws(IOException::class)
    private fun downloadUrl(strUrl: String): String {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null

        try {
            val url = URL(strUrl)
            // Creating an http connection to communicate with url
            urlConnection = url.openConnection() as HttpURLConnection

            // Connecting to url
            urlConnection.connect()

            // Reading data from url
            iStream = urlConnection.getInputStream()
            val br = BufferedReader(InputStreamReader(iStream))
            val sb = StringBuffer()
            var line: String? = ""
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
            data = sb.toString()
            br.close()
        } catch (e: java.lang.Exception) {
            Log.d("Exception on download", e.toString())
        } finally {
            iStream?.close()
            urlConnection?.disconnect()
        }
        return data
    }


    inner class DownloadTask(private val url: String) : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg p0: String?): String? {
            var data = ""

            try {
                // Fetching the data from web service
                data = downloadUrl(url)
                Log.d("DownloadTask", "DownloadTask : $data")
            } catch (e: java.lang.Exception) {
                Log.d("Background Task", e.toString())
            }
            return data
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            val parserTask = ParserTask(result)

            // Invokes the thread for parsing the JSON data
            // Invokes the thread for parsing the JSON data

            parserTask.execute()
        }
    }

    inner class ParserTask(private val result: String?) : AsyncTask<String?, Int?, List<List<HashMap<String, String>>>?>() {
        // Parsing the data in non-ui thread

        // Executes in UI thread, after the parsing process
        override fun onPostExecute(result: List<List<HashMap<String, String>>>?) {
            var points: ArrayList<LatLng?>
            var lineOptions: PolylineOptions? = null

            // Traversing through all the routes

            for (i in result!!.indices) {
                points = ArrayList()
                lineOptions = PolylineOptions()

                // Fetching i-th route
                val path = result[i]

                // Fetching all the points in i-th route
                for (j in path.indices) {
                    val point = path[j]
                    val lat = point["lat"]!!.toDouble()
                    val lng = point["lng"]!!.toDouble()
                    val position = LatLng(lat, lng)
                    points.add(position)
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points)
                lineOptions.width(10f)
                lineOptions.color(Color.parseColor("#1167B1"))
                Log.d("onPostExecute", "onPostExecute lineoptions decoded")
            }


            // Drawing polyline in the Google Map for the i-th route

            if (lineOptions != null) {
                mMap?.clear()
                val origin = LatLng(mLatCurrentUser, mLngCurrentUser)
                val destination = LatLng(mLatSeller, mLngSeller)

                val startMarker = MarkerOptions()
                    .position(origin)
                    .icon(
                        BitmapDescriptorFactory.fromBitmap(
                            createCustomMarker(
                                requireContext(),
                                mIMainActivity?.getPreference()?.getCurrentUser()?.userImage!!,
                            ) ?: return
                        )
                    )
                val endMarker = MarkerOptions()
                    .position(destination)
                    .icon(
                        BitmapDescriptorFactory.fromBitmap(
                            createCustomMarker(
                                requireContext(),
                                navArgs.message?.senderImage!!
                            ) ?: return
                        )
                    )

                mMap?.addMarker(startMarker)
                mMap?.addMarker(endMarker)

                mMap?.addPolyline(lineOptions)

            } else {
                Log.d("onPostExecute", "without Polylines drawn")
            }
        }

        override fun doInBackground(vararg p0: String?): List<List<HashMap<String, String>>>? {

            val jObject: JSONObject
            var routes: List<List<HashMap<String, String>>>? = null

            try {
                jObject = JSONObject(result)
                // Log.d("ParserTask", result)
                val parser = DataParser()
                Log.d("ParserTask", parser.toString())

                // Starts parsing data
                routes = parser.parse(jObject)
                Log.d("ParserTask", "Executing routes")
                Log.d("ParserTask", routes.toString())
            } catch (e: Exception) {
                Log.d("ParserTask", e.toString())
                e.printStackTrace()
            }
            return routes
        }
    }

    override fun onClickSafetyCenter() {
        Intent(requireContext(), SafetyCenterActivity::class.java).apply {
            startActivity(this)
        }
    }

    override fun onClickShareLocation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.alert))
            .setMessage(getString(R.string.messageShareLocation))
            .setPositiveButton(getString(R.string.cancel)){dialog,_ ->
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.shareLocation)){dialog,_ ->
                dialog.dismiss()
                shareLocation()
            }
            .show()
    }

    private fun shareLocation(){
        val uri = "Hi i am here\n" + "http://maps.google.com/maps?saddr= $mLatCurrentUser,$mLngCurrentUser"

        val intent = Intent(Intent.ACTION_SEND)
            .apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT,"")
                putExtra(Intent.EXTRA_TEXT,uri)

            }
       startActivity(Intent.createChooser(intent,"Share via"))

    }

    override fun onClickReportSafetyIssue() {
        val direction =
            LocationDetailFragmentDirections.actionLocationDetailFragmentToReportSafetyIssueFragment(
                navArgs.postedByUserId,
                navArgs.postType,
                navArgs.postId
            )
        findNavController().navigate(direction)
    }

    override fun onClickCallAssistance() {
        val direction = NavGraphDirections.actionNavChatFragmentToCallAssistanceFragment()
        findNavController().navigate(direction)
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

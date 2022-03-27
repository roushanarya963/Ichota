package com.ichota.fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.ichota.NavGraphDirections
import com.ichota.activities.FullImageActivity
import com.ichota.activities.MapActivity
import com.ichota.R
import com.ichota.adapter.ImgSlideShowAdapter
import com.ichota.adapter.PostDetailImagesAdapter
import com.ichota.adapterClasses.MyPageChangeCallback
import com.ichota.databinding.FragmentProductDetailBinding
import com.ichota.dialogs.ItemReportDialogFragment
import com.ichota.dialogs.MarkAsSoldDialogFragment
import com.ichota.interfaces.IMainActivity
import com.ichota.model.*
import com.ichota.network.APIFactory
import com.ichota.preferences.PrefKeys
import com.ichota.preferences.PreferenceHelper
import com.ichota.repositories.PostDetailRepository
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.viewModel.*
import kotlin.collections.ArrayList

private const val TAG = "ProductDetailFragment"


class PostDetailFragment : Fragment(), OnMapReadyCallback, PostDetailImagesAdapter.IPostImageClick,
    ImgSlideShowAdapter.ImageClickInterface {

    private val navArgs: PostDetailFragmentArgs by navArgs()

    private var mMap: GoogleMap? = null
    private lateinit var binding: FragmentProductDetailBinding
    private val postDetailViewModel: PostDetailViewModel by viewModels()
    private val authViewModel:AuthViewModel by viewModels()
    private val saleViewModel: SaleViewModel by viewModels()
    private val serviceViewModel: ServiceViewModel by viewModels()
    private val favViewModel: FavouriteViewModel by viewModels()
    private val myListingViewModel: MyListingViewModel by viewModels()
    private var mPostedUserId: String = ""
    private var mIMainActivity: IMainActivity? = null
    private var mCategoryType: String? = null
    private var mLat: Double = 0.0
    private var mLon: Double = 0.0
    private var mPostImages = ArrayList<String>()
    private var postDetailImageAdapter: PostDetailImagesAdapter? = null
    private val postDetailRepo = PostDetailRepository(APIFactory.makeServiceAPi())
    private var mShareLink: String? = null
    private var isUserLogIn = false
    private var helper:PreferenceHelper? = null

    companion object {
        @JvmStatic
        fun newInstance() = PostDetailFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        helper=PreferenceHelper.getPreferences(requireContext())
        requireActivity().window.statusBarColor = Color.TRANSPARENT
        isUserLogIn = PreferenceHelper.getPreferences(requireContext())
            .getBoolean(PrefKeys.KEY_IS_USER_LOG_IN)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mShareLink =
            "www.ichota.com/post/${navArgs.categoryType}/${navArgs.postId}/${navArgs.postedUserId}"
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        setupPostDetailObservers()
        setupFavouriteObserver()

      //  setMakAsSoldObeserver()

        setDeleteItemObserver()
        setProductDetailAvailable()

        mCategoryType = navArgs.categoryType

        Log.d(TAG, "onViewCreated: $navArgs")

        if (mCategoryType.equals(Constants.CATEGORY_SALE)) {
            postDetailViewModel.getPostDetail(
                mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: "", navArgs.postId
            )
        } else {
            postDetailViewModel.getServiceDetail(
                mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: "", navArgs.postId
            )
        }

        mPostedUserId = navArgs.postedUserId

        var visibility = View.VISIBLE

        if (mPostedUserId == mIMainActivity?.getPreference()?.getCurrentUser()?.userId) {

            binding.constraintLayoutUserProfile.visibility=View.GONE
            binding.btSendAnOffer.text = getString(R.string.markAsSold)
            binding.btInquire.text = getString(R.string.delete)
            binding.btInquire.visibility = visibility
            binding.btSendAnOffer.visibility = visibility
            binding.fabReport.visibility=View.GONE
            binding.fabFav.visibility=View.GONE

            binding.btSendAnOffer.setOnClickListener {

                if(navArgs.categoryType == Constants.CATEGORY_SALE){
                    openMarkSoldDialog(navArgs.categoryType)
                }else{
                 // mark as available
                    authViewModel.productDetailAvailable(navArgs.postId)
                }

               }
            binding.btInquire.setOnClickListener { openDeleteItemDialog() }

        } else {
            binding.constraintLayoutUserProfile.visibility=View.VISIBLE
            binding.btInquire.visibility = visibility
            binding.btSendAnOffer.visibility = visibility
            binding.fabFav.visibility=View.VISIBLE
            binding.btSendAnOffer.setOnClickListener { openSendOfferDialog() }

            binding.btInquire.setOnClickListener {

                if (isUserLogIn == false) {
                    Global.showLoginAlertMessage(requireActivity())
                } else {

                    val direction = NavGraphDirections.actionGlobalNavChatFragment(
                        prepareChatData(""),
                        MessageType.TYPE_INQUIRE,
                        mCategoryType.toString()
                    )
                    it.findNavController().navigate(direction)

                }

                /* val direction = NavGraphDirections.actionGlobalNavChatFragment(prepareChatData(""), MessageType.TYPE_INQUIRE)
                 it.findNavController().navigate(direction)*/

            }
        }

     //   binding.fabFav.visibility = visibility
       // binding.fabReport.visibility = visibility
        binding.cardProfile.visibility = visibility

        setupListeners()
    }

    private fun setProductDetailAvailable() {
      authViewModel.getMessage.observe(viewLifecycleOwner){
          mIMainActivity?.showMessage(it)

          findNavController().navigateUp()

         /* val direction=NavGraphDirections.actionGlobalNavGraphHome()
          findNavController().navigate(direction)*/

      }
      authViewModel.getProgress.observe(viewLifecycleOwner) {
          mIMainActivity?.showProgress(it)
      }
      authViewModel.getChangePasswordObserver.observe(viewLifecycleOwner){
          it.let {
               if(it.message=="Success"){
                   postDetailViewModel.getServiceDetail(
                       mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: "", navArgs.postId
                   )

                  // findNavController().popBackStack(R.id.nav_graph_home, false)
                 /*  val direction=NavGraphDirections.actionGlobalNavGraphHome()
                   findNavController().navigate(direction)*/

               }
          }
      }
    }

    private fun setDeleteItemObserver() {
        myListingViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showProgress(it)
        }
        myListingViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showMessage(it)
            findNavController().navigateUp()
        }
        myListingViewModel.getMarkAsDeletedObserver.observe(viewLifecycleOwner) {
            it?.let {
               if(it.success==Constants.RESPONSE_SUCCESS){
                   val directions =NavGraphDirections.actionGlobalNavPostsListFragment( )
                   findNavController().navigate(directions)
                  // findNavController().navigateUp()
               }
            }
        }
    }

    private fun setMakAsSoldObeserver() {
        myListingViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showMessage(it)
            findNavController().navigateUp()
        }
        myListingViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showProgress(it)
        }

        myListingViewModel.getMarkAsSoldObserver.observe(viewLifecycleOwner) {
            it?.let {

            }
        }

    }


    private fun setupFavouriteObserver() {
        favViewModel.getFavouriteStatusObserver.observe(viewLifecycleOwner) {
            setFavourite(it)
        }
        favViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            Global.showMessage(binding.root, it)
        }
    }

    private fun setupPostDetailObservers() {

        postDetailViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showMessage(it)
        }
        postDetailViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showProgress(it)
        }
        postDetailViewModel.getPostDetailObserver.observe(viewLifecycleOwner) {
            it?.let { post ->
                setPostData(post)
            }
        }
        postDetailViewModel.getServicePostDetailObserver.observe(viewLifecycleOwner) {
            it?.let { post ->
                setPostData(post)
            }
        }

    }


    private fun setPostData(post: Post) {
     //   post.category
        mPostImages = if (post.productImages.isEmpty()) {
            arrayListOf(post.productCoverImage ?: "")
        } else {
            post.productImages
        }

        binding.tvRatings.text = post.postedUser[0].rating
        // binding.tvRatingsCount.text=post.postedUser[0].rating

        val postDetailImages = ArrayList<PostDetailImageModel>()

        for ((index, value) in mPostImages.withIndex()) {
            if (index == 0) {
                binding.pagerProductImage.currentItem = 0
                binding.tabLayout.visibility = View.GONE
                postDetailImages.add(PostDetailImageModel(value, true))
            } else {
                binding.tabLayout.visibility = View.VISIBLE
                postDetailImages.add(PostDetailImageModel(value, false))
            }
        }


        binding.tvItemCondition.visibility = View.VISIBLE

        binding.tvItemCondition.text = post.conditions?.replaceFirstChar { it.uppercase() }
        when (post.conditions?.lowercase()) {
            Constants.CONDITION_NEW.lowercase() -> {
                binding.tvItemCondition.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.colorConditionNew)
            }
            Constants.CONDITION_LIKE_NEW.lowercase() -> {
                binding.tvItemCondition.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.colorConditionLikeNew)
            }
            Constants.CONDITION_GOOD.lowercase() -> {
                binding.tvItemCondition.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.colorConditionGood)
            }
            Constants.CONDITION_FAIR.lowercase() -> {
                binding.tvItemCondition.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.colorConditionFair)
            }
            Constants.CONDITION_BAD.lowercase() -> {
                binding.tvItemCondition.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.colorConditionBad)
            }
            else -> {
                binding.tvItemCondition.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.colorTextSecondary)
                binding.tvItemCondition.text = "NA"
            }

        }
        binding.gpFirmOnPrice.visibility =
            if (post.firmPriceStatus == "1") View.VISIBLE else View.GONE

        try {
            binding.tvPrice.text =
                String.format("$%,d", post.productPrice?.split(".")?.get(0)?.toLong())

        } catch (e: NumberFormatException) {
            binding.tvPrice.text = getString(R.string.priceInDoller, post.productPrice)
        }

        binding.switchFirmOnPrice.isChecked = post.firmPriceStatus == "1"
        binding.pagerProductImage.adapter = ImgSlideShowAdapter(mPostImages, this)

        TabLayoutMediator(
            binding.tabLayout,
            binding.pagerProductImage
        ) { tab, position -> }.attach()

        postDetailImageAdapter = PostDetailImagesAdapter(postDetailImages)
        postDetailImageAdapter?.setOnImageClickListener(this)
        binding.rvPostImages.adapter = postDetailImageAdapter

        mPostedUserId = post.userId
        // sliderPagerAdapter?.setData(post.productImages)
        binding.tvProductName.text = post.productName
        val distance = "${(post.distance) ?: 0} mi"
        binding.tvDistance.text = distance

        setFavourite(
            post.favStatus.equals("1")
        )

        val description = StringBuilder()

        if (!post.productDescription.isNullOrEmpty()) {
            description.append(post.productDescription)
            description.append("\n")
        }

        if (!post.carTitle.isNullOrEmpty()) {
            description.append(post.carTitle)
            description.append("\n")
        }
        if (!post.carAdditionalInfo.isNullOrEmpty()) {
            description.append(post.carAdditionalInfo)
            description.append("\n")
        }
        if (!post.brandName.isNullOrEmpty()) {
            description.append("Brand: ${post.brandName}")
            description.append("\n")
        }
        if (!post.year.isNullOrEmpty()) {
            description.append("Year: ${post.year}")
            description.append("\n")
        }
        if (!post.fuelType.isNullOrEmpty()) {
            description.append("Fuel Type: ${post.fuelType}")
            description.append("\n")
        }

        if (!post.transmissionType.isNullOrEmpty()) {
            description.append("TransMission Type: ${post.transmissionType}")
            description.append("\n")
        }

        if (!post.kmDriven.isNullOrEmpty()) {
            description.append("Km Driven: ${post.kmDriven}")
            description.append("\n")
        }
        if (!post.numOfOwner.isNullOrEmpty()) {
            description.append("${post.numOfOwner} Owner")

        }

        binding.tvItemDescription.text = description.trim()

        val itemListedDateLocation =
            "${Global.formatItemListingDate(post.postCreatedTime ?: " ")} \n${
                Global.getCompleteAddress(
                    requireContext(),
                    (post.latitude ?: "0"),
                    (post.longitude ?: "0")
                )
            }"

        binding.tvItemListedSince.text = itemListedDateLocation


        //  binding.tvRatingsCount.text= "("+ post.userprofileratingcount+")"
        //  binding.tvItemListedSince.text = itemListedDateLocation

        try {
            binding.tvPersonName.text = post.postedUser[0].name
            binding.tvRatingsProfile.text=post.postedUser[0].rating
        } catch (e: IndexOutOfBoundsException) {
            binding.tvPersonName.text = "No User"
        }




        binding.tvItemLocation.text = Global.getCompleteAddress(
            requireContext(),
            (post.latitude ?: "0"),
            (post.longitude ?: "0")
        )

        /* binding.tvSellerLocation.text = Global.getCompleteAddress(
             requireContext(),
             (post.latitude ?: "0"),
             (post.longitude ?: "0")
         )*/

        try {
            binding.tvSellerLocation.text = Global.getCompleteAddress(
                requireContext(),
                (post.latitude ?: "0"),
                (post.longitude ?: "0")
            )

        } catch (e: Exception) {
            binding.tvSellerLocation.text = getString(R.string.noAddressAvailable)
        }


        setMapLocation(
            (post.latitude ?: "0"),
            (post.longitude ?: "0")
        )

        Glide.with(requireActivity())
            .load(Global.getImageUrl(post.postedUser[0].userImage))
            .placeholder(R.drawable.app_logo)
            .error(R.drawable.app_logo)
            .into(binding.ivProfile)


    }

    private fun setPostData(post: ServicePostDetail) {

        mPostImages = if (post.productImages.isEmpty()) {
            arrayListOf(post.productCoverImage ?: "")
        } else {
            post.productImages
        }

      // post.category
        val postDetailImages = ArrayList<PostDetailImageModel>()

        for ((index, value) in mPostImages.withIndex()) {

            if (index == 0) {
                binding.pagerProductImage.currentItem = 0
                binding.tabLayout.visibility = View.GONE
                postDetailImages.add(PostDetailImageModel(value, true))
            } else {
                binding.tabLayout.visibility = View.VISIBLE
                postDetailImages.add(PostDetailImageModel(value, false))
            }
        }


        try {
            binding.tvPrice.text =
                String.format("$%,d", post.serviceMinimumPrice.split(".")[0].toLong())

        } catch (e: NumberFormatException) {
            binding.tvPrice.text = getString(R.string.priceInDoller, post.serviceMinimumPrice)
        }
        binding.gpFirmOnPrice.visibility = View.GONE
        postDetailImageAdapter = PostDetailImagesAdapter(postDetailImages)
        postDetailImageAdapter?.setOnImageClickListener(this)
        binding.rvPostImages.adapter = postDetailImageAdapter
        binding.pagerProductImage.adapter = ImgSlideShowAdapter(mPostImages, this)

        TabLayoutMediator(
            binding.tabLayout,
            binding.pagerProductImage
        ) { tab, position ->

        }.attach()

        if(post.userId==mIMainActivity?.getPreference()?.getCurrentUser()?.userId){
            binding.btSendAnOffer.text = if(post.markAsAvailable =="0") "Not available" else  "Available"
        }

        mPostedUserId = post.userId
        binding.tvProductName.text = post.productName
        binding.tvRatingsProfile.text = post.userprofileratingcount
        binding.tvRatings.text = post.userprofileavgrating
        binding.tvRatingsCount.text = "(" + post.userprofileratingcount + ")"


        binding.tvSellerLocation.text = Global.getCompleteAddress(
            requireContext(),
            (post.postedUser[0].latitude ?: "0"),
            (post.postedUser[0].longitude ?: "0")
        )

        val distance = "${(post.distance) ?: 0} mi"
        binding.tvDistance.text = distance

        setFavourite(
            post.favStatus.equals("1")
        )
        binding.tvItemDescription.text =
            post.productDescription ?: "No Description available"


        val itemListedDateLocation =
            "${Global.formatItemListingDate(post.postCreatedTime ?: "")} \n${
                Global.getCompleteAddress(
                    requireContext(),
                    (post.latitude ?: "0"),
                    (post.longitude ?: "0")
                )
            }"

        binding.tvItemListedSince.text = itemListedDateLocation

        binding.tvPersonName.text = post.postedUser[0].name

        binding.tvItemLocation.text = Global.getCompleteAddress(
            requireContext(),
            (post.latitude ?: "0"),
            (post.longitude ?: "0")
        )

        setMapLocation(
            (post.latitude ?: "0"),
            (post.longitude ?: "0")
        )

        Glide.with(requireActivity())
            .load(post.postedUser[0].userImage)
            .placeholder(R.drawable.app_logo)
            .error(R.drawable.app_logo)
            .into(binding.ivProfile)

    }


    private fun setupListeners() {

        binding.btBack.setOnClickListener {
            it.findNavController().popBackStack()
            mIMainActivity?.showProgress(false)
        }
        binding.ibNextLocation.setOnClickListener {

            Intent(requireActivity(), MapActivity::class.java).also {
                it.putExtra(MapActivity.KEY_LATITUDE, mLat)
                it.putExtra(MapActivity.KEY_LONGITUDE, mLon)
                startActivity(it)
            }

        }
        binding.tvItemLocationHeader.setOnClickListener { binding.ibNextLocation.callOnClick() }
        binding.tvItemLocation.setOnClickListener { binding.ibNextLocation.callOnClick() }



        binding.fabReport.setOnClickListener {


            if (isUserLogIn == false) {
                Global.showLoginAlertMessage(requireActivity())
            } else {
                ItemReportDialogFragment.newInstance(
                    navArgs.postId,
                    mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: "",
                    navArgs.categoryType
                )
                    .show(requireActivity().supportFragmentManager, TAG)
            }


        }

        binding.fabShare.setOnClickListener {

            if (
                navArgs.categoryType.isEmpty()
                || navArgs.postId.isEmpty()
                || navArgs.postedUserId.isEmpty()
                || mShareLink.isNullOrEmpty()
            ) return@setOnClickListener


            Global.shareLink(requireActivity(), mShareLink!!)
        }

        binding.fabFav.setOnClickListener {

            if (isUserLogIn == false) {
                Global.showLoginAlertMessage(requireActivity())
            } else {

                if(navArgs.categoryType == Constants.CATEGORY_SALE){

                    favViewModel.changeFavStatus(
                        mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: "",
                        if (navArgs.categoryType == Constants.CATEGORY_SERVICE)
                            postDetailViewModel.getServicePost[0].id
                        else
                            postDetailViewModel.getPost[0].id
                    )
                    setFavourite(postDetailViewModel.getPost[0].favStatus != "1")

                }else{

                    favViewModel.changeFavStatusService(
                        mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: "",
                        if (navArgs.categoryType == Constants.CATEGORY_SERVICE)
                            postDetailViewModel.getServicePost[0].id
                        else
                            postDetailViewModel.getPost[0].id
                    )
                    setFavourite(postDetailViewModel.getServicePost[0].favStatus != "1")

                }

              /*  favViewModel.changeFavStatus(
                    mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: "",
                    if (navArgs.categoryType == Constants.CATEGORY_SERVICE)
                        postDetailViewModel.getServicePost[0].id
                    else
                        postDetailViewModel.getPost[0].id
                )*/

            }

            /* favViewModel.changeFavStatus(
                 mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: "",
                 postDetailViewModel.getPost[0].id
             )*/

        }

        binding.cardProfile.setOnClickListener {

            if (isUserLogIn == false) {
                Global.showLoginAlertMessage(requireActivity())
            } else {
                val direction =
                    NavGraphDirections.actionGlobalNavPublicProfileFragment(
                        mPostedUserId
                    )
                it.findNavController().navigate(direction)
            }

            /* val direction =
                 NavGraphDirections.actionGlobalNavPublicProfileFragment(
                     mPostedUserId
                 )
             it.findNavController().navigate(direction)*/
        }

        binding.ibNextProfile.setOnClickListener {
            binding.cardProfile.callOnClick()
        }

        /* binding.btSendAnOffer.setOnClickListener {
             openSendOfferDialog()
         }*/


        binding.pagerProductImage.registerOnPageChangeCallback(object : MyPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                postDetailImageAdapter?.setSelected(position)
            }

        })

    }

    private fun openDeleteItemDialog() {


        if (isUserLogIn == false) {
            Global.showLoginAlertMessage(requireActivity())
        } else {
            val builder = MaterialAlertDialogBuilder(requireContext())
            builder.setTitle(R.string.alert)
            builder.setMessage(R.string.deleteitem)
            builder.setPositiveButton("Yes") { dialog, which ->
                mIMainActivity?.getPreference()?.getCurrentUser()?.userId?.let {
                    myListingViewModel.markAsDeleted(navArgs.postId, it)
                }

            }
            builder.setNegativeButton("No") { dialog, which ->
                builder.setCancelable(true)
            }
            val dialog = builder.create()
            dialog.show()
        }

    }

    private fun openMarkSoldDialog(categoryType: String) {

       /* if (isUserLogIn == false) {
            Global.showLoginAlertMessage(requireActivity())
        } else {
            val dialog = Dialog(requireContext())
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_mark_sold)
            dialog.window?.let {
                it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                it.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
                )
                val params = it.attributes
                params.gravity = Gravity.CENTER_VERTICAL
            }

            val btnOk = dialog.findViewById<Button>(R.id.bt_mark_as_sold)
            val btCancel = dialog.findViewById<Button>(R.id.bt_mark_as_sold_cancel)
            btCancel.setOnClickListener { dialog.dismiss() }
            btnOk.setOnClickListener {
                mIMainActivity?.getPreference()?.getCurrentUser()?.userId?.let { it1 ->
                    myListingViewModel.markAsSold(navArgs.postId, it1)
                }
                dialog.dismiss()
            }
            dialog.show()
        }*/



       var userId: String? = null
        var productId : String? = null

        if(categoryType==Constants.CATEGORY_SALE){
            userId = postDetailViewModel.getPost[0].userId
            productId = postDetailViewModel.getPost[0].id
        }else{
            userId = postDetailViewModel.getServicePost[0].userId
            productId = postDetailViewModel.getServicePost[0].id
        }

        MarkAsSoldDialogFragment.newInstance(userId,productId,navArgs.postId).show(requireActivity().supportFragmentManager, TAG)


    }


    private fun openSendOfferDialog() {

        val dialog = Dialog(requireContext())
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_send_offer)
        dialog.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            val params = it.attributes
            params.gravity = Gravity.CENTER_VERTICAL
        }
        val etOfferPrice = dialog.findViewById<EditText>(R.id.et_offer_price)
        val btSend = dialog.findViewById<Button>(R.id.bt_send)
        val btCancel = dialog.findViewById<Button>(R.id.bt_cancel)

        btCancel.setOnClickListener { dialog.dismiss() }

        btSend.setOnClickListener {

            if (!isUserLogIn) {
                Global.showLoginAlertMessage(requireActivity())
            } else {
                val offerPrice = etOfferPrice.text.toString().trim()
                if (offerPrice.isEmpty()) {
                    Global.showMessage(binding.root, "Enter offer price")
                    return@setOnClickListener
                }

                val direction =
                    NavGraphDirections.actionGlobalNavChatFragment(
                        prepareChatData(offerPrice),
                        MessageType.TYPE_OFFER,
                        navArgs.categoryType
                    )
                findNavController().navigate(direction)
                dialog.dismiss()
            }

            /* val offerPrice = etOfferPrice.text.toString().trim()

             if (offerPrice.isEmpty()) {
                 Global.showMessage(binding.root, "Enter offer price")
                 return@setOnClickListener
             }
             dialog.dismiss()

             val direction =
                 NavGraphDirections.actionGlobalNavChatFragment(
                     prepareChatData(offerPrice),
                     MessageType.TYPE_OFFER
                 )
             findNavController().navigate(direction)*/


        }
        dialog.show()
    }


    private fun prepareChatData(offerPrice: String): ChatDialog {

        return ChatDialog(
            navArgs.categoryType,
            "",
            "0",
            MessageType.TYPE_OFFER,
            if (navArgs.categoryType == Constants.CATEGORY_SALE) postDetailViewModel.getPost[0].id else postDetailViewModel.getServicePost[0].id,
            if (navArgs.categoryType == Constants.CATEGORY_SALE) postDetailViewModel.getPost[0].userId else postDetailViewModel.getServicePost[0].userId,
           "",
            offerPrice,
            "",
            if (navArgs.categoryType == Constants.CATEGORY_SALE) postDetailViewModel.getPost[0].productName
                ?: "" else postDetailViewModel.getServicePost[0].productName ?: "",
            if (navArgs.categoryType == Constants.CATEGORY_SALE) postDetailViewModel.getPost[0].productPrice
                ?: "" else postDetailViewModel.getServicePost[0].serviceMinimumPrice ?: "",

            if (navArgs.categoryType == Constants.CATEGORY_SALE) postDetailViewModel.getPost[0].productCoverImage
                ?: "" else postDetailViewModel.getServicePost[0].productCoverImage ?: "",
            "",
            if (navArgs.categoryType == Constants.CATEGORY_SALE) postDetailViewModel.getPost[0].postedUser[0].userImage
            else postDetailViewModel.getServicePost[0].postedUser[0].userImage,
            "",
            if (navArgs.categoryType == Constants.CATEGORY_SALE)
                postDetailViewModel.getPost[0].postedUser[0].name
            else postDetailViewModel.getServicePost[0].postedUser[0].name
        )
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()

    }


    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()

    }

    override fun onMapReady(googleMap: GoogleMap?) {
        Log.d(TAG, "onMapReady: ")
        mMap = googleMap
        mMap?.uiSettings?.isMapToolbarEnabled = false
        GoogleMapOptions().liteMode(true)
        mMap?.setOnMapClickListener {

            Intent(requireActivity(), MapActivity::class.java).also {
                it.putExtra(MapActivity.KEY_LATITUDE, googleMap?.cameraPosition?.target?.latitude)
                it.putExtra(MapActivity.KEY_LONGITUDE, googleMap?.cameraPosition?.target?.longitude)
                startActivity(it)
            }
        }


    }

    private fun setMapLocation(latitude: String, longitude: String) {
        Log.d(TAG, "setMapLocation: Lat => $latitude  and Lon => $longitude")
        try {
            val currentLocation = LatLng(latitude.toDouble(), longitude.toDouble())
            mLat = latitude.toDouble()
            mLon = longitude.toDouble()

            //  val markerOptions = MarkerOptions()
            // markerOptions.position(currentLocation)
            // mMap?.addMarker(markerOptions)

            val circleOptions = CircleOptions()
            circleOptions.center(currentLocation)
            circleOptions.fillColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorMapCircle
                )
            )
            circleOptions.strokeColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorMapCircle
                )
            )
            circleOptions.radius(700.0)
            circleOptions.strokeWidth(0f)
            mMap?.addCircle(circleOptions)

            mMap?.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
        } catch (e: NumberFormatException) {
            val currentLocation = LatLng(0.0, 0.0)
            mMap?.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
        }


    }

    private fun setFavourite(isFav: Boolean) {

        binding.fabFav.imageTintList =
            ColorStateList.valueOf(
                if (isFav)
                    ContextCompat.getColor(requireContext(), R.color.colorWhite)
                else
                    ContextCompat.getColor(requireContext(), R.color.colorPrimary)
            )

        binding.fabFav.backgroundTintList =
            ColorStateList.valueOf(
                if (isFav)
                    ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                else
                    ContextCompat.getColor(requireContext(), R.color.colorWhite)
            )


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIMainActivity = context as IMainActivity
    }

    override fun onDetach() {
        super.onDetach()
        mIMainActivity = null
    }

    override fun onImageClicked(position: Int) {
        Log.d(TAG, "onImageClicked: $position")
        binding.pagerProductImage.currentItem = position
        //  setPostImage(image)
    }

    override fun onImageClick(position: Int) {
        Intent(requireContext(), FullImageActivity::class.java).apply {
            putExtra("images", mPostImages)
            putExtra("position", position)
            startActivity(this)
        }
    }

    /* fun setPostImage(image: String) {
         Glide.with(requireContext())
             .load(Global.getImageUrl(image))
             .placeholder(R.drawable.app_logo)
             .error(R.drawable.app_logo)
             .into(binding.ivProduct)
     }*/

}






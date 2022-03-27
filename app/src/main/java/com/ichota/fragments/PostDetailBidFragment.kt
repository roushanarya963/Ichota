package com.ichota.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.ichota.R
import com.ichota.activities.FullImageActivity
import com.ichota.adapter.BidsAdapter
import com.ichota.adapter.ImgSlideShowAdapter
import com.ichota.adapter.PostDetailImagesAdapter
import com.ichota.adapterClasses.MyPageChangeCallback
import com.ichota.databinding.FragmentPostDetailBidBinding
import com.ichota.dialogs.ItemReportDialogFragment
import com.ichota.interfaces.IMainActivity
import com.ichota.model.Post
import com.ichota.model.PostDetailImageModel
import com.ichota.model.PostedUser
import com.ichota.model.ServicePostDetail
import com.ichota.preferences.PrefKeys
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.viewModel.FavouriteViewModel
import com.ichota.viewModel.MyListingViewModel
import com.ichota.viewModel.PostDetailViewModel
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

private const val TAG = "PostDetailBidFragment"
private const val KEY_POST_ID = "key_post_id"
private const val KEY_PRODUCT_PRICE = "key_product_price"
private const val KEY_MIN_BID_PRICE = "key_min_bid_price"
private const val KEY_MAX_BID_PRICE = "key_max_bid_price"
private const val COUNT_DOWN_INTERVAL = 1000L


class PostDetailBidFragment : Fragment(), PostDetailImagesAdapter.IPostImageClick, ImgSlideShowAdapter.ImageClickInterface {
    private val navArgs: PostDetailBidFragmentArgs by navArgs()
    private lateinit var binding: FragmentPostDetailBidBinding
    private val postDetailViewModel: PostDetailViewModel by viewModels()
    private val favViewModel: FavouriteViewModel by viewModels()
    private val myListingViewModel: MyListingViewModel by viewModels()
    private var mBidsAdapter: BidsAdapter? = null
    private var mIMainActivity: IMainActivity? = null
    private var mPostImages = ArrayList<String>()
    private var postDetailImageAdapter: PostDetailImagesAdapter? = null
    private var isCanceled : Boolean = false
    private val minutes : Long = 1000 * 60
    private var isUserLogIn = false
    private var helper:PreferenceHelper? = null
    private var soldMessage:String?=null
    // 1 day, 2 hours, 35 minutes,50 seconds

    val millisInFuture : Long = (minutes * 1440) + (minutes * 155) +(1000 * 50)

    private var timer : CountDownTimer? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isUserLogIn = PreferenceHelper.getPreferences(requireContext()).getBoolean(PrefKeys.KEY_IS_USER_LOG_IN)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostDetailBidBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        helper=PreferenceHelper.getPreferences(requireContext())
        setupObserver()
        setupFavouriteObserver()
        setupListeners()
        setMakAsSoldObeserver()
        setDeleteItemObserver()
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

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: postId = ${navArgs.postId}")
            Handler().postDelayed({
                postDetailViewModel.getPostDetail(
                    mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: "",
                    navArgs.postId
                )
            }, 200)
    }

    private fun setupObserver() {
        postDetailViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            Global.showMessage(binding.root, it)
        }
        postDetailViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            binding.progressBar.root.visibility = if (it)View.VISIBLE else View.GONE

        }
        postDetailViewModel.getPostDetailObserver.observe(viewLifecycleOwner) {
            it?.let { post ->
                setPostData(post)
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

    @SuppressLint("NewApi")
    private fun setPostData(post: Post) {

        if (post.userId == mIMainActivity?.getPreference()
                ?.getCurrentUser()?.userId
        ) {
            binding.fabFav.visibility = View.GONE
            binding.fabReport.visibility = View.GONE
            binding.btPlaceBid.visibility = View.GONE
            binding.btSendAnOffer.visibility=View.VISIBLE
            binding.btInquire.visibility=View.VISIBLE
            binding.btSendAnOffer.text = getString(R.string.markAsSold)
            binding.btInquire.text = getString(R.string.delete)
            binding.btSendAnOffer.setOnClickListener { openMarkSoldDialog() }
            binding.btInquire.setOnClickListener { openDeleteItemDialog() }


        }else{
            binding.fabFav.visibility = View.VISIBLE
            binding.fabReport.visibility = View.VISIBLE
            binding.btPlaceBid.visibility = View.VISIBLE
            binding.btSendAnOffer.visibility=View.GONE
            binding.btInquire.visibility=View.GONE
        }

        mPostImages = if (post.productImages.isEmpty()){
            arrayListOf(post.productCoverImage?:"")
        } else{
            post.productImages
        }

        val postDetailImages = ArrayList<PostDetailImageModel>()

        for ((index, value) in mPostImages.withIndex()) {
            if (index == 0) {
                binding.pagerProductImage.currentItem = 0
                binding.tabLayout.visibility=View.GONE
                postDetailImages.add(PostDetailImageModel(value, true))
            } else {
                binding.tabLayout.visibility=View.VISIBLE
                postDetailImages.add(PostDetailImageModel(value, false))
            }

        }

        binding.pagerProductImage.adapter = ImgSlideShowAdapter(mPostImages,this)
        TabLayoutMediator(binding.tabLayout,binding.pagerProductImage){tab,position -> }.attach()
        postDetailImageAdapter =  PostDetailImagesAdapter(postDetailImages)
        val postDetailImagesAdapter = postDetailImageAdapter
        postDetailImagesAdapter?.setOnImageClickListener(this)
        binding.rvPostImages.adapter = postDetailImagesAdapter

        binding.tvProductName.text = post.productName
        binding.tvProductCost.text=  "$"+post.productPrice
        binding.tvRatings.text=post.postedUser[0].rating



        val distance = "${(post.distance) ?: 0} mi"
        binding.tvDistance.text = distance
        setFavourite(
            post.favStatus == "1"
        )



        binding.tvItemDescription.text =
            post.productDescription ?: "No Description available"
        mBidsAdapter = BidsAdapter(post.bidDetails)
        binding.rvBids.adapter = mBidsAdapter
        binding.tvNoBids.visibility = if (mBidsAdapter?.itemCount == 0) View.VISIBLE else View.GONE



        try {
            val estimatePrice = "${
                String.format("$%,d", post.minBidAmount)
            }-${String.format("$%,d", post.maxBidAmount)}"


            binding.tvEstimationPrice.text = estimatePrice

            binding.tvCurrentBidPrice.text = String.format("$%,d", post.currentBid)

            binding.tvCurrentBidPrice.text = String.format("$%,d", post.maxBidAmount)


        } catch (e: Exception) {

        }
       if(post.activeSoldStatus=="1"){
           binding.tvBidRunningTime.text="0.0"
           binding.btPlaceBid.visibility=View.GONE
           binding.btSendAnOffer.visibility=View.GONE
           binding.btInquire.visibility=View.GONE
           binding.progressPrice.max=100
           binding.progressPrice.progress=100

           binding.progressPrice.progressDrawable?.colorFilter=BlendModeColorFilter(Color.RED,BlendMode.SRC_IN)

         //  val color = ContextCompat.getColor(requireContext(), R.color.colorRed500)
         //  binding.progressPrice.setBackgroundColor(color)
          // binding.progressPrice.progressTintList=ColorStateList.valueOf(Color.RED)

       }else{

          /* binding.progressPrice.max=100
           binding.progressPrice.progress=50
           binding.progressPrice.trackColor=Color.parseColor("##0000FF")*/

           if (!Global.isOutDated(post.bidStartTime ?: return)) {
               binding.rootCardBidList.visibility = View.GONE

            //   binding.btPlaceBid.visibility = View.VISIBLE

               startCountDownTimer(
                   "Auction starts in ",
                   Global.getTimeDifference(post.bidStartTime),
                   COUNT_DOWN_INTERVAL
               )

           } else if (!Global.isOutDated(post.bidEndTime ?: return)) {
               binding.rootCardBidList.visibility = View.VISIBLE
               startCountDownTimer("Auction remaining time",Global.getTimeDifference(post.bidEndTime), COUNT_DOWN_INTERVAL)
           }
           else{
               binding.rootCardBidList.visibility = View.GONE
             //  binding.btPlaceBid.visibility = View.GONE
               binding.tvBidRunningTime.text = getString(R.string.auctionhasended)
           }
       }


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
                    myListingViewModel.markAsDeleted(
                        navArgs.postId,
                        it
                    )
                }

            }
            builder.setNegativeButton("No") { dialog, which ->
                builder.setCancelable(true)
            }
            val dialog = builder.create()
            dialog.show()
        }


        /* val builder = MaterialAlertDialogBuilder(requireContext())
          builder.setTitle(R.string.alert)
          builder.setMessage(R.string.deleteitem)
          builder.setPositiveButton("Yes"){
                  dialog,which->
                  mIMainActivity?.getPreference()?.getCurrentUser()?.userId?.let {
                  myListingViewModel.markAsDeleted(navArgs.postId,
                      it
                  )
              }

          }
          builder.setNegativeButton("No"){
                  dialog,which->
                  builder.setCancelable(true)
          }
          val dialog = builder.create()
          dialog.show()*/


    }

    private  fun startCountDownTimer(title : String, time : Long, interval : Long) {

        if (timer != null){
            timer?.cancel()
            timer = null
        }
        timer = object : CountDownTimer(time,interval) {
            override fun onTick(millisUntilFinished: Long) {
                val runningTime = millisUntilFinished/interval
                binding.tvBidRunningTime.text = getString(R.string.formatBidTime,title,getTimeString(millisUntilFinished))
            }
            override fun onFinish() {
                Log.d(TAG, "onFinish: ")
                binding.rootCardBidList.visibility = View.GONE
                binding.btPlaceBid.visibility = View.GONE
                binding.progressPrice.max=100
                binding.progressPrice.progress=100
                binding.progressPrice.trackColor=Color.parseColor("#FF0000")
                binding.tvBidRunningTime.text = getString(R.string.auctionhasended)
            }
        }
        timer?.start()



    }

    private fun getTimeString(millisUntilFinished : Long) : String{
        var millisUntilFinished : Long = millisUntilFinished

        val days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished)
        millisUntilFinished -= TimeUnit.DAYS.toMillis(days)

        val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
        millisUntilFinished -= TimeUnit.HOURS.toMillis(hours)

        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
        millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes)

        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)


      return  when{
            days == 0L -> String.format(
                Locale.getDefault(),
                "%02d:%02d:%02d",
                hours,minutes,seconds
            )
          hours == 0L -> String.format(
              Locale.getDefault(),
              "%02d:%02d",
              minutes,seconds
          )
          minutes == 0L ->String.format(
              Locale.getDefault(),
              "%02d seconds",
               seconds
          )

          else -> String.format(
              Locale.getDefault(),
              "%02d day: %02d:%02d:%02d",
              days,hours,minutes,seconds
          )
        }

    }

    private fun openMarkSoldDialog() {

        if (isUserLogIn == false) {
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
                    myListingViewModel.markAsSold(
                        navArgs.postId,
                        it1
                    )
                }

                dialog.dismiss()
            }
            dialog.show()
        }


        /*val dialog = Dialog(requireContext())
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_mark_sold)
        dialog.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            val params = it.attributes
            params.gravity=Gravity.CENTER_VERTICAL
        }

        val btnOk=dialog.findViewById<Button>(R.id.bt_mark_as_sold)
        val btCancel =dialog.findViewById<Button>(R.id.bt_mark_as_sold_cancel)
        btCancel.setOnClickListener { dialog.dismiss() }
        btnOk.setOnClickListener {
            mIMainActivity?.getPreference()?.getCurrentUser()?.userId?.let { it1 ->
                myListingViewModel.markAsSold(navArgs.postId,
                    it1
                )

            }

            dialog.dismiss()
        }
        dialog.show()*/

    }

    private fun setupListeners() {

        binding.btBack.setOnClickListener {
            it.findNavController().navigateUp()
        }

        binding.btPlaceBid.setOnClickListener {

            if(isUserLogIn==false){
                Global.showLoginAlertMessage(requireActivity())
            }else{

                val directions = PostDetailBidFragmentDirections.actionNavPostDetailBidFragmentToNavAddBidFragment(
                    postDetailViewModel.getPost[0].id,
                    postDetailViewModel.getPost[0].maxBidAmount,
                    postDetailViewModel.getPost[0].productPrice?:"",
                    postDetailViewModel.getPost[0].productCoverImage?:""
                )

                it.findNavController().navigate(directions)

            }




        }

        binding.fabReport.setOnClickListener {

            if(isUserLogIn==false){
                Global.showLoginAlertMessage(requireActivity())
            }else{
                ItemReportDialogFragment.newInstance(
                    postDetailViewModel.getPost[0].id,
                    mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: "",
                    navArgs.categoryType
                )
                    .show(requireActivity().supportFragmentManager, TAG)
            }

            /*ItemReportDialogFragment.newInstance(
                postDetailViewModel.getPost[0].id,
                mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: "",
                navArgs.categoryType
            )
                .show(requireActivity().supportFragmentManager, TAG)*/

        }
        binding.fabShare.setOnClickListener {
            Global.shareLink(requireActivity(), "Link goes here")
        }

        binding.fabFav.setOnClickListener {

            if(isUserLogIn==false){
                Global.showLoginAlertMessage(requireActivity())
            }else{


                if(navArgs.categoryType == Constants.CATEGORY_SALE){

                    setFavourite(postDetailViewModel.getPost[0].favStatus != "1")
                    favViewModel.changeFavStatus(
                        mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: "",
                        if (navArgs.categoryType == Constants.CATEGORY_SERVICE)
                            postDetailViewModel.getServicePost[0].id
                        else
                            postDetailViewModel.getPost[0].id
                    )

                }else{
                    setFavourite(postDetailViewModel.getServicePost[0].favStatus != "1")

                    favViewModel.changeFavStatusService(
                        mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: "",
                        if (navArgs.categoryType == Constants.CATEGORY_SERVICE)
                            postDetailViewModel.getServicePost[0].id
                        else
                            postDetailViewModel.getPost[0].id
                    )

                }




            }


        }

        binding.pagerProductImage.registerOnPageChangeCallback(object : MyPageChangeCallback(){

            override fun onPageSelected(position: Int) {
                postDetailImageAdapter?.setSelected(position)
            }
        })

        binding.pagerProductImage.setOnClickListener {
            Intent(requireContext(), FullImageActivity::class.java).apply {
                putExtra("images",mPostImages)
                startActivity(this)
            }
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

    companion object {

        @JvmStatic
        fun newInstance() = PostDetailBidFragment()

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
        binding.pagerProductImage.currentItem = position
    }

    override fun onImageClick(position: Int) {
        Intent(requireContext(), FullImageActivity::class.java).apply {
            putExtra("images", mPostImages)
            putExtra("position",position)
            startActivity(this)
        }
    }

    override fun onStop() {
        super.onStop()
        if (timer != null){
            timer?.cancel()
            timer = null
        }
    }


}
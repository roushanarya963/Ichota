package com.ichota.publicProfile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.app.ActivityCompat.recreate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.ichota.NavGraphDirections
import com.ichota.activities.FullImageActivity
import com.ichota.R
import com.ichota.adapter.ProfilePagerAdapter
import com.ichota.databinding.FragmentPublicProfileBinding
import com.ichota.dialogs.UserReportDialogFragment
import com.ichota.fragments.UserReviewFragment
import com.ichota.interfaces.IMainActivity
import com.ichota.model.HomeTabs
import com.ichota.model.User
import com.ichota.profile.UserMarketplaceFragment
import com.ichota.utils.Global
import com.ichota.viewModel.MyNetworkViewModel
import com.ichota.viewModel.ProfileViewModel

private const val TAG = "PublicProfileFragment"

class PublicProfileFragment : Fragment() {
    private lateinit var binding: FragmentPublicProfileBinding
    private var profilePagerAdapter: ProfilePagerAdapter? = null
    private val profileViewModel: ProfileViewModel by viewModels()
    private val myNetworkViewModel: MyNetworkViewModel by viewModels()
    private val navArgs : PublicProfileFragmentArgs by navArgs()
    private var mFollowerStatus: Int = 0
    private var strcount: Int=0
    private var mUser: User? = null

    private var mIMainActivity: IMainActivity? = null
    private var profileImg: String = ""
    private var str: String = ""
    private var mShareLink: String? = null
    private var ft : FragmentTransaction ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUser = mIMainActivity?.getPreference()?.getCurrentUser()
       // mUser =  navArgs.userId
        ft= childFragmentManager.beginTransaction()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPublicProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mShareLink = "www.ichota.com/userprofile/${navArgs.userId}"
        profileViewModel.getUserProfile(navArgs.userId)
        setupObserver()
        setupListeners()
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
    private fun setPaymentMethodsTitle(title:String, view: TextView, status:String?){
        view.text = title
        if (status == "0") {
            view.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorTextPrimary))
        } else {
            view.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        }
    }


    private fun setupObserver() {

        profileViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showProgress(it)
        }

        profileViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showMessage(it)
        }

        profileViewModel.getUserObserver.observe(viewLifecycleOwner) {
            setupData(it[0])
        }

        myNetworkViewModel.getToggleFollowUnfollowObserver.observe(viewLifecycleOwner){
            myNetworkViewModel.checkFollowerStatus(
                mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: "", navArgs.userId
            )
        }

        myNetworkViewModel.getCheckFollowerStatusObserver.observe(viewLifecycleOwner) {
            if (it.followerStatus[0].status
                == "1") {
                mFollowerStatus = 1
                binding.pbProfileFollowerbtn.text = getString(R.string.following)
                binding.tvFollowingCount.text= (strcount+1).toString()
            } else {
                mFollowerStatus = 0
                binding.pbProfileFollowerbtn.text = getString(R.string.follow)
                binding.tvFollowingCount.text= (strcount).toString()
            }
        }


    }

    private fun setupData(user: User) {

        user.userId?.let {

            binding.cbEmailVerified.isChecked=user.emailStatus=="1"
            binding.cbFbVerified.isChecked=user.facebookStatus=="1"
            binding.cbPhoneVerified.isChecked=user.phoneNumStatus=="1"

            binding.cbPaymentApplePay.isChecked=user.googlePayStatus=="1"
            binding.cbPaymentCashapp.isChecked=user.cashAppStatus=="1"
            binding.cbPaymentVenmo.isChecked=user.venmoStatus=="1"
            binding.cbPaymentPaypal.isChecked=user.paypalStatus=="1"


            setVerifyStatus(binding.tvMailNotVerified, user.emailStatus)
            setVerifyStatus(binding.tvPhoneVerified, user.phoneNumStatus)
            setVerifyStatus(binding.tvFacebookVerified, user.facebookStatus)

            setPaymentMethodsTitle(getString(R.string.venmo),binding.tvVenmo,user.venmoStatus)
            setPaymentMethodsTitle(getString(R.string.paypal),binding.tvPaypal,user.paypalStatus)
            setPaymentMethodsTitle(getString(R.string.cashapp),binding.tvCashapp,user.cashAppStatus)
            setPaymentMethodsTitle(getString(R.string.applePay),binding.tvApplePay,user.applePayStatus)

        }



        if (user.userId == mIMainActivity?.getPreference()?.getCurrentUser()?.userId){
         //   binding.btFollow.visibility = View.GONE
        //    binding.btReport.visibility = View.GONE
        }

        myNetworkViewModel.checkFollowerStatus(
            mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: "",
            user.userId
        )

        profileImg = user.userImage

        Glide.with(requireActivity())
            .load(Global.getImageUrl(user.userImage))
            .placeholder(R.drawable.img_user_placeholder)
            .error(R.drawable.img_user_placeholder)
            .into(binding.ivUser)

        binding.tvUserName.text = user.name

        val bio = Global.formatItemListingDate(user.createDtm)


        /* - ${
            Global.getCompleteAddress(
                requireContext(),
                user.latitude ?: "0",
                user.longitude ?: "0"
            )
        }"*/

         strcount =user.followingCount.toInt()
         binding.tvJoinedDate.text = bio

     //   binding.tvFollowingCount.text=user.followingCount

       /* if(mFollowerStatus==1){
            binding.tvFollowingCount.text= (strcount+1).toString()
        }else if(mFollowerStatus==0){
            binding.tvFollowingCount.text=strcount.toString()
        }*/


        binding.tvFollowerCount.text=user.followerCount
        binding.tvFollowingCount.setOnClickListener {
            setMyNetworkPagerFragment()
        }

        binding.tvFollowerCount.setOnClickListener {

            setMyNetworkPagerFragment()
        }

        binding.tvFollowingHeader.setOnClickListener {
            setMyNetworkPagerFragment()
        }

        binding.tvFollowerHeader.setOnClickListener {
            setMyNetworkPagerFragment()
        }

        try {
            binding.ratingBar.rating = user.rating?.toFloat() ?: 1F
        } catch (e: NumberFormatException) {
          //  binding.ratingBar.rating = 1F
        }

        if (user.rating.isNullOrEmpty()) {
        //    binding.tvRating.text = getString(R.string.stars, "1")
        } else {
         //   binding.tvRating.text = getString(R.string.stars, user.rating)
        }


        val tabs = arrayOf(

           /* HomeTabs(PublicProfileSubFragment.newInstance(user), ""),
            HomeTabs(PublicProfileMarketplaceFragment.newInstance(user.userId), "")*/

            HomeTabs(UserMarketplaceFragment.newInstance(user?.userId ?: ""), ""),
            HomeTabs(UserReviewFragment.newInstance(user?.userId ?: ""),"")
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




    }



    private fun setupListeners() {

        binding.ivBack.setOnClickListener { it.findNavController().popBackStack() }

        binding.ivMore.setOnClickListener {

           /* UserReportDialogFragment.newInstance(
                mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: "",
                 navArgs.userId
            ).show(childFragmentManager, TAG)
*/
            val popupmenu:PopupMenu = PopupMenu(requireContext(),binding.ivMore)
            popupmenu.menuInflater.inflate(R.menu.menu_report_user,popupmenu.menu)

            popupmenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                    item ->
                when(item.itemId) {
                    R.id.menu_shareprofile ->

                          Global.shareLink(requireContext(),mShareLink)

                        R.id.menu_report_user->

                            UserReportDialogFragment.newInstance(
                                mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: "",
                                navArgs.userId
                            ).show(childFragmentManager, TAG)

                        R.id.menu_cancel->

                           popupmenu.dismiss()
                }

              true
                })
            popupmenu.show()
        }





        binding.pbProfileFollowerbtn.setOnClickListener {
            myNetworkViewModel.toggleFollowUnfollow(mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: "", navArgs.userId)
        }

       /* binding.fabShare.setOnClickListener {
            if (
                navArgs.userId.isEmpty() || mShareLink.isNullOrEmpty()
            ) return@setOnClickListener
            Global.shareLink(requireContext(),mShareLink!!)
        }*/


        binding.ivUser.setOnClickListener {
            Log.d(TAG, "setupListeners: profile img click")
            if (profileImg.isEmpty()) return@setOnClickListener
            val images = ArrayList<String>()
            images.add(profileImg)
            Intent(requireContext(), FullImageActivity::class.java).apply {
                putExtra("images", images)
                startActivity(this)
            }
        }

    }

    private fun setMyNetworkPagerFragment() {

        val direction = NavGraphDirections.actionGlobalNavMyNetworkPagerFragment(
            navArgs.userId
        )
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



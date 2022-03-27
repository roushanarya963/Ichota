package com.ichota.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.ichota.NavGraphDirections
import com.ichota.R
import com.ichota.adapter.SalePostsAdapter
import com.ichota.adapter.ServicePostsAdapter
import com.ichota.adapter.UserMarketplaceAdapter
import com.ichota.adapterClasses.MyTabSelectorListener
import com.ichota.databinding.FragmentUserMarketplaceBinding
import com.ichota.model.MarketPlacePost
import com.ichota.model.SalePost
import com.ichota.model.ServicePost
import com.ichota.preferences.PreferenceHelper
import com.ichota.publicProfile.PublicProfileFragmentDirections
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.viewModel.ProfileViewModel
import kotlin.math.log
import kotlin.math.tan


private const val TAG ="UserMarketplaceFragment"
class UserMarketplaceFragment : Fragment(), SalePostsAdapter.SaleItemClickInterface,
    ServicePostsAdapter.ServiceItemClickInterface {
    private lateinit var binding: FragmentUserMarketplaceBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var helper: PreferenceHelper
    private var mSalePostAdapter: SalePostsAdapter? = null
    private var mServicePostAdapter: ServicePostsAdapter? = null
    private var mUserId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            mUserId = it.getString(Constants.KEY_USER_ID)
        }

        helper = PreferenceHelper.getPreferences(requireContext())
        mSalePostAdapter = SalePostsAdapter()
        mServicePostAdapter = ServicePostsAdapter()
        mSalePostAdapter?.setOnSaleItemClickListener(this)
        mServicePostAdapter?.setOnServiceItemClickListener(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentUserMarketplaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObserver()

        binding.rvUserMarketplace.adapter = mSalePostAdapter
        setupTabChangeListener()
        profileViewModel.getSalePost(mUserId?: "")
        profileViewModel.getServicePost(mUserId ?: "")
    }

    private fun setupTabChangeListener() {
        binding.tabLayoutMarketplace.addOnTabSelectedListener(object : MyTabSelectorListener() {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                super.onTabSelected(tab)
                Log.d(TAG, "onTabSelected: position = ${tab?.position}")

                if (tab?.position == 1) {
                    binding.rvUserMarketplace.adapter = mServicePostAdapter
                    mServicePostAdapter?.let {
                        binding.emptyFile.root.visibility = if(it.itemCount >0) View.GONE else View.VISIBLE
                    }
                } else {
                    binding.rvUserMarketplace.adapter = mSalePostAdapter
                    Log.d(TAG, "onTabSelected: SalePostAdapter = ${mSalePostAdapter?.itemCount}")
                    mSalePostAdapter?.let {
                        binding.emptyFile.root.visibility = if(it.itemCount >0) View.GONE else View.VISIBLE
                    }
                }
            }
        })
    }

    private fun setupObserver() {

        profileViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        profileViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            Global.showMessage(binding.root, it)
        }

        profileViewModel.getSalePostsObserver.observe(viewLifecycleOwner) {
            mSalePostAdapter?.setData(it)
        }

        profileViewModel.getServicePostsObserver.observe(viewLifecycleOwner) {
            mServicePostAdapter?.setData(it)
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(userId: String) =
            UserMarketplaceFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.KEY_USER_ID, userId)
                }
            }
    }

    override fun onServiceItemClick(post: ServicePost) {
        val direction =
            NavGraphDirections.actionGlobalNavPostDetailFragment(
                post.serviceCategory.toString(),
                post.id,
                post.userId
            )

        findNavController().navigate(direction)
    }

    override fun onSalePostClick(position: Int) {
        val salePost = mSalePostAdapter?.getItemAt(position)
        salePost?.let { post ->
            if (post.buyingOptions.equals("BID", true))
            {
                val direction =
                    NavGraphDirections.actionGlobalNavPostDetailBidFragment(
                        post.postType,
                        post.id,
                        post.userId
                    )

                findNavController().navigate(direction)
            }
            else {

                val direction =
                    NavGraphDirections.actionGlobalNavPostDetailFragment(
                        post.postType,
                        post.id,
                        post.userId
                    )

                findNavController().navigate(direction)

            }

        }

    }

    override fun onSalePostSize(size: Int) {

    }

    /* override fun onPostClick(post: MarketPlacePost) {


     }*/



}
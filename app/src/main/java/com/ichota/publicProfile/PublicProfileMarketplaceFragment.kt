package com.ichota.publicProfile

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ichota.NavGraphDirections
import com.ichota.R
import com.ichota.adapter.PublicProfileMarketplaceAdapter
import com.ichota.databinding.FragmentPublicProfileMarketplaceBinding
import com.ichota.model.MarketPlacePost
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.viewModel.ProfileViewModel


private const val TAG = "PublicProfileMarketplaceFragment"

class PublicProfileMarketplaceFragment : Fragment(),
    PublicProfileMarketplaceAdapter.MarketplaceItemClickInterface {
    private lateinit var binding: FragmentPublicProfileMarketplaceBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var helper: PreferenceHelper
    private var mMarketPlaceAdapter: PublicProfileMarketplaceAdapter? = null
    private var mUserId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            arguments?.let {
                profileViewModel.getMarketPlacePosts(
                    it.getString(Constants.KEY_USER_ID) ?: ""
                )
            }
        }, 500)



        helper = PreferenceHelper.getPreferences(requireContext())


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPublicProfileMarketplaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupObserver()
    }


    private fun setupObserver() {
        profileViewModel.getMarketPlaceObserver.observe(viewLifecycleOwner) {



                binding.emptyFile.root.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE

            mMarketPlaceAdapter = PublicProfileMarketplaceAdapter(it, this)
            binding.rvUserMarketplace.adapter = mMarketPlaceAdapter

        }

        profileViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        profileViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            Global.showMessage(binding.root, it)
        }
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(userId: String) =
            PublicProfileMarketplaceFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.KEY_USER_ID, userId)
                }
            }
    }

    override fun onPostClick(post: MarketPlacePost) {


        if (post.buyingOptions.equals("BID", true)) {
            val direction =
                NavGraphDirections.actionGlobalNavPostDetailBidFragment(
                    post.postType,
                    post.id,
                    post.userId
                )

            findNavController().navigate(direction)
        } else {

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
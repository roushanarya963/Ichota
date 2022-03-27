package com.ichota.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ichota.NavGraphDirections
import com.ichota.R
import com.ichota.adapter.FavouritesAdapter
import com.ichota.adapter.ServicePostsAdapter
import com.ichota.databinding.FragmentFavouriteBinding
import com.ichota.model.FavouriteItem
import com.ichota.preferences.PrefKeys
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.viewModel.FavouriteViewModel
import com.ichota.viewModel.PostDetailViewModel
import java.util.*

private const val TAG = "FavouriteFragment"

class FavouriteFragment : Fragment(), FavouritesAdapter.FavItemClickListener {
    private lateinit var binding: FragmentFavouriteBinding
    private val favouriteViewModel: FavouriteViewModel by viewModels()
    private val postDetailViewModel: PostDetailViewModel by viewModels()
    private val navArgs: PostDetailBidFragmentArgs by navArgs()
    private var mProgressDialog: Dialog? = null
    private var favouriteAdapter: FavouritesAdapter? = null
    private var helper: PreferenceHelper? = null
    private var mClickedPosition = -1
    private var mCallback: ServicePostsAdapter.ServiceItemClickInterface? = null


    companion object {
        @JvmStatic
        fun newInstance() =
            FavouriteFragment()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mProgressDialog = Global.getProgressDialogFullScreen(requireContext())
        if (mProgressDialog?.isShowing == false) mProgressDialog?.show()
        favouriteAdapter = FavouritesAdapter()
        favouriteAdapter?.onFavItemClickListener(this)
        helper = PreferenceHelper.getPreferences(requireContext())
        favouriteViewModel.getFavouritePosts(helper?.getCurrentUser()?.userId ?: "")
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvFavourites.adapter = favouriteAdapter
        setupObserver()
        setupListener()
    }

    private fun setupObserver() {

        favouriteViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            Global.showMessage(binding.root, it)
        }

        favouriteViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            if (it) mProgressDialog?.show() else mProgressDialog?.dismiss()
        }

        favouriteViewModel.getFavouritesObserver.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {

             //  binding.emptyFile.root.visibility = View.VISIBLE
                 binding.lottieanimationEmptyFileAnim.visibility=View.VISIBLE
                return@observe

            }
            favouriteAdapter?.setData(it)
        }


        favouriteViewModel.getFavouriteStatusObserver.observe(viewLifecycleOwner) {
            if (!it) {
                favouriteAdapter?.removeItem(mClickedPosition)
            }
        }
    }

    private fun setupListener() {
        binding.fabBack.setOnClickListener {
            it.findNavController().popBackStack()
        }
    }

    override fun onFavClick(position: Int, postId: String) {
        mClickedPosition = position
        favouriteViewModel.changeFavStatus(helper?.getCurrentUser()?.userId ?: "", postId)

    }

    override fun onFavServiceClick(position: Int, postId: String) {
        mClickedPosition = position

        favouriteViewModel.changeFavStatusService(
            helper?.getCurrentUser()?.userId ?: "",
            postId
        )

       /* favouriteViewModel.changeFavStatusService(
            helper?.getStringValue(PrefKeys.KEY_POSTED_USER_ID).toString(),
            postId
        )*/

    }

    override fun onFavoriteSalePostClick(position: Int,postType: String, id: String, userId: String) {

            val direction = NavGraphDirections.actionGlobalNavPostDetailFragment(postType, id , userId)
            findNavController().navigate(direction)
    }

}
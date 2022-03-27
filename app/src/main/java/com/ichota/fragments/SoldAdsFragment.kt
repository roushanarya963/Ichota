package com.ichota.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import com.ichota.NavGraphDirections
import com.ichota.adapter.SoldAdsAdapter
import com.ichota.databinding.FragmentSoldAdsBinding
import com.ichota.interfaces.IMainActivity
import com.ichota.model.SoldPost
import com.ichota.viewModel.MyListingViewModel


private val TAG = "SoldAdsFragment"

class SoldAdsFragment : Fragment(), SoldAdsAdapter.SoldAdsClickInterface {
    private lateinit var binding: FragmentSoldAdsBinding
    private val myListingViewModel: MyListingViewModel by viewModels()
    private var mIMainActivity: IMainActivity? = null
    private var mSoldAdsAdapter: SoldAdsAdapter? = null
    private var mClickedPosition: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSoldAdsAdapter = SoldAdsAdapter()
        mSoldAdsAdapter?.onSoldItemClickListener(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSoldAdsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvSoldAds.itemAnimator = DefaultItemAnimator()


        setupObserver()
    }

    override fun onResume() {
        super.onResume()

        myListingViewModel.getSoldAds(
            mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: ""
        )

    }

    private fun setupObserver() {
        myListingViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showProgress(it)
        }

        myListingViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showMessage(it)
        }

        myListingViewModel.getMarkAsDeletedObserver.observe(viewLifecycleOwner) {
            mSoldAdsAdapter?.removeItem(mClickedPosition)
        }
        myListingViewModel.getSoldAdsObserver.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.emptyFile.root.visibility = View.VISIBLE
                binding.emptyFile.btContinue.visibility=View.GONE
                return@observe
            }
            binding.emptyFile.root.visibility = View.GONE
            binding.rvSoldAds.adapter = mSoldAdsAdapter
            mSoldAdsAdapter?.setData(it)
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIMainActivity = context as IMainActivity
    }

    override fun onDetach() {
        super.onDetach()
        mIMainActivity = null
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            SoldAdsFragment()

    }

    override fun onDeleteMenuClick(postId: String, position: Int) {
        mClickedPosition = position
        myListingViewModel.markAsDeleted(
            postId,
            mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: ""
        )

    }

    override fun onItemClick(post: SoldPost) {
        if (post.buyingOptions.equals("Bid",true)){
            val direction = NavGraphDirections.actionGlobalNavPostDetailBidFragment(
                post.postType,post.postId,post.userId
            )
            findNavController().navigate(direction)
        }
        else{
            val direction = NavGraphDirections.actionGlobalNavPostDetailFragment(
                post.postType,post.postId,post.userId
            )
            findNavController().navigate(direction)
        }
    }


}
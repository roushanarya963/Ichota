package com.ichota.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import com.ichota.NavGraphDirections
import com.ichota.adapter.ActiveAdsAdapter
import com.ichota.databinding.FragmentActiveAdsBinding
import com.ichota.interfaces.IMainActivity
import com.ichota.model.ActivePost
import com.ichota.viewModel.MyListingViewModel


private const val TAG = "ActiveAdsFragment"

class ActiveAdsFragment : Fragment(), ActiveAdsAdapter.MenuClickInterface {
    private lateinit var binding: FragmentActiveAdsBinding
    private val myListingViewModel: MyListingViewModel by viewModels()
    private var mActiveAdsAdapter: ActiveAdsAdapter? = null
    private var mIMainActivity: IMainActivity? = null
    private var mClickedPosition : Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActiveAdsAdapter = ActiveAdsAdapter()
        mActiveAdsAdapter?.onMenuClickListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentActiveAdsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvActiveAds.itemAnimator = DefaultItemAnimator()

        Handler(Looper.getMainLooper()).postDelayed({
            myListingViewModel.getActiveAds(
                mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: ""
            )
        }, 500)
        setupObserver()
    }

    private fun setupObserver() {
        myListingViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showProgress(it)
        }
        myListingViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showMessage(it)
        }
        myListingViewModel.getActiveAdsObserver.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.emptyFile.root.visibility = View.VISIBLE
                binding.emptyFile.btContinue.visibility=View.GONE
                return@observe
            }
            binding.emptyFile.root.visibility = View.GONE
            binding.rvActiveAds.adapter = mActiveAdsAdapter
            mActiveAdsAdapter?.setData(it)
        }

        myListingViewModel.getMarkAsDeletedObserver.observe(viewLifecycleOwner) {
            mActiveAdsAdapter?.removeItem(mClickedPosition)
        }

        myListingViewModel.getMarkAsSoldObserver.observe(viewLifecycleOwner){
            mActiveAdsAdapter?.removeItem(mClickedPosition)
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
        fun newInstance() = ActiveAdsFragment()
    }


    override fun onMarkAsSold(position: Int,postId: String) {
        mClickedPosition = position
        myListingViewModel.markAsSold(
            postId,
            mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: ""
        )

    }

    override fun onItemDelete(position:Int,postId: String) {
        mClickedPosition = position
        myListingViewModel.markAsDeleted(
            postId,
            mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: ""
        )
    }

    override fun onItemClick(post: ActivePost) {
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
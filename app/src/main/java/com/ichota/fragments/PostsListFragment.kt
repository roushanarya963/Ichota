package com.ichota.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ichota.NavGraphDirections
import com.ichota.adapter.SalePostsAdapter
import com.ichota.adapter.ServicePostsAdapter
import com.ichota.databinding.FragmentPostsListBinding
import com.ichota.dialogs.AddPostOptionsDialogFragment
import com.ichota.dialogs.FilterDialogFragment
import com.ichota.dialogs.LocationDialogFragment
import com.ichota.dialogs.SortDialogFragment
import com.ichota.interfaces.IMainActivity
import com.ichota.model.SalePost
import com.ichota.model.ServicePost
import com.ichota.preferences.PrefKeys
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.utils.Sort
import com.ichota.viewModel.SaleViewModel
import com.ichota.viewModel.ServiceViewModel
import kotlin.math.log


private const val TAG = "PostsListFragment"

class PostsListFragment : Fragment(), SortDialogFragment.SortOptionClickListener,
    SalePostsAdapter.SaleItemClickInterface, ServicePostsAdapter.ServiceItemClickInterface,
    FilterDialogFragment.ApplyFilterInterface {

    private lateinit var binding: FragmentPostsListBinding
    private val navArgs: PostsListFragmentArgs by navArgs()
    private val saleViewModel: SaleViewModel by viewModels()
    private val serviceViewModel: ServiceViewModel by viewModels()
    private var mSalePostsAdapter: SalePostsAdapter? = null
    private var mServicePostsAdapter: ServicePostsAdapter? = null
    private var mCategoryType: String? = null
    private var mCategoryId: String? = null
    private var mIMainActivity: IMainActivity? = null
    private var helper:PreferenceHelper?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mSalePostsAdapter = SalePostsAdapter()
        mServicePostsAdapter = ServicePostsAdapter()
        mServicePostsAdapter?.setOnServiceItemClickListener(this)
        mSalePostsAdapter?.setOnSaleItemClickListener(this)

        helper=PreferenceHelper.getPreferences(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mCategoryType = navArgs.categoryType
        mCategoryId = navArgs.categoryId
        when {
            mCategoryType.equals(Constants.CATEGORY_SALE) -> saleViewModel.getSalePosts(
                mCategoryId ?: "0"
            )
            mCategoryType.equals(Constants.CATEGORY_SERVICE) -> serviceViewModel.getServicePosts(
                mCategoryId ?: "0"
            )
        }
        Log.d(TAG, "onCreate: Cat _id $mCategoryId and Cat_type = $mCategoryType")

        setupSalePostsObserver()
        setupServicePostsObserver()
        setupListeners()
    }


    private fun setupSalePostsObserver() {
        saleViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            Global.showMessage(binding.root, it)
        }
        saleViewModel.getProgressObserver.observe(viewLifecycleOwner) {

            if (it) binding.progressBar.root.visibility = View.VISIBLE else
                binding.progressBar.root.visibility = View.GONE
        }
        saleViewModel.getSalePostObserver.observe(viewLifecycleOwner) {
            
            binding.emptyFile.root.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            binding.constraintLayoutFilterSort.visibility= if(it.isEmpty()) View.GONE else View.VISIBLE
            binding.rvPosts.adapter = mSalePostsAdapter
            mSalePostsAdapter?.setData(it)

        }

    }

    private fun setupServicePostsObserver() {
        serviceViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            Global.showMessage(binding.root, it)
        }
        serviceViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            if (it) binding.progressBar.root.visibility = View.VISIBLE else
                binding.progressBar.root.visibility = View.GONE
        }
        serviceViewModel.getServicePostsObserver.observe(viewLifecycleOwner) {
            Log.d(TAG, "setupServicePostsObserver: ${it.isEmpty()}")
            binding.emptyFile.root.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            binding.constraintLayoutFilterSort.visibility= if(it.isEmpty()) View.GONE else View.VISIBLE
            binding.rvPosts.adapter = mServicePostsAdapter
            mServicePostsAdapter?.setData(it)

        }


    }

    private fun setupListeners() {

        binding.emptyFile.btContinue.setOnClickListener {
            AddPostOptionsDialogFragment.newInstance().show(childFragmentManager, TAG)
        }
        binding.ivBack.setOnClickListener {
            it.findNavController().popBackStack()
        }
        binding.btSort.setOnClickListener {
            val sortDialogFragment = SortDialogFragment.newInstance()
            sortDialogFragment.setListener(this)
            sortDialogFragment.show(requireActivity().supportFragmentManager, TAG)
        }
        binding.btFilter.setOnClickListener {
            FilterDialogFragment.newInstance(this)
                .show(requireActivity().supportFragmentManager, TAG)
        }
        binding.ivLocation.setOnClickListener {
            LocationDialogFragment.newInstance().show(requireActivity().supportFragmentManager, TAG)
        }

        binding.etSearch.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                val directions=  NavGraphDirections.actionGlobalNavProductSearchFragment(
                    binding.etSearch.text.toString().trim()
                )
                findNavController().navigate(directions)
            }
            true
        }

    }

    override fun onSortOptionClick(sort: String) {
        var newest: String = ""
        var lowToHigh: String = ""
        var highToLow: String = ""
        var lat: String = ""
        var lon: String = ""
        when (sort) {
            Sort.NEWEST -> newest = "1"
            Sort.CLOSEST -> {
                lat = mIMainActivity?.getPreference()?.getStringValue(PrefKeys.KEY_LATITUDE) ?: ""
                lon = mIMainActivity?.getPreference()?.getStringValue(PrefKeys.KEY_LONGITUDE) ?: ""

            }
            Sort.LOW_TO_HIGH -> lowToHigh = "1"
            Sort.HIGH_TO_LOW -> highToLow = "1"
        }

        if (navArgs.categoryType == Constants.CATEGORY_SALE) {
            saleViewModel.searchSalePostWithFilter(
                "",
                "",
                "",
                "",
                newest,
                highToLow,
                lowToHigh,
                lat,
                lon,
                navArgs.categoryId,
                ""
            )
        } else {
            serviceViewModel.searchServicePostWithFilter(
                "",
                "",
                "",
                "",
                "",
                newest,
                highToLow,
                lowToHigh,
                lat,
                lon,
                navArgs.categoryId
            )

        }

    }




    override fun onServiceItemClick(post: ServicePost) {

        val directions =
            NavGraphDirections.actionGlobalNavPostDetailFragment(
                Constants.CATEGORY_SERVICE,
                post.id,
                post.userId,
                mCategoryId.toString()
            )

        findNavController()
            .navigate(directions)
    }

    override fun onApplyFilterClick(
        minPrice: String,
        maxPrice: String,
        buyingOption: String,
        condition: String,
        distance: String
    ) {
        if (navArgs.categoryType == Constants.CATEGORY_SALE) {
            saleViewModel.searchSalePostWithFilter(
                minPrice,
                maxPrice,
                buyingOption,
                condition,
                "",
                "",
                "",
                "",
                "",
                navArgs.categoryId,
                distance,
            )
        } else {
            serviceViewModel.searchServicePostWithFilter(
                minPrice,
                maxPrice,
                buyingOption,
                condition,
                distance,
                "",
                "",
                "",
                "",
                "",
                navArgs.categoryId
            )
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

    override fun onSalePostClick(position: Int) {


        val salePost = mSalePostsAdapter?.getItemAt(position)
        salePost?.let { post->
            if (post.buyingOptions.equals("Bid", true)) {
                val direction =
                    NavGraphDirections.actionGlobalNavPostDetailBidFragment(
                        Constants.CATEGORY_SALE, post.id, post.userId
                    )
                findNavController().navigate(direction)
            } else {
                val direction =
                    NavGraphDirections.actionGlobalNavPostDetailFragment(
                        Constants.CATEGORY_SALE, post.id ?: "", post.userId
                    )
                findNavController().navigate(direction)
            }

        }

    }

    override fun onSalePostSize(size: Int) {
        TODO("Not yet implemented")
    }

}
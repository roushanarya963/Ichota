package com.ichota.fragments

import android.annotation.SuppressLint
import android.app.Dialog
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
import com.ichota.R
import com.ichota.R.string
import com.ichota.adapter.ISearchItemClick
import com.ichota.adapter.SalePostsAdapter
import com.ichota.adapter.SearchPostAdapter
import com.ichota.databinding.FragmentProductSearchBinding
import com.ichota.dialogs.AddPostOptionsDialogFragment
import com.ichota.dialogs.FilterDialogFragment
import com.ichota.dialogs.LocationDialogFragment
import com.ichota.dialogs.SortDialogFragment
import com.ichota.model.SalePost
import com.ichota.model.SearchPost
import com.ichota.preferences.PrefKeys
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.viewModel.SearchViewModel
import kotlin.math.log

private const val TAG = "SearchFragment"

class SearchFragment : Fragment(), SortDialogFragment.SortOptionClickListener,
     ISearchItemClick {
    private lateinit var binding: FragmentProductSearchBinding

    private val navArgs: SearchFragmentArgs by navArgs()
    private val searchViewModel: SearchViewModel by viewModels()
    private var mSalePostAdapter: SearchPostAdapter? = null
    private var mProgressDialog: Dialog? = null
    internal lateinit var helper: PreferenceHelper
    var latitude : String ? =  null
    var longitude:String ? = null
    var distance:Float ? =  null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mProgressDialog = Global.getProgressDialogFullScreen(requireContext())
        helper = PreferenceHelper.getPreferences(requireContext())
        latitude = helper.getStringValue(PrefKeys.KEY_LATITUDE)
        longitude = helper.getStringValue(PrefKeys.KEY_LONGITUDE)
        distance = helper.getInt(PrefKeys.KEY_SEARCH_DISTANCE, 5).toFloat()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProductSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        distance?.let { latitude?.let { it1 -> searchViewModel.getSearch(it1,
            longitude!!, it,navArgs.searchKeyword) } }

        binding.etSearch.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                latitude?.let { longitude?.let { it1 ->
                    distance?.let { it2 ->
                        searchViewModel.getSearch(it,
                            it1, it2,binding.etSearch.text.toString().trim())
                    }
                } }
            }
            true
        }
        setupListeners()
        setupObserver()

    }

    @SuppressLint("ResourceType")
    private fun setupObserver() {
        searchViewModel.getMessageObserver.observe(viewLifecycleOwner) {
         //   Global.showMessage(binding.root, it)
           if(it==getString(R.string.messageCheckInternet)){
               binding.emptyFileSearchInternet.root.visibility=View.VISIBLE
               binding.emptyFile.root.visibility=View.GONE
           }
        }
        searchViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            if (it) mProgressDialog?.show() else mProgressDialog?.dismiss()

        }
        searchViewModel.getSearchObserver.observe(viewLifecycleOwner) {


            binding.emptyFile.btContinue.setOnClickListener {
                AddPostOptionsDialogFragment.newInstance().show(childFragmentManager, TAG)
            }
            binding.emptyFileSearchInternet.root.visibility=View.GONE
            binding.emptyFile.root.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            mSalePostAdapter = SearchPostAdapter(it,requireContext(),this)
            binding.rvSearch.adapter = mSalePostAdapter

        }
    }

    private fun setupListeners() {
        binding.btBack.setOnClickListener { requireActivity().onBackPressed() }
        binding.btSort.setOnClickListener {
            val ee: SortDialogFragment = SortDialogFragment.newInstance()
            ee.setListener(this)
            ee.show(requireActivity().supportFragmentManager, TAG)
        }
        binding.btFilter.setOnClickListener {
           // FilterDialogFragment.newInstance(this).show(requireActivity().supportFragmentManager, TAG)
        }
        binding.ivLocation.setOnClickListener {
            LocationDialogFragment.newInstance().show(requireActivity().supportFragmentManager, TAG)
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() = SearchFragment()

    }

    override fun onSortOptionClick(sort: String) {
        Log.d(TAG, "onSortOptionClick: ")
        Toast.makeText(requireContext(), sort, Toast.LENGTH_SHORT).show()
    }



    override fun onItemClick(searchPost: SearchPost) {
        if (searchPost.buyingOptions.equals("Bid",true)) {
            val direction =
                NavGraphDirections.actionGlobalNavPostDetailBidFragment(
                    searchPost.postType, searchPost.postId ?: "", searchPost.userId
                )
            findNavController().navigate(direction)
        } else {

            val direction =
                SearchFragmentDirections.actionGlobalNavPostDetailFragment(
                    searchPost.postType, searchPost.postId, searchPost.userId
                )
            findNavController().navigate(direction)
        }

    }
}
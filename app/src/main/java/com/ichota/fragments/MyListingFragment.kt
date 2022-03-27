package com.ichota.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.ichota.R
import com.ichota.adapter.MyListingsPagerAdapter
import com.ichota.databinding.FragmentMyListingBinding
import com.ichota.model.HomeTabs


private const val TAG = "MyListingFragment"

class MyListingFragment : Fragment() {
    private lateinit var binding: FragmentMyListingBinding

    private var mMyListingPagerAdapter: MyListingsPagerAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tabs = arrayOf(

            HomeTabs(
                ActiveAdsFragment.newInstance(),
                getString(R.string.active)
            ),
            HomeTabs(
                SoldAdsFragment.newInstance(),
                getString(R.string.sold)
            )

        )

        mMyListingPagerAdapter = MyListingsPagerAdapter(childFragmentManager, tabs)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyListingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tabMyListings.setupWithViewPager(binding.pagerMyListings)
        binding.pagerMyListings.adapter = mMyListingPagerAdapter
        setupListeners()

    }

    private fun setupListeners() {
        binding.fabBack.setOnClickListener {
            it.findNavController().navigateUp()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyListingFragment()

    }
}
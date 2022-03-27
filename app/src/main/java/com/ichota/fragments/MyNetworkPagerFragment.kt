package com.ichota.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.ichota.R
import com.ichota.adapter.FollowersPagerAdapter
import com.ichota.databinding.FragmentMyNetworkPagerBinding
import com.ichota.model.HomeTabs

private const val TAG = "FollowersPagerFragment"


class MyNetworkPagerFragment : Fragment() {
    private lateinit var binding: FragmentMyNetworkPagerBinding
    private var mFollowersPagerAdapter: FollowersPagerAdapter? = null
    private val navArgs : MyNetworkPagerFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tabs = arrayOf(
            HomeTabs(FollowersFragment.newInstance(navArgs.userId), getString(R.string.followers)),
            HomeTabs(FollowingFragment.newInstance(navArgs.userId), getString(R.string.followings))
        )
        mFollowersPagerAdapter = FollowersPagerAdapter(childFragmentManager, tabs)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyNetworkPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tabLayoutFollowers.setupWithViewPager(binding.viewPagerFollowers)
        binding.viewPagerFollowers.adapter = mFollowersPagerAdapter
        setupListener()
    }

    private fun setupListener() {
        binding.ibBack.setOnClickListener { it.findNavController().navigateUp() }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MyNetworkPagerFragment()
    }
}
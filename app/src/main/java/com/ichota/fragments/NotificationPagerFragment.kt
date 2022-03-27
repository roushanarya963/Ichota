package com.ichota.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ichota.R
import com.ichota.adapter.NotificationPagerAdapter
import com.ichota.databinding.FragmentNotificationPagerBinding
import com.ichota.model.NotificationTabsModel


private const val TAG = "NotificationPagerFragment"

class NotificationPagerFragment : Fragment() {
    private lateinit var binding: FragmentNotificationPagerBinding

    private var notificationPagerAdapter: NotificationPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tabs = arrayOf(
            NotificationTabsModel(
                getString(R.string.notifications),
                NotificationFragment.newInstance()
            ),
            NotificationTabsModel(getString(R.string.favourites), FavouriteFragment.newInstance())
        )
        notificationPagerAdapter =
            NotificationPagerAdapter(tabs, childFragmentManager)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.notificationTab.setupWithViewPager(binding.notificationPager)
        binding.notificationPager.adapter = notificationPagerAdapter

    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = NotificationPagerFragment()

    }
}
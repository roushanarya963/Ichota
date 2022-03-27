package com.ichota.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ichota.model.HomeTabs

class FollowersPagerAdapter(fm: FragmentManager, tabs: Array<HomeTabs>) : FragmentPagerAdapter(
    fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    private val mTabs = tabs
    override fun getCount(): Int {
        return mTabs.size

    }

    override fun getItem(position: Int): Fragment {
        return mTabs[position].fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mTabs[position].title
    }
}
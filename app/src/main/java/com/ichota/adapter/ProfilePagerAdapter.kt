package com.ichota.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ichota.model.HomeTabs

class ProfilePagerAdapter(fragment: Fragment, val tabs: Array<HomeTabs>) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return tabs.size
    }

    override fun createFragment(position: Int): Fragment {
        return tabs[position].fragment
    }


}
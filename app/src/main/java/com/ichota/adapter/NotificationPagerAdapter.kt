package com.ichota.adapter

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ichota.model.NotificationTabsModel

class NotificationPagerAdapter(val fragments: Array<NotificationTabsModel>, fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {


    override fun getCount(): Int {
        Log.d("TAG", "getCount: ${fragments.size}")
        return fragments.size

    }

    override fun getItem(position: Int): Fragment {
        return fragments[position].fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragments[position].title
    }
}
package com.ichota.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ichota.fragments.SalesFragment
import com.ichota.fragments.ServicesFragment
import com.ichota.model.HomeTabs
import com.ichota.utils.Global

class SalesServicesPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {



    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {

        return if (position == 1){
            ServicesFragment.newInstance()
        } else{
            SalesFragment.newInstance()
        }

    }
}
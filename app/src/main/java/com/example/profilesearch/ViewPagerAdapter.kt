package com.example.profilesearch

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity, private val uname: String) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    private val username = uname

    override fun createFragment(position: Int): Fragment {
        val fragment: Fragment = FollowsFragment()
        fragment.arguments = Bundle().apply {
            putString(FollowsFragment.ARG_USERNAME, username)
            putInt(FollowsFragment.ARG_POSITION, position + 1)
        }
        return fragment
    }
}
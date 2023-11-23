package com.moonlightsplitter.toktok.adapter

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.moonlightsplitter.toktok.fragment.StoryViewFragment
import com.moonlightsplitter.toktok.model.VideoModel

class VideoPagerAdapter(
    fragment: FragmentActivity,
    private val storyList: MutableList<VideoModel> = mutableListOf()
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return storyList.size
    }

    override fun createFragment(position: Int): Fragment {
        return StoryViewFragment.newInstance(storyList[position])
    }
}
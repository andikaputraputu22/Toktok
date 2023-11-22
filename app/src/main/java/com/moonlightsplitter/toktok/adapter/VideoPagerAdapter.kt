package com.moonlightsplitter.toktok.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.moonlightsplitter.toktok.fragment.StoryViewFragment
import com.moonlightsplitter.toktok.model.VideoModel

class VideoPagerAdapter(
    fragment: Fragment,
    private val storyList: MutableList<VideoModel> = mutableListOf()
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return storyList.size
    }

    override fun createFragment(position: Int): Fragment {
        return StoryViewFragment.newInstance(storyList[position])
    }
}
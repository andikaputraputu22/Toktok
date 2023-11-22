package com.moonlightsplitter.toktok.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.moonlightsplitter.toktok.model.VideoModel
import com.moonlightsplitter.toktok.utils.Constants

class StoryViewFragment : Fragment() {

    companion object {
        fun newInstance(videoModel: VideoModel) = StoryViewFragment()
            .apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.KEY_STORY_DATA, videoModel)
                }
            }
    }
}
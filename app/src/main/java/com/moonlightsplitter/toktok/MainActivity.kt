package com.moonlightsplitter.toktok

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.moonlightsplitter.toktok.adapter.AdapterVideo
import com.moonlightsplitter.toktok.adapter.VideoPagerAdapter
import com.moonlightsplitter.toktok.databinding.ActivityMainBinding
import com.moonlightsplitter.toktok.model.VideoModel
import com.moonlightsplitter.toktok.utils.Constants
import com.moonlightsplitter.toktok.utils.PreCachingWork
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: AdapterVideo
    private lateinit var videoPagerAdapter: VideoPagerAdapter
    private val videos = arrayListOf(
        VideoModel("https://assets.mixkit.co/videos/preview/mixkit-eastern-egg-picnic-in-the-garden-48599-large.mp4"),
        VideoModel("https://assets.mixkit.co/videos/preview/mixkit-little-girl-eating-easter-egg-chocolate-in-the-garden-48603-large.mp4"),
        VideoModel("https://assets.mixkit.co/videos/preview/mixkit-little-girl-finds-an-easter-egg-in-the-garden-48600-large.mp4"),
        VideoModel("https://assets.mixkit.co/videos/preview/mixkit-portrait-of-a-woman-in-a-pool-1259-large.mp4"),
        VideoModel("https://assets.mixkit.co/videos/preview/mixkit-girl-in-neon-sign-1232-large.mp4"),
        VideoModel("https://assets.mixkit.co/videos/preview/mixkit-womans-feet-splashing-in-the-pool-1261-large.mp4")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initVideo()
    }

    private fun initVideo() {
        videoPagerAdapter = VideoPagerAdapter(this, videos)
        binding.viewPager.adapter = videoPagerAdapter
        startPreCaching(videos)
    }

    private fun startPreCaching(videoList: ArrayList<VideoModel>) {
        val urlList = arrayOfNulls<String>(videoList.size)
        videoList.mapIndexed { index, videoModel ->
            urlList[index] = videoModel.url
        }
        val inputData = Data.Builder().putStringArray(Constants.KEY_STORIES_LIST_DATA, urlList).build()
        val preCachingWork = OneTimeWorkRequestBuilder<PreCachingWork>().setInputData(inputData).build()
        WorkManager.getInstance(this).enqueue(preCachingWork)
    }

    private fun clearCache() {
        try {
            this.cacheDir?.let {
                deleteDir(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteDir(dir: File): Boolean {
        if (dir.isDirectory) {
            val children: Array<String> = dir.list() ?: arrayOf()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
        }
        return dir.delete()
    }
}
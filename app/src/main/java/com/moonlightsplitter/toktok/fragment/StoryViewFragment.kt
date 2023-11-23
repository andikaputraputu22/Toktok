package com.moonlightsplitter.toktok.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.util.Util
import com.moonlightsplitter.toktok.MyApp
import com.moonlightsplitter.toktok.databinding.FragmentVideoBinding
import com.moonlightsplitter.toktok.model.VideoModel
import com.moonlightsplitter.toktok.utils.Constants

class StoryViewFragment : Fragment() {

    private lateinit var binding: FragmentVideoBinding
    private var videoUrl: String? = null
    private var videoModel: VideoModel? = null
    private var player: ExoPlayer? = null
    private var cacheDataSourceFactory: CacheDataSource.Factory? = null
    private var toPlayVideoPosition = -1
    private val simpleCache = MyApp.simpleCache

    companion object {
        fun newInstance(videoModel: VideoModel) = StoryViewFragment()
            .apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.KEY_STORY_DATA, videoModel)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
    }

    override fun onPause() {
        pauseVideo()
        super.onPause()
    }

    override fun onResume() {
        restartVideo()
        super.onResume()
    }

    override fun onDestroy() {
        releaseVideo()
        super.onDestroy()
    }

    private fun setData() {
        videoModel = arguments?.getParcelable(Constants.KEY_STORY_DATA)
        val simplePlayer = getPlayer()
        binding.playerView.player = simplePlayer
        videoUrl = videoModel?.url
        videoUrl?.let { prepareMedia(it) }
    }

    private fun getPlayer(): ExoPlayer? {
        if (player == null) {
            prepareVideoPlayer()
        }
        return player
    }

    private fun prepareVideoPlayer() {
        val trackSelector = DefaultTrackSelector(requireContext())
        val loadControl = DefaultLoadControl()
        player = ExoPlayer.Builder(requireContext())
            .setTrackSelector(trackSelector)
            .setLoadControl(loadControl)
            .build()

        cacheDataSourceFactory = simpleCache?.let {
            CacheDataSource.Factory()
                .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory().setUserAgent(
                    Util.getUserAgent(requireContext(), "toktok")
                ))
                .setCache(it)
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        }
    }

    private fun prepareMedia(url: String) {
        val uri = Uri.parse(url)
        val mediaItem = MediaItem.fromUri(uri)
        val mediaSource = cacheDataSourceFactory?.let { ProgressiveMediaSource.Factory(it).createMediaSource(mediaItem) }
        mediaSource?.let { player?.setMediaSource(it) }
        player?.prepare()
        player?.repeatMode = Player.REPEAT_MODE_ONE
        player?.playWhenReady = true
        player?.addListener(playerCallback)
        toPlayVideoPosition = -1
    }

    private val playerCallback: Player.Listener = object : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            Toast.makeText(requireContext(), "Cannot play this video", Toast.LENGTH_LONG).show()
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

        }
    }

    private fun restartVideo() {
        if (player == null) {
            videoUrl?.let { prepareMedia(it) }
        } else {
            player?.seekToDefaultPosition()
            player?.playWhenReady = true
        }
    }

    private fun pauseVideo() {
        player?.playWhenReady = false
    }

    private fun releaseVideo() {
        player?.stop()
        player?.clearMediaItems()
        player?.release()
    }
}
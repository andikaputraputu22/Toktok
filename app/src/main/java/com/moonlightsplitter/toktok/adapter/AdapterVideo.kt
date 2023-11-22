package com.moonlightsplitter.toktok.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.moonlightsplitter.toktok.databinding.ItemVideoBinding
import com.moonlightsplitter.toktok.item.ExoPlayerItem
import com.moonlightsplitter.toktok.model.VideoModel

class AdapterVideo(
    private var context: Context,
    private var videos: List<VideoModel>,
    private var videoPreparedListener: OnVideoPreparedListener
) : RecyclerView.Adapter<AdapterVideo.VideoViewHolder>() {

    class VideoViewHolder(
        private val binding: ItemVideoBinding,
        private var context: Context,
        private var videoPreparedListener: OnVideoPreparedListener
    ) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var exoPlayer: ExoPlayer
        private lateinit var mediaSource: MediaSource

        fun setVideoPath(url: String) {
            exoPlayer = ExoPlayer.Builder(context).build()
            exoPlayer.addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    Toast.makeText(context, "Cannot play this video", Toast.LENGTH_LONG).show()
                }

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    if (playbackState == Player.STATE_BUFFERING) {
                        binding.progressBar.visibility = View.VISIBLE
                    } else if (playbackState == Player.STATE_READY) {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            })

            binding.playerView.player = exoPlayer
            exoPlayer.seekTo(0)
            exoPlayer.repeatMode = Player.REPEAT_MODE_ONE

            val dataSourceFactory = DefaultDataSource.Factory(context)
            mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(url)))
            exoPlayer.setMediaSource(mediaSource)
            exoPlayer.prepare()

            if (absoluteAdapterPosition == 0) {
                exoPlayer.playWhenReady = true
                exoPlayer.play()
            }

            videoPreparedListener.onVideoPrepared(ExoPlayerItem(exoPlayer, absoluteAdapterPosition))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = ItemVideoBinding.inflate(LayoutInflater.from(context), parent, false)
        return VideoViewHolder(view, context, videoPreparedListener)
    }

    override fun getItemCount(): Int {
        return videos.size
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val data = videos[position]
        holder.setVideoPath(data.url)
    }

    interface OnVideoPreparedListener {
        fun onVideoPrepared(exoPlayerItem: ExoPlayerItem)
    }
}
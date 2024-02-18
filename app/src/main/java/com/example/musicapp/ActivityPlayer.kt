package com.example.musicapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.bumptech.glide.Glide
import com.example.musicapp.databinding.ActivityPlayer2Binding

class ActivityPlayer : AppCompatActivity() {
    private lateinit var binding: ActivityPlayer2Binding
    private lateinit var exoPlayer:ExoPlayer
    private var playerListener = object:Player.Listener{
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            showGif(isPlaying)
        }
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            if (playbackState == Player.STATE_ENDED) {
                // Song has ended, stop playback
                stopPlaybackAfterPreview()
            }
        }
    }

//    private var playbackTimer: Handler = Handler(Looper.getMainLooper())
//    private val playbackDuration: Long = 33 * 1000 // 30 seconds
    @OptIn(UnstableApi::class) override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPlayer2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        MyExoplayer.getCurrentSong()?.apply {
            binding.songTitleTextview.text = title
            binding.songSubtitleTextview.text  = subtitle
            Glide.with(binding.songCover).load(coverUrl)
                .circleCrop()
                .into(binding.songCover)
            Glide.with(binding.songGifImageView).load(R.drawable.media_playing)
                .circleCrop()
                .into(binding.songGifImageView)
            exoPlayer = MyExoplayer.getInstance()!!
            binding.playerView.player=exoPlayer
            binding.playerView.showController()
           exoPlayer.addListener(playerListener)
          //  startPreviewPlayback()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.removeListener(playerListener)
    }
//    private fun startPreviewPlayback() {
//
//        exoPlayer.play()
//
//        playbackTimer.postDelayed({
//            stopPlaybackAfterPreview()
//        }, playbackDuration)
//    }
    private fun stopPlaybackAfterPreview() {
        exoPlayer.stop()
        exoPlayer.release()
    }
    fun showGif(show:Boolean)
    {
        if(show)
            binding.songGifImageView.visibility= View.VISIBLE
        else{
            binding.songGifImageView.visibility=View.INVISIBLE
        }
    }

}
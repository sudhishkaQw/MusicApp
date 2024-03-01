package com.example.musicapp

import android.content.Context
import android.media.MediaPlayer
import android.util.Log

class CustomPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    fun play(url: String) {
        try {
            mediaPlayer?.release() // Release any existing MediaPlayer
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error playing song: ${e.message}")
        }
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    companion object {
        private const val TAG = "CustomPlayer"
    }
    fun release() {
        mediaPlayer?.release()
    }
}

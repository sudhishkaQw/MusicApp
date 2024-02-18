package com.example.musicapp

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicapp.model.SongsModel
import com.google.firebase.firestore.FirebaseFirestore

object MyExoplayer {

    private var exoPlayer : ExoPlayer? = null
    private var currentSong : SongsModel? = null


    fun getCurrentSong():SongsModel?
    {
        return currentSong
    }
    fun getInstance() : ExoPlayer?
    {
        return exoPlayer
    }
    fun startPlaying(context: Context, song: SongsModel)
    {   if(exoPlayer==null)
        exoPlayer=ExoPlayer.Builder(context).build()

        if(currentSong!=song)
        {
            //new song start
            currentSong=song
            updateCount()
            currentSong?.url?.apply {
                val mediaItem = MediaItem.fromUri(this)
                exoPlayer?.setMediaItem(mediaItem)
                exoPlayer?.prepare()
                exoPlayer?.play()
            }
        }
    }
    fun startOrResume() {
        if (exoPlayer?.playbackState == ExoPlayer.STATE_READY) {
            exoPlayer?.playWhenReady = true
        } else {
            exoPlayer?.let {
                currentSong?.url?.apply {
                    val mediaItem = MediaItem.fromUri(this)
                    it.setMediaItem(mediaItem)
                    it.prepare()
                    it.playWhenReady = true
                }
            }
        }
    }
    fun pausePlaying() {
        exoPlayer?.pause()
    }

    fun isPlaying(): Boolean {
        return exoPlayer?.isPlaying ?: false
    }
    fun updateCount()
    {
        currentSong?.id?.let {id->
            FirebaseFirestore.getInstance().collection("songs")
                .document(id)
                .get().addOnSuccessListener {
                    var latestCount= it.getLong("count")
                    if(latestCount==null)
                    {
                        latestCount=1L
                    }
                    else
                    {
                        latestCount=latestCount+1
                    }
                    FirebaseFirestore.getInstance().collection("songs")
                        .document(id)
                        .update(mapOf("count" to latestCount))
                }
        }
    }
}
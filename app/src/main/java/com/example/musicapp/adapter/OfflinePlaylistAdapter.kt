package com.example.musicapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.musicapp.ActivityPlayer
import com.example.musicapp.MyExoplayer
import com.example.musicapp.R
import com.example.musicapp.databinding.SongListItemRecyclerBinding
import com.example.musicapp.model.SongsModel

class OfflinePlaylistAdapter(private var context: Context,private var songs: MutableList<SongsModel>) :
    RecyclerView.Adapter<OfflinePlaylistAdapter.ViewHolder>()  {

    inner class ViewHolder(private val binding: SongListItemRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(song: SongsModel) {
            with(binding) {
                songTextView.text = song.title
                songSubtitleView.text = song.subtitle
                Glide.with(songCoverImageView)
                    .load(song.coverUrl)
                    .apply(RequestOptions().transform(RoundedCorners(32)))
                    .into(songCoverImageView)

                playPauseButton.setOnClickListener {
                    if (MyExoplayer.isPlaying()) {
                        MyExoplayer.pausePlaying()
                        // Update button icon to play
                        playPauseButton.setImageResource(R.drawable.baseline_play_circle_24)
                    } else {
                        MyExoplayer.startPlaying(context, song)
                        // Update button icon to pause
                        playPauseButton.setImageResource(R.drawable.baseline_pause_circle_24)
                    }
                }

                root.setOnClickListener {
                    MyExoplayer.startPlaying(context, song)
                    context.startActivity(Intent(context, ActivityPlayer::class.java))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SongListItemRecyclerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
//    fun updateDownloadedSongs(downloadedSongs: List<SongsModel>) {
//        this.downloadedSongs.clear()
//        this.downloadedSongs.addAll(downloadedSongs)
//        notifyDataSetChanged()
//    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(songs[position])
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    fun addSong(song: SongsModel) {
        songs.add(song)
        notifyItemInserted(songs.size - 1)
    }
}
//    private fun isSongAvailableOffline(song: SongsModel): Boolean {
//        // Check if the song is available offline by checking if it exists in the local cache
//        // You need to implement this logic based on how you cache and store the songs locally
//        // Return true if the song is available offline, false otherwise
//        // Example: Check if the audio file exists in the device's local storage
//        val audioFile = File(context.cacheDir, song.audioFileName)
//        return audioFile.exists()
//    }



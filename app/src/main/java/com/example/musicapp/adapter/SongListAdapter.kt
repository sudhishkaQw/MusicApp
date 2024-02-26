package com.example.musicapp.adapter

import android.annotation.SuppressLint
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
import com.google.firebase.firestore.FirebaseFirestore

class SongListAdapter(private var songIdList:List<String>) :
    RecyclerView.Adapter<SongListAdapter.MyViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = SongListItemRecyclerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return  songIdList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindData(songIdList[position],songIdList,position)
    }

    class MyViewHolder(private val binding: SongListItemRecyclerBinding ):RecyclerView.ViewHolder(binding.root)
    {
        @SuppressLint("UnsafeOptInUsageError")
        fun bindData(songId: String, songIdList: List<String>, position: Int)
        {
            FirebaseFirestore.getInstance().collection("songs")
                .document(songId).get()
                .addOnSuccessListener {
                    val song=it.toObject(SongsModel::class.java)
                    song?.apply {
                        binding.songTextView.text= title
                        binding.songSubtitleView.text= subtitle
                        Glide.with(binding.songCoverImageView).load(coverUrl)
                            .apply(
                                RequestOptions().transform(RoundedCorners(32))
                            )
                            .into(binding.songCoverImageView)
                        binding.playPauseButton.setOnClickListener {
                            if (MyExoplayer.isPlaying()) {
                                MyExoplayer.pausePlaying()
                                // Update button icon to play
                                binding.playPauseButton.setImageResource(R.drawable.baseline_play_circle_24)
                            } else {
                                MyExoplayer.startPlaying(binding.root.context, song)
                                // Update button icon to pause
                                binding.playPauseButton.setImageResource(R.drawable.baseline_pause_circle_24)
                            }
                        }
                        binding.root.setOnClickListener{ it ->
                            val intent = Intent(it.context, ActivityPlayer::class.java)
                            intent.putExtra("index",position)
                            intent.putStringArrayListExtra("songsList",songIdList as ArrayList<String>)
                            it.context.startActivity(intent)

                        }
                    }
                }

        }

    }

}
    package com.example.musicapp.adapter

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

    class PurchasedSongAdapter(private var purchasedSongsWithCover: List<Pair<SongsModel, String>>) :
        RecyclerView.Adapter<PurchasedSongAdapter.ViewHolder>() {

        inner class ViewHolder(private val binding: SongListItemRecyclerBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(
                songWithCover: Pair<SongsModel, String>,
                purchasedSongsWithCover: List<Pair<SongsModel, String>>,
                position: Int
            ) {
                val (song, coverUrl) = songWithCover
                with(binding) {
                    songTextView.text = song.title
                    songSubtitleView.text = song.subtitle
                    Glide.with(songCoverImageView)
                        .load(coverUrl)
                        .apply(RequestOptions().transform(RoundedCorners(32)))
                        .into(songCoverImageView)
                    playPauseButton.setOnClickListener {
                        if (MyExoplayer.isPlaying()) {
                            MyExoplayer.pausePlaying()
                            // Update button icon to play
                            playPauseButton.setImageResource(R.drawable.baseline_play_circle_24)
                        } else {
                            MyExoplayer.startPlaying(binding.root.context, song)
                            // Update button icon to pause
                            playPauseButton.setImageResource(R.drawable.baseline_pause_circle_24)
                        }
                    }
                    root.setOnClickListener {
                        val intent = Intent(it.context, ActivityPlayer::class.java)
                        intent.putExtra("index",position)
                        intent.putStringArrayListExtra("songsList",purchasedSongsWithCover as ArrayList<String>)
                        it.context.startActivity(intent)
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

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(purchasedSongsWithCover[position],purchasedSongsWithCover,position)
        }

        override fun getItemCount(): Int {
            return purchasedSongsWithCover.size
        }
        fun updatePurchasedSongs(newPurchasedSongsWithCover: List<Pair<SongsModel, String>>) {
            purchasedSongsWithCover = newPurchasedSongsWithCover
            notifyDataSetChanged()
        }
    }
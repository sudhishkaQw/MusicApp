package com.example.musicapp

import PurchasedSongAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapp.databinding.ActivityPurchasedSongsBinding
import com.example.musicapp.model.PurchasedSong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PurchasedSongs : AppCompatActivity() {

    private lateinit var binding: ActivityPurchasedSongsBinding
    private lateinit var purchasedSongAdapter: PurchasedSongAdapter
    private val userFetcher = UserFetcher()
    private lateinit var customPlayer: CustomPlayer
    private lateinit var purchasedSongsList: ArrayList<PurchasedSong>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchasedSongsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customPlayer=CustomPlayer(this@PurchasedSongs)
        // Initialize RecyclerView and its adapter
        purchasedSongAdapter = PurchasedSongAdapter(emptyList(),customPlayer)
        binding.recyclerViewPurchasedSongs.apply {
            layoutManager = LinearLayoutManager(this@PurchasedSongs)
            adapter = purchasedSongAdapter
        }

        // Fetch and display purchased songs
        fetchPurchasedSongs()

    }

    private fun fetchPurchasedSongs() {
        lifecycleScope.launch(Dispatchers.Main) {
            userFetcher.fetchPurchasedSongsForCurrentUser(
                onSuccess = { purchasedSongs ->

                    purchasedSongAdapter.updatePurchasedSongs(purchasedSongs)
                },
                onFailure = { exception ->

                }
            )
        }
    }

    companion object {
        private const val TAG = "PurchasedActivity"
    }
}

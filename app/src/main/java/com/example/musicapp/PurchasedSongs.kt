package com.example.musicapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapp.adapter.PurchasedSongAdapter
import com.example.musicapp.databinding.ActivityPurchasedSongsBinding
import com.example.musicapp.model.SongsModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PurchasedSongs : AppCompatActivity() {
    private lateinit var binding: ActivityPurchasedSongsBinding
    private lateinit var purchasedSongAdapter: PurchasedSongAdapter
    private val userFetcher = UserFetcher()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchasedSongsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeRecyclerView()

        // Fetch purchased songs from Firestore
        fetchPurchasedSongs()

        // Fetch and display purchased songs

    }

    private fun initializeRecyclerView() {
        purchasedSongAdapter = PurchasedSongAdapter(emptyList())
        binding.recyclerViewPurchasedSongs.apply {
            layoutManager = LinearLayoutManager(this@PurchasedSongs)
            adapter = purchasedSongAdapter
        }
    }

    private fun fetchPurchasedSongs() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                // Fetch purchased songs from Firestore
                val purchasedSongs = fetchPurchasedSongsFromFirestore()

                // Map each SongsModel to a Pair<SongsModel, String> where the second element is the cover URL
                val purchasedSongsWithCover = purchasedSongs.map { Pair(it, it.coverUrl) }

                // Update RecyclerView with purchased songs and cover URLs
                updateRecyclerView(purchasedSongsWithCover)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching purchased songs", e)
                Toast.makeText(this@PurchasedSongs, "Failed to fetch purchased songs", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private suspend fun fetchPurchasedSongsFromFirestore(): List<SongsModel> {
        return try {
            val deferred = CompletableDeferred<List<SongsModel>>()

            userFetcher.fetchPurchasedSongsForCurrentUser(
                onSuccess = { purchasedSongs ->
                    // Resolve the deferred with the list of purchased songs
                    deferred.complete(purchasedSongs)
                },
                onFailure = { exception ->
                    // Reject the deferred with the exception
                    deferred.completeExceptionally(exception)
                }
            )

            // Wait for the result of the asynchronous operation
            deferred.await()
        } catch (e: Exception) {
            // Handle any exception that might occur during the function call
            Log.e(TAG, "Error fetching purchased songs", e)
            emptyList()
        }
    }


    private fun updateRecyclerView(purchasedSongsWithCover: List<Pair<SongsModel, String>>) {
        purchasedSongAdapter.updatePurchasedSongs(purchasedSongsWithCover)
    }


    companion object {
        private const val TAG = "PurchasedActivity"
    }

}


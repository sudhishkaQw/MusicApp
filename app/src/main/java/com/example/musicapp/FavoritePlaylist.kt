package com.example.musicapp

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.adapter.FavoritePlaylistAdapter
import kotlinx.coroutines.launch

class FavoritePlaylist : AppCompatActivity() {
    private lateinit var favoriteSongsRecyclerView: RecyclerView
    private lateinit var favoriteSongsAdapter: FavoritePlaylistAdapter
    private lateinit var userFetcher: UserFetcher
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_playlist)
        // Initialize RecyclerView and adapter
        favoriteSongsRecyclerView = findViewById(R.id.recyclerViewFavorite)
        favoriteSongsAdapter = FavoritePlaylistAdapter(emptyList())
        favoriteSongsRecyclerView.adapter = favoriteSongsAdapter
        favoriteSongsRecyclerView.layoutManager = LinearLayoutManager(this)
        userFetcher = UserFetcher()
        lifecycleScope.launch {
            fetchFavoriteSongs()
        }


    }

    private suspend fun fetchFavoriteSongs() {
        userFetcher.fetchFavoriteSongs(
            onSuccess = { favoriteSongs ->
                // Update the UI with the list of favorite songs
                updateFavoriteSongs(favoriteSongs.map { it.id })
            },
            onFailure = { exception ->
                // Handle failure to fetch favorite songs
                // For example, show an error message
                Log.e(TAG, "Failed to fetch favorite songs", exception)
            }
        )
    }


    private fun updateFavoriteSongs(newFavoriteSongIds: List<String>) {
        favoriteSongsAdapter.updateFavoriteSongs(newFavoriteSongIds)
    }
}
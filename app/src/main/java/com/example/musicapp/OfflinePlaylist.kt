package com.example.musicapp

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapp.adapter.OfflinePlaylistAdapter
import com.example.musicapp.databinding.ActivityOfflinePlaylistBinding
import com.example.musicapp.model.MyViewModel
import com.example.musicapp.model.SongsModel
import com.google.firebase.firestore.FirebaseFirestore

class OfflinePlaylist : AppCompatActivity() {

    private lateinit var binding: ActivityOfflinePlaylistBinding
    private lateinit var songViewModel: MyViewModel
    private lateinit var offlinePlaylistAdapter: OfflinePlaylistAdapter
    private val offlineSongs: MutableList<SongsModel> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOfflinePlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        songViewModel = ViewModelProvider(this).get(MyViewModel::class.java)

        setupRecyclerView()
        loadOfflineSongs()
    }

    private fun setupRecyclerView() {
        offlinePlaylistAdapter = OfflinePlaylistAdapter(this, mutableListOf())
        binding.recyclerViewOfflinePlaylist.apply {
            adapter = offlinePlaylistAdapter
            layoutManager = LinearLayoutManager(this@OfflinePlaylist)
        }
    }

//    private fun fetchDownloadedSongs() {
//        val db = FirebaseFirestore.getInstance()
//        db.collection("songs")
//            .whereEqualTo("downloaded", true)
//            .get()
//            .addOnSuccessListener { documents ->
//                val downloadedSongs = mutableListOf<SongsModel>()
//                for (document in documents) {
//                    val song = document.toObject(SongsModel::class.java)
//                    downloadedSongs.add(song)
//                }
//
//             //   offlinePlaylistAdapter.updateDownloadedSongs(downloadedSongs)
//            }
//            .addOnFailureListener { exception ->
//                // Handle failure
//            }
//    }
    private fun loadOfflineSongs() {
        val db = FirebaseFirestore.getInstance()
        db.collection("songs")
            .whereEqualTo("downloaded", true)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val song = document.toObject(SongsModel::class.java)
                    offlineSongs.add(song)
                }
                offlinePlaylistAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting offline songs", exception)
            }
    }

}

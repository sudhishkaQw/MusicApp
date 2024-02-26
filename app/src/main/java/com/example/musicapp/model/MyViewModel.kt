package com.example.musicapp.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.musicapp.LocalDatabase.AppDatabase
import com.example.musicapp.LocalDatabase.DownloadedSong
import kotlinx.coroutines.launch

class MyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SongRepository
    val allDownloadedSongs: LiveData<List<DownloadedSong>>

    init {
        val downloadedSongDao = AppDatabase.getInstance(application).downloadedSongDao()
        repository = SongRepository(downloadedSongDao)
        allDownloadedSongs = repository.allDownloadedSongs
    }

    fun insert(song: DownloadedSong) {
        viewModelScope.launch {
            repository.insert(song)
        }
    }

    // Implement download functionality here
}

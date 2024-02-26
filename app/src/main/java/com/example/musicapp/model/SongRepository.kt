package com.example.musicapp.model

import androidx.lifecycle.LiveData
import com.example.musicapp.LocalDatabase.DownloadedSong
import com.example.musicapp.LocalDatabase.DownloadedSongDao

class SongRepository(private val downloadedSongDao: DownloadedSongDao) {
    val allDownloadedSongs: LiveData<List<DownloadedSong>> = downloadedSongDao.getAllDownloadedSongs()

    suspend fun insert(song: DownloadedSong) {
        downloadedSongDao.insert(song)
    }
}

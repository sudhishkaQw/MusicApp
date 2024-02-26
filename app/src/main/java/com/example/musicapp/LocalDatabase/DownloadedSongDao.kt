package com.example.musicapp.LocalDatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface DownloadedSongDao {
    @Insert
    fun insert(song: DownloadedSong)

    @Query("SELECT * FROM downloaded_songs")
    fun getAllDownloadedSongs(): LiveData<List<DownloadedSong>>
    // Add other necessary queries
}


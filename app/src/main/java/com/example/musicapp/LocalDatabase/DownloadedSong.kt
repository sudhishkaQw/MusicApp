    package com.example.musicapp.LocalDatabase

    import androidx.room.ColumnInfo
    import androidx.room.Entity
    import androidx.room.PrimaryKey

    @Entity(tableName = "downloaded_songs")
    data class DownloadedSong(
        @PrimaryKey(autoGenerate = true)
        val id: Int = 0,
        @ColumnInfo(name = "firebase_id")
        val firebaseId: String,
        @ColumnInfo(name = "song_name")
        val title: String,
        @ColumnInfo(name = "song_file_path")
        val filePath: String,
        @ColumnInfo(name = "song_coverUrl")
        val songCoverUrl: String,
        @ColumnInfo(name = "credits")
        val subtitle: String
    )



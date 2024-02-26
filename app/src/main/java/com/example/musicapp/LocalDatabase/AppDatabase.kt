package com.example.musicapp.LocalDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [DownloadedSong::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun downloadedSongDao(): DownloadedSongDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Since the primary key is changing from TEXT to INTEGER,
                // you need to create a new table and copy data to it
                database.execSQL("CREATE TABLE IF NOT EXISTS downloaded_songs_temp " +
                        "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "song_name TEXT NOT NULL, " +
                        "song_file_path TEXT NOT NULL, " +
                        "song_coverUrl TEXT NOT NULL, " +
                        "credits TEXT NOT NULL, " +
                        "url TEXT NOT NULL)")
                database.execSQL("INSERT INTO downloaded_songs_temp (id, song_name, song_file_path, song_coverUrl, credits, url) " +
                        "SELECT id, song_name, song_file_path, song_coverUrl, credits, url FROM downloaded_songs")
                database.execSQL("DROP TABLE downloaded_songs")
                database.execSQL("ALTER TABLE downloaded_songs_temp RENAME TO downloaded_songs")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}



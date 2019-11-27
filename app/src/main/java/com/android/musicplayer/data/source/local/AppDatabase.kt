package com.android.musicplayer.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android.musicplayer.data.model.Song
import com.android.musicplayer.data.source.local.dao.SongDao

/**
 * To manage data items that can be accessed, updated
 * & maintain relationships between them
 *
 * @Created by ZARA
 */
@Database(entities = [Song::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract val songDao: SongDao

    companion object {
        const val DB_NAME = "MusicPlayerApp.db"
    }
}

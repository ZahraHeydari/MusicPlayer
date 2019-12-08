package com.android.musicplayer.data.repository

import com.android.musicplayer.data.model.Song
import com.android.musicplayer.data.source.local.AppDatabase
import com.android.musicplayer.domain.repository.PlaylistRepository

class PlaylistRepositoryImp(private val appDatabase: AppDatabase) : PlaylistRepository {

    override fun delete(song: Song) {
        appDatabase.songDao.delete(song)
    }

    override fun getSongs(): List<Song>? {
        return appDatabase.songDao.loadAll()
    }

    override fun saveSongData(song: Song):Long {
        return appDatabase.songDao.insert(song)
    }
}
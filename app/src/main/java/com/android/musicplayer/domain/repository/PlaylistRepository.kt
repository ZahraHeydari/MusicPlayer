package com.android.musicplayer.domain.repository

import com.android.musicplayer.data.model.Song

interface PlaylistRepository {

    fun saveSongData(song: Song)

    fun getSongs(): List<Song>

    fun delete(song: Song)

}
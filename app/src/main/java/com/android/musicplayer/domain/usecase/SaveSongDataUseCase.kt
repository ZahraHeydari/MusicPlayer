package com.android.musicplayer.domain.usecase

import com.android.musicplayer.data.model.Song
import com.android.musicplayer.domain.repository.PlaylistRepository

class SaveSongDataUseCase(private val playlistRepository: PlaylistRepository) {
    fun save(song: Song) {
        playlistRepository.saveSongData(song)
    }
}
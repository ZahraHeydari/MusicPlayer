package com.android.musicplayer.domain.usecase

import com.android.musicplayer.data.model.Song
import com.android.musicplayer.domain.repository.PlaylistRepository

class GetSongsUseCase(private val playlistRepository: PlaylistRepository) {
    fun getSongs(): List<Song>? {
        return playlistRepository.getSongs()
    }
}
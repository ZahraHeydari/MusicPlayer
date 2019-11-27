package com.android.musicplayer.domain.usecase

import com.android.musicplayer.data.model.Song
import com.android.musicplayer.domain.repository.PlaylistRepository

class DeleteSongUseCase(private val playlistRepository: PlaylistRepository) {


    fun deleteItem(song: Song) {
        playlistRepository.delete(song)
    }
}
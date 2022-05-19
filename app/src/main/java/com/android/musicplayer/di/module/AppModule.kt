package com.android.musicplayer.di.module

import com.android.musicplayer.domain.repository.PlaylistRepository
import com.android.musicplayer.domain.usecase.DeleteSongUseCase
import com.android.musicplayer.domain.usecase.GetSongsUseCase
import com.android.musicplayer.domain.usecase.SaveSongDataUseCase
import com.android.musicplayer.presentation.playlist.PlaylistViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val AppModule = module {
    viewModel {
        PlaylistViewModel(
            saveSongDataUseCase = get(),
            getSongsUseCase = get(),
            deleteSongUseCase = get()
        )
    }
    single { createGetSongsUseCase(get()) }
    single { createDeleteSongUseCase(get()) }
    single { createSaveSongDataUseCase(get()) }
    single { createPlaylistRepository(get()) }
}

fun createSaveSongDataUseCase(
    playlistRepository: PlaylistRepository
): SaveSongDataUseCase {
    return SaveSongDataUseCase(playlistRepository)
}

fun createDeleteSongUseCase(
    playlistRepository: PlaylistRepository
): DeleteSongUseCase {
    return DeleteSongUseCase(playlistRepository)
}

fun createGetSongsUseCase(
    playlistRepository: PlaylistRepository
): GetSongsUseCase {
    return GetSongsUseCase(playlistRepository)
}

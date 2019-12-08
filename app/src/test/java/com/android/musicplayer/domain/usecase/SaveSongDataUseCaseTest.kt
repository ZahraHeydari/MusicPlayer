package com.android.musicplayer.domain.usecase

import com.android.musicplayer.data.model.Song
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*

class SaveSongDataUseCaseTest {

    @MockK
    lateinit var saveSongUseCase: SaveSongDataUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun testSaveSongItem() {
        val song = mockk<Song>()
        every {
            saveSongUseCase.saveSongItem(song)
        }
    }
}
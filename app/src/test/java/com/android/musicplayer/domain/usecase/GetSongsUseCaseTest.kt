package com.android.musicplayer.domain.usecase

import com.android.musicplayer.data.model.Song
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*

class GetSongsUseCaseTest {

    @MockK
    lateinit var getSongsUseCase: GetSongsUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun testGetSongs() {
        val songs = mockk<List<Song>>()
        every {
            getSongsUseCase.getSongs()
        } returns (songs)
        val songList = getSongsUseCase.getSongs()
        assertEquals(songs, songList)
    }
}
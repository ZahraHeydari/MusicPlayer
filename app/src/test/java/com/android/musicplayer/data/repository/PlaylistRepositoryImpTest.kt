package com.android.musicplayer.data.repository

import com.android.musicplayer.data.model.Song
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*

class PlaylistRepositoryImpTest {


    @MockK
    lateinit var repository: PlaylistRepositoryImp


    @Before
    fun setUp() {
        MockKAnnotations.init(this)//for initialization
    }


    @Test
    fun testGetSongs() {
        val songs = mockk<List<Song>>()
        every { runBlocking { repository.getSongs() } } returns (songs)

        val result = repository.getSongs()
        MatcherAssert.assertThat(
            "Received result [$result] & mocked [$songs] must be matches on each other!",
            result,
            CoreMatchers.`is`(songs)
        )
    }


    @Test
    fun testSaveSongData() {
        val song = mockk<Song>()
        val id = 1L // id of stored song
        every {
            runBlocking {
                repository.saveSongData(song)
            }
        } returns (id)
        val result = repository.saveSongData(song)
        assertEquals(id, result)
    }


    @Test
    fun testDeleteSongFromDb() {
        val song = mockk<Song>()
        every{
            runBlocking {
                repository.delete(song)
            }
        }
    }

}
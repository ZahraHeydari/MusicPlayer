package com.android.player.playlist


import com.android.player.model.ASong
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PlaylistTest {

    private val mockedPlaylist = Playlist()


    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun isShuffleTest() {
        mockedPlaylist.isShuffle
        assertNotNull(mockedPlaylist.isShuffle)
    }

    @Test
    fun setShuffleTest() {
        mockedPlaylist.isShuffle = true
        assertEquals(
            "Received result of ${mockedPlaylist.isShuffle} & [true] must be matches on each other!",
            true, mockedPlaylist.isShuffle
        )
    }

    @Test
    fun isRepeatTest() {
        assertNotNull(mockedPlaylist.isRepeat)
    }

    @Test
    fun setRepeatTest() {
        mockedPlaylist.isRepeat = true
        assertEquals(
            "Received result ${mockedPlaylist.isRepeat} & mocked [true] must be matches on each other!",
            true, mockedPlaylist.isRepeat
        )
    }

    @Test
    fun isRepeatAllTest() {
        assertNotNull(mockedPlaylist.isRepeatAll)
    }

    @Test
    fun setRepeatAllTest() {
        mockedPlaylist.isRepeatAll = true
        assertEquals(
            "Received result ${mockedPlaylist.isRepeatAll} & mocked [true] must be matches on each other!",
            true, mockedPlaylist.isRepeatAll
        )
    }

    @Test
    fun getShuffleOrNormalListTest() {
        val result = mockedPlaylist.getShuffleOrNormalList()
        assertNotNull(result)
    }


    @Test
    fun addItemsTest() {
        val songA = mock(ASong::class.java)//mock creation
        val songB = mock(ASong::class.java)
        val songs = arrayListOf<ASong>(songA, songB)
        mockedPlaylist.addItems(songs)
        val result = mockedPlaylist.getShuffleOrNormalList()
        assertEquals(
            "Received result ${mockedPlaylist.getShuffleOrNormalList()} & mocked $songs must be matches on each other!",
            songs, result
        )
    }

    @Test
    fun setListTest() {
        val song = mock(ASong::class.java)//mock creation
        val songs = mutableListOf<ASong>(song)
        mockedPlaylist.setList(songs)

        val result = mockedPlaylist.getShuffleOrNormalList()
        assertEquals(songs, result)
    }

    @Test
    fun addItemTest() {
        val song = mock(ASong::class.java)//mock creation
        mockedPlaylist.addItem(song)
        val result = mockedPlaylist.getShuffleOrNormalList().contains(song)
        assertEquals(true, result)
    }
}
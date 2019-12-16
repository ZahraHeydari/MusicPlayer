package com.android.player.queue

import com.android.player.model.ASong
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class QueueHelperTest {

    private val songA = Mockito.mock(ASong::class.java)
    private val songB = Mockito.mock(ASong::class.java)
    private val songs = arrayListOf<ASong>(songA, songB)

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testGetSongIndexOnQueueTest() {
        val randomIndex = QueueHelper.getRandomIndex(songs)
        assert(randomIndex >= 0)
    }

    @Test
    fun testGetRandomIndexTest() {
        val songIndexOnQueue = QueueHelper.getSongIndexOnQueue(songs, songA)
        assert(songIndexOnQueue != -1)
    }

    @Test
    fun testEqualsListsTest() {
        val list = arrayListOf<ASong>(songA)
        val result = QueueHelper.equals(songs, list)
        assert(!result)
    }
}
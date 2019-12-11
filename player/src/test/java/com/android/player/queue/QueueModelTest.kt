package com.android.player.queue


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
class QueueModelTest {

    private val mockedQueueModel = QueueModel()


    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun isShuffleTest() {
        mockedQueueModel.isShuffle
        assertNotNull(mockedQueueModel.isShuffle)
    }

    @Test
    fun setShuffleTest() {
        mockedQueueModel.isShuffle = true
        assertEquals(
            "Received result of ${mockedQueueModel.isShuffle} & [true] must be matches on each other!",
            true, mockedQueueModel.isShuffle
        )
    }

    @Test
    fun isRepeatTest() {
        assertNotNull(mockedQueueModel.isRepeat)
    }

    @Test
    fun setRepeatTest() {
        mockedQueueModel.isRepeat = true
        assertEquals(
            "Received result ${mockedQueueModel.isRepeat} & mocked [true] must be matches on each other!",
            true, mockedQueueModel.isRepeat
        )
    }

    @Test
    fun isRepeatAllTest() {
        assertNotNull(mockedQueueModel.isRepeatAll)
    }

    @Test
    fun setRepeatAllTest() {
        mockedQueueModel.isRepeatAll = true
        assertEquals(
            "Received result ${mockedQueueModel.isRepeatAll} & mocked [true] must be matches on each other!",
            true, mockedQueueModel.isRepeatAll
        )
    }

    @Test
    fun getShuffleOrNormalListTest() {
        val result = mockedQueueModel.getShuffleOrNormalList()
        assertNotNull(result)
    }


    @Test
    fun addItemsTest() {
        val songOne = mock(ASong::class.java)//mock creation
        val songTwo = mock(ASong::class.java)
        val songs = arrayListOf<ASong>(songOne, songTwo)
        mockedQueueModel.addItems(songs)
        val result = mockedQueueModel.getShuffleOrNormalList()
        assertEquals(
            "Received result ${mockedQueueModel.getShuffleOrNormalList()} & mocked $songs must be matches on each other!",
            songs, result
        )
    }

    @Test
    fun setListTest() {
        val song = mock(ASong::class.java)//mock creation
        val songs = mutableListOf<ASong>(song)
        mockedQueueModel.setList(songs)

        val result = mockedQueueModel.getShuffleOrNormalList()
        assertEquals(songs, result)
    }

    @Test
    fun addItemTest() {
        val song = mock(ASong::class.java)//mock creation
        mockedQueueModel.addItem(song)
        val result = mockedQueueModel.getShuffleOrNormalList().contains(song)
        assertEquals(true, result)
    }
}
package com.android.player.queue

import com.android.player.model.ASong
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class QueueManagerTest {


    lateinit var queueManager: QueueManager
    private val mListener = mock(QueueManager.OnSongUpdateListener::class.java)
    private var queueModel = QueueModel()
    private var mCurrentIndex: Int = 0

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        queueManager = QueueManager(mListener)
    }

    @Test
    fun isRepeatTest() {
        queueModel.isRepeat = true
        assertNotNull(queueModel.isRepeat)
        assertTrue(queueModel.isRepeat)
    }

    @Test
    fun isRepeatAllTest() {
        queueModel.isRepeatAll = true
        assertNotNull(queueModel.isRepeatAll)
        assertTrue(queueModel.isRepeatAll)
    }

    @Test
    fun setCurrentQueueItem() {
    }

    @Test
    fun hasQueueNext_firstCallTest() {
        val hasQueueNext = queueManager.hasQueueNext()
        assertNotNull(hasQueueNext)
        assertFalse(hasQueueNext)
    }

    @Test
    fun hasQueueNext_whenHasOneSongTest() {
        val aSong = mock(ASong::class.java)
        queueManager.addToQueue(aSong)

        val hasQueueNext = queueManager.hasQueueNext()
        assertNotNull(hasQueueNext)
        assertFalse(hasQueueNext)
    }



    @Test
    fun getCurrentSongList() {
        val song = Mockito.mock(ASong::class.java)
        queueManager.addToQueue(song)

        assertEquals(1, queueManager.getCurrentSongList().size)
        val result = queueManager.getCurrentSongList().contains(song)
        assertTrue(
            "Received result ${queueManager.getCurrentSongList().contains(song)}" +
                    " & mocked [true] must be matches on each other!", result
        )
    }
}
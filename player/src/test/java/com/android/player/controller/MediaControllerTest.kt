package com.android.player.controller

import com.android.player.exo.OnExoPlayerManagerCallback
import com.android.player.exo.PlaybackState
import com.android.player.model.ASong
import com.android.player.queue.QueueManager
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MediaControllerTest {

    lateinit var mMediaController: MediaController
    lateinit var queueManager: QueueManager
    private val mListener = mock(QueueManager.OnSongUpdateListener::class.java)
    private val mMediaControllerCallback = mock(OnMediaControllerCallback::class.java)
    private val onExoPlayerManagerCallback = Mockito.mock(OnExoPlayerManagerCallback::class.java)
    private val mediaControllerCallback = Mockito.mock(OnMediaControllerCallback::class.java)


    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        mMediaController = MediaController(onExoPlayerManagerCallback, mediaControllerCallback)

        queueManager = QueueManager(mListener)
    }

    @Test
    fun registerMediaControllerCallbackTest() {
        mMediaController.registerCallback(mMediaControllerCallback)
        assertTrue(mMediaController.mMediaControllersCallbacksHashSet.size != 0)
        assertTrue(mMediaController.mMediaControllersCallbacksHashSet.contains(mMediaControllerCallback))
    }

    @Test
    fun unregisterMediaControllerCallbackTest() {
        mMediaController.unregisterCallback(mMediaControllerCallback)
        assertTrue(mMediaController.mMediaControllersCallbacksHashSet.size == 0)
        assertFalse(mMediaController.mMediaControllersCallbacksHashSet.contains(mMediaControllerCallback))
    }


    @Test
    fun addSongToQueueTest() {
        val song = Mockito.mock(ASong::class.java)
        queueManager.addToQueue(song)

        val result = queueManager.getCurrentSongList().contains(song)
        assertTrue(
            "Received result ${queueManager.getCurrentSongList().contains(song)}" +
                    " & mocked [true] must be matches on each other!", result
        )
        assertEquals(1, queueManager.getCurrentSongList().size)
    }


    @Test
    fun addSongListToQueueTest() {
        val song = Mockito.mock(ASong::class.java)
        val songList = arrayListOf<ASong>(song)
        queueManager.addToQueue(songList)

        val result = queueManager.getCurrentSongList().containsAll(songList)
        assertTrue(
            "Received result ${queueManager.getCurrentSongList().containsAll(songList)}" +
                    " & mocked [true] must be matches on each other!", result
        )
        assertEquals(1, queueManager.getCurrentSongList().size)
    }



    @Test
    fun getSongPlayingState_firstCallTest() {
        val state = onExoPlayerManagerCallback.getCurrentSongState()
        assertEquals(PlaybackState.STATE_NONE,state)
    }


    @Test
    fun getCurrentSong_whenNotPlayingTest() {
        val currentSong = onExoPlayerManagerCallback.getCurrentSong()
        assertNull(currentSong)
    }
}
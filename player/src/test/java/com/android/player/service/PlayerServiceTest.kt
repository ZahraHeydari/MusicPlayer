package com.android.player.service

import android.media.session.PlaybackState
import com.android.player.controller.MediaController
import com.android.player.controller.OnMediaControllerCallback
import com.android.player.exo.OnExoPlayerManagerCallback
import com.android.player.model.ASong
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class PlayerServiceTest {

    lateinit var mPlayerService: PlayerService
    private var mListener = mock(OnPlayerServiceListener::class.java)
    lateinit var mMediaController: MediaController


    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        mPlayerService = PlayerService()

        val onExoPlayerManagerCallback = Mockito.mock(OnExoPlayerManagerCallback::class.java)
        val mediaControllerCallback = Mockito.mock(OnMediaControllerCallback::class.java)
        mMediaController = MediaController(onExoPlayerManagerCallback, mediaControllerCallback)
    }

    @Test
    fun addListenerTest() {
        mPlayerService.addListener(mListener)
        assertNotNull(mPlayerService.mListener)
    }

    @Test
    fun playOnCurrentQueueTest() {
        val song = Mockito.mock(ASong::class.java)
        mMediaController.addToCurrentQueue(song)

        assertEquals(1, mMediaController.getCurrentSongList()?.size)
        val result = mMediaController.getCurrentSongList()?.contains(song) ?: false
        assertTrue(
            "Received result ${mMediaController.getCurrentSongList()?.contains(song)}" +
                    " & mocked [true] must be matches on each other!", result
        )
    }

    @Test
    fun addSongListToQueueTest() {
        val song = mock(ASong::class.java)
        val songList = arrayListOf<ASong>(song)
        mMediaController.addToCurrentQueue(songList)


        val result = mMediaController.getCurrentSongList()?.containsAll(songList) ?: false
        assertTrue(result)
        assertEquals("Received result ${mMediaController.getCurrentSongList()?.size}" +
                " & mocked [1] must be matches on each other!"
            ,1, mMediaController.getCurrentSongList()?.size)
    }


    @Test
    fun getSongPlayingState_firstCallTest() {
        val state = mPlayerService.getSongPlayingState()
        assertEquals(PlaybackState.STATE_NONE, state)
    }
}
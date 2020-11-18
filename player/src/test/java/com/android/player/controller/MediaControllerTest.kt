package com.android.player.controller

import com.android.player.exo.OnExoPlayerManagerCallback
import com.android.player.exo.PlaybackState
import com.android.player.model.ASong
import com.android.player.playlist.PlaylistManager
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
    lateinit var playlistManager: PlaylistManager
    private val mListener = mock(PlaylistManager.OnSongUpdateListener::class.java)
    private val mMediaControllerCallback = mock(OnMediaControllerCallback::class.java)
    private val onExoPlayerManagerCallback = Mockito.mock(OnExoPlayerManagerCallback::class.java)
    private val mediaControllerCallback = Mockito.mock(OnMediaControllerCallback::class.java)

    private val song = Mockito.mock(ASong::class.java)
    private val songList = arrayListOf<ASong>(song)


    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        mMediaController = MediaController(onExoPlayerManagerCallback, mediaControllerCallback)
        playlistManager = PlaylistManager(mListener)
    }

    @Test
    fun testRegisterMediaControllerCallback() {
        mMediaController.registerCallback(mMediaControllerCallback)
        assertTrue(mMediaController.mMediaControllersCallbacksHashSet.size != 0)
        assertTrue(mMediaController.mMediaControllersCallbacksHashSet.contains(mMediaControllerCallback))
    }

    @Test
    fun testUnregisterMediaControllerCallback() {
        mMediaController.unregisterCallback(mMediaControllerCallback)
        assertTrue(mMediaController.mMediaControllersCallbacksHashSet.size == 0)
        assertFalse(mMediaController.mMediaControllersCallbacksHashSet.contains(mMediaControllerCallback))
    }


    @Test
    fun testAddSongToPlaylist() {
        val song = Mockito.mock(ASong::class.java)
        playlistManager.addToPlaylist(song)

        val result = playlistManager.getCurrentSongList().contains(song)
        assertTrue(
            "Received result ${playlistManager.getCurrentSongList().contains(song)}" +
                    " & mocked [true] must be matches on each other!", result
        )
        assertEquals(1, playlistManager.getCurrentSongList().size)
    }


    @Test
    fun testAddSongListToPlaylist() {
        playlistManager.addToPlaylist(songList)

        val result = playlistManager.getCurrentSongList().containsAll(songList)
        assertTrue(
            "Received result ${playlistManager.getCurrentSongList().containsAll(songList)}" +
                    " & mocked [true] must be matches on each other!", result
        )
        assertEquals(1, playlistManager.getCurrentSongList().size)
    }


    @Test
    fun testPlaySongs(){
        mMediaController.playSongs(songList)
        val currentSongList = mMediaController.getCurrentSongList()
        assertNotNull(currentSongList)
        assertTrue(currentSongList?.size != 0)
    }

    @Test
    fun testGetSongPlayingState_firstCallTest() {
        val state = onExoPlayerManagerCallback.getCurrentSongState()
        assertEquals(PlaybackState.STATE_NONE,state)
    }

    @Test
    fun testGetCurrentSong_whenNotPlayingTest() {
        val currentSong = onExoPlayerManagerCallback.getCurrentSong()
        assertNull(currentSong)
    }

}
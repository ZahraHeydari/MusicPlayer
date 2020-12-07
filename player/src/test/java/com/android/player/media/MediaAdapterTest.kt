package com.android.player.media

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
class MediaAdapterTest {

    lateinit var mMediaAdapter: MediaAdapter
    lateinit var playlistManager: PlaylistManager
    private val mListener = mock(PlaylistManager.OnSongUpdateListener::class.java)
    private val onExoPlayerManagerCallback = mock(OnExoPlayerManagerCallback::class.java)
    private val mediaControllerCallback = mock(OnMediaAdapterCallback::class.java)
    private val song = mock(ASong::class.java)
    private val songList = arrayListOf<ASong>(song)


    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        mMediaAdapter = MediaAdapter(onExoPlayerManagerCallback, mediaControllerCallback)
        playlistManager = PlaylistManager(mListener)
    }

    @Test
    fun testAddSongToPlaylist() {
        val song = mock(ASong::class.java)
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
        mMediaAdapter.play(songList , song)
        val currentSongList = mMediaAdapter.getCurrentSongList()
        assertNotNull(currentSongList)
        assertTrue(currentSongList?.size != 0)
    }

}
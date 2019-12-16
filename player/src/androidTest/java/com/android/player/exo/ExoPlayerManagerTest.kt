package com.android.player.exo

import android.content.Context
import android.os.Parcelable
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.android.player.model.ASong
import com.google.android.exoplayer2.C
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.concurrent.thread

@RunWith(AndroidJUnit4::class)
class ExoPlayerManagerTest {

    private lateinit var context: Context
    private lateinit var exoPlayerManager: ExoPlayerManager
    lateinit var song: Song

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        exoPlayerManager = ExoPlayerManager(context)

        song = Song(3).apply {
            title = "BOOM"
            path = "http://android.programmerguru.com/wp-content/uploads/2013/04/hosannatelugu.mp3"
        }
    }


    @Test
    @UiThreadTest
    fun testGetCurrentSong() {
        exoPlayerManager.play(song)
        Thread.sleep(500)
        assertNotNull(exoPlayerManager.getCurrentSong())
    }

    @Test
    @UiThreadTest
    fun testIsPlaying() {
        exoPlayerManager.play(song)
        Thread.sleep(500)
        assertTrue(exoPlayerManager.isPlaying())
    }


    @Test
    fun testGetCurrentStreamPosition_forFirstCall() {
        val currentStreamPosition = exoPlayerManager.getCurrentStreamPosition()
        assertTrue(currentStreamPosition == 0L)
    }

    @Test
    @UiThreadTest
    fun testSeekTo() {
        exoPlayerManager.play(song)
        Thread.sleep(500)
        exoPlayerManager.seekTo(10)
        val currentStreamPosition = exoPlayerManager.getCurrentStreamPosition()
        assertTrue(currentStreamPosition != 0L)
        assertTrue(currentStreamPosition == 10L)
    }

    @Test
    fun testGetCurrentSongState() {
        assertNotNull(exoPlayerManager.getCurrentSongState())
    }

    @Test
    @UiThreadTest
    fun testStop(){
        exoPlayerManager.play(song)
        Thread.sleep(500)
        exoPlayerManager.stop()
        assertTrue(PlaybackState.STATE_STOPPED == exoPlayerManager.getCurrentSongState())
    }
}


data class Song(
    var id: Int,
    var title: String?,
    var path: String,
    var artist: String?,
    var clipArt: String?,
    var duartion: String?
) : ASong() {

    constructor(id: Int) : this(id, "", "", "", "", "")

    override fun getSongId(): Int {
        return id
    }

    override fun getName(): String? {
        return title
    }

    override fun getFeatureAvatar(): String? {
        return clipArt
    }

    override fun getArtistName(): String? {
        return artist
    }

    override fun getCategory(): String {
        return ""
    }

    override fun getSource(): String {
        return path
    }

    override fun getSongType(): Int {
        return C.TYPE_HLS
    }

    override fun getDownloadPath(): String {
        return path
    }

}
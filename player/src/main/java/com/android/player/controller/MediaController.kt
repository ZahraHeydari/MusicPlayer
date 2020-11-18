package com.android.player.controller

import android.os.Handler
import android.util.Log
import com.android.player.exo.OnExoPlayerManagerCallback
import com.android.player.model.ASong
import com.android.player.playlist.PlaylistManager
import com.android.player.playlist.Playlist
import java.util.*


/**
 * This class is used to interact with [ExoPlayerManager] & [PlaylistManager]
 *
 * @author ZARA
 * */
class MediaController(
    private val onExoPlayerManagerCallback: OnExoPlayerManagerCallback,
    private val mediaControllerCallback: OnMediaControllerCallback
) : OnExoPlayerManagerCallback.OnSongStateCallback {


    val mMediaControllersCallbacksHashSet = HashSet<OnMediaControllerCallback>()
    private var playlistManager: PlaylistManager? = null

    init {
        this.onExoPlayerManagerCallback.setCallback(this)

        playlistManager = PlaylistManager(object : PlaylistManager.OnSongUpdateListener {
            override fun onSongChanged(song: ASong) {
              //  Log.d(TAG, "onSongChanged() called with: song = $song")
                play(song)
            }

            override fun onSongRetrieveError() {
              //  Log.d(TAG, "onSongRetrieveError() called")
            }

            override fun onCurrentPlaylistIndexUpdate(index: Int) {
               // Log.d(TAG, "onCurrentPlaylistIndexUpdate() called with: index = $index")
            }

            override fun onPlaylistUpdate(newPlaylist: Playlist) {
              //  Log.d(TAG, "onPlaylistUpdate() called with: newPlaylist = $newPlaylist")
            }
        })
    }

    fun registerCallback(onMediaControllerCallback: OnMediaControllerCallback?) {
        onMediaControllerCallback?.let { nonNullCallback ->
            mMediaControllersCallbacksHashSet.add(nonNullCallback)
        }


        onExoPlayerManagerCallback.getCurrentSong()?.let { nonNullSong ->
            Handler().postDelayed({
                runOnSongChanged(onMediaControllerCallback)
                runOnPlaybackStateChanged(
                    onMediaControllerCallback
                )
            }, 1000)
        }
    }

    fun unregisterCallback(callback: OnMediaControllerCallback) {
        mMediaControllersCallbacksHashSet.remove(callback)
    }

    fun getSongPlayingState(): Int {
        return onExoPlayerManagerCallback.getCurrentSongState()
    }

    fun play(song: ASong) {
        this.onExoPlayerManagerCallback.play(song)
        val iterator = mMediaControllersCallbacksHashSet.iterator()
        while (iterator.hasNext()) {
            runOnSongChanged(iterator.next())
        }
    }

    fun playSongs(songList: MutableList<ASong>) {
        playlistManager?.setCurrentPlaylist(songList)
    }

    override fun shuffle(isShuffle: Boolean) {
        playlistManager?.setShuffle(isShuffle)
    }

    override fun repeatAll(isRepeatAll: Boolean) {
        playlistManager?.setRepeatAll(isRepeatAll)
    }

    override fun repeat(isRepeat: Boolean) {
        playlistManager?.setRepeat(isRepeat)
    }

    fun play(playlist: Playlist, song: ASong) {
        playlistManager?.setCurrentPlaylist(playlist, song)
    }

    fun playOnCurrentPlaylist(song: ASong) {
        playlistManager?.setSongIndexOnCurrentPlaylist(song)
    }

    fun play(songList: MutableList<ASong>, song: ASong) {
        playlistManager?.setCurrentPlaylist(songList, song)
    }

    fun pause() {
        this.onExoPlayerManagerCallback.pause()
    }

    fun seekTo(position: Long) {
        this.onExoPlayerManagerCallback.seekTo(position)
    }


    fun stop() {
        this.onExoPlayerManagerCallback.stop()
        mediaControllerCallback.onServiceStop()
        val iterator = mMediaControllersCallbacksHashSet.iterator()
        while (iterator.hasNext()) {
            runOnPlaybackStateChanged(
                iterator.next()
            )
        }
    }

    override fun getCurrentSongList(): ArrayList<ASong>? {
        return playlistManager?.getCurrentSongList() as ArrayList<ASong>
    }

    override fun getCurrentSong(): ASong? {
        return onExoPlayerManagerCallback.getCurrentSong()
    }


    override fun setCurrentPosition(position: Long, duration: Long) {
        mediaControllerCallback.setDuration(duration, position)
    }

    fun skipToNext() {
        this.playlistManager?.skipPosition(1)
    }

    fun skipToPrevious() {
        this.playlistManager?.skipPosition(-1)
    }

    fun addToCurrentPlaylist(songList: ArrayList<ASong>) {
        Log.d(TAG, "addToCurrentPlaylist() called with: songList = $songList")
        playlistManager?.addToPlaylist(songList)
    }

    fun addToCurrentPlaylist(song: ASong) {
        Log.d(TAG, "addToCurrentPlaylist() called with: song = $song")
        playlistManager?.addToPlaylist(song)
    }


    override fun onCompletion() {
        if (this.playlistManager?.isRepeat() == true) {
            this.onExoPlayerManagerCallback.stop()
            this.playlistManager?.repeat()
            return
        }

        if (this.playlistManager?.hasNext() == true) {
            this.playlistManager?.skipPosition(1)
            return
        }

        if (this.playlistManager?.isRepeatAll() == true) {
            this.playlistManager?.skipPosition(-1)
            return
        }

        val iterator = mMediaControllersCallbacksHashSet.iterator()
        while (iterator.hasNext()) {
            runOnPlaybackStateChanged(
                iterator.next()
            )
        }
        this.onExoPlayerManagerCallback.stop()
        mediaControllerCallback.onSongComplete()
    }

    override fun onPlaybackStatusChanged(state: Int) {
        val iterator = mMediaControllersCallbacksHashSet.iterator()
        while (iterator.hasNext()) {
            runOnPlaybackStateChanged(
                iterator.next()
            )
        }
    }

    override fun clearPlaylist() {
        playlistManager?.clearPlaylist()
    }


    override fun onError(error: String) {
        Log.i(TAG, "error: $error")
    }

    private fun runOnSongChanged(callback: OnMediaControllerCallback?) {
        callback?.onSongChanged()
    }

    private fun runOnPlaybackStateChanged(mediaControllerCallback: OnMediaControllerCallback?) {
        mediaControllerCallback?.onPlaybackStateChanged()
    }


    companion object {
        private val TAG = MediaController::class.java.name
    }
}
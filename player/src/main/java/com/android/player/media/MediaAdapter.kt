package com.android.player.media

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
class MediaAdapter(
    private val onExoPlayerManagerCallback: OnExoPlayerManagerCallback,
    private val mediaAdapterCallback: OnMediaAdapterCallback
) : OnExoPlayerManagerCallback.OnSongStateCallback, PlaylistManager.OnSongUpdateListener {

    private var playlistManager: PlaylistManager? = null

    init {
        onExoPlayerManagerCallback.setCallback(this)
        playlistManager = PlaylistManager(this)
    }

    fun play(song: ASong) {
        onExoPlayerManagerCallback.play(song)
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
        onExoPlayerManagerCallback.pause()
    }

    fun seekTo(position: Long) {
        onExoPlayerManagerCallback.seekTo(position)
    }

    fun stop() {
        onExoPlayerManagerCallback.stop()
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

    override fun onSongChanged(song: ASong) {
        play(song)
        mediaAdapterCallback.onSongChanged(song)
    }

    override fun onSongRetrieveError() {
        //Log.d(TAG, "onSongRetrieveError() called")
    }

    override fun onCurrentPlaylistIndexUpdate(index: Int) {
        //Log.d(TAG, "onCurrentPlaylistIndexUpdate() called with: index = $index")
    }

    override fun onPlaylistUpdate(newPlaylist: Playlist) {
        //Log.d(TAG, "onPlaylistUpdate() called with: newPlaylist = $newPlaylist")
    }

    override fun onPlaybackStatusChanged(state: Int) {
        mediaAdapterCallback.onPlaybackStateChanged(state)
    }

    override fun getCurrentSongList(): ArrayList<ASong>{
        return playlistManager?.getCurrentSongList() as ArrayList<ASong>
    }

    override fun getCurrentSong(): ASong? {
        return onExoPlayerManagerCallback.getCurrentSong()
    }

    override fun setCurrentPosition(position: Long, duration: Long) {
        mediaAdapterCallback.setDuration(duration, position)
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

        this.onExoPlayerManagerCallback.stop()
        mediaAdapterCallback.onSongComplete()
    }


    override fun clearPlaylist() {
        playlistManager?.clearPlaylist()
    }


    companion object {
        private val TAG = MediaAdapter::class.java.name
    }

}
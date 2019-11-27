package com.android.musicplayer.utils.player.exo

import com.android.musicplayer.utils.player.ASong
import java.util.ArrayList

/**
 * To make an interaction between [ExoPlayerManager]
 * & [MediaController]
 *
 * @author Zara
 * */
interface OnExoPlayerManagerCallback {

    /**
     * Get the current [PlaybackState.getState]
     */
    /**
     * Set the latest playback state
     */
    fun getState(): Int

    /**
     * @return boolean indicating whether the player is playing or is supposed to be
     * playing when we gain audio focus.
     */
    fun isPlaying(): Boolean

    /**
     * @return pos if currently playing an item
     */
    fun getCurrentStreamPosition(): Long


    fun getCurrentSong(): ASong?

    /**
     * Start/setup the playback.
     */
    fun start()

    /**
     * Stop the playback
     */
    fun stop()

    /**
     * Queries the underlying stream and update the internal last known stream position.
     */
    fun updateLastKnownStreamPosition()

    fun play(item: ASong)

    fun pause()

    fun seekTo(position: Long)


    interface OnSongStateCallback {

        fun onCompletion()

        fun onPlaybackStatusChanged(state: Int)

        fun onError(error: String)

        fun setCurrentPosition(position: Long, duration: Long)

        fun getCurrentSong(): ASong?

        fun getCurrentSongList(): ArrayList<ASong>?

    }

    fun setCallback(callback: OnSongStateCallback)
}

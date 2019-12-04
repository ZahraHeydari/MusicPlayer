package com.android.musicplayer.utils.player.exo

import com.android.musicplayer.utils.player.model.ASong
import java.util.ArrayList

/**
 * To make an interaction between [ExoPlayerManager]
 * & [MediaController]
 *
 * and to return result from [ExoPlayerManager]
 *
 * @author Zara
 * */
interface OnExoPlayerManagerCallback {

    fun getCurrentSongState(): Int

    fun isPlaying(): Boolean

    fun getCurrentStreamPosition(): Long

    fun getCurrentSong(): ASong?

    fun start()

    fun stop()

    fun updateLastKnownStreamPosition()

    fun play(aSong: ASong)

    fun pause()

    fun seekTo(position: Long)


    /**
     * This class gives the information about current song
     * (position, state of completion, when it`s changed, ...)
     *
     * */
    interface OnSongStateCallback {

        fun onCompletion()

        fun onPlaybackStatusChanged(state: Int)

        fun onError(error: String)

        fun setCurrentPosition(position: Long, duration: Long)

        fun getCurrentSong(): ASong?

        fun getCurrentSongList(): ArrayList<ASong>?

        fun shuffle(isShuffle: Boolean)

        fun repeat(isRepeat: Boolean)

        fun repeatAll(isRepeatAll: Boolean)

    }

    fun setCallback(callback: OnSongStateCallback)
}

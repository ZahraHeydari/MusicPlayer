package com.android.player.exo

import com.android.player.model.ASong
import java.util.ArrayList

/**
 * To make an interaction between [ExoPlayerManager] & [MediaController]
 *
 * and to return result from [ExoPlayerManager]
 *
 * @author Zara
 * */
interface OnExoPlayerManagerCallback {

    fun getCurrentStreamPosition(): Long

    fun stop()

    fun play(aSong: ASong)

    fun pause()

    fun seekTo(position: Long)

    fun setCallback(callback: OnSongStateCallback)

    /**
     * This class gives the information about current song
     * (position, the state of completion, when it`s changed, ...)
     *
     * */
    interface OnSongStateCallback {

        fun onCompletion()

        fun onPlaybackStatusChanged(state : Int)

        fun setCurrentPosition(position: Long, duration: Long)

        fun getCurrentSong(): ASong?

        fun getCurrentSongList(): ArrayList<ASong>?

        fun shuffle(isShuffle: Boolean)

        fun repeat(isRepeat: Boolean)

        fun repeatAll(isRepeatAll: Boolean)

    }

}

package com.android.musicplayer.utils.player

import com.android.musicplayer.data.model.Song
import com.android.musicplayer.utils.player.model.ASong

/**
 * This class make an interaction [PlayerViewModel] & [BaseSongPlayerActivity]
 *
 * @author Zara
 * */
interface OnPlayerActionCallback {


    fun play(songList: MutableList<ASong>)

    fun play(song: ASong)

    fun playOnCurrentQueue(song: ASong)

    fun play(songList: MutableList<ASong>?, song: ASong)

    fun pause()

    fun stop()

    fun skipToNext()

    fun skipToPrevious()

    fun seekTo(position: Long?)

    fun addToQueue(songList: ArrayList<Song>)

    fun shuffle(isShuffle: Boolean)

    fun onRepeat(isRepeat: Boolean)

}
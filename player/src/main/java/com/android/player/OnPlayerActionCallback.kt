package com.android.player

import com.android.player.model.ASong

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

    fun addToQueue(songList: ArrayList<ASong>)

    fun shuffle(isShuffle: Boolean)

    fun onRepeat(isRepeat: Boolean)

    fun repeatAll(isRepeatAll: Boolean)

    fun clearAllItemsInQueue()

}
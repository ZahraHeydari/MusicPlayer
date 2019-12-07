package com.android.player.controller


import com.android.player.model.ASong
import java.util.ArrayList

/**
 * To return the result of [MediaController]
 *
 * and also to make an interaction between [PlayerService] & [MediaController]
 *
 * @author ZARA
 * */
interface OnMediaControllerCallback {

    fun onSongChanged()

    fun onPlaybackStateChanged()

    fun onPlaybackStart()

    fun onNotificationRequired()

    fun onPlaybackStop()

    fun onPlaybackStateUpdated()

    fun setDuration(duration: Long, position: Long)

    fun addToQueue(songList: ArrayList<ASong>)

    fun getSongPlayingState(): Int

    fun onSongComplete()

    fun onShuffle(isShuffle: Boolean)

    fun onRepeat(isRepeat: Boolean)

    fun onRepeatAll(repeatAll: Boolean)

}
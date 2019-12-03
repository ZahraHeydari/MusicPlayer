package com.android.musicplayer.utils.player.controller


import com.android.musicplayer.data.model.Song
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

    fun addToQueue(songList: ArrayList<Song>)

    fun getSongPlayingState(): Int

    fun onSongComplete()

    fun shuffle(isShuffle: Boolean)

    fun onRepeat(isRepeat: Boolean)

}
package com.android.player.media


import com.android.player.model.ASong
import java.util.ArrayList

/**
 * To return the result of [MediaAdapter]
 *
 * and also to make an interaction between [PlayerService] & [MediaAdapter]
 *
 * @author ZARA
 * */
interface OnMediaAdapterCallback {

    fun onSongChanged(song : ASong)

    fun onPlaybackStateChanged(state : Int)

    fun setDuration(duration: Long, position: Long)

    fun addNewPlaylistToCurrent(songList: ArrayList<ASong>)

    fun onShuffle(isShuffle: Boolean)

    fun onRepeat(isRepeat: Boolean)

    fun onRepeatAll(repeatAll: Boolean)

}
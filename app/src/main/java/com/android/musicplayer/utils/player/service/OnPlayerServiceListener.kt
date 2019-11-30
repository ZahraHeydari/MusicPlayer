package com.android.musicplayer.utils.player.service

import com.android.musicplayer.utils.player.ASong

/**
 * To make an interaction between [PlayerService]
 * & [BaseSongPlayerActivity]
 *
 * @author ZARA
 * */
interface OnPlayerServiceListener {

    fun updateSongData(song: ASong?)

    fun updateSongProgress(duration: Long, position: Long)

    fun setBufferingData(isBuffering: Boolean)

    fun setVisibilityData(isVisibility: Boolean)

    fun setPlay(isPlay: Boolean)

}
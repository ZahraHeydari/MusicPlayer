package com.android.player.service

import com.google.android.exoplayer2.MediaItem

/**
 * To make an interaction between [SongPlayerService] & [BaseSongPlayerActivity]
 *
 * @author ZARA
 * */
interface OnPlayerServiceCallback {
    fun updateSongData(mediaItem: MediaItem)
    fun updateSongProgress(duration: Long, position: Long)
    fun onIsPlayingChanged(isPlaying: Boolean)
    fun updateUiForPlayingMediaItem(mediaItem: MediaItem?)
}
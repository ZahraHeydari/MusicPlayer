package com.android.player.exo

import com.google.android.exoplayer2.MediaItem

/**
 * To make an interaction between [ExoPlayerManager] & [SongPlayerService]
 *
 * which returns the result from [ExoPlayerManager]
 *
 * @author Zara
 * */
interface OnExoPlayerManagerCallback {
    fun onIsPlayingChanged(isPlaying: Boolean)
    fun onUpdateProgress(duration: Long, position: Long)
    fun updateUiForPlayingMediaItem(mediaItem: MediaItem?)
}

package com.android.musicplayer.presentation.playlist

import com.android.musicplayer.data.model.Song

interface OnPlaylistAdapterListener {

    fun playSong(
        song: Song,
        songs: ArrayList<Song>
    )

    fun removeSongItem(song: Song)
}
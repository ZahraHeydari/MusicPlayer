package com.android.musicplayer.presentation.playlist

import com.android.musicplayer.data.model.Song

/**
 * To make an interaction between [PlaylistActivity]
 * & [PlaylistAdapter]
 *
 * @author ZARA
 * */
interface OnPlaylistAdapterListener {

    fun playSong(song: Song, songs: ArrayList<Song>)

    fun removeSongItem(song: Song)
}
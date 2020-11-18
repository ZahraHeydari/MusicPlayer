package com.android.player.playlist

import com.android.player.model.ASong
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max

/**
 * This class is used to manage the playlist
 * (normal list, onShuffle list, repetition, ...)
 *
 * @author ZARA
 **/
class PlaylistManager(private val mListener: OnSongUpdateListener) {

    private var playlist: Playlist? = null
    private var mCurrentIndex: Int = 0


    init {
        playlist = Playlist()
        mCurrentIndex = 0
    }

    private fun getCurrentSong(): ASong? {
        return if (mCurrentIndex >= getCurrentPlaylistSize()) null
        else playlist?.getShuffleOrNormalList()?.get(mCurrentIndex)
    }

    private fun getCurrentPlaylistSize(): Int {
        return if (playlist == null) 0 else playlist?.getShuffleOrNormalList()?.size ?: 0
    }

    fun setRepeat(isRepeat: Boolean) {
        playlist?.isRepeat = isRepeat
    }

    fun isRepeat(): Boolean {
        return playlist?.isRepeat ?: false
    }

    fun isRepeatAll(): Boolean = playlist?.isRepeatAll ?: false


    private fun setCurrentPlaylistIndex(index: Int) {
        if (index >= 0 && index < playlist?.getShuffleOrNormalList()?.size ?: 0) {
            mCurrentIndex = index
            mListener.onCurrentPlaylistIndexUpdate(mCurrentIndex)
        }
        updateSong()
    }

    fun setSongIndexOnCurrentPlaylist(song: ASong?): Boolean {
        if (song == null) return false
        val index = getSongIndexOnPlaylist(playlist?.getShuffleOrNormalList() as ArrayList<ASong>, song)
        setCurrentPlaylistIndex(index)
        return index >= 0
    }

    fun hasNext(): Boolean = mCurrentIndex < getCurrentPlaylistSize() - 1


    fun skipPosition(amount: Int): Boolean {
        var index = mCurrentIndex + amount
        if (playlist?.getShuffleOrNormalList()?.size == 0 || index >= playlist?.getShuffleOrNormalList()?.size ?: 0) return false
        if (index < 0) {
            // skip backwards before the first song will keep you on the first song
            index = if (isRepeatAll()) playlist?.getShuffleOrNormalList()?.size ?: 0 else 0
        } else {
            // skip forwards when in last song will cycle back to start of the playlist
            if (playlist?.getShuffleOrNormalList()?.size != 0) {
                index %= playlist?.getShuffleOrNormalList()?.size ?: 0
            }
        }
        return if (mCurrentIndex == index) {
            setCurrentPlaylistIndex(mCurrentIndex)
            false
        } else {
            mCurrentIndex = index
            setCurrentPlaylistIndex(mCurrentIndex)
            true
        }
    }

    fun clearPlaylist() {
        playlist?.clearList()
    }

    fun setCurrentPlaylist(newPlaylist: MutableList<ASong>, initialSong: ASong? = null) {
        setCurrentPlaylist(Playlist().setList(newPlaylist), initialSong)
    }

    fun setCurrentPlaylist(newPlaylist: Playlist, initialSong: ASong?) {
        playlist = newPlaylist
        var index = 0
        initialSong?.let {
            index = getSongIndexOnPlaylist(playlist?.getShuffleOrNormalList() as Iterable<ASong>, it)
        }
        mCurrentIndex = max(index, 0)
        setCurrentPlaylistIndex(index)
    }

    private fun updateSong() {
        val currentSong = getCurrentSong()
        if (currentSong == null) {
            mListener.onSongRetrieveError()
            return
        }
        mListener.onSongChanged(currentSong)
    }

    fun addToPlaylist(songList: ArrayList<ASong>) {
        playlist?.addItems(songList)
    }

    fun addToPlaylist(song: ASong) {
        playlist?.addItem(song)
    }

    fun repeat(): Boolean {
        if (playlist?.isRepeat == true) {
            setCurrentPlaylistIndex(mCurrentIndex)
            return true
        }
        return false
    }

    fun getCurrentSongList(): ArrayList<ASong> {
        return playlist?.getShuffleOrNormalList() as ArrayList<ASong>
    }

    fun setShuffle(isShuffle: Boolean) {
        playlist?.isShuffle = isShuffle
    }

    fun setRepeatAll(isRepeatAll: Boolean) {
        playlist?.isRepeatAll = isRepeatAll
    }

    private fun getSongIndexOnPlaylist(list: Iterable<ASong>, song: ASong): Int {
        for ((index, item) in list.withIndex()) {
            if (song.songId == item.songId) {
                return index
            }
        }
        return -1
    }

    fun getRandomIndex(list: List<ASong>) = Random().nextInt(list.size)


    /*
     * Determine if two playlists contain identical song id's in order.
     *
     * @param list1 containing [ASong]'s
     * @param list2 containing [ASong]'s
     * @return boolean indicating whether the playlist's match
     */
    fun equals(list1: List<ASong>?, list2: List<ASong>?): Boolean {
        if (list1 === list2) {
            return true
        }
        if (list1 == null || list2 == null) {
            return false
        }
        if (list1.size != list2.size) {
            return false
        }
        for (i in list1.indices) {
            if (list1[i].songId != list2[i].songId) {
                return false
            }
        }
        return true
    }

    /**
     * To make an interaction between [PlaylistManager] & [MediaController]
     *
     * to update the state of playing [Song]
     * */
    interface OnSongUpdateListener {

        fun onSongChanged(song: ASong)

        fun onSongRetrieveError()

        fun onCurrentPlaylistIndexUpdate(index: Int)

        fun onPlaylistUpdate(newPlaylist: Playlist)
    }


}

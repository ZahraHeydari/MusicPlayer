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

    fun getCurrentSong(): ASong? {
        return playlist?.getItem(mCurrentIndex)
    }

    fun getCurrentSongList(): java.util.ArrayList<ASong> {
        return playlist?.getShuffleOrNormalList() as java.util.ArrayList<ASong>
    }

    private fun setCurrentPlaylistIndex(index: Int) {
        if (index >= 0 && index < playlist?.getShuffleOrNormalList()?.size ?: 0) {
            mCurrentIndex = index
            //mListener.onUpdatePlaylistIndex(mCurrentIndex)
        }
        updateSong()
    }

    fun hasNext(): Boolean = mCurrentIndex < (playlist?.getCurrentPlaylistSize()?.minus(1) ?: 0)

    fun skipPosition(amount: Int): Boolean {
        var index = mCurrentIndex + amount
        val currentPlayListSize = playlist?.getCurrentPlaylistSize() ?: 0

        if (currentPlayListSize == 0 || index >= currentPlayListSize) return false
        if (index < 0) {
            // skip backwards before the first song will keep you on the first song
            index = if (isRepeatAll()) currentPlayListSize else 0
        } else {
            // skip forwards when in last song will cycle back to start of the playlist
            if (currentPlayListSize != 0) index %= currentPlayListSize
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

    fun setCurrentPlaylist(newPlaylist: MutableList<ASong>, initialSong: ASong? = null) {
        playlist = Playlist().setList(newPlaylist)
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

    fun setRepeat(isRepeat: Boolean) {
        playlist?.isRepeat = isRepeat
    }

    fun isRepeat(): Boolean {
        return playlist?.isRepeat ?: false
    }

    fun isRepeatAll(): Boolean = playlist?.isRepeatAll ?: false

    fun repeat(): Boolean {
        if (playlist?.isRepeat == true) {
            setCurrentPlaylistIndex(mCurrentIndex)
            return true
        }
        return false
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


    /**
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

    }


}

package com.android.musicplayer.utils.player.queue

import com.android.musicplayer.utils.player.model.ASong
import kotlin.math.max

/**
 * This class is used to manage the queue of playlist
 * (normal list, shuffle list, repetition, ...)
 *
 * @author ZARA
 **/
class QueueManager(private val mListener: SongUpdateListener) {

    private var queue: QueueEntity? = null
    private var mCurrentIndex: Int = 0


    init {
        queue = QueueEntity()
        mCurrentIndex = 0
    }

    private fun getCurrentSong(): ASong? {
        return if (mCurrentIndex >= getCurrentQueueSize()) null
        else queue?.getShuffleOrNormalList()?.get(mCurrentIndex)
    }

    private fun getCurrentQueueSize(): Int {
        return if (queue == null) 0 else queue?.getShuffleOrNormalList()?.size ?: 0
    }

    fun isRepeat(): Boolean {
        return queue?.isRepeat ?: false
    }


    private fun setCurrentQueueIndex(index: Int) {
        if (index >= 0 && index < queue?.getShuffleOrNormalList()?.size ?: 0) {
            mCurrentIndex = index
            mListener.onCurrentQueueIndexUpdated(mCurrentIndex)
        }
        updateSong()
    }

    fun setCurrentQueueItem(song: ASong?): Boolean {
        if (song == null) return false
        val index = QueueHelper.getSongIndexOnQueue(queue?.getShuffleOrNormalList() as ArrayList<ASong>, song)
        setCurrentQueueIndex(index)
        return index >= 0
    }

    fun hasQueueNext(): Boolean {
        return mCurrentIndex < getCurrentQueueSize() - 1
    }

    fun skipQueuePosition(amount: Int): Boolean {
        var index = mCurrentIndex + amount
        if (index < 0) {
            // skip backwards before the first song will keep you on the first song
            index = 0
        } else {
            // skip forwards when in last song will cycle back to start of the queue
            index %= queue?.getShuffleOrNormalList()?.size ?: 0
        }
        return if (mCurrentIndex == index) {
            setCurrentQueueIndex(mCurrentIndex)
            false
        } else {
            mCurrentIndex = index
            setCurrentQueueIndex(mCurrentIndex)
            true
        }
    }


    fun setCurrentQueue(
        newQueue: MutableList<ASong>,
        initialSong: ASong? = null
    ) {
        setCurrentQueue(QueueEntity().setList(newQueue), initialSong)
    }

    fun setCurrentQueue(
        newQueue: QueueEntity,
        initialSong: ASong?
    ) {
        queue = newQueue
        var index = 0
        initialSong?.let {
            index = QueueHelper.getSongIndexOnQueue(
                queue?.getShuffleOrNormalList() as Iterable<ASong>,
                it
            )
        }
        mCurrentIndex = max(index, 0)
        mListener.onQueueUpdated(newQueue)
        setCurrentQueueIndex(index)
    }

    private fun updateSong() {
        val currentSong = getCurrentSong()
        if (currentSong == null) {
            mListener.onSongRetrieveError()
            return
        }
        mListener.onSongChanged(currentSong)
    }

    fun addToQueue(songList: ArrayList<ASong>) {
        queue?.addItems(songList)
    }

    fun addToQueue(song: ASong) {
        queue?.addItem(song)
    }

    fun repeat(): Boolean {
        if (queue?.isRepeat == true) {
            setCurrentQueueIndex(mCurrentIndex)
            return true
        }
        return false
    }

    fun getCurrentSongList(): ArrayList<ASong> {
        return queue?.getShuffleOrNormalList() as ArrayList<ASong>
    }

    interface SongUpdateListener {

        fun onSongChanged(song: ASong)

        fun onSongRetrieveError()

        fun onCurrentQueueIndexUpdated(queueIndex: Int)

        fun onQueueUpdated(newQueue: QueueEntity)
    }
}

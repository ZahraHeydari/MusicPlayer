package com.android.musicplayer.utils.player.queue

import android.util.Log
import com.android.musicplayer.utils.player.model.ASong
import java.util.*
import kotlin.collections.ArrayList

class QueueEntity {

    private val TAG = QueueEntity::class.java.name
    private var list: MutableList<ASong> = ArrayList()
    private var shuffleList: MutableList<ASong> = ArrayList()
    var isShuffle = false
    var isRepeat = false
    var isRepeatAll = false

    fun getShuffleOrNormalList(): MutableList<ASong> {
        return if (isShuffle) shuffleList else list
    }

    fun setList(list: MutableList<ASong>): QueueEntity {
        this.list = list
        list.shuffle()
        this.shuffleList = ArrayList(list)
        Log.i(TAG,"setList onShuffle list: $shuffleList")
        return this
    }

    fun addItems(songList: List<ASong>) {
        this.list.addAll(songList)
        songList.shuffled()
        this.shuffleList.addAll(songList)
    }

    fun addItem(song: ASong) {
        this.list.add(song)
        this.shuffleList.add(song)
    }
}

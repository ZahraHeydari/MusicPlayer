package com.android.musicplayer.utils.player.queue

import com.android.musicplayer.utils.player.model.ASong
import java.util.*
import kotlin.collections.ArrayList

class QueueEntity {

    private var list: MutableList<ASong> = ArrayList()
    private var shuffleList: MutableList<ASong> = ArrayList()
    var isShuffle = false
    var isRepeat = false

    fun getShuffleOrNormalList(): MutableList<ASong> {
        return if (isShuffle) shuffleList else list
    }

    fun getList(): List<ASong> {
        return list
    }

    fun getShuffleList(): List<ASong> {
        return shuffleList
    }

    fun setList(list: MutableList<ASong>): QueueEntity {
        this.list = list
        this.shuffleList = ArrayList(list)
        shuffleList.shuffle()
        return this
    }

    fun addItems(songList: List<ASong>) {
        this.list.addAll(songList)
        Collections.shuffle(songList)
        this.shuffleList.addAll(songList)
    }

    fun addItem(song: ASong){
        this.list.add(song)
        this.shuffleList.add(song)
    }
}

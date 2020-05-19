package com.android.player.queue


import com.android.player.model.ASong

class QueueModel {

    private var list: MutableList<ASong> = ArrayList()
    private var shuffleList: MutableList<ASong> = ArrayList()
    var isShuffle = false
    var isRepeat = false
    var isRepeatAll = false

    fun getShuffleOrNormalList(): MutableList<ASong> {
        return if (isShuffle) shuffleList else list
    }

    fun setList(list: MutableList<ASong>): QueueModel {
        this.list = list
        list.shuffle()
        this.shuffleList = ArrayList(list)
//        Log.i(TAG, "setList onShuffle list: $shuffleList")
        return this
    }

    fun addItems(songList: ArrayList<ASong>) {
        this.list.addAll(songList)
        songList.shuffled()
        this.shuffleList.addAll(songList)
    }

    fun addItem(song: ASong) {
        this.list.add(song)
        this.shuffleList.add(song)
    }

    fun clearQueue() {
        this.list.clear()
        this.shuffleList.clear()
    }


    companion object {

        private val TAG = QueueModel::class.java.name
    }
}

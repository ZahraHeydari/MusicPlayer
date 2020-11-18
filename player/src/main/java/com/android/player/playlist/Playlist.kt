package com.android.player.playlist


import com.android.player.model.ASong

class Playlist {

    private var list: MutableList<ASong> = ArrayList()
    private var shuffleList: MutableList<ASong> = ArrayList()
    var isShuffle = false
    var isRepeat = false
    var isRepeatAll = false

    fun getShuffleOrNormalList(): MutableList<ASong> {
        return if (isShuffle) shuffleList else list
    }

    fun setList(list: MutableList<ASong>): Playlist {
        this.list = list
        list.shuffle()
        this.shuffleList = ArrayList(list)
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

    fun clearList() {
        this.list.clear()
        this.shuffleList.clear()
    }


    companion object {

        private val TAG = Playlist::class.java.name
    }
}

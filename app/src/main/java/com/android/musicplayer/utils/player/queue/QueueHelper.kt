package com.android.musicplayer.utils.player.queue

import com.android.musicplayer.utils.player.ASong
import java.util.*

object QueueHelper {


    private val TAG = QueueHelper::class.java.name

    fun getSongIndexOnQueue(
        queue: Iterable<ASong>,
        song: ASong
    ): Int {
        for ((index, item) in queue.withIndex()) {
            if (song.getSongId() == item.getSongId()) {
                return index
            }
        }
        return -1
    }


    fun getRandomIndex(queue: List<ASong>): Int {
        val random = Random()
        return random.nextInt(queue.size)
    }

    /**
     * Determine if two queues contain identical song id's in order.
     *
     * @param list1 containing [ASong]'s
     * @param list2 containing [ASong]'s
     * @return boolean indicating whether the queue's match
     */
    fun equals(
        list1: List<ASong>?,
        list2: List<ASong>?
    ): Boolean {
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
            if (list1[i].getSongId() != list2[i].getSongId()) {
                return false
            }
        }
        return true
    }

}

package com.android.musicplayer.utils.player.queue

/**
 * To make an interaction between [QueueManager] &
 * [MediaController]
 *
 * @author Zara
 *
 * */
interface OnQueueManagerCallback {

    fun onQueueUpdated(newQueue: QueueEntity)
}
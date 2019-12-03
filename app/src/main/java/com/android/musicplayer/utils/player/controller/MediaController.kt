package com.android.musicplayer.utils.player.controller

import android.os.Handler
import android.util.Log
import com.android.musicplayer.utils.player.model.ASong
import com.android.musicplayer.utils.player.exo.OnExoPlayerManagerCallback
import com.android.musicplayer.utils.player.queue.QueueEntity
import com.android.musicplayer.utils.player.queue.QueueManager
import com.android.musicplayer.utils.player.queue.OnQueueManagerCallback
import java.util.HashSet

/**
 * This class is used to interact with [ExoPlayerManager] & [QueueManager]
 *
 * @author ZARA
 * */
class MediaController(
    private val onExoPlayerManagerCallback: OnExoPlayerManagerCallback,
    private val mediaControllerCallback: OnMediaControllerCallback
) : OnExoPlayerManagerCallback.OnSongStateCallback {

    private val TAG = MediaController::class.java.name
    private val mMediaControllersCallbacksHashSet = HashSet<OnMediaControllerCallback>()
    private var queueManager: QueueManager? = null
    private var mQueueManagerCallback: OnQueueManagerCallback? = null


    init {
        this.onExoPlayerManagerCallback.setCallback(this)

        queueManager = QueueManager(object : QueueManager.SongUpdateListener {
            override fun onSongChanged(song: ASong) {
                play(song)
            }

            override fun onSongRetrieveError() {
                Log.d(TAG, "onSongRetrieveError() called")
            }

            override fun onCurrentQueueIndexUpdated(queueIndex: Int) {
                Log.d(TAG, "onCurrentQueueIndexUpdated() called with: queueIndex = [$queueIndex]")
            }

            override fun onQueueUpdated(newQueue: QueueEntity) {

                mQueueManagerCallback?.onQueueUpdated(newQueue)
            }
        })
    }

    fun registerCallback(onMediaControllerCallback: OnMediaControllerCallback?) {
        onMediaControllerCallback?.let { nonNullCallback ->
            mMediaControllersCallbacksHashSet.add(nonNullCallback)
        }
        onExoPlayerManagerCallback.getCurrentSong()?.let { nonNullSong ->
            Handler().postDelayed({
                runOnSongChanged(onMediaControllerCallback)
                runOnPlaybackStateChanged(
                    onMediaControllerCallback
                )
            }, 1000)
        }
    }

    fun unregisterCallback(callback: OnMediaControllerCallback) {
        mMediaControllersCallbacksHashSet.remove(callback)
    }

    fun getSongPlayingState(): Int {
        return onExoPlayerManagerCallback.getCurrentSongState()
    }

    fun play(song: ASong) {
        this.onExoPlayerManagerCallback.play(song)
        val iterator = mMediaControllersCallbacksHashSet.iterator()
        while (iterator.hasNext()) {
            runOnSongChanged(iterator.next())
        }
        mediaControllerCallback.onPlaybackStart()
        mediaControllerCallback.onNotificationRequired()
    }

    fun playSongs(songList: MutableList<ASong>) {
        queueManager?.setCurrentQueue(songList)
    }

    override fun shuffle(isShuffle: Boolean) {
        queueManager?.setShuffle(isShuffle)
    }

    override fun repeat(isRepeat: Boolean) {
        queueManager?.setRepeat(isRepeat)
    }

    fun play(queueEntity: QueueEntity, song: ASong) {
        queueManager?.setCurrentQueue(queueEntity, song)
    }

    fun playOnCurrentQueue(song: ASong) {
        queueManager?.setCurrentQueueItem(song)
    }

    fun play(songList: MutableList<ASong>, song: ASong) {
        queueManager?.setCurrentQueue(songList, song)
    }

    fun pause() {
        this.onExoPlayerManagerCallback.pause()
    }

    fun seekTo(position: Long) {
        this.onExoPlayerManagerCallback.seekTo(position)
    }

    fun stop() {
        this.onExoPlayerManagerCallback.stop()
        mediaControllerCallback.onPlaybackStop()
        val iterator = mMediaControllersCallbacksHashSet.iterator()
        while (iterator.hasNext()) {
            runOnPlaybackStateChanged(
                iterator.next()
            )
        }
    }

    override fun getCurrentSongList(): java.util.ArrayList<ASong>? {
        return queueManager?.getCurrentSongList()
    }

    override fun getCurrentSong(): ASong? {
        return onExoPlayerManagerCallback.getCurrentSong()
    }


    override fun setCurrentPosition(position: Long, duration: Long) {
        mediaControllerCallback.setDuration(duration, position)
    }

    fun skipToNext() {
        this.queueManager?.skipQueuePosition(1)
    }

    fun skipToPrevious() {
        this.queueManager?.skipQueuePosition(-1)
    }


    fun addToCurrentQueue(songList: ArrayList<ASong>) {
        Log.i(TAG, "addToQueue songList: $songList")
        queueManager?.addToQueue(songList)
    }

    fun addToCurrentQueue(song: ASong) {
        queueManager?.addToQueue(song)
    }


    override fun onCompletion() {
        if (this.queueManager?.isRepeat() == true) {
            this.onExoPlayerManagerCallback.stop()
            this.queueManager?.repeat()
            return
        }

        if (this.queueManager?.hasQueueNext() == true) {
            this.queueManager?.skipQueuePosition(1)
            return
        }

        val iterator = mMediaControllersCallbacksHashSet.iterator()
        while (iterator.hasNext()) {
            runOnPlaybackStateChanged(
                iterator.next()
            )
        }
        this.onExoPlayerManagerCallback.stop()
        mediaControllerCallback.onSongComplete()
    }

    override fun onPlaybackStatusChanged(state: Int) {
        val iterator = mMediaControllersCallbacksHashSet.iterator()
        while (iterator.hasNext()) {
            runOnPlaybackStateChanged(
                iterator.next()
            )
        }
        mediaControllerCallback.onPlaybackStateUpdated()
    }

    override fun onError(error: String) {
        Log.i(TAG, "error: $error")
    }

    private fun runOnSongChanged(callback: OnMediaControllerCallback?) {
        callback?.onSongChanged()
    }

    private fun runOnPlaybackStateChanged(mediaControllerCallback: OnMediaControllerCallback?) {
        mediaControllerCallback?.onPlaybackStateChanged()
    }
}
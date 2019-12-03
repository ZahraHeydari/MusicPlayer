package com.android.musicplayer.utils.player.service

import android.app.Service
import android.content.Intent
import android.media.session.PlaybackState
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.android.musicplayer.data.model.Song
import com.android.musicplayer.utils.player.model.ASong
import com.android.musicplayer.utils.player.controller.MediaController
import com.android.musicplayer.utils.player.controller.OnMediaControllerCallback
import com.android.musicplayer.utils.player.exo.ExoPlayerManager
import com.android.musicplayer.utils.player.notification.MediaNotificationManager
import java.util.*
import kotlin.collections.ArrayList

class PlayerService : Service(), OnMediaControllerCallback {

    private val TAG = PlayerService::class.java.name
    private var mMediaController: MediaController? = null
    private var mNotificationManager: MediaNotificationManager? = null
    private val mMediaControllerCallbackHashSet = HashSet<OnMediaControllerCallback>()
    private var mListener: OnPlayerServiceListener? = null


    override fun onCreate() {
        val exoPlayerManager = ExoPlayerManager(this)
        mMediaController = MediaController(exoPlayerManager, this)
        mNotificationManager = MediaNotificationManager(this)
        registerMediaControllerCallbacks()
        super.onCreate()
    }

    override fun onStartCommand(startIntent: Intent?, flags: Int, startId: Int): Int {
        startIntent?.let { nonNullIntent ->
            val action = nonNullIntent.action
            val command = nonNullIntent.getStringExtra(CMD_NAME)
            if (ACTION_CMD == action && CMD_PAUSE == command) {
                mMediaController?.pause()
            }
        }
        return START_STICKY
    }

    private fun registerMediaControllerCallbacks() {
        mMediaController?.registerCallback(this)
        for (callback in mMediaControllerCallbackHashSet) {
            mMediaController?.registerCallback(callback)
        }
        mMediaControllerCallbackHashSet.clear()
    }


    private fun unregisterAllControllerCallback() {
        for (callback in mMediaControllerCallbackHashSet) {
            mMediaController?.unregisterCallback(callback)
        }
    }

    fun addListener(listener: OnPlayerServiceListener) {
        mListener = listener
    }

    fun getCurrentSong(): ASong? {
        return mMediaController?.getCurrentSong()
    }

    fun getCurrentSongList(): ArrayList<ASong>? {
        return mMediaController?.getCurrentSongList()
    }

    override fun onSongChanged() {
        mListener?.updateSongData(getCurrentSong())
        mNotificationManager?.updateNotification()
    }

    override fun getSongPlayingState(): Int {
        return mMediaController?.getSongPlayingState() ?: 0
    }

    override fun onPlaybackStateChanged() {
        when (mMediaController?.getSongPlayingState()) {
            PlaybackState.STATE_BUFFERING -> {
                mListener?.setBufferingData(true)
                mListener?.setVisibilityData(true)
                mListener?.setPlay(true)
            }

            PlaybackState.STATE_PLAYING -> {
                mListener?.setBufferingData(false)
                mListener?.setVisibilityData(true)
                mListener?.setPlay(true)
            }

            PlaybackState.STATE_PAUSED -> {
                mListener?.setBufferingData(false)
                mListener?.setVisibilityData(true)
                mListener?.setPlay(false)
            }

            else -> {
                mListener?.setBufferingData(false)
                mListener?.setVisibilityData(false)
                mListener?.setPlay(false)
            }
        }
    }

    fun playSongs(songList: MutableList<ASong>) {
        mMediaController?.playSongs(songList)
    }

    fun play(song: ASong) {
        mMediaController?.play(song)
    }

    fun playOnCurrentQueue(song: ASong) {
        mMediaController?.playOnCurrentQueue(song)
    }

    override fun addToQueue(songList: ArrayList<Song>) {
        mMediaController?.addToCurrentQueue(songList as ArrayList<ASong>)
    }

    fun play(songList: MutableList<ASong>, song: ASong) {
        mMediaController?.play(songList, song)
        mNotificationManager?.updateNotification()
    }

    fun pause() {
        mMediaController?.pause()
        mNotificationManager?.updateNotification()
    }

    fun stop() {
        mMediaController?.stop()
    }

    override fun setDuration(duration: Long, position: Long) {
        mListener?.updateSongProgress(duration, position)
    }

    fun skipToNext() {
        mMediaController?.skipToNext()
    }

    fun skipToPrevious() {
        mMediaController?.skipToPrevious()
    }

    fun seekTo(position: Long) {
        mMediaController?.seekTo(position)
    }

    override fun onBind(intent: Intent): IBinder? {
        return LocalBinder()
    }

    override fun onPlaybackStart() {
        Log.d(TAG, "onPlaybackStart() called")
    }

    override fun onNotificationRequired() {
        mNotificationManager?.startNotification()
    }

    override fun onSongComplete() {
        mNotificationManager?.updateNotification()
        mListener?.onSongEnded()
    }

    override fun onPlaybackStop() {
        mNotificationManager?.stopNotification()

    }

    override fun onPlaybackStateUpdated() {
        if (mMediaController?.getSongPlayingState() == PlaybackState.STATE_STOPPED ||
            mMediaController?.getSongPlayingState() == PlaybackState.STATE_NONE
        ) mNotificationManager?.stopNotification()
        else mNotificationManager?.startNotification()

    }

    inner class LocalBinder : Binder() {
        // Return this instance of PlayerService so clients can call public methods
        val service: PlayerService
            get() = this@PlayerService
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
        unregisterAllControllerCallback()
        mMediaController?.stop()
        mListener = null
    }

    companion object {

        const val ACTION_CMD = "app.ACTION_CMD"
        const val CMD_NAME = "CMD_NAME"
        const val CMD_STOP_CASTING = "CMD_STOP_CASTING"
        const val CMD_PAUSE = "CMD_PAUSE"
    }
}

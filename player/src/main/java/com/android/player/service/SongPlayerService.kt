package com.android.player.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.android.player.exo.ExoPlayerManager
import com.android.player.exo.OnExoPlayerManagerCallback
import com.android.player.notification.PlayerNotificationManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import java.util.*

class SongPlayerService : Service(), OnExoPlayerManagerCallback {

    private var mNotificationManager: PlayerNotificationManager? = null
    private var exoPlayerManager: ExoPlayerManager? = null
    private val binder = LocalBinder()
    var mCallback: OnPlayerServiceCallback? = null
    var command: String? = null
    var mMediaItems : ArrayList<MediaItem> ?= null

    override fun onCreate() {
        super.onCreate()
        exoPlayerManager = ExoPlayerManager(this, this@SongPlayerService)
        mNotificationManager = PlayerNotificationManager(this)
        mNotificationManager?.createMediaNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    fun subscribeToSongPlayerUpdates() {
        /* Binding to this service doesn't actually trigger onStartCommand(). That is needed to
        * ensure this Service can be promoted to a foreground service.
        * */
        ContextCompat.startForegroundService(
            applicationContext,
            Intent(this, SongPlayerService::class.java)
        )
    }

    fun addListener(callback: OnPlayerServiceCallback) {
        mCallback = callback
    }

    fun removeListener() {
        mCallback = null
    }

    fun play() {
        exoPlayerManager?.play()
    }

    fun play(mediaItems: ArrayList<MediaItem>?, mediaItem: MediaItem?) {
        mMediaItems = mediaItems
        exoPlayerManager?.let {
            mediaItem?.let { mediaItem -> it.play(mediaItem) }
            mediaItems?.let { mediaItems -> it.setPlaylist(mediaItems) }
        }
    }

    fun onShuffle(isShuffle: Boolean) {
        exoPlayerManager?.onShuffleModeEnabledChanged(isShuffle)
    }

    fun onRepeatAll() {
        exoPlayerManager?.onRepeatModeChanged(Player.REPEAT_MODE_ALL)
    }

    fun onRepeat(isRepeat: Boolean) {
        exoPlayerManager?.onRepeatModeChanged(if (isRepeat) Player.REPEAT_MODE_OFF else Player.REPEAT_MODE_ONE)
    }

    fun seekTo(position: Long) {
        exoPlayerManager?.seekTo(position)
    }

    fun pause() {
        exoPlayerManager?.pause()
    }

    fun stop() {
        exoPlayerManager?.stop()
        stopForeground(true)
        stopSelf()
        mNotificationManager = null
    }

    fun skipToNext() {
        exoPlayerManager?.skipPosition(NEXT)
    }

    fun skipToPrevious() {
        exoPlayerManager?.skipPosition(PREVIOUS)
    }

    fun toggle() {
        exoPlayerManager?.toggle()
    }

    fun getCurrentMediaItem(): MediaItem? {
        return exoPlayerManager?.getCurrentMediaItem()
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        mNotificationManager?.generateNotification(isPlaying)
        mCallback?.onIsPlayingChanged(isPlaying)
    }

    override fun onUpdateProgress(duration: Long, position: Long) {
        mCallback?.updateSongProgress(duration, position)
    }

    override fun updateUiForPlayingMediaItem(mediaItem: MediaItem?) {
        mCallback?.updateUiForPlayingMediaItem(mediaItem)
    }

    private fun unsubscribeToSongPlayerUpdates() {
        removeListener()
    }

    override fun onDestroy() {
        unsubscribeToSongPlayerUpdates()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder {
        val action = intent.action
        command = intent.getStringExtra(CMD_NAME)
        if (ACTION_CMD == action && CMD_PAUSE == command) {
            exoPlayerManager?.pause()
        }
        return binder
    }

    inner class LocalBinder : Binder() {
        // Return this instance of PlayerService so clients can call public methods
        val service: SongPlayerService
            get() = this@SongPlayerService
    }

    companion object {
        const val ACTION_CMD = "app.ACTION_CMD"
        const val CMD_NAME = "CMD_NAME"
        const val CMD_PAUSE = "CMD_PAUSE"
        const val NEXT = 1L
        const val PREVIOUS = -1L
    }
}

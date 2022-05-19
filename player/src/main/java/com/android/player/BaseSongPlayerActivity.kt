package com.android.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import com.android.player.SongPlayerViewModel.Companion.getPlayerViewModelInstance
import com.android.player.service.OnPlayerServiceCallback
import com.android.player.service.SongPlayerService
import com.android.player.util.orFalse
import com.google.android.exoplayer2.MediaItem

open class BaseSongPlayerActivity : AppCompatActivity(), OnPlayerServiceCallback {

    private var mService: SongPlayerService? = null
    private var mBound = false
    private var mMediaItem: MediaItem? = null
    private var mMediaItems: ArrayList<MediaItem>? = null
    private var msg = 0
    val songPlayerViewModel: SongPlayerViewModel = getPlayerViewModelInstance()

    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                ACTION_PLAY_SONG_IN_LIST -> mService?.play(mMediaItems, mMediaItem)
                ACTION_PAUSE -> mService?.pause()
                ACTION_STOP -> {
                    mService?.stop()
                    songPlayerViewModel.stop()
                }
            }
        }
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to SongPlayerService, cast the IBinder and get SongPlayerService instance
            val binder = service as SongPlayerService.LocalBinder
            mService = binder.service
            mBound = true
            mService?.subscribeToSongPlayerUpdates()
            mHandler.sendEmptyMessage(msg)
            mService?.addListener(this@BaseSongPlayerActivity)
        }

        override fun onServiceDisconnected(classname: ComponentName) {
            mBound = false
            mService?.removeListener()
            mService = null
        }
    }

    private fun bindPlayerService() {
        if (!mBound) bindService(
            Intent(this, SongPlayerService::class.java),
            mConnection, Context.BIND_AUTO_CREATE
        )
    }

    fun play(mediaItems: ArrayList<MediaItem>, song: MediaItem?) {
        msg = ACTION_PLAY_SONG_IN_LIST
        mMediaItem = song
        mMediaItems = mediaItems
        if (mService == null) bindPlayerService()
        else mHandler.sendEmptyMessage(msg)
    }

    fun pause() {
        msg = ACTION_PAUSE
        if (mService == null) bindPlayerService()
        else mHandler.sendEmptyMessage(msg)
    }

    fun stop() {
        msg = ACTION_STOP
        if (mService == null) bindPlayerService()
        else mHandler.sendEmptyMessage(msg)
    }

    fun next() {
        mService?.skipToNext()
    }

    fun previous() {
        mService?.skipToPrevious()
    }

    fun toggle() {
        mService?.toggle()
    }

    fun seekTo(position: Long?) {
        position?.let { nonNullPosition ->
            mService?.seekTo(nonNullPosition)
        }
    }

    fun shuffle() {
        mService?.onShuffle(songPlayerViewModel.isShuffleData.value.orFalse())
        songPlayerViewModel.shuffle()
    }

    fun repeat() {
        mService?.onRepeat(songPlayerViewModel.isRepeatData.value.orFalse())
        songPlayerViewModel.repeat()
    }

    override fun updateSongData(mediaItem: MediaItem) {
        songPlayerViewModel.updateMediaItem(mediaItem)
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        songPlayerViewModel.setPlayingStatus(isPlaying)
    }

    override fun updateSongProgress(duration: Long, position: Long) {
        songPlayerViewModel.setChangePosition(position, duration)
    }

    override fun updateUiForPlayingMediaItem(mediaItem: MediaItem?) {
        songPlayerViewModel.updateMediaItem(mediaItem)
    }

    private fun unbindService() {
        if (mBound) {
            unbindService(mConnection)
            mBound = false
        }
    }

    override fun onDestroy() {
        unbindService()
        mService = null
        super.onDestroy()
    }

    companion object {
        const val PLAY_LIST_KEY = "PLAY_LIST_KEY"
        const val MEDIA_ITEM_KEY = "MEDIA_ITEM_KEY"
        private const val ACTION_PLAY_SONG_IN_LIST = 1
        private const val ACTION_PAUSE = 2
        private const val ACTION_STOP = 3
    }
}
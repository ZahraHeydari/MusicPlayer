package com.android.player.service

import android.app.Service
import android.content.Intent
import android.media.session.PlaybackState
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.android.player.media.MediaAdapter
import com.android.player.media.OnMediaAdapterCallback
import com.android.player.exo.ExoPlayerManager
import com.android.player.model.ASong
import com.android.player.notification.MediaNotificationManager
import java.util.*


class SongPlayerService : Service(), OnMediaAdapterCallback {

    private var mMediaAdapter: MediaAdapter? = null
    private var mNotificationManager: MediaNotificationManager? = null
    private val binder = LocalBinder()
    private var playState = 0
    var mCallback: OnPlayerServiceCallback? = null
    var command: String? = null


    override fun onCreate() {
        super.onCreate()
        val exoPlayerManager = ExoPlayerManager(this)
        mMediaAdapter = MediaAdapter(exoPlayerManager, this)
        mNotificationManager = MediaNotificationManager(this)
        mNotificationManager?.createMediaNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand() called with: intent = $intent, flags = $flags, startId = $startId")
        return START_NOT_STICKY
    }

    fun subscribeToSongPlayerUpdates(){
        Log.d(TAG, "subscribeToSongPlayerUpdates() called")

        /* Binding to this service doesn't actually trigger onStartCommand(). That is needed to
        * ensure this Service can be promoted to a foreground service.
        * */
        ContextCompat.startForegroundService(applicationContext, Intent(this, SongPlayerService::class.java))
    }

    fun addListener(callback: OnPlayerServiceCallback) {
        mCallback = callback
    }

    fun removeListener(){
        mCallback = null
    }

    fun getCurrentSong(): ASong? {
        return mMediaAdapter?.getCurrentSong()
    }

    fun getCurrentSongList(): ArrayList<ASong>? {
        return mMediaAdapter?.getCurrentSongList()
    }

    fun getPlayState(): Int = playState

    override fun onSongChanged(song : ASong) {
        mCallback?.updateSongData(song)
    }

    override fun onShuffle(isShuffle: Boolean) {
        mMediaAdapter?.shuffle(isShuffle)
    }

    override fun onRepeatAll(repeatAll: Boolean) {
        mMediaAdapter?.repeatAll(repeatAll)
    }

    override fun onRepeat(isRepeat: Boolean) {
        mMediaAdapter?.repeat(isRepeat)
    }

    fun playCurrentSong(){
        getCurrentSong()?.let { play(it) }
    }

    fun play(song: ASong?) {
        song?.let { mMediaAdapter?.play(it) }
    }

    fun play(songList: MutableList<ASong>?, song: ASong?) {
        song?.let { nonNullSong->
            songList?.let { mMediaAdapter?.play(it, nonNullSong) } ?: play(nonNullSong)
        }
    }

    fun pause() {
        mMediaAdapter?.pause()
    }

    fun stop() {
        mMediaAdapter?.stop()
        stopForeground(true)
        mNotificationManager = null
        stopSelf()
        mCallback?.stopService()
    }

    override fun addNewPlaylistToCurrent(songList: ArrayList<ASong>) {
        mMediaAdapter?.addToCurrentPlaylist(songList)
    }

    override fun setDuration(duration: Long, position: Long) {
        mCallback?.updateSongProgress(duration, position)
    }

    fun skipToNext() {
        mMediaAdapter?.skipToNext()
    }

    fun skipToPrevious() {
        mMediaAdapter?.skipToPrevious()
    }

    fun seekTo(position: Long) {
        mMediaAdapter?.seekTo(position)
    }


    override fun onPlaybackStateChanged(state : Int) {
        playState = state
        when (state) {
            PlaybackState.STATE_BUFFERING -> {
                mCallback?.setBufferingData(true)
                mCallback?.setVisibilityData(true)
                mCallback?.setPlayStatus(true)
            }

            PlaybackState.STATE_PLAYING -> {
                mCallback?.setBufferingData(false)
                mCallback?.setVisibilityData(true)
                mCallback?.setPlayStatus(true)
            }

            PlaybackState.STATE_PAUSED -> {
                mCallback?.setBufferingData(false)
                mCallback?.setVisibilityData(true)
                mCallback?.setPlayStatus(false)
            }

            else -> {
                mCallback?.setBufferingData(false)
                mCallback?.setVisibilityData(false)
                mCallback?.setPlayStatus(false)
            }
        }
        mNotificationManager?.generateNotification()
    }

    private fun unsubscribeToSongPlayerUpdates(){
        Log.d(TAG, "unsubscribeToSongPlayerUpdates() called")
        removeListener()
    }

    override fun onDestroy() {
        unsubscribeToSongPlayerUpdates()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder{
        val action = intent.action
        command = intent.getStringExtra(CMD_NAME)
        if (ACTION_CMD == action && CMD_PAUSE == command) {
            mMediaAdapter?.pause()
        }
        return binder
    }

    inner class LocalBinder : Binder() {
        // Return this instance of PlayerService so clients can call public methods
        val service: SongPlayerService
            get() = this@SongPlayerService
    }


    companion object {

        private val TAG = SongPlayerService::class.java.name
        const val ACTION_CMD = "app.ACTION_CMD"
        const val CMD_NAME = "CMD_NAME"
        const val CMD_PAUSE = "CMD_PAUSE"
    }
}

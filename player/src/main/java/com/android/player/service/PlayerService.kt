package com.android.player.service

import android.app.Service
import android.content.Intent
import android.media.session.PlaybackState
import android.os.Binder
import android.os.IBinder
import com.android.player.media.MediaAdapter
import com.android.player.media.OnMediaAdapterCallback
import com.android.player.exo.ExoPlayerManager
import com.android.player.model.ASong
import com.android.player.notification.MediaNotificationManager
import java.util.*


class PlayerService : Service(), OnMediaAdapterCallback {


    private var mMediaAdapter: MediaAdapter? = null
    private var mNotificationManager: MediaNotificationManager? = null
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

    override fun onStartCommand(startIntent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    fun addListener(callback: OnPlayerServiceCallback) {
        mCallback = callback
    }

    fun getCurrentSong(): ASong? {
        return mMediaAdapter?.getCurrentSong()
    }

    fun getPlayState(): Int {
        return playState
    }

    fun getCurrentSongList(): ArrayList<ASong>? {
        return mMediaAdapter?.getCurrentSongList()
    }

    override fun onSongChanged(song : ASong) {
        mCallback?.updateSongData(song)
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

    override fun onBind(intent: Intent): IBinder{
        val action = intent.action
        command = intent.getStringExtra(CMD_NAME)
        if (ACTION_CMD == action && CMD_PAUSE == command) {
            mMediaAdapter?.pause()
        }
        return LocalBinder()
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

    fun play(song: ASong) {
        mMediaAdapter?.play(song)
    }

    fun play(songList: MutableList<ASong>) {
        mMediaAdapter?.playSongs(songList)
    }

    fun play(songList: MutableList<ASong>, song: ASong) {
        mMediaAdapter?.play(songList, song)
    }

    fun playOnCurrentPlaylist(song: ASong) {
        mMediaAdapter?.playOnCurrentPlaylist(song)
    }

    override fun addNewPlaylistToCurrent(songList: ArrayList<ASong>) {
        mMediaAdapter?.addToCurrentPlaylist(songList)
    }

    fun pause() {
        mMediaAdapter?.pause()
    }

    fun stop() {
        mMediaAdapter?.stop()
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

    fun clearPlaylist() {
        mMediaAdapter?.clearPlaylist()
    }

    fun seekTo(position: Long) {
        mMediaAdapter?.seekTo(position)
    }

    override fun onSongComplete() {
        mCallback?.onSongEnded()
        //onServiceStop() //it`s optional
    }

    override fun onDestroy() {
        mMediaAdapter?.stop()
        mCallback = null
        super.onDestroy()
    }


    inner class LocalBinder : Binder() {
        // Return this instance of PlayerService so clients can call public methods
        val service: PlayerService
            get() = this@PlayerService
    }


    companion object {

        private val TAG = PlayerService::class.java.name
        const val ACTION_CMD = "app.ACTION_CMD"
        const val CMD_NAME = "CMD_NAME"
        const val CMD_STOP_CASTING = "CMD_STOP_CASTING"
        const val CMD_PAUSE = "CMD_PAUSE"
    }
}

package com.android.player

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import com.android.player.model.ASong
import com.android.player.service.OnPlayerServiceListener
import com.android.player.service.PlayerService


open class BaseSongPlayerActivity : AppCompatActivity(), OnPlayerActionCallback,
    OnPlayerServiceListener {

    private var mService: PlayerService? = null
    private var mBound = false
    val playerViewModel: PlayerViewModel = PlayerViewModel()
    private var mSong: ASong? = null
    private var mSongList: MutableList<ASong>? = null
    private var msg = 0

    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                ACTION_PLAY_SONG_IN_LIST -> mSong?.let { play(mSongList, it) }
                ACTION_PLAY_LIST -> mSongList?.let { play(it) }
                ACTION_PLAY_SONG -> mSong?.let { play(it) }
            }
        }
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to PlayerService, cast the IBinder and get PlayerService instance
            val binder = service as PlayerService.LocalBinder
            mService = binder.service
            mBound = true
            mHandler.sendEmptyMessage(msg)
            mService?.addListener(this@BaseSongPlayerActivity)
        }

        override fun onServiceDisconnected(classname: ComponentName) {
            mBound = false
        }
    }


    override fun onStart() {
        super.onStart()
        playerViewModel.setPlayer(this)
        // Bind to PlayerService
        val intent = Intent(this, PlayerService::class.java)
        startService(intent)
        if (!mBound) bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
    }

    override fun setBufferingData(isBuffering: Boolean) {
        playerViewModel.setBuffering(isBuffering)
    }

    override fun setVisibilityData(isVisibility: Boolean) {
        playerViewModel.setVisibility(isVisibility)
    }

    override fun onDestroy() {
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection)
            mBound = false
        }
        super.onDestroy()
    }

    override fun play(songList: MutableList<ASong>) {
        msg = ACTION_PLAY_LIST
        mSongList = songList
        mService?.playSongs(songList)
    }

    override fun play(song: ASong) {
        msg = ACTION_PLAY_SONG
        mSong = song
        mService?.play(song)
    }

    override fun addToQueue(songList: ArrayList<ASong>) {
        mService?.addToQueue(songList)
    }

    override fun shuffle(isShuffle: Boolean) {
        mService?.onShuffle(isShuffle)
    }

    override fun repeatAll(isRepeatAll: Boolean) {
        mService?.onRepeatAll(isRepeatAll)
    }

    override fun onRepeat(isRepeat: Boolean) {
        mService?.onRepeat(isRepeat)
    }

    override fun updateSongData(song: ASong?) {
        playerViewModel.setData(song)
    }

    override fun setPlay(isPlay: Boolean) {
        playerViewModel.setPlay(isPlay)
    }

    override fun updateSongProgress(duration: Long, position: Long) {
        playerViewModel.setChangePosition(position, duration)
    }

    override fun onSongEnded() {
        playerViewModel.onComplete()
    }

    override fun play(songList: MutableList<ASong>?, song: ASong) {
        msg = ACTION_PLAY_SONG_IN_LIST
        mSong = song
        mSongList = songList
        if (songList.isNullOrEmpty()) play(song)
        else mService?.play(songList, song)
    }

    override fun playOnCurrentQueue(song: ASong) {
        mService?.playOnCurrentQueue(song)
    }

    override fun pause() {
        mService?.pause()
    }

    override fun stop() {
        mService?.stop()
    }

    override fun skipToNext() {
        mService?.skipToNext()
    }

    override fun skipToPrevious() {
        mService?.skipToPrevious()
    }

    override fun seekTo(position: Long?) {
        position?.let { nonNullPosition ->
            mService?.seekTo(nonNullPosition)
        }
    }

    companion object {

        private val TAG = BaseSongPlayerActivity::class.java.name
        const val SONG_LIST_KEY = "SONG_LIST_KEY"
        private const val ACTION_PLAY_SONG = 1
        private const val ACTION_PLAY_LIST = 2
        private const val ACTION_PLAY_SONG_IN_LIST = 3
        private const val ACTION_ADD_TO_QUEUE = 4
        private const val ACTION_PAUSE = 5
        private const val ACTION_STOP = 6
        private const val ACTION_SKIP_TO_NEXT = 7
        private const val ACTION_SKIP_TO_PREVIOUS = 8
        private const val ACTION_SEEK_TO = 9
        private const val ACTION_PLAY_ON_CURRENT_QUEUE = 10
    }
}
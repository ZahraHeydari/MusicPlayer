package com.android.musicplayer.utils.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.musicplayer.data.model.Song
import com.android.musicplayer.utils.player.service.OnPlayServiceConnectionCallback
import com.android.musicplayer.utils.player.service.OnPlayerServiceListener
import com.android.musicplayer.utils.player.service.PlayerService
import kotlin.collections.ArrayList

open class BaseSongPlayerActivity : AppCompatActivity(), OnMusicPlayerActionCallback,
    OnPlayerServiceListener {

    private val TAG = BaseSongPlayerActivity::class.java.name
    private var mService: PlayerService? = null
    private var mBound = false
    private var onPlayerServiceConnectionCallback: OnPlayServiceConnectionCallback? = null
    val playerViewModel: PlayerViewModel = PlayerViewModel()

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to PlayerService, cast the IBinder and get PlayerService instance
            val binder = service as PlayerService.LocalBinder
            mService = binder.service
            mBound = true
            onPlayerServiceConnectionCallback?.onServiceConnected()
            addPlayerServiceListener()
        }

        override fun onServiceDisconnected(classname: ComponentName) {
            mBound = false
        }
    }

    private fun addPlayerServiceListener() {
        mService?.addListener(this)
    }

    private fun registerConnectionCallback(callback: OnPlayServiceConnectionCallback) {
        onPlayerServiceConnectionCallback = callback
        onPlayerServiceConnectionCallback?.onServiceConnected()
    }


    override fun onStart() {
        super.onStart()
        playerViewModel.setPlayer(this)
        // Bind to PlayerService
        val intent = Intent(this, PlayerService::class.java)
        startService(intent)
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
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
            onPlayerServiceConnectionCallback?.onServiceDisconnected()
            unbindService(mConnection)
            mBound = false
        }
        super.onDestroy()
    }

    override fun play(songList: MutableList<ASong>) {
        mService?.playSongs(songList)
    }

    override fun play(song: ASong) {
        mService?.play(song)
    }

    override fun addToQueue(songList: ArrayList<Song>) {
        mService?.addToQueue(songList)
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

    override fun play(songList: MutableList<ASong>, song: ASong) {
        registerConnectionCallback(object : OnPlayServiceConnectionCallback {
            override fun onServiceConnected() {
                mService?.play(songList, song)
            }

            override fun onServiceDisconnected() {
                Log.i(TAG, "onServiceDisconnected()")
            }
        })
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

}
package com.android.musicplayer.utils.player.service

/**
 * This class is interacted with [BaseSongPlayerActivity]
 * to give the state of service connection
 *
 * @author ZARA
 * */
interface OnPlayServiceConnectionCallback {

    fun onServiceConnected()

    fun onServiceDisconnected()
}

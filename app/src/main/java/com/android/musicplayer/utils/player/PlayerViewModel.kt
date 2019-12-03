package com.android.musicplayer.utils.player

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.musicplayer.utils.AppConstants
import com.android.musicplayer.utils.player.model.ASong

class PlayerViewModel : ViewModel() {

    private val TAG = PlayerViewModel::class.java.name
    val playerData = MutableLiveData<ASong>()
    val isVisibleData = MutableLiveData<Boolean>()
    val isBufferingData = MutableLiveData<Boolean>()
    val isPlayData = MutableLiveData<Boolean>()
    val playingPercentData = MutableLiveData<Int>()
    val songDurationTextData = MutableLiveData<String>()
    val songPositionTextData = MutableLiveData<String>()
    val songDurationData = MutableLiveData<Int>()
    val songPositionData = MutableLiveData<Int>()
    val isShuffleData = MutableLiveData<Boolean>()
    val isRepeatData = MutableLiveData<Boolean>()
    private var mNavigator: OnPlayerActionCallback? = null

    val song: ASong?
        get() = playerData.value

    init {
        isPlayData.value = false
        isRepeatData.value = false
        isVisibleData.value = false
    }

    fun setData(song: ASong?) {
        if (song == playerData.value) return
        this.playerData.value = song
        this.isRepeatData.value = false
        mNavigator?.onRepeat(false)
        songPositionTextData.value = AppConstants.formatTimeInMillisToString(0)
        songPositionData.value = 0
        songDurationTextData.value = AppConstants.formatTimeInMillisToString(0)
        songDurationData.value = 0
    }

    fun shuffle() {
        isShuffleData.value = isShuffleData.value != true
        mNavigator?.shuffle(isShuffleData.value ?: false)
    }

    fun repeat() {
        isRepeatData.value = isRepeatData.value != true
        mNavigator?.onRepeat(isRepeatData.value ?: false)
    }

    fun setPlayer(onAudioPlayerActionCallback: OnPlayerActionCallback) {
        this.mNavigator = onAudioPlayerActionCallback
    }

    fun setPlay(play: Boolean) {
        isPlayData.value = play
    }

    fun setVisibility(isVisible: Boolean) {
        this.isVisibleData.value = isVisible
    }

    fun setBuffering(isBuffering: Boolean) {
        this.isBufferingData.value = isBuffering
    }

    fun play() {
        if (isPlayData.value == true) {
            mNavigator?.pause()
        } else {
            playerData.value?.let {
                mNavigator?.playOnCurrentQueue(it)
            }
        }
    }

    fun pause() {
        this.playerData.value?.setPlay(false)
        isPlayData.value = false
        mNavigator?.pause()
    }

    fun play(song: ASong?) {
        song?.let {
            mNavigator?.play(it)
        }
    }

    fun play(songList: MutableList<ASong>?, song: ASong?) {
        song?.let { nonNullSong ->
            songList?.let { nonNullSongList ->
                mNavigator?.play(nonNullSongList, nonNullSong)
            }
        }
    }

    fun playOnCurrentQueue(song: ASong?) {
        song?.let { nonNullSong ->
            mNavigator?.playOnCurrentQueue(nonNullSong)
        }
    }

    fun next() {
        mNavigator?.skipToNext()
    }

    fun previous() {
        mNavigator?.skipToPrevious()
    }

    fun seekTo(position: Long) {
        songPositionTextData.value = AppConstants.formatTimeInMillisToString(position)
        songPositionData.value = position.toInt()
        mNavigator?.seekTo(position)
    }

    fun stop() {
        songPositionData.value = 0
        songPositionTextData.value =
            AppConstants.formatTimeInMillisToString(songPositionData.value?.toLong() ?: 0)
        isPlayData.value = false
        this.playerData.value?.setPlay(false)
        mNavigator?.stop()
        isVisibleData.value = false
    }

    fun setPlayingPercent(playingPercent: Int) {
        if (this.playingPercentData.value == 100) return
        this.playingPercentData.value = playingPercent
    }

    fun setChangePosition(currentPosition: Long, duration: Long) {
        Log.i(TAG, "currentPosition: $currentPosition >>>>> duration: $duration")
        if (currentPosition > duration) return
        songPositionTextData.value = AppConstants.formatTimeInMillisToString(currentPosition)
        songPositionData.value = currentPosition.toInt()

        val durationText = AppConstants.formatTimeInMillisToString(duration)
        if (!songDurationTextData.value.equals(durationText)) {
            songDurationTextData.value = durationText
            songDurationData.value = duration.toInt()
        }
    }

    fun onComplete() {
        songPositionTextData.value = songDurationTextData.value
    }

}
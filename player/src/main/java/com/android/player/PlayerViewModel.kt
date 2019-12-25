package com.android.player

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.player.model.ASong
import com.android.player.utils.AppConstants


class PlayerViewModel : ViewModel() {

    private val playerData = MutableLiveData<ASong>()
    private val isVisibleData = MutableLiveData<Boolean>()
    private val isBufferingData = MutableLiveData<Boolean>()
    private val isPlayData = MutableLiveData<Boolean>()
    private val playingPercentData = MutableLiveData<Int>()
    private val songDurationTextData = MutableLiveData<String>()
    private val songPositionTextData = MutableLiveData<String>()
    private val songDurationData = MutableLiveData<Int>()
    private val songPositionData = MutableLiveData<Int>()
    private val isShuffleData = MutableLiveData<Boolean>()
    private val isRepeatAllData = MutableLiveData<Boolean>()
    private val isRepeatData = MutableLiveData<Boolean>()
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

    fun repeatAll() {
        isRepeatAllData.value = isRepeatAllData.value != true
        mNavigator?.repeatAll(isRepeatAllData.value ?: false)
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


    fun getSongDurationTextData():MutableLiveData<String>{
        return songDurationTextData
    }

    fun getSongDurationData():MutableLiveData<Int>{
        return songDurationData
    }

    fun getSongPositionTextData():MutableLiveData<String>{
        return songPositionTextData
    }

    fun getSongPositionData():MutableLiveData<Int>{
        return songPositionData
    }

    fun getRepeatData():MutableLiveData<Boolean>{
        return isRepeatData
    }

    fun getShuffleData():MutableLiveData<Boolean>{
        return isShuffleData
    }

    fun getPlayingData():MutableLiveData<Boolean>{
        return isPlayData
    }

    fun getPlayerData():MutableLiveData<ASong>{
        return playerData
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

    companion object{

        private val TAG = PlayerViewModel::class.java.name
    }

}
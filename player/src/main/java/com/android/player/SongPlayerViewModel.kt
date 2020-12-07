package com.android.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.player.model.ASong
import com.android.player.util.formatTimeInMillisToString


class SongPlayerViewModel : ViewModel() {

    private val _playerData = MutableLiveData<ASong>()
    val playerData: LiveData<ASong> = _playerData
    private val _isVisibleData = MutableLiveData<Boolean>()
    val isVisibleData: LiveData<Boolean> = _isVisibleData
    private val _isBufferingData = MutableLiveData<Boolean>()
    val isBufferingData: LiveData<Boolean> = _isBufferingData
    private val _isPlayData = MutableLiveData<Boolean>()
    val isPlayData: LiveData<Boolean> = _isPlayData
    private val _playingPercentData = MutableLiveData<Int>()
    val playingPercentData: LiveData<Int> = _playingPercentData
    private val _songDurationTextData = MutableLiveData<String>()
    val songDurationTextData: LiveData<String> = _songDurationTextData
    private val _songPositionTextData = MutableLiveData<String>()
    val songPositionTextData: LiveData<String> = _songPositionTextData
    private val _songDurationData = MutableLiveData<Int>()
    val songDurationData: LiveData<Int> = _songDurationData
    private val _songPositionData = MutableLiveData<Int>()
    val songPositionData: LiveData<Int> = _songPositionData
    private val _isShuffleData = MutableLiveData<Boolean>()
    val isShuffleData: LiveData<Boolean> = _isShuffleData
    private val _isRepeatAllData = MutableLiveData<Boolean>()
    val isRepeatAllData: LiveData<Boolean> = _isRepeatAllData
    private val _isRepeatData = MutableLiveData<Boolean>()
    val isRepeatData: LiveData<Boolean> = _isRepeatData

    val song: ASong?
        get() = _playerData.value

    init {
        _isPlayData.value = false
        _isRepeatData.value = false
        _isVisibleData.value = false
    }

    fun updateSong(song : ASong){
        _playerData.value = song
    }

    fun setData(song: ASong?) {
        if (song == _playerData.value) return
        this._playerData.value = song
        this._isRepeatData.value = false
        _songPositionTextData.value = formatTimeInMillisToString(0)
        _songPositionData.value = 0
        _songDurationTextData.value = formatTimeInMillisToString(0)
        _songDurationData.value = 0
    }

    fun shuffle() {
        _isShuffleData.value = _isShuffleData.value != true
    }

    fun repeatAll() {
        _isRepeatAllData.value = _isRepeatAllData.value != true
    }

    fun repeat() {
        _isRepeatData.value = _isRepeatData.value != true
    }

    fun setVisibility(isVisible: Boolean) {
        this._isVisibleData.value = isVisible
    }

    fun setBuffering(isBuffering: Boolean) {
        this._isBufferingData.value = isBuffering
    }

    fun setPlayStatus(playStatus : Boolean){
        _isPlayData.value = playStatus
    }

    fun seekTo(position: Long) {
        _songPositionTextData.value = formatTimeInMillisToString(position)
        _songPositionData.value = position.toInt()
    }

    fun stop() {
        setPlayStatus(false)
        _songPositionData.value = 0
        _songPositionTextData.value = formatTimeInMillisToString(_songPositionData.value?.toLong() ?: 0)
        _isVisibleData.value = false
    }

    fun setPlayingPercent(playingPercent: Int) {
        if (this._playingPercentData.value == 100) return
        this._playingPercentData.value = playingPercent
    }

    fun setChangePosition(currentPosition: Long, duration: Long) {
        if (currentPosition > duration) return
        _songPositionTextData.value = formatTimeInMillisToString(currentPosition)
        _songPositionData.value = currentPosition.toInt()

        val durationText = formatTimeInMillisToString(duration)
        if (!_songDurationTextData.value.equals(durationText)) {
            _songDurationTextData.value = durationText
            _songDurationData.value = duration.toInt()
        }
    }


    companion object {

        private val TAG = SongPlayerViewModel::class.java.name
        private var mInstance: SongPlayerViewModel? = null

        @Synchronized
        fun getPlayerViewModelInstance(): SongPlayerViewModel {
            if (mInstance == null) {
                mInstance = SongPlayerViewModel()
            }
            return mInstance as SongPlayerViewModel
        }
    }

}
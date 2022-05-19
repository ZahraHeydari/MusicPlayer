package com.android.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.player.util.formatTimeInMillisToString
import com.google.android.exoplayer2.MediaItem

class SongPlayerViewModel : ViewModel() {

    private val _mediaItemData = MutableLiveData<MediaItem>()
    val mediaItemData: LiveData<MediaItem> = _mediaItemData
    private val _isPlayingData = MutableLiveData<Boolean>()
    val isPlayingData: LiveData<Boolean> = _isPlayingData
    private val _songDurationTextData = MutableLiveData<String>()
    val songDurationTextData: LiveData<String> = _songDurationTextData
    private val _songDurationData = MutableLiveData<Int>()
    val songDurationData: LiveData<Int> = _songDurationData
    private val _songPositionTextData = MutableLiveData<String>()
    val songPositionTextData: LiveData<String> = _songPositionTextData
    private val _songPositionData = MutableLiveData<Long>()
    val songPositionData: LiveData<Long> = _songPositionData
    private val _isShuffleData = MutableLiveData<Boolean>()
    val isShuffleData: LiveData<Boolean> = _isShuffleData
    private val _isRepeatAllData = MutableLiveData<Boolean>()
    val isRepeatAllData: LiveData<Boolean> = _isRepeatAllData
    private val _isRepeatData = MutableLiveData<Boolean>()
    val isRepeatData: LiveData<Boolean> = _isRepeatData

    fun updateMediaItem(mediaItem: MediaItem?){
        _mediaItemData.value = mediaItem
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

    fun setPlayingStatus(playStatus : Boolean){
        _isPlayingData.value = playStatus
    }

    fun stop() {
        _songPositionData.value = 0
        _songPositionTextData.value = formatTimeInMillisToString(_songPositionData.value?.toLong() ?: 0)
    }

    fun setChangePosition(currentPosition: Long, duration: Long) {
        _songPositionTextData.value = formatTimeInMillisToString(currentPosition)
        _songPositionData.value = currentPosition

        if(_songDurationTextData.value != formatTimeInMillisToString(duration)) {
            _songDurationTextData.value = formatTimeInMillisToString(duration)
            _songDurationData.value = duration.toInt()
        }
    }

    companion object {
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
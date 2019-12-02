package com.android.musicplayer.utils.player

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.musicplayer.utils.player.queue.QueueEntity
import com.android.musicplayer.utils.AppConstants
import com.android.musicplayer.utils.player.model.ASong

class PlayerViewModel : ViewModel() {

    val playerdata = MutableLiveData<ASong>()
    val isVisible = MutableLiveData<Boolean>()
    val isBuffering = MutableLiveData<Boolean>()
    val isPlay = MutableLiveData<Boolean>()
    val playingPercent = MutableLiveData<Int>()
    val songDurationText = MutableLiveData<String>()
    val songPositionText = MutableLiveData<String>()
    val songDuration = MutableLiveData<Int>()
    val songPosition = MutableLiveData<Int>()
    val queue = MutableLiveData<QueueEntity>()
    val shuffle = MutableLiveData<Boolean>()
    val repeat = MutableLiveData<Boolean>()
    private var mNavigator: OnPlayerActionCallback? = null

    val song: ASong?
        get() = playerdata.value

    init {
        isPlay.value = false
        isVisible.value = false
    }

    fun setData(song: ASong?) {
        if (song == null || song == playerdata.value) return
        this.playerdata.value = song
        songPositionText.value = AppConstants.formatMillis(0)
        songPosition.value = 0
        songDurationText.value = AppConstants.formatMillis(0)
        songDuration.value = 0
    }

    fun shuffle() {
        shuffle.value = shuffle.value
        this.queue.value?.isShuffle = shuffle.value ?: false
    }

    fun repeat() {
        repeat.value = repeat.value
        this.queue.value?.isRepeat = repeat.value ?: false
    }

    fun setQueue(queue: QueueEntity) {
        this.queue.value = queue
        this.shuffle.value = queue.isShuffle
        this.repeat.value = queue.isRepeat
    }

    fun setPlayer(onAudioPlayerActionCallback: OnPlayerActionCallback) {
        this.mNavigator = onAudioPlayerActionCallback
    }

    fun setPlay(play: Boolean) {
        isPlay.value = play
    }

    fun setVisibility(isVisible: Boolean) {
        this.isVisible.value = isVisible
    }

    fun setBuffering(isBuffering: Boolean) {
        this.isBuffering.value = isBuffering
    }

    fun play() {
        if (isPlay.value == true) {
            mNavigator?.pause()
        } else {
            playerdata.value?.let {
                mNavigator?.playOnCurrentQueue(it)
            }
        }
    }

    fun pause() {
        this.playerdata.value?.setPlay(false)
        isPlay.value = false
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
        songPositionText.value = AppConstants.formatMillis(position)
        songPosition.value = position.toInt()
        mNavigator?.seekTo(position)
    }

    fun stop() {
        songPosition.value = 0
        songPositionText.value = AppConstants.formatMillis(songPosition.value?.toLong() ?: 0)
        isPlay.value = false
        this.playerdata.value?.setPlay(false)
        mNavigator?.stop()
        isVisible.value = false
    }

    fun setPlayingPercent(playingPercent: Int) {
        if (this.playingPercent.value == 100) return
        this.playingPercent.value = playingPercent
    }

    fun setChangePosition(currentPosition: Long, duration: Long) {
        if (currentPosition > duration) return
        songPositionText.value = AppConstants.formatMillis(currentPosition)
        songPosition.value = currentPosition.toInt()
        val durationText = AppConstants.formatMillis(duration)

        if (!songDurationText.value.equals(durationText)) {
            songDurationText.value = durationText
            songDuration.value = duration.toInt()
        }
    }

}
package com.android.player.exo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.annotation.Nullable
import com.android.player.service.SongPlayerService
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.C.CONTENT_TYPE_MUSIC
import com.google.android.exoplayer2.C.USAGE_MEDIA
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.Player.MediaItemTransitionReason

/**
 * This class is responsible for managing the player(actions, state, ...) using [ExoPlayer]
 *
 *
 * @author ZARA
 * */
class ExoPlayerManager(
    val context: Context,
    private val callback: OnExoPlayerManagerCallback
) : Player.Listener {

    private val mAudioNoisyIntentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    private var mWifiLock: WifiManager.WifiLock? = null
    private var mAudioManager: AudioManager? = null
    private var mPlayOnFocusGain: Boolean = false
    private var mAudioNoisyReceiverRegistered: Boolean = false
    private var mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK
    private var playWhenReady = true
    private var currentItem = 0
    private var playbackPosition = 0L
    private var player: ExoPlayer? = null

    private val mAudioNoisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY == intent.action) {
                if (mPlayOnFocusGain || player != null && player?.playWhenReady == true) {
                    val i = Intent(context, SongPlayerService::class.java).apply {
                        action = SongPlayerService.ACTION_CMD
                        putExtra(SongPlayerService.CMD_NAME, SongPlayerService.CMD_PAUSE)
                    }
                    context.applicationContext.startService(i)
                }
            }
        }
    }

    private val mUpdateProgressHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val duration = player?.duration ?: 0
            val position = player?.currentPosition ?: 0
            callback.onUpdateProgress(duration, position)
            sendEmptyMessageDelayed(0, UPDATE_PROGRESS_DELAY)
        }
    }

    // Whether to return STATE_NONE or STATE_STOPPED when mExoPlayer is null
    private var mExoPlayerIsStopped = false
    private val mOnAudioFocusChangeListener =
        AudioManager.OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> mCurrentAudioFocusState = AUDIO_FOCUSED
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK ->
                    // Audio focus was lost, but it's possible to duck (i.e.: play quietly)
                    mCurrentAudioFocusState = AUDIO_NO_FOCUS_CAN_DUCK
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    // Lost audio focus, but will gain it back (shortly), so note whether
                    // playback should resume
                    mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK
                    mPlayOnFocusGain = player != null && player?.playWhenReady ?: false
                }
                AudioManager.AUDIOFOCUS_LOSS ->
                    // Lost audio focus, probably "permanently"
                    mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK
            }
            // Update the player state based on the change
            configurePlayerState()
        }

    init {
        this.mAudioManager =
            context.applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        // Create the Wifi lock (this does not acquire the lock, this just creates it)
        this.mWifiLock =
            (context.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager)
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "app_lock")
        initializePlayer()
    }

    private fun initializePlayer() {
        if (player == null) {
            player = ExoPlayer.Builder(context.applicationContext).build()
            player?.addListener(this)
        }
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        player?.repeatMode = repeatMode
    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        player?.shuffleModeEnabled = shuffleModeEnabled
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            Player.STATE_IDLE, Player.STATE_BUFFERING, Player.STATE_READY -> {
                mUpdateProgressHandler.sendEmptyMessage(0)
            }
            Player.STATE_ENDED -> {
                // The media player finished playing the current song.
                mUpdateProgressHandler.removeMessages(0)
            }
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        callback.onIsPlayingChanged(isPlaying)
    }

    private fun tryToGetAudioFocus() {
        val result = mAudioManager?.requestAudioFocus(
            mOnAudioFocusChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        mCurrentAudioFocusState = if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            AUDIO_FOCUSED
        } else {
            AUDIO_NO_FOCUS_NO_DUCK
        }
    }

    private fun giveUpAudioFocus() {
        if (mAudioManager?.abandonAudioFocus(mOnAudioFocusChangeListener) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK
        }
    }

    /**
     * Reconfigures the player according to audio focus settings and starts/restarts it. This method
     * starts/restarts the ExoPlayer instance respecting the current audio focus state. So if we
     * have focus, it will play normally; if we don't have focus, it will either leave the player
     * paused or set it to a low volume, depending on what is permitted by the current focus
     * settings.
     */
    private fun configurePlayerState() {
        if (mCurrentAudioFocusState == AUDIO_NO_FOCUS_NO_DUCK) {
            // We don't have audio focus and can't duck, so we have to pause
            pause()
        } else {
            registerAudioNoisyReceiver()

            if (mCurrentAudioFocusState == AUDIO_NO_FOCUS_CAN_DUCK)
            // We're permitted to play, but only if we 'duck', ie: play softly
                player?.volume = VOLUME_DUCK
            else
                player?.volume = VOLUME_NORMAL

            // If we were playing when we lost focus, we need to resume playing.
            if (mPlayOnFocusGain) {
                player?.playWhenReady = true
                mPlayOnFocusGain = false
            }
        }
    }

    /**
     * Releases resources used by the service for playback, which is mostly just the WiFi lock for
     * local playback. If requested, the ExoPlayer instance is also released.
     *
     * @param releasePlayer Indicates whether the player should also be released
     */
    private fun releaseResources(releasePlayer: Boolean) {
        // Stops and releases player (if requested and available).
        if (releasePlayer) {
            player?.let { exoPlayer ->
                playbackPosition = exoPlayer.currentPosition
                currentItem = exoPlayer.currentMediaItemIndex
                playWhenReady = exoPlayer.playWhenReady
                exoPlayer.release()
                exoPlayer.removeListener(this)
            }
            mUpdateProgressHandler.removeMessages(0)
            mExoPlayerIsStopped = true
            mPlayOnFocusGain = false
            player = null
        }
        if (mWifiLock?.isHeld == true) {
            mWifiLock?.release()
        }
    }

    private fun registerAudioNoisyReceiver() {
        if (!mAudioNoisyReceiverRegistered) {
            context.applicationContext.registerReceiver(
                mAudioNoisyReceiver,
                mAudioNoisyIntentFilter
            )
            mAudioNoisyReceiverRegistered = true
        }
    }

    private fun unregisterAudioNoisyReceiver() {
        if (mAudioNoisyReceiverRegistered) {
            context.applicationContext.unregisterReceiver(mAudioNoisyReceiver)
            mAudioNoisyReceiverRegistered = false
        }
    }

    fun play(mediaItem: MediaItem) {
        mPlayOnFocusGain = true
        tryToGetAudioFocus()
        registerAudioNoisyReceiver()
        releaseResources(false) // release everything except the player
        initializePlayer()

        // Android "O" makes much greater use of AudioAttributes, especially
        // with regards to AudioFocus. All of tracks are music, but
        // if your content includes spoken word such as audio books or pod casts
        // then the content type should be set to CONTENT_TYPE_SPEECH for those
        // tracks.
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(CONTENT_TYPE_MUSIC)
            .setUsage(USAGE_MEDIA)
            .build()
        player?.let {
            it.setAudioAttributes(audioAttributes, false)
            it.setMediaItem(mediaItem)
            it.prepare()
            it.play()
        }
        // If we are streaming from the internet, we want to hold a
        // Wifi lock, which prevents the Wifi radio from going to
        // sleep while the song is playing.
        mWifiLock?.acquire()
        configurePlayerState()
    }

    fun play() {
        player?.let {
            if (!it.isPlaying) player?.play()
        }
    }

    fun pause() {
        player?.playWhenReady = false
        // While paused, retain the player instance, but give up audio focus.
        releaseResources(false)
        unregisterAudioNoisyReceiver()
    }

    fun stop() {
        giveUpAudioFocus()
        releaseResources(true)
        unregisterAudioNoisyReceiver()
    }

    fun seekTo(position: Long) {
        registerAudioNoisyReceiver()
        player?.seekTo(position)
    }

    fun skipPosition(position: Long) {
        player?.let {
            if (position == 1L && it.hasNextMediaItem()) {
                it.seekToNextMediaItem()
            } else if (position == -1L && it.hasPreviousMediaItem()) {
                it.seekToPreviousMediaItem()
            }
        }
    }

    fun toggle() {
        if (player?.isPlaying == true) player?.pause() else player?.play()
    }

    fun getCurrentMediaItem(): MediaItem? {
        return player?.currentMediaItem
    }

    fun setPlaylist(mediaItems: MutableList<MediaItem>) {
        mediaItems.forEach {
            player?.addMediaItem(it)
        }
    }

    fun hasNext(): Boolean {
        return player?.hasNextMediaItem() ?: false
    }

    override fun onMediaItemTransition(
        @Nullable mediaItem: MediaItem?,
        reason: @MediaItemTransitionReason Int
    ) {
        callback.updateUiForPlayingMediaItem(mediaItem)
    }

    companion object {
        const val UPDATE_PROGRESS_DELAY = 500L

        // The volume we set the media player to when we lose audio focus, but are
        // allowed to reduce the volume instead of stopping playback.
        private const val VOLUME_DUCK = 0.2f

        // The volume we set the media player when we have audio focus.
        private const val VOLUME_NORMAL = 1.0f

        // we don't have audio focus, and can't duck (play at a low volume)
        private const val AUDIO_NO_FOCUS_NO_DUCK = 0

        // we don't have focus, but can duck (play at a low volume)
        private const val AUDIO_NO_FOCUS_CAN_DUCK = 1

        // we have full audio focus
        private const val AUDIO_FOCUSED = 2
    }
}
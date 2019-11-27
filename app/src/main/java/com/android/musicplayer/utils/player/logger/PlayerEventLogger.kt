package com.android.musicplayer.utils.player.logger

import android.os.SystemClock
import android.util.Log
import android.view.Surface
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioRendererEventListener
import com.google.android.exoplayer2.decoder.DecoderCounters
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.metadata.MetadataRenderer
import com.google.android.exoplayer2.metadata.emsg.EventMessage
import com.google.android.exoplayer2.metadata.id3.*
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.video.VideoRendererEventListener
import java.io.IOException
import java.text.NumberFormat
import java.util.*

class PlayerEventLogger(private val trackSelector: MappingTrackSelector) : ExoPlayer.EventListener,
    AudioRendererEventListener, VideoRendererEventListener, AdaptiveMediaSourceEventListener,
    ExtractorMediaSource.EventListener, DefaultDrmSessionManager.EventListener,
    MetadataRenderer.Output {
    private val window: Timeline.Window
    private val period: Timeline.Period
    private val startTimeMs: Long

    private val sessionTimeString: String
        get() = getTimeString(SystemClock.elapsedRealtime() - startTimeMs)

    init {
        window = Timeline.Window()
        period = Timeline.Period()
        startTimeMs = SystemClock.elapsedRealtime()
    }

    // MetadataRenderer.Output

    override fun onLoadingChanged(isLoading: Boolean) {
        Log.d(TAG, "loading [$isLoading]")
    }

    // AudioRendererEventListener

    override fun onPlayerStateChanged(playWhenReady: Boolean, state: Int) {
        Log.d(
            TAG, "state [" + sessionTimeString + ", " + playWhenReady + ", "
                    + getStateString(state) + "]"
        )
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        Log.d(TAG, "onRepeatModeChanged() called with: repeatMode = [$repeatMode]")
    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

    }


    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
        Log.d(
            TAG,
            "onPlaybackParametersChanged() called with: playbackParameters = [$playbackParameters]"
        )
    }

    override fun onSeekProcessed() {

    }

    override fun onPlayerError(e: ExoPlaybackException?) {
        Log.e(TAG, "playerFailed [$sessionTimeString]", e)
    }

    override fun onPositionDiscontinuity(reason: Int) {

    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
        if (timeline == null) {
            return
        }
        val periodCount = timeline.periodCount
        val windowCount = timeline.windowCount
        Log.d(TAG, "sourceInfo [periodCount=$periodCount, windowCount=$windowCount")
        for (i in 0 until Math.min(periodCount, MAX_TIMELINE_ITEM_LINES)) {
            timeline.getPeriod(i, period)
            Log.d(TAG, "  " + "period [" + getTimeString(period.durationMs) + "]")
        }
        if (periodCount > MAX_TIMELINE_ITEM_LINES) {
            Log.d(TAG, "  ...")
        }
        for (i in 0 until Math.min(windowCount, MAX_TIMELINE_ITEM_LINES)) {
            timeline.getWindow(i, window)
            Log.d(
                TAG, "  " + "window [" + getTimeString(window.durationMs) + ", "
                        + window.isSeekable + ", " + window.isDynamic + "]"
            )
        }
        if (windowCount > MAX_TIMELINE_ITEM_LINES) {
            Log.d(TAG, "  ...")
        }
        Log.d(TAG, "]")
    }

    override fun onTracksChanged(ignored: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
        val mappedTrackInfo = trackSelector.currentMappedTrackInfo
        if (mappedTrackInfo == null) {
            Log.d(TAG, "Tracks []")
            return
        }
        Log.d(TAG, "Tracks [")
        // Log tracks associated to renderers.
        for (rendererIndex in 0 until mappedTrackInfo.length) {
            val rendererTrackGroups = mappedTrackInfo.getTrackGroups(rendererIndex)
            val trackSelection = trackSelections!!.get(rendererIndex)
            if (rendererTrackGroups.length > 0) {
                Log.d(TAG, "  Renderer:$rendererIndex [")
                for (groupIndex in 0 until rendererTrackGroups.length) {
                    val trackGroup = rendererTrackGroups.get(groupIndex)
                    val adaptiveSupport = getAdaptiveSupportString(
                        trackGroup.length,
                        mappedTrackInfo.getAdaptiveSupport(rendererIndex, groupIndex, false)
                    )
                    Log.d(TAG, "    Group:$groupIndex, adaptive_supported=$adaptiveSupport [")
                    for (trackIndex in 0 until trackGroup.length) {
                        val status = getTrackStatusString(trackSelection, trackGroup, trackIndex)
                        val formatSupport = getFormatSupportString(
                            mappedTrackInfo.getTrackFormatSupport(
                                rendererIndex,
                                groupIndex,
                                trackIndex
                            )
                        )
                        Log.d(
                            TAG, "      " + status + " Track:" + trackIndex + ", "
                                    + Format.toLogString(trackGroup.getFormat(trackIndex))
                                    + ", supported=" + formatSupport
                        )
                    }
                    Log.d(TAG, "    ]")
                }
                // Log metadata for at most one of the tracks selected for the renderer.
                if (trackSelection != null) {
                    for (selectionIndex in 0 until trackSelection.length()) {
                        val metadata = trackSelection.getFormat(selectionIndex).metadata
                        if (metadata != null) {
                            Log.d(TAG, "    Metadata [")
                            printMetadata(metadata, "      ")
                            Log.d(TAG, "    ]")
                            break
                        }
                    }
                }
                Log.d(TAG, "  ]")
            }
        }
        // Log tracks not associated with a renderer.
        val unassociatedTrackGroups = mappedTrackInfo.unassociatedTrackGroups
        if (unassociatedTrackGroups.length > 0) {
            Log.d(TAG, "  Renderer:None [")
            for (groupIndex in 0 until unassociatedTrackGroups.length) {
                Log.d(TAG, "    Group:$groupIndex [")
                val trackGroup = unassociatedTrackGroups.get(groupIndex)
                for (trackIndex in 0 until trackGroup.length) {
                    val status = getTrackStatusString(false)
                    val formatSupport = getFormatSupportString(
                        RendererCapabilities.FORMAT_UNSUPPORTED_TYPE
                    )
                    Log.d(
                        TAG, "      " + status + " Track:" + trackIndex + ", "
                                + Format.toLogString(trackGroup.getFormat(trackIndex))
                                + ", supported=" + formatSupport
                    )
                }
                Log.d(TAG, "    ]")
            }
            Log.d(TAG, "  ]")
        }
        Log.d(TAG, "]")
    }

    override fun onMetadata(metadata: Metadata) {
        Log.d(TAG, "onMetadata [")
        printMetadata(metadata, "  ")
        Log.d(TAG, "]")
    }

    // VideoRendererEventListener

    override fun onAudioEnabled(counters: DecoderCounters) {
        Log.d(TAG, "audioEnabled [$sessionTimeString]")
    }

    override fun onAudioSessionId(audioSessionId: Int) {
        Log.d(TAG, "audioSessionId [$audioSessionId]")
    }

    override fun onAudioDecoderInitialized(
        decoderName: String, elapsedRealtimeMs: Long,
        initializationDurationMs: Long
    ) {
        Log.d(TAG, "audioDecoderInitialized [$sessionTimeString, $decoderName]")
    }

    override fun onAudioInputFormatChanged(format: Format) {
        Log.d(
            TAG, "audioFormatChanged [" + sessionTimeString + ", " + Format.toLogString(format)
                    + "]"
        )
    }

    override fun onAudioSinkUnderrun(
        bufferSize: Int,
        bufferSizeMs: Long,
        elapsedSinceLastFeedMs: Long
    ) {

    }

    override fun onAudioDisabled(counters: DecoderCounters) {
        Log.d(TAG, "audioDisabled [$sessionTimeString]")
    }

    override fun onVideoEnabled(counters: DecoderCounters) {
        Log.d(TAG, "videoEnabled [$sessionTimeString]")
    }

    // DefaultDrmSessionManager.EventListener

    override fun onVideoDecoderInitialized(
        decoderName: String,
        elapsedRealtimeMs: Long,
        initializationDurationMs: Long
    ) {
        Log.d(TAG, "videoDecoderInitialized [$sessionTimeString, $decoderName]")
    }

    override fun onVideoInputFormatChanged(format: Format) {
        Log.d(
            TAG, "videoFormatChanged [" + sessionTimeString + ", " + Format.toLogString(format)
                    + "]"
        )
    }

    override fun onVideoDisabled(counters: DecoderCounters) {
        Log.d(TAG, "videoDisabled [$sessionTimeString]")
    }

    override fun onDroppedFrames(count: Int, elapsed: Long) {
        Log.d(TAG, "droppedFrames [$sessionTimeString, $count]")
    }

    // ExtractorMediaSource.EventListener

    override fun onVideoSizeChanged(
        width: Int, height: Int, unappliedRotationDegrees: Int,
        pixelWidthHeightRatio: Float
    ) {
        // Do nothing.
    }

    // AdaptiveMediaSourceEventListener

    override fun onRenderedFirstFrame(surface: Surface?) {
        // Do nothing.
    }

    override fun onDrmSessionManagerError(e: Exception) {
        printInternalError("drmSessionManagerError", e)
    }

    override fun onDrmKeysRestored() {
        Log.d(TAG, "drmKeysRestored [$sessionTimeString]")
    }

    override fun onDrmKeysRemoved() {
        Log.d(TAG, "drmKeysRemoved [$sessionTimeString]")
    }

    override fun onDrmKeysLoaded() {
        Log.d(TAG, "drmKeysLoaded [$sessionTimeString]")
    }

    override fun onLoadError(error: IOException) {
        printInternalError("loadError", error)
    }


    private fun printInternalError(type: String, e: Exception) {
        Log.e(TAG, "internalError [$sessionTimeString, $type]", e)
    }

    private fun printMetadata(metadata: Metadata, prefix: String) {
        for (i in 0 until metadata.length()) {
            val entry = metadata.get(i)
            if (entry is TextInformationFrame) {
                Log.d(
                    TAG, prefix + String.format(
                        "%s: value=%s", entry.id,
                        entry.value
                    )
                )
            } else if (entry is UrlLinkFrame) {
                Log.d(TAG, prefix + String.format("%s: url=%s", entry.id, entry.url))
            } else if (entry is PrivFrame) {
                Log.d(TAG, prefix + String.format("%s: owner=%s", entry.id, entry.owner))
            } else if (entry is GeobFrame) {
                Log.d(
                    TAG, prefix + String.format(
                        "%s: mimeType=%s, filename=%s, description=%s",
                        entry.id, entry.mimeType, entry.filename, entry.description
                    )
                )
            } else if (entry is ApicFrame) {
                Log.d(
                    TAG, prefix + String.format(
                        "%s: mimeType=%s, description=%s",
                        entry.id, entry.mimeType, entry.description
                    )
                )
            } else if (entry is CommentFrame) {
                Log.d(
                    TAG, prefix + String.format(
                        "%s: language=%s, description=%s", entry.id,
                        entry.language, entry.description
                    )
                )
            } else if (entry is Id3Frame) {
                Log.d(TAG, prefix + String.format("%s", entry.id))
            } else if (entry is EventMessage) {
                Log.d(
                    TAG, prefix + String.format(
                        "EMSG: scheme=%s, id=%d, value=%s",
                        entry.schemeIdUri, entry.id, entry.value
                    )
                )
            }
        }
    }

    override fun onMediaPeriodCreated(windowIndex: Int, mediaPeriodId: MediaSource.MediaPeriodId) {

    }

    override fun onMediaPeriodReleased(windowIndex: Int, mediaPeriodId: MediaSource.MediaPeriodId) {

    }

    override fun onLoadStarted(
        windowIndex: Int, mediaPeriodId: MediaSource.MediaPeriodId?,
        loadEventInfo: MediaSourceEventListener.LoadEventInfo,
        mediaLoadData: MediaSourceEventListener.MediaLoadData
    ) {

    }

    override fun onLoadCompleted(
        windowIndex: Int, mediaPeriodId: MediaSource.MediaPeriodId?,
        loadEventInfo: MediaSourceEventListener.LoadEventInfo,
        mediaLoadData: MediaSourceEventListener.MediaLoadData
    ) {

    }

    override fun onLoadCanceled(
        windowIndex: Int, mediaPeriodId: MediaSource.MediaPeriodId?,
        loadEventInfo: MediaSourceEventListener.LoadEventInfo,
        mediaLoadData: MediaSourceEventListener.MediaLoadData
    ) {

    }

    override fun onLoadError(
        windowIndex: Int, mediaPeriodId: MediaSource.MediaPeriodId?,
        loadEventInfo: MediaSourceEventListener.LoadEventInfo,
        mediaLoadData: MediaSourceEventListener.MediaLoadData,
        error: IOException,
        wasCanceled: Boolean
    ) {

    }

    override fun onReadingStarted(windowIndex: Int, mediaPeriodId: MediaSource.MediaPeriodId) {

    }

    override fun onUpstreamDiscarded(
        windowIndex: Int,
        mediaPeriodId: MediaSource.MediaPeriodId,
        mediaLoadData: MediaSourceEventListener.MediaLoadData
    ) {

    }

    override fun onDownstreamFormatChanged(
        windowIndex: Int, mediaPeriodId: MediaSource.MediaPeriodId?,
        mediaLoadData: MediaSourceEventListener.MediaLoadData
    ) {

    }

    companion object {

        private val TAG = "EventLogger"
        private val MAX_TIMELINE_ITEM_LINES = 3
        private val TIME_FORMAT: NumberFormat

        init {
            TIME_FORMAT = NumberFormat.getInstance(Locale.US)
            TIME_FORMAT.minimumFractionDigits = 2
            TIME_FORMAT.maximumFractionDigits = 2
            TIME_FORMAT.isGroupingUsed = false
        }

        // ExoPlayer.EventListener

        private fun getTimeString(timeMs: Long): String {
            return if (timeMs == C.TIME_UNSET) "?" else TIME_FORMAT.format((timeMs / 1000f).toDouble())
        }

        private fun getStateString(state: Int): String {
            return when (state) {
                ExoPlayer.STATE_BUFFERING -> "B"
                ExoPlayer.STATE_ENDED -> "E"
                ExoPlayer.STATE_IDLE -> "I"
                ExoPlayer.STATE_READY -> "R"
                else -> "?"
            }
        }

        private fun getFormatSupportString(formatSupport: Int): String {
            return when (formatSupport) {
                RendererCapabilities.FORMAT_HANDLED -> "YES"
                RendererCapabilities.FORMAT_EXCEEDS_CAPABILITIES -> "NO_EXCEEDS_CAPABILITIES"
                RendererCapabilities.FORMAT_UNSUPPORTED_SUBTYPE -> "NO_UNSUPPORTED_TYPE"
                RendererCapabilities.FORMAT_UNSUPPORTED_TYPE -> "NO"
                else -> "?"
            }
        }

        private fun getAdaptiveSupportString(trackCount: Int, adaptiveSupport: Int): String {
            if (trackCount < 2) {
                return "N/A"
            }
            return when (adaptiveSupport) {
                RendererCapabilities.ADAPTIVE_SEAMLESS -> "YES"
                RendererCapabilities.ADAPTIVE_NOT_SEAMLESS -> "YES_NOT_SEAMLESS"
                RendererCapabilities.ADAPTIVE_NOT_SUPPORTED -> "NO"
                else -> "?"
            }
        }

        private fun getTrackStatusString(
            selection: TrackSelection?, group: TrackGroup,
            trackIndex: Int
        ): String {
            return getTrackStatusString(
                selection != null && selection.trackGroup === group
                        && selection.indexOf(trackIndex) != C.INDEX_UNSET
            )
        }

        private fun getTrackStatusString(enabled: Boolean): String {
            return if (enabled) "[X]" else "[ ]"
        }
    }
}

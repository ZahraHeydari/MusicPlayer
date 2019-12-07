package com.android.player.exo

class PlaybackState(state: Int) {

    var state = STATE_NONE

    init {
        this.state = state
    }

    companion object {

        /**
         * This is the default playback state and indicates that no media has been
         * added yet, or the performer has been reset and has no content to play.
         *
         * @see .setState
         */
        const val STATE_NONE = 0

        /**
         * State indicating this item is currently stopped.
         *
         * @see .setState
         */
        const val STATE_STOPPED = 1

        /**
         * State indicating this item is currently paused.
         *
         * @see .setState
         */
        const val STATE_PAUSED = 2

        /**
         * State indicating this item is currently playing.
         *
         * @see .setState
         */
        const val STATE_PLAYING = 3


        /**
         * State indicating this item is currently buffering and will begin playing
         * when enough data has buffered.
         *
         * @see .setState
         */
        const val STATE_BUFFERING = 4
    }
}
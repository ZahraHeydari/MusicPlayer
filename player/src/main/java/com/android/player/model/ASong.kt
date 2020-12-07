package com.android.player.model

import android.os.Parcelable

abstract class ASong(
    var songId: Int = 0,
    var title: String? = "",
    var clipArt: String? = "",
    var artist: String? = "",
    var source: String? = "",
    var songType: Int = 0,
    var length: String? = "",
    var downloadPath: String? = "",
    var category: String? = ""
) : Parcelable {

    @Transient
    var totalDuration: Long = 0
    @Transient
    var currentPosition: Long = 0
    @Transient
    var playingPercent = 0

    private fun calculatePlayingPercent(): Int {
        return if (currentPosition == 0L || totalDuration == 0L) 0 else (currentPosition * 100 / totalDuration).toInt()
    }

}
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
    var isPlay = false
    @Transient
    var totalDuration: Long = 0
    @Transient
    var currentPosition: Long = 0
    @Transient
    var playingPercent = 0

    private fun calculatePercentPlay(): Int {
        return if (currentPosition == 0L || totalDuration == 0L) 0 else (currentPosition * 100 / totalDuration).toInt()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || o !is ASong) return false
        val song = o as ASong?
        if (this.songId != this.songId) return false
        if (if (this.title != null) this.title != song?.title else song?.title != null)
            return false
        if (if (this.clipArt != null) this.clipArt != song?.clipArt else song?.clipArt != null)
            return false
        return if (this.artist != null) this.artist == song?.artist else song?.artist == null
    }

    override fun hashCode(): Int {
        var result = songId xor songId.ushr(32)
        result = 31 * result + if (title != null) title.hashCode() else 0
        result = 31 * result + if (clipArt != null) clipArt.hashCode() else 0
        result = 31 * result + if (artist != null) artist.hashCode() else 0
        return result
    }

}
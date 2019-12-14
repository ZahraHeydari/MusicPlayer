package com.android.player.model


abstract class ASong {

    private var isPlay = false
    @Transient
    private var duration: Long = 0
    @Transient
    private var currentPosition: Long = 0
    @Transient
    private var playingPercent = 0

    abstract fun getSongId(): Int

    abstract fun getName(): String?

    abstract fun getFeatureAvatar(): String?

    abstract fun getArtistName(): String?

    abstract fun getCategory(): String

    abstract fun getSource(): String

    abstract fun getSongType(): Int

    abstract fun getDownloadPath(): String

    private fun calculatePercentPlay(): Int {
        return if (currentPosition == 0L || duration == 0L) 0 else (currentPosition * 100 / duration).toInt()
    }

    fun isPlay(): Boolean {
        return isPlay
    }

    fun setPlay(play: Boolean) {
        isPlay = play
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || o !is ASong) return false
        val song = o as ASong?
        if (this.getSongId() != this.getSongId()) return false
        if (if (this.getName() != null) this.getName() != song?.getName() else song?.getName() != null)
            return false
        if (if (this.getFeatureAvatar() != null) this.getFeatureAvatar() != song?.getFeatureAvatar() else song?.getFeatureAvatar() != null)
            return false
        return if (this.getArtistName() != null) this.getArtistName() == song?.getArtistName() else song?.getArtistName() == null
    }

    override fun hashCode(): Int {
        var result = getSongId() xor getSongId().ushr(32)
        result = 31 * result + if (getName() != null) getName().hashCode() else 0
        result = 31 * result + if (getFeatureAvatar() != null) getFeatureAvatar().hashCode() else 0
        result = 31 * result + if (getArtistName() != null) getArtistName().hashCode() else 0
        return result
    }

}
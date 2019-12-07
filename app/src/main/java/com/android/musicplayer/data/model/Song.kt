package com.android.musicplayer.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.player.exo.ExoPlayerManager.Companion.AUDIO_TYPE
import com.android.player.model.ASong
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Song(
    @PrimaryKey var id: Int,
    var title: String?,
    var path: String,
    var artist: String?,
    var clipArt: String?,
    var duartion: String?
) : ASong(), Parcelable {

    override fun getSongId(): Int {
        return id
    }

    override fun getName(): String? {
        return title
    }

    override fun getFeatureAvatar(): String? {
        return clipArt
    }

    override fun getSingerName(): String? {
        return artist
    }

    override fun getCategory(): String {
        return ""
    }

    override fun getSource(): String {
        return path
    }

    override fun getSongType(): Int {
        return AUDIO_TYPE
    }

    override fun getDownloadPath(): String {
        return path
    }

}
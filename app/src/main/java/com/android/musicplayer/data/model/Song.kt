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
    var songName: String?,
    var path: String,
    var artistName: String?,
    var albumArt: String?,
    var duration: String?,
    var type: Int = 0
) : ASong(id, songName, albumArt, artistName, path, type, duration), Parcelable
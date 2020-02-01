package com.android.musicplayer.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.player.model.ASong
import kotlinx.android.parcel.Parcelize

@Suppress("DIFFERENT_NAMES_FOR_THE_SAME_PARAMETER_IN_SUPERTYPES")
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
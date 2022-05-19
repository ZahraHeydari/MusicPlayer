package com.android.musicplayer.data.model

import android.os.Parcelable
import androidx.core.net.toUri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.util.MimeTypes
import kotlinx.android.parcel.Parcelize
import java.io.File

@Entity
@Parcelize
data class Song(
    @PrimaryKey var id: String,
    var title: String?,
    var artist: String?,
    var path: String,
    var albumArt: String?
) : Parcelable {

    constructor(mediaItem: MediaItem) : this(
        mediaItem.mediaId,
        mediaItem.mediaMetadata.albumTitle.toString(),
        mediaItem.mediaMetadata.albumArtist.toString(),
        mediaItem.mediaMetadata.mediaUri.toString(),
        mediaItem.mediaMetadata.artworkUri?.path.toString()
    )

    companion object {
        fun Song.createMediaItem() = MediaItem.Builder().setMediaId(this.id)
            .setUri(File(path).toUri())
            .setMimeType(MimeTypes.AUDIO_MPEG)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setAlbumTitle(title)
                    .setAlbumArtist(artist)
                    .setArtworkUri(File(albumArt ?: "").toUri())
                    .build()
            ).build()
    }
}
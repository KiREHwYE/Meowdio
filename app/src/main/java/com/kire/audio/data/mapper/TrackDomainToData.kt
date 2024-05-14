package com.kire.audio.data.mapper

import android.net.Uri
import com.kire.audio.data.model.TrackEntity
import com.kire.audio.domain.model.TrackDomain

fun TrackDomain.asTrackEntity() = TrackEntity(
    title, album, artist, duration, lyrics, path, albumId, Uri.parse(imageUri), dateAdded, isFavourite, Uri.parse(defaultImageUri), id
)
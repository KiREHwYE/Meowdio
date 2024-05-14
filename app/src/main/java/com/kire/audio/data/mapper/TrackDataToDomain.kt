package com.kire.audio.data.mapper

import com.kire.audio.data.model.TrackEntity
import com.kire.audio.domain.model.TrackDomain

fun TrackEntity.asTrackDomain() = TrackDomain(
    title = title,
    album = album,
    artist = artist,
    duration = duration,
    lyrics = lyrics,
    path = path,
    albumId = albumId,
    imageUri = imageUri.toString(),
    dateAdded = dateAdded,
    isFavourite = isFavourite,
    defaultImageUri = defaultImageUri.toString(),
    id = id
)
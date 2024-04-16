package com.kire.audio.presentation.mappers

import com.kire.audio.data.models.TrackEntity
import com.kire.audio.presentation.models.Track

fun Track.asEntity() = TrackEntity(
    title = title,
    album = album,
    artist = artist,
    duration = duration,
    lyrics = lyrics,
    path = path,
    albumId = albumId,
    imageUri = imageUri,
    dateAdded = dateAdded,
    isFavourite = isFavourite,
    defaultImageUri = defaultImageUri,
    id = id
)
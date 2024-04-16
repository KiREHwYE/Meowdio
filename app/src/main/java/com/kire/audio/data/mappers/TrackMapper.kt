package com.kire.audio.data.mappers

import com.kire.audio.data.models.TrackEntity
import com.kire.audio.presentation.models.Track

fun TrackEntity.asExternalModel() = Track(
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
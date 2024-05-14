package com.kire.audio.presentation.mapper

import com.kire.audio.domain.model.TrackDomain
import com.kire.audio.presentation.model.ILyricsRequestState
import com.kire.audio.presentation.model.Track

fun Track.asTrackDomain() = TrackDomain(
    title = this.title,
    album = this.album,
    artist = this.artist,
    duration = this.duration,
    lyrics = when(this.lyrics) {
        is ILyricsRequestState.Success -> this.lyrics.lyrics
        else -> ""
    },
    path = this.path,
    albumId = this.albumId,
    imageUri = imageUri.toString(),
    dateAdded = this.dateAdded,
    isFavourite = this.isFavourite,
    defaultImageUri = defaultImageUri.toString(),
    id = this.id
)
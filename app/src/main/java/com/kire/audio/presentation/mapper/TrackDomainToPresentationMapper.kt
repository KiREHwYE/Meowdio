package com.kire.audio.presentation.mapper

import android.net.Uri
import com.kire.audio.domain.model.TrackDomain
import com.kire.audio.presentation.model.ILyricsRequestState
import com.kire.audio.presentation.model.Track

fun TrackDomain.asTrack() = Track(
    title = this.title,
    album = this.album,
    artist = this.artist,
    duration = this.duration,
    lyrics = ILyricsRequestState.Success(this.lyrics),
    path = this.path,
    albumId = this.albumId,
    imageUri = Uri.parse(imageUri),
    dateAdded = this.dateAdded,
    isFavourite = this.isFavourite,
    defaultImageUri = Uri.parse(defaultImageUri),
    id = this.id
)
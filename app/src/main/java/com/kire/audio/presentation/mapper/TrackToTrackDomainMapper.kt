package com.kire.audio.presentation.mapper

import com.kire.audio.domain.model.TrackDomain
import com.kire.audio.presentation.model.Track

fun Track.asTrackDomain() = TrackDomain(
    title, album, artist, duration, lyrics, path, albumId, imageUri.toString(), dateAdded, isFavourite, defaultImageUri.toString(), id
)
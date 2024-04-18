package com.kire.audio.presentation.mapper

import android.net.Uri
import com.kire.audio.domain.model.TrackDomain
import com.kire.audio.presentation.model.Track

fun TrackDomain.asTrack() = Track(
    title, album, artist, duration, lyrics, path, albumId, Uri.parse(imageUri), dateAdded, isFavourite, Uri.parse(defaultImageUri), id
)
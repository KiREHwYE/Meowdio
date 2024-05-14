package com.kire.audio.presentation.mapper

import com.kire.audio.domain.model.TrackDomain

fun List<TrackDomain>.asListTrack() =
    this.map { track ->
        track.asTrack()
    }
package com.kire.audio.presentation.mapper

import com.kire.audio.presentation.model.Track

fun List<Track>.asListTrackDomain() =
    this.map { track ->
        track.asTrackDomain()
    }
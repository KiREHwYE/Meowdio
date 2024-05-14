package com.kire.audio.data.mapper

import com.kire.audio.data.model.TrackEntity

fun List<TrackEntity>.asListTrackDomain() =
    this.map { track ->
        track.asTrackDomain()
    }
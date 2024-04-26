package com.kire.audio.presentation.mapper

import com.kire.audio.domain.model.TrackDomain

fun Map<String, List<TrackDomain>>.asMapAlbumListTrack() =
    this.mapValues { pair ->
        pair.value.asListTrack()
    }
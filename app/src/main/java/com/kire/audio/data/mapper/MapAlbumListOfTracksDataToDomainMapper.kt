package com.kire.audio.data.mapper

import com.kire.audio.data.model.TrackEntity

fun Map<String, List<TrackEntity>>.asMapAlbumListTrackDomain() =
    this.mapValues { pair ->
        pair.value.asListTrackDomain()
    }
package com.kire.audio.data.mapper

import com.kire.audio.data.model.TrackEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun Flow<List<TrackEntity>>.asFlowListOfTracksDomain() = map { trackEntities ->
    trackEntities.map {
        it.asTrackDomain()
    }
}
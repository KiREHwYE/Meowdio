package com.kire.audio.data.mappers

import com.kire.audio.data.models.TrackEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun Flow<List<TrackEntity>>.asFlowListOfTracks() = map { trackEntities ->
    trackEntities.map {
        it.asExternalModel()
    }
}
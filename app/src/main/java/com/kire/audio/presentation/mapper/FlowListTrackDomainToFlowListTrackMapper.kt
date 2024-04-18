package com.kire.audio.presentation.mapper

import com.kire.audio.domain.model.TrackDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun Flow<List<TrackDomain>>.asListOfTrack() = map { listOfTrackDomain ->
    listOfTrackDomain.map { trackDomain ->
        trackDomain.asTrack()
    }
}

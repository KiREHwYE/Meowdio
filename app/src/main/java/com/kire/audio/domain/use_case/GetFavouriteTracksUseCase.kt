package com.kire.audio.domain.use_case

import com.kire.audio.domain.model.TrackDomain
import com.kire.audio.domain.repository.ITrackRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavouriteTracksUseCase @Inject constructor(
    private val trackRepository: ITrackRepository
) {
    operator fun invoke(): Flow<List<TrackDomain>> =
        trackRepository.getFavouriteTracks()

}
package com.kire.audio.domain.use_case

import com.kire.audio.domain.model.TrackDomain
import com.kire.audio.domain.repository.ITrackRepository
import javax.inject.Inject

class DeleteNoLongerExistingTracksFromDatabaseUseCase @Inject constructor(
    private val trackRepository: ITrackRepository
) {
    suspend operator fun invoke(tracks: List<TrackDomain>) =
        trackRepository.deleteNoLongerExistingTracksFromDatabaseUseCase(tracks)
}
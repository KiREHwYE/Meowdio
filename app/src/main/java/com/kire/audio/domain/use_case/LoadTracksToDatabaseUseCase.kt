package com.kire.audio.domain.use_case

import com.kire.audio.domain.repository.ITrackRepository
import javax.inject.Inject

class LoadTracksToDatabaseUseCase @Inject constructor(
    private val trackRepository: ITrackRepository
) {
    suspend operator fun invoke() =
        trackRepository.loadTracksToDatabase()
}
package com.kire.audio.domain.use_case

import com.kire.audio.domain.repository.ITrackRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateTrackDataBaseUseCase @Inject constructor(
    private val trackRepository: ITrackRepository
) {
    suspend operator fun invoke(coroutineDispatcher: CoroutineDispatcher){

            withContext(coroutineDispatcher){
                launch {
                    trackRepository.loadTracksToDatabase()
                }
                launch {
                    trackRepository.deleteNoLongerExistingTracksFromDatabaseUseCase()
                }
            }
    }
}
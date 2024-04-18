package com.kire.audio.domain.use_case

import com.kire.audio.domain.util.PreferencesDataStoreConstants
import com.kire.audio.domain.model.TrackDomain
import com.kire.audio.domain.repository.IPreferencesRepository
import com.kire.audio.domain.repository.ITrackRepository
import com.kire.audio.domain.util.SortTypeDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class GetSortedTracksUseCase @Inject constructor(
    private val preferencesDataStoreRepository: IPreferencesRepository,
    private val trackRepository: ITrackRepository
) {

    operator fun invoke(): Flow<List<TrackDomain>> =
        preferencesDataStoreRepository.readSortOption(PreferencesDataStoreConstants.SORT_OPTION_KEY)
            .flatMapLatest { sortType ->
                when(sortType) {
                    SortTypeDomain.DATA_ASC_ORDER -> trackRepository.getTracksOrderedByDateAddedASC()
                    SortTypeDomain.DATA_DESC_ORDER -> trackRepository.getTracksOrderedByDateAddedDESC()
                    SortTypeDomain.TITLE_ASC_ORDER -> trackRepository.getTracksOrderedByTitleASC()
                    SortTypeDomain.TITLE_DESC_ORDER -> trackRepository.getTracksOrderedByTitleDESC()
                    SortTypeDomain.ARTIST_ASC_ORDER -> trackRepository.getTracksOrderedByArtistASC()
                    SortTypeDomain.ARTIST_DESC_ORDER -> trackRepository.getTracksOrderedByArtistDESC()
                    SortTypeDomain.DURATION_ASC_ORDER -> trackRepository.getTracksOrderedByDurationASC()
                    SortTypeDomain.DURATION_DESC_ORDER -> trackRepository.getTracksOrderedByDurationDESC()
                }
            }
}
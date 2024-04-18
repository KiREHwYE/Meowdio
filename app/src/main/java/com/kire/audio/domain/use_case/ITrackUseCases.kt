package com.kire.audio.domain.use_case

interface ITrackUseCases {
    val getSortedTracksUseCase: GetSortedTracksUseCase
    val getFavouriteTracksUseCase: GetFavouriteTracksUseCase
    val saveSortOptionUseCase: SaveSortOptionUseCase
    val readSortOptionUseCase: ReadSortOptionUseCase
    val saveRepeatModeUseCase: SaveRepeatModeUseCase
    val readRepeatModeUseCase: ReadRepeatModeUseCase
    val upsertTrackUseCase: UpsertTrackUseCase
    val loadTracksToDatabaseUseCase: LoadTracksToDatabaseUseCase
    val deleteNoLongerExistingTracksFromDatabaseUseCase: DeleteNoLongerExistingTracksFromDatabaseUseCase
}
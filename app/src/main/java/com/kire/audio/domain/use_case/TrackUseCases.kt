package com.kire.audio.domain.use_case

import javax.inject.Inject

data class TrackUseCases @Inject constructor(
    override val getSortedTracksUseCase: GetSortedTracksUseCase,
    override val getFavouriteTracksUseCase: GetFavouriteTracksUseCase,
    override val saveSortOptionUseCase: SaveSortOptionUseCase,
    override val readSortOptionUseCase: ReadSortOptionUseCase,
    override val saveRepeatModeUseCase: SaveRepeatModeUseCase,
    override val readRepeatModeUseCase: ReadRepeatModeUseCase,
    override val upsertTrackUseCase: UpsertTrackUseCase,
    override val updateTrackDataBaseUseCase: UpdateTrackDataBaseUseCase
): ITrackUseCases

package com.kire.audio.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kire.audio.di.IoDispatcher
import com.kire.audio.domain.repository.ITrackRepository
import com.kire.audio.domain.use_case.TrackUseCases
import com.kire.audio.presentation.mapper.asMapAlbumListTrack
import com.kire.audio.presentation.model.Track
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

//class AlbumViewModel @Inject constructor(
//    private val trackUseCases: TrackUseCases,
//    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
//): ViewModel() {
//
//    var artistWithTracks: Map<String, List<Track>> = emptyMap()
//
//    init {
//        viewModelScope.launch(coroutineDispatcher) {
//            artistWithTracks = trackUseCases.getAlbumsWithTracksUseCase().asMapAlbumListTrack()
//        }
//    }
//}

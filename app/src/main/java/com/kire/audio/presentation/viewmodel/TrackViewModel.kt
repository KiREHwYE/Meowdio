package com.kire.audio.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kire.audio.di.IoDispatcher
import com.kire.audio.domain.use_case.ITrackUseCases

import com.kire.audio.screen.functional.ListSelector

import com.kire.audio.presentation.functional.events.SortOptionEvent
import com.kire.audio.presentation.util.SortType
import com.kire.audio.presentation.mapper.asListOfTrack
import com.kire.audio.presentation.mapper.asSortType
import com.kire.audio.presentation.mapper.asSortTypeDomain
import com.kire.audio.presentation.mapper.asTrackDomain
import com.kire.audio.presentation.model.SearchUiState
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.TrackUiState
import kotlinx.coroutines.CoroutineDispatcher

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class TrackViewModel @Inject constructor(
    private val trackUseCases: ITrackUseCases,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel(){


    /*
    * Tracks-providing params and funcs
    * */

    private val _sortType = MutableStateFlow(SortType.DATA_DESC_ORDER)
    val sortType : StateFlow<SortType>
        get() = _sortType.asStateFlow()


    private var _tracks = trackUseCases.getSortedTracksUseCase().asListOfTrack()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), mutableListOf())


    fun onEvent(event: SortOptionEvent) {
        when(event){
            is SortOptionEvent.ListTrackSortOption -> {
                _sortType.value = event.sortType
            }
        }
    }

    private var _favouriteTracks:  StateFlow<List<Track>> =
        trackUseCases.getFavouriteTracksUseCase().asListOfTrack()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun selectListOfTracks(selectList: ListSelector): StateFlow<List<Track>> =
        when(selectList){
            ListSelector.MAIN_LIST -> _tracks
            ListSelector.SEARCH_LIST -> _searchResult
            ListSelector.FAVOURITE_LIST -> _favouriteTracks
        }


    /*
    TrackUiState params and funcs
    * */

    private val _trackUiState: MutableStateFlow<TrackUiState> = MutableStateFlow(TrackUiState())
    val trackUiState: StateFlow<TrackUiState> = _trackUiState.asStateFlow()

    fun changeTrackUiState(
        trackUiState: TrackUiState
    ) = _trackUiState.update { _ ->
        trackUiState
    }

    /*
     * DataStore funcs
     * */

    fun saveSortOption(value: SortType) =
        viewModelScope.launch(coroutineDispatcher) {
            trackUseCases.saveSortOptionUseCase(value.asSortTypeDomain())
        }

    fun saveRepeatMode(value: Int) =
        viewModelScope.launch(coroutineDispatcher) {
            trackUseCases.saveRepeatModeUseCase(value)
        }


    /*
    * Database updating funcs
    * */

    fun upsertTrack(track: Track) =
        viewModelScope.launch(coroutineDispatcher) {
            trackUseCases.upsertTrackUseCase(track.asTrackDomain())
        }

    fun updateTrackDataBase() =
        viewModelScope.launch(coroutineDispatcher) {
            trackUseCases.updateTrackDataBaseUseCase(coroutineDispatcher)
        }



    /*
    * Search params and funcs
    * */

    private val _searchUiState = MutableStateFlow(SearchUiState())
    val searchUiState: StateFlow<SearchUiState> = _searchUiState.asStateFlow()

    fun changeSearchUiState(searchUiState: SearchUiState) {
        _searchUiState.update { _ ->
            searchUiState
        }
    }

    private val _searchResult = searchUiState
        .combine(_tracks) { searchUiState, _ ->
            if (searchUiState.searchText.isBlank())
                emptyList()
            else
                _tracks.value.filter{
                    it.doesMatchSearchQuery(searchUiState.searchText)
                }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    /*
    * Initialization block
    * */

    init {
        viewModelScope.launch(coroutineDispatcher) {
            launch {
                trackUseCases.readSortOptionUseCase().collect { sortTypeDomain ->
                    _sortType.value = sortTypeDomain.asSortType()
                }

            }
            launch {
                trackUseCases.readRepeatModeUseCase().collect {
                    _trackUiState.update { currentState ->
                        currentState.copy(
                            trackRepeatMode = it
                        )
                    }
                }
            }
        }
    }
}
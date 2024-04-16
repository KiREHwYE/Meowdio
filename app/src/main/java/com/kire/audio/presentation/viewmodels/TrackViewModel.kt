package com.kire.audio.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kire.audio.data.repositories.interfaces.ITrackRepository

import com.kire.audio.screen.functional.ListSelector

import com.kire.audio.data.preferencesDataStore.PreferencesDataStoreConstants.REPEAT_MODE_KEY
import com.kire.audio.data.preferencesDataStore.PreferencesDataStoreConstants.SORT_OPTION_KEY
import com.kire.audio.data.repositories.interfaces.IPreferencesDataStoreRepository
import com.kire.audio.presentation.functional.events.SortOptionEvent
import com.kire.audio.presentation.functional.events.SortType
import com.kire.audio.device.audio.functional.RepeatMode
import com.kire.audio.presentation.mappers.asEntity
import com.kire.audio.presentation.models.SearchUiState
import com.kire.audio.presentation.models.Track
import com.kire.audio.presentation.models.TrackUiState
import kotlinx.coroutines.CoroutineDispatcher

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrackViewModel(
    private val preferencesDataStoreRepository: IPreferencesDataStoreRepository,
    private val trackRepository: ITrackRepository,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel(){


    class Factory(
        private val preferencesDataStoreRepository: IPreferencesDataStoreRepository,
        private val trackRepository: ITrackRepository
    ): ViewModelProvider.Factory{
        override fun <T:ViewModel> create(modelClass: Class<T>):T{
            if(modelClass.isAssignableFrom(TrackViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return TrackViewModel(
                    preferencesDataStoreRepository,
                    trackRepository
                ) as T
            }
            throw IllegalArgumentException("UNKNOWN VIEW MODEL CLASS")
        }
    }


    /*
    * Tracks-providing params and funcs
    * */

    private val _sortType = MutableStateFlow(SortType.DATA_DESC_ORDER)
    val sortType : StateFlow<SortType>
        get() = _sortType.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private var _tracks = _sortType
        .flatMapLatest { sortType ->
            when(sortType) {
                SortType.DATA_ASC_ORDER -> trackRepository.getTracksOrderedByDateAddedASC()
                SortType.DATA_DESC_ORDER -> trackRepository.getTracksOrderedByDateAddedDESC()
                SortType.TITLE_ASC_ORDER -> trackRepository.getTracksOrderedByTitleASC()
                SortType.TITLE_DESC_ORDER -> trackRepository.getTracksOrderedByTitleDESC()
                SortType.ARTIST_ASC_ORDER -> trackRepository.getTracksOrderedByArtistASC()
                SortType.ARTIST_DESC_ORDER -> trackRepository.getTracksOrderedByArtistDESC()
                SortType.DURATION_ASC_ORDER -> trackRepository.getTracksOrderedByDurationASC()
                SortType.DURATION_DESC_ORDER -> trackRepository.getTracksOrderedByDurationDESC()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), mutableListOf())

    fun onEvent(event: SortOptionEvent) {
        when(event){
            is SortOptionEvent.ListTrackSortOption -> {
                _sortType.value = event.sortType
            }
        }
    }

    private var _favouriteTracks:  StateFlow<List<Track>> =
        trackRepository.getFavouriteTracks()
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
        viewModelScope.launch {
            preferencesDataStoreRepository.saveSortOption(SORT_OPTION_KEY, value.toString())
        }

    fun saveRepeatMode(value: Int) =
        viewModelScope.launch {
            preferencesDataStoreRepository.saveRepeatMode(REPEAT_MODE_KEY, RepeatMode.entries[value].name)
        }


    /*
    * Database updating funcs
    * */

    suspend fun upsertTrack(track: Track) = trackRepository.upsertTrack(track.asEntity())
    suspend fun loadTracksToDatabase() = trackRepository.loadTracksToDatabase()
    suspend fun deleteTracksFromDatabase(tracks :List<Track>) = trackRepository.deleteTracksFromDatabase(tracks)


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
                preferencesDataStoreRepository.readSortOption(SORT_OPTION_KEY).collect {
                    _sortType.value = it
                }
            }
            launch {
                preferencesDataStoreRepository.readRepeatMode(REPEAT_MODE_KEY).collect {
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
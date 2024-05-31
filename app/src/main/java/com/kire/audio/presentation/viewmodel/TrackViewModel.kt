package com.kire.audio.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.kire.audio.di.IoDispatcher
import com.kire.audio.domain.use_case.util.ITrackUseCases
import com.kire.audio.presentation.model.SortOption
import com.kire.audio.presentation.util.ListSelector
import com.kire.audio.presentation.mapper.asILyricsRequestState
import com.kire.audio.presentation.util.SortType
import com.kire.audio.presentation.mapper.asListOfTrack
import com.kire.audio.presentation.mapper.asLyricsRequestModeDomain
import com.kire.audio.presentation.mapper.asMapAlbumListTrack
import com.kire.audio.presentation.mapper.asSortType
import com.kire.audio.presentation.mapper.asSortTypeDomain
import com.kire.audio.presentation.mapper.asTrackDomain
import com.kire.audio.presentation.model.AlbumUiState
import com.kire.audio.presentation.model.ILyricsRequestState
import com.kire.audio.presentation.model.LyricsUiState
import com.kire.audio.presentation.model.SearchUiState
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.TrackUiState
import com.kire.audio.presentation.util.LyricsRequestMode
import dagger.hilt.android.lifecycle.HiltViewModel

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
import kotlinx.coroutines.withContext

import javax.inject.Inject

@HiltViewModel
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
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            mutableListOf()
        )


    fun updateSortOption(event: SortOption) {
        _sortType.value = event.sortType
    }

    private var _favouriteTracks:  StateFlow<List<Track>> =
        trackUseCases.getFavouriteTracksUseCase().asListOfTrack()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    var artistWithTracks: Map<String, List<Track>> = emptyMap()

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

    fun updateTrackUiState(
        trackUiState: TrackUiState
    ) = _trackUiState.update { _ ->
        Log.d("MINE", "TRACK UPDATED")
        trackUiState
    }

    suspend fun getTrackLyricsFromGenius(
        mode: LyricsRequestMode,
        title: String?,
        artist: String?,
        userInput: String?,
    ): ILyricsRequestState =
        trackUseCases.getTrackLyricsFromGenius(
            mode = mode.asLyricsRequestModeDomain(),
            title,
            artist,
            userInput
        ).asILyricsRequestState()


    /*
    LyricsUiState params and funcs
    * */

    private val _lyricsUiState: MutableStateFlow<LyricsUiState> = MutableStateFlow(LyricsUiState())
    val lyricsUiState: StateFlow<LyricsUiState> = _lyricsUiState

    fun updateLyricsUiState(
       lyricsUiState: LyricsUiState
    ) = _lyricsUiState.update { _ ->
            lyricsUiState
        }


    /*
    AlbumUiState params and funcs
    * */

    private val _albumUiState: MutableStateFlow<AlbumUiState> = MutableStateFlow(AlbumUiState())
    val albumUiState: StateFlow<AlbumUiState> = _albumUiState.asStateFlow()

    fun updateAlbumUiState(
        albumUiState: AlbumUiState
    ) = _albumUiState.update { _ ->
        albumUiState
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
    * Database funcs
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

    fun updateSearchUiState(searchUiState: SearchUiState) {
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
        viewModelScope.launch {
            withContext(coroutineDispatcher){
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
                launch {
                    viewModelScope.launch(coroutineDispatcher) {
                        artistWithTracks = trackUseCases.getAlbumsWithTracksUseCase().asMapAlbumListTrack()
                    }
                }
            }
        }
    }
}
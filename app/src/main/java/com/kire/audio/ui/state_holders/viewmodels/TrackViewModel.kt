package com.kire.audio.ui.state_holders.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kire.audio.data.repositories.ITrackRepository

import com.kire.audio.screen.functional.ListSelector

import com.kire.audio.data.repositories.TrackRepository
import com.kire.audio.data.preferencesDataStore.PreferencesDataStoreConstants.REPEAT_MODE_KEY
import com.kire.audio.data.preferencesDataStore.PreferencesDataStoreConstants.SORT_OPTION_KEY
import com.kire.audio.data.repositories.IPreferencesDataStoreRepository
import com.kire.audio.ui.functional.events.SortOptionEvent
import com.kire.audio.ui.functional.events.SortType
import com.kire.audio.mediaHandling.functional.RepeatMode
import com.kire.audio.ui.state_holders.models.BottomSheetUiState
import com.kire.audio.ui.state_holders.models.Track
import com.kire.audio.ui.state_holders.models.TrackUiState
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
        private val trackRepository: TrackRepository
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

    fun selectListTracks(listSelect: ListSelector): StateFlow<List<Track>> =
        when(listSelect){
            ListSelector.MAIN_LIST -> _tracks
            ListSelector.SEARCH_LIST -> _searchResult
            ListSelector.FAVOURITE_LIST -> _favouriteTracks
        }


    private val _uiState: MutableStateFlow<TrackUiState> = MutableStateFlow(TrackUiState())
    val uiState: StateFlow<TrackUiState> = _uiState.asStateFlow()



    /*
     * DataStore funcs
     * */

    fun saveSortOption(value: SortType) =
        viewModelScope.launch {
            preferencesDataStoreRepository.saveSortOption(SORT_OPTION_KEY, value.toString())
        }

    fun saveRepeatMode(value: Int) =
        viewModelScope.launch {
            preferencesDataStoreRepository.saveRepeatMode(REPEAT_MODE_KEY, value.toString())
        }





    /*
    * Database updating funcs
    * */

    suspend fun upsertTrack(track: Track) = trackRepository.upsertTrack(track)
    fun updateIsLoved(track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            trackRepository.updateIsLoved(track)
        }
    }
    suspend fun loadTracksToDatabase() = trackRepository.loadTracksToDatabase()
    suspend fun deleteTracksFromDatabase(tracks :List<Track>) = trackRepository.deleteTracksFromDatabase(tracks)





    /*
    * Search params and funcs
    * */

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String>
        get() = _searchText.asStateFlow()

    private val _active = MutableStateFlow(false)
    val active: StateFlow<Boolean>
        get() = _active.asStateFlow()


    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun onActiveChange(active: Boolean) {
        _active.value = active
    }

    private val _searchResult = searchText
        .combine(_tracks) { text, _ ->
            if (text.isBlank())
                emptyList()
            else
                _tracks.value.filter{
                    it.doesMatchSearchQuery(text)
                }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )





    /*
    BottomSheet params and funcs
    * */

    private val _bottomSheetUiState: MutableStateFlow<BottomSheetUiState> = MutableStateFlow(
        BottomSheetUiState()
    )
    val bottomSheetUiState: StateFlow<BottomSheetUiState> = _bottomSheetUiState.asStateFlow()

    private val _selectList: MutableStateFlow<ListSelector> = MutableStateFlow(ListSelector.MAIN_LIST)
    val selectList: StateFlow<ListSelector>
        get() = _selectList.asStateFlow()


    fun changeSelectList(selectList: ListSelector) {
        _selectList.value = selectList
    }
    fun changeIsExpanded(isExpanded: Boolean) {
        _bottomSheetUiState.update { currentState ->
            currentState.copy(
                isExpanded = isExpanded
            )
        }
    }
    fun changeIsShown(isShown: Boolean) {
        _bottomSheetUiState.update { currentState ->
            currentState.copy(
                isShown = isShown
            )
        }
    }

    fun changeRepeatMode(value: Int) {
        _bottomSheetUiState.update { currentState ->
            currentState.copy(
                repeatMode = RepeatMode.entries[value]
            )
        }
    }

    fun sentInfoToBottomSheet(
        track: Track,
        listSelect: ListSelector,
        trackINDEX: Int,
        currentTrackPlayingURI: String
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                currentTrackPlaying = track,
                currentTrackPlayingIndex = trackINDEX,
                currentTrackPlayingURI = currentTrackPlayingURI
            )
        }

        _selectList.value = listSelect
        _bottomSheetUiState.update { currentState ->
            currentState.copy(
                isShown = true
            )
        }
    }

    fun sentInfoToBottomSheet(
        track: Track
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                currentTrackPlaying = track,
            )
        }
    }



    /*
    * Notification funcs + isRepeated
    * */

    companion object {
        val reason = MutableStateFlow(true)
        val nextTrack = MutableStateFlow(false)
        val previousTrack = MutableStateFlow(false)

        val isRepeated = MutableStateFlow(false)
    }




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
                    _bottomSheetUiState.update { currentState ->
                        currentState.copy(
                            repeatMode = it
                        )
                    }
                }
            }
        }
    }
}
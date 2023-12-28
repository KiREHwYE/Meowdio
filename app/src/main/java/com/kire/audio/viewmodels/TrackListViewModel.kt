package com.kire.audio.viewmodels

import android.content.Context

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope

import com.kire.audio.functional.ListSelector

import com.kire.audio.repository.TrackRepository
import com.kire.audio.datastore.DataStore
import com.kire.audio.datastore.DataStoreConstants.REPEAT_MODE_KEY
import com.kire.audio.datastore.DataStoreConstants.SORT_OPTION_KEY
import com.kire.audio.events.SortOptionEvent
import com.kire.audio.events.SortType
import com.kire.audio.mediaHandling.AudioPlayer
import com.kire.audio.models.Track
import com.kire.audio.notification.AudioNotification

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class TrackListViewModel(
    private val dataStore: DataStore,
    private val trackRepository: TrackRepository,
    audioPlayer: AudioPlayer,
    private val audioNotification: AudioNotification,
) : ViewModel(){

    class Factory(
        private val dataStore: DataStore,
        private val trackRepository: TrackRepository,
        private val audioPlayer: AudioPlayer,
        private val audioNotification: AudioNotification
    ): ViewModelProvider.Factory{
        override fun <T:ViewModel> create(modelClass: Class<T>):T{
            if(modelClass.isAssignableFrom(TrackListViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return TrackListViewModel(
                    dataStore,
                    trackRepository,
                    audioPlayer,
                    audioNotification
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
    private val _tracks = _sortType
        .flatMapLatest {sortType ->
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
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


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
            else -> TODO()
        }




    /*
     * DataStore funcs
     * */

    fun saveSortOption(value: SortType) =
        viewModelScope.launch {
            dataStore.saveSortOption(SORT_OPTION_KEY, value.toString())
        }

    fun saveRepeatMode(value: Int) =
        viewModelScope.launch {
            dataStore.saveRepeatMode(REPEAT_MODE_KEY, value)
        }





    /*
    * Database updating funcs
    * */

    fun loadTracksToDatabase(context: Context) = trackRepository.loadTracksToDatabase(context)
    fun deleteTracksFromDatabase(tracks :List<Track>) = trackRepository.deleteTracksFromDatabase(tracks)
    fun updateIsLoved(track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            trackRepository.updateIsLoved(track)
        }
    }




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

    private val _currentTrackPlaying: MutableStateFlow<Track?> = MutableStateFlow(null)
    val currentTrackPlaying: StateFlow<Track?>
        get() = _currentTrackPlaying.asStateFlow()

    private val _currentTrackPlayingURI: MutableStateFlow<String> = MutableStateFlow(_currentTrackPlaying.value?.path ?: "")
    val currentTrackPlayingURI: StateFlow<String>
        get() = _currentTrackPlayingURI.asStateFlow()

    private val _bottomSheetTrackINDEX = MutableStateFlow(0)
    val bottomSheetTrackINDEX: StateFlow<Int>
        get() = _bottomSheetTrackINDEX


    private val _isExpanded: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isExpanded: StateFlow<Boolean>
        get() = _isExpanded.asStateFlow()

    fun changeIsExpanded(isExpanded: Boolean) {
        _isExpanded.value = isExpanded
    }

    private val _selectList: MutableStateFlow<ListSelector> = MutableStateFlow(ListSelector.MAIN_LIST)
    val selectList: StateFlow<ListSelector>
        get() = _selectList.asStateFlow()


    fun changeSelectList(selectList: ListSelector) {
        _selectList.value = selectList
    }

    private val _isShown = MutableStateFlow(_currentTrackPlaying.value != null)
    val isShown: StateFlow<Boolean>
        get() = _isShown.asStateFlow()

    fun changeIsShown(isShown: Boolean) {
        _isShown.value = isShown
    }

    fun sentInfoToBottomSheet(
        track: Track,
        listSelect: ListSelector,
        trackINDEX: Int,
        currentTrackPlayingURI: String
    ) {
        _currentTrackPlaying.value = track
        _bottomSheetTrackINDEX.value = trackINDEX
        _selectList.value = listSelect
        _currentTrackPlayingURI.value = currentTrackPlayingURI
        _isShown.value = true
        updateNotification()
    }

    fun sentInfoToBottomSheet(
        track: Track
    ) {
        _currentTrackPlaying.value = track
    }




    /*
    ExoPlayer params and funcs
    * */

    val exoPlayer = audioPlayer.exoPlayer


    private val _repeatMode = MutableStateFlow(0)
    val repeatMode: StateFlow<Int>
        get() = _repeatMode

    fun changeRepeatMode(value: Int) {
        _repeatMode.value = value
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


    fun updateNotification() =
        audioNotification
            .updateNotification(currentTrackPlaying.value)

    /*
    * Initialization block
    * */

    init {
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                dataStore.readSortOption(SORT_OPTION_KEY).collect {
                    _sortType.value = it
                }
            }
            launch {
                dataStore.readRepeatMode(REPEAT_MODE_KEY).collect {
                    _repeatMode.value = it
                }
            }
        }
    }
}
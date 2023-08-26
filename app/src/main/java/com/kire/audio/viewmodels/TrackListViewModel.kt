package com.kire.audio.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kire.audio.repository.TrackRepository
import com.kire.audio.datastore.DataStore
import com.kire.audio.datastore.DataStoreConstants.SORT_OPTION_KEY
import com.kire.audio.events.SortOptionEvent
import com.kire.audio.events.SortType
import com.kire.audio.models.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackListViewModel @Inject constructor(
    private val dataStore: DataStore,
    private val trackRepository: TrackRepository
) : ViewModel(){


    private val _sortType = MutableStateFlow(SortType.DATA_DESC_ORDER)
    val sortType : StateFlow<SortType>
        get() = _sortType.asStateFlow()


    @OptIn(ExperimentalCoroutinesApi::class)
    private val _tracks = _sortType
        .flatMapLatest {sortType ->
            when(sortType) {
                SortType.DATA_ACS_ORDER -> trackRepository.getTracksOrderedByDateAddedASC()
                SortType.DATA_DESC_ORDER -> trackRepository.getTracksOrderedByDateAddedDESC()
                SortType.TITLE_ACS_ORDER -> trackRepository.getTracksOrderedByTitleASC()
                SortType.TITLE_DESC_ORDER -> trackRepository.getTracksOrderedByTitleDESC()
                SortType.ARTIST_ASC_ORDER -> trackRepository.getTracksOrderedByArtistASC()
                SortType.ARTIST_DESC_ORDER -> trackRepository.getTracksOrderedByArtistDESC()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tracks : StateFlow<List<Track>>
        get() = _tracks



    fun onEvent(event: SortOptionEvent) {
        when(event){
            is SortOptionEvent.ListTrackSortOption -> {
                _sortType.value = event.sortType
            }
        }
    }
    fun saveSortOption(value: SortType) =
        viewModelScope.launch {
            dataStore.saveSortOption(SORT_OPTION_KEY, value.toString())
        }


    fun loadTracksToDatabase(context: Context) = trackRepository.loadTracksToDatabase(context)
    fun deleteTracksFromDatabase(tracks :List<Track>) = trackRepository.deleteTracksFromDatabase(tracks)


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
        .combine(_tracks) { text, tracks ->
            if (text.isBlank())
                emptyList<Track>()
            else
                _tracks.value.filter{
                    it.doesMatchSearchQuery(text)
                }

        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList<Track>()
        )

    val searchResult : StateFlow<List<Track>>
        get() = _searchResult


    private val _bottomSheetTrackTitle = MutableStateFlow("")
    val bottomSheetTrackTitle: StateFlow<String>
        get() = _bottomSheetTrackTitle.asStateFlow()

    private val _bottomSheetTrackArtist = MutableStateFlow("")
    val bottomSheetTrackArtist: StateFlow<String>
        get() = _bottomSheetTrackArtist

    private val _bottomSheetTrackImageUri =
        MutableStateFlow(Uri.parse("android.resource://com.kire.audio/drawable/music_icon"))
    val bottomSheetTrackImageUri: StateFlow<Uri?>
        get() = _bottomSheetTrackImageUri


    private val _isExpanded: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isExpanded: StateFlow<Boolean>
        get() = _isExpanded.asStateFlow()

    fun changeIsExpended(isExpanded: Boolean) {
        _isExpanded.value = isExpanded
    }

    fun isShown(): Boolean {
        return _bottomSheetTrackTitle.value != "" && _bottomSheetTrackArtist.value != ""
    }

    fun sentInfoToBottomSheet(
        title: String,
        artist: String,
        imageUri: Uri?
    ) {
        _bottomSheetTrackTitle.value = title
        _bottomSheetTrackArtist.value = artist
        _bottomSheetTrackImageUri.value = imageUri
    }


    init {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.readSortOption(SORT_OPTION_KEY).collect {
                _sortType.value = it
            }
        }
    }
}
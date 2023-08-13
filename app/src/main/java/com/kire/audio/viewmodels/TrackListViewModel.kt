package com.kire.audio.viewmodels

import android.content.Context
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class TrackListViewModel @Inject constructor(
    private val dataStore: DataStore,
    val trackRepository: TrackRepository
) : ViewModel(){


    private val _sortType = MutableStateFlow(SortType.DATA_DESC_ORDER)
    val sortType : StateFlow<SortType>
        get() = _sortType



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
    fun readSortOption() : Flow<SortType> =
        runBlocking {
            dataStore.readSortOption(SORT_OPTION_KEY)
        }




    fun loadTracksToDatabase(context: Context) = trackRepository.loadTracksToDatabase(context)
    fun deleteTracksFromDatabase(tracks :List<Track>) = trackRepository.deleteTracksFromDatabase(tracks)




    init {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.readSortOption(SORT_OPTION_KEY).collect {
                _sortType.value = it
            }
        }
    }
}
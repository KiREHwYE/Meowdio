package com.kire.audio.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kire.audio.data.repositories.interfaces.ITrackRepository
import com.kire.audio.presentation.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/*
class ArtistViewModel(
    private val trackRepository: ITrackRepository
): ViewModel() {

    class Factory(
        private val trackRepository: ITrackRepository
    ): ViewModelProvider.Factory{
        override fun <T:ViewModel> create(modelClass: Class<T>):T{
            if(modelClass.isAssignableFrom(TrackViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return ArtistViewModel(
                    trackRepository
                ) as T
            }
            throw IllegalArgumentException("UNKNOWN VIEW MODEL CLASS")
        }
    }

    var artistWithTracks: Map<String, List<Track>> = emptyMap()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            artistWithTracks = trackRepository.getArtistsWithTracks()
        }
    }
}*/

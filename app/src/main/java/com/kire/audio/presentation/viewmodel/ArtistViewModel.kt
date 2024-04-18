package com.kire.audio.presentation.viewmodel

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

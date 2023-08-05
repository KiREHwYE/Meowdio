package com.kire.audio.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kire.audio.TrackRepository
import com.kire.audio.models.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackListViewModel @Inject constructor() : ViewModel(){

    private val trackRepository: TrackRepository = TrackRepository.get()

    private val _tracks: MutableStateFlow<List<Track>> = MutableStateFlow(mutableStateListOf())
    val tracks: StateFlow<List<Track>>
        get() = _tracks

    init{
        viewModelScope.launch(Dispatchers.Default) {
            trackRepository.getTracks().collect{
                _tracks.value = it
            }
        }
    }
}
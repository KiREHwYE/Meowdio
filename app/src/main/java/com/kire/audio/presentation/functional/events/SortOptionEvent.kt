package com.kire.audio.presentation.functional.events

sealed interface SortOptionEvent {
    data class ListTrackSortOption(val sortType: SortType) : SortOptionEvent
}
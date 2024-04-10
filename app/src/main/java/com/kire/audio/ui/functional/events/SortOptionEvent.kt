package com.kire.audio.ui.functional.events

sealed interface SortOptionEvent {
    data class ListTrackSortOption(val sortType: SortType) : SortOptionEvent
}
package com.kire.audio.events

sealed interface SortOptionEvent {

    data class ListTrackSortOption(val sortType: SortType) : SortOptionEvent

}
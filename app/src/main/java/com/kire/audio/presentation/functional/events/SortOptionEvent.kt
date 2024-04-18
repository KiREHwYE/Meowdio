package com.kire.audio.presentation.functional.events

import com.kire.audio.presentation.util.SortType

sealed interface SortOptionEvent {
    data class ListTrackSortOption(val sortType: SortType) : SortOptionEvent
}
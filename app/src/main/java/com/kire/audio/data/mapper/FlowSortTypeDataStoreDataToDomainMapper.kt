package com.kire.audio.data.mapper

import com.kire.audio.data.util.SortTypeDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun Flow<SortTypeDataStore>.asFlowSortType() = map { sortTypeDataStore ->
    sortTypeDataStore.asSortTypeDomain()
}

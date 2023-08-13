package com.kire.audio.datastore

import com.kire.audio.events.SortType
import kotlinx.coroutines.flow.Flow

interface DataStoreInterface {
    suspend fun saveSortOption(key: String, value: String)
    suspend fun readSortOption(key: String) : Flow<SortType>
}
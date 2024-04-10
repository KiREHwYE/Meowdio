package com.kire.audio.data.repositories

import com.kire.audio.mediaHandling.functional.RepeatMode
import com.kire.audio.ui.functional.events.SortType
import kotlinx.coroutines.flow.Flow

interface IPreferencesDataStoreRepository {

    suspend fun saveSortOption(key: String, value: String)
    fun readSortOption(key: String): Flow<SortType>
    suspend fun saveRepeatMode(key: String, value: String)
    fun readRepeatMode(key: String): Flow<RepeatMode>
}
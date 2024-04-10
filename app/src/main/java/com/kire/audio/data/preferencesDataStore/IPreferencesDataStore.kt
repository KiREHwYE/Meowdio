package com.kire.audio.data.preferencesDataStore

import com.kire.audio.ui.functional.events.SortType
import com.kire.audio.mediaHandling.functional.RepeatMode
import kotlinx.coroutines.flow.Flow

interface IPreferencesDataStore {
    suspend fun saveSortOption(key: String, value: String)
    fun readSortOption(key: String): Flow<SortType>
    suspend fun saveRepeatMode(key: String, value: String)
    fun readRepeatMode(key: String): Flow<RepeatMode>
}
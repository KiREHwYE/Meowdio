package com.kire.audio.data.preferencesDataStore

import com.kire.audio.data.util.SortTypeDataStore
import com.kire.audio.device.audio.functional.RepeatMode
import kotlinx.coroutines.flow.Flow

interface IPreferencesDataStore {
    suspend fun saveSortOption(key: String, value: String)
    fun readSortOption(key: String): Flow<SortTypeDataStore>
    suspend fun saveRepeatMode(key: String, value: String)
    fun readRepeatMode(key: String): Flow<RepeatMode>
}
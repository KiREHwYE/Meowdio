package com.kire.audio.data.repositories.interfaces

import com.kire.audio.device.audio.functional.RepeatMode
import com.kire.audio.presentation.functional.events.SortType
import kotlinx.coroutines.flow.Flow

interface IPreferencesDataStoreRepository {

    suspend fun saveSortOption(key: String, value: String)
    fun readSortOption(key: String): Flow<SortType>
    suspend fun saveRepeatMode(key: String, value: String)
    fun readRepeatMode(key: String): Flow<RepeatMode>
}
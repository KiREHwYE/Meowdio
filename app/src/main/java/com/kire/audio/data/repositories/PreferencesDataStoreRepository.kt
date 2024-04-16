package com.kire.audio.data.repositories

import com.kire.audio.data.preferencesDataStore.PreferencesDataStore
import com.kire.audio.data.repositories.interfaces.IPreferencesDataStoreRepository
import com.kire.audio.device.audio.functional.RepeatMode
import com.kire.audio.presentation.functional.events.SortType
import kotlinx.coroutines.flow.Flow

class PreferencesDataStoreRepository(
    private val preferencesDataStore: PreferencesDataStore
) : IPreferencesDataStoreRepository {

    override suspend fun saveSortOption(key: String, value: String) =
        preferencesDataStore.saveSortOption(key, value)

    override fun readSortOption(key: String): Flow<SortType> =
        preferencesDataStore.readSortOption(key)

    override suspend fun saveRepeatMode(key: String, value: String) =
        preferencesDataStore.saveRepeatMode(key, value)

    override fun readRepeatMode(key: String): Flow<RepeatMode> =
        preferencesDataStore.readRepeatMode(key)
}
package com.kire.audio.data.repository

import com.kire.audio.data.mapper.asFlowSortType
import com.kire.audio.data.preferencesDataStore.PreferencesDataStore
import com.kire.audio.domain.repository.IPreferencesRepository
import com.kire.audio.device.audio.util.RepeatMode
import com.kire.audio.domain.util.SortTypeDomain
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PreferencesRepository @Inject constructor(
    private val preferencesDataStore: PreferencesDataStore
) : IPreferencesRepository {

    override suspend fun saveSortOption(key: String, value: String) =
        preferencesDataStore.saveSortOption(key, value)

    override fun readSortOption(key: String): Flow<SortTypeDomain> =
        preferencesDataStore.readSortOption(key).asFlowSortType()

    override suspend fun saveRepeatMode(key: String, value: String) =
        preferencesDataStore.saveRepeatMode(key, value)

    override fun readRepeatMode(key: String): Flow<RepeatMode> =
        preferencesDataStore.readRepeatMode(key)
}
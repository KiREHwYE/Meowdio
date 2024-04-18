package com.kire.audio.domain.repository

import com.kire.audio.device.audio.functional.RepeatMode
import com.kire.audio.domain.util.SortTypeDomain
import kotlinx.coroutines.flow.Flow

interface IPreferencesRepository {
    suspend fun saveSortOption(key: String, value: String)
    fun readSortOption(key: String): Flow<SortTypeDomain>
    suspend fun saveRepeatMode(key: String, value: String)
    fun readRepeatMode(key: String): Flow<RepeatMode>
}
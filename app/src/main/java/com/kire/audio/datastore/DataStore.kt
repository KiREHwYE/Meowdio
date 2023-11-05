package com.kire.audio.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kire.audio.datastore.DataStoreConstants.DATASTORE_NAME
import com.kire.audio.events.SortType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

class DataStore(
    context: Context
): DataStoreInterface{

    private val dataStore = context.dataStore

    override suspend fun saveSortOption(key: String, value: String) {
        val dataStoreKey = stringPreferencesKey(key)
        dataStore.edit {
            it[dataStoreKey] = value
        }
    }
    override suspend fun readSortOption(key: String): Flow<SortType> {
        val dataStoreKey = stringPreferencesKey(key)
        return dataStore.data.map {
            SortType.valueOf(it[dataStoreKey] ?: "DATA_DESC_ORDER")
        }
    }

    override suspend fun saveRepeatMode(key: String, value: Int) {
        val dataStoreKey = intPreferencesKey(key)
        dataStore.edit {
            it[dataStoreKey] = value
        }
    }

    override suspend fun readRepeatMode(key: String): Flow<Int> {
        val dataStoreKey = intPreferencesKey(key)
        return dataStore.data.map {
            it[dataStoreKey] ?: 0
        }
    }
}
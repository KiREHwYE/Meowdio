package com.kire.audio.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import javax.inject.Inject
import androidx.datastore.preferences.preferencesDataStore
import com.kire.audio.datastore.DataStoreConstants.DATASTORE_NAME
import com.kire.audio.events.SortType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

class DataStore @Inject constructor(
    private val context: Context
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
}
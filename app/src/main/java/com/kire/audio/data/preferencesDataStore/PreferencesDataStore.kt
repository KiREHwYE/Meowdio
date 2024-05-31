package com.kire.audio.data.preferencesDataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kire.audio.data.util.SortTypeDataStore
import com.kire.audio.device.audio.util.RepeatMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
): IPreferencesDataStore {

    override suspend fun saveSortOption(key: String, value: String) {
        val dataStoreKey = stringPreferencesKey(key)
        dataStore.edit {
            it[dataStoreKey] = value
        }
    }
    override fun readSortOption(key: String): Flow<SortTypeDataStore> {
        val dataStoreKey = stringPreferencesKey(key)
        return dataStore.data.map {
            SortTypeDataStore.valueOf(it[dataStoreKey] ?: "DATA_DESC_ORDER")
        }
    }

    override suspend fun saveRepeatMode(key: String, value: String) {
        val dataStoreKey = stringPreferencesKey(key)
        dataStore.edit {
            it[dataStoreKey] = value
        }
    }

    override fun readRepeatMode(key: String): Flow<RepeatMode> {
        val dataStoreKey = stringPreferencesKey(key)
        return dataStore.data.map {
            RepeatMode.valueOf(it[dataStoreKey] ?: "REPEAT_ONCE")
        }
    }
}
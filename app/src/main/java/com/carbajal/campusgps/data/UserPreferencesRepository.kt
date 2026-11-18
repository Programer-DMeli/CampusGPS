package com.carbajal.campusgps.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

interface UserPreferencesRepository {
    val ultimoLugarId: Flow<Int?>
    suspend fun guardarUltimoLugarId(id: Int)
}

class UserPreferencesRepositoryImpl(
    private val context: Context
) : UserPreferencesRepository {

    private object PreferencesKeys {
        val ULTIMO_LUGAR_ID = intPreferencesKey("ultimo_lugar_id")
    }

    override val ultimoLugarId: Flow<Int?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.ULTIMO_LUGAR_ID]
    }

    override suspend fun guardarUltimoLugarId(id: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ULTIMO_LUGAR_ID] = id
        }
    }
}

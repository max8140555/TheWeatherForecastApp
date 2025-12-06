package com.max.theweatherforecastapp.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.max.theweatherforecastapp.core.domain.model.GeocodingLocation
import com.max.theweatherforecastapp.core.domain.repository.UserPreferencesRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val locationAdapter = moshi.adapter(GeocodingLocation::class.java)

    private object PreferencesKeys {
        val LAST_SELECTED_LOCATION = stringPreferencesKey("last_selected_location")
    }

    override val lastSelectedCity: Flow<GeocodingLocation?> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.LAST_SELECTED_LOCATION]?.let {
                locationAdapter.fromJson(it)
            }
        }

    override suspend fun saveLastSelectedLocation(location: GeocodingLocation) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_SELECTED_LOCATION] = locationAdapter.toJson(location)
        }
    }
}

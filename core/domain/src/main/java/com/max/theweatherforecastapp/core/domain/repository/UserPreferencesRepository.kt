package com.max.theweatherforecastapp.core.domain.repository

import com.max.theweatherforecastapp.core.domain.model.GeocodingLocation
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val lastSelectedCity: Flow<GeocodingLocation?>

    suspend fun saveLastSelectedLocation(location: GeocodingLocation)
}

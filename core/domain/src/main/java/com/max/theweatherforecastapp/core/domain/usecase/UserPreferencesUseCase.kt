package com.max.theweatherforecastapp.core.domain.usecase

import com.max.theweatherforecastapp.core.domain.repository.UserPreferencesRepository
import com.max.theweatherforecastapp.core.domain.model.GeocodingLocation
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserPreferencesUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    val lastSelectedCity: Flow<GeocodingLocation?> = userPreferencesRepository.lastSelectedCity

    suspend fun saveLastSelectedLocation(location: GeocodingLocation) {
        userPreferencesRepository.saveLastSelectedLocation(location)
    }
}

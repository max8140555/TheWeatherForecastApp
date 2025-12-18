package com.max.theweatherforecastapp.core.domain.usecase

import com.max.theweatherforecastapp.core.domain.model.AppResult
import com.max.theweatherforecastapp.core.domain.model.GeocodingLocation
import com.max.theweatherforecastapp.core.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationsByCityNameUseCase@Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(cityName: String): Flow<AppResult<List<GeocodingLocation>>> {
        return weatherRepository.getLocationsByCityName(cityName = cityName)
    }
}
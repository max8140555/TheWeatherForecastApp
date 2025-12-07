package com.max.theweatherforecastapp.core.domain.usecase

import com.max.theweatherforecastapp.core.domain.model.AppResult
import com.max.theweatherforecastapp.core.domain.model.GeocodingLocation
import com.max.theweatherforecastapp.core.domain.model.Weather
import com.max.theweatherforecastapp.core.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
     operator fun invoke(location: GeocodingLocation): Flow<AppResult<Weather>> {
        return weatherRepository.getWeather(location)
    }
}

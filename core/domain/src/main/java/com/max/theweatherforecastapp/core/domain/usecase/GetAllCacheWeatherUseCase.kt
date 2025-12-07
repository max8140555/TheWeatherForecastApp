package com.max.theweatherforecastapp.core.domain.usecase

import com.max.theweatherforecastapp.core.domain.model.Weather
import com.max.theweatherforecastapp.core.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCacheWeatherUseCase@Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    operator fun invoke(): Flow<List<Weather>> {
        return weatherRepository.getAllCacheWeather()
    }
}
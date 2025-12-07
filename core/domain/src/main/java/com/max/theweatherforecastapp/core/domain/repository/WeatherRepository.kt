package com.max.theweatherforecastapp.core.domain.repository

import com.max.theweatherforecastapp.core.domain.model.GeocodingLocation
import com.max.theweatherforecastapp.core.domain.model.Weather
import com.max.theweatherforecastapp.core.domain.model.AppResult
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getWeather(
        location: GeocodingLocation,
        exclude: String? = null
    ): Flow<AppResult<Weather>>

    fun getLocationsByCityName(
        cityName: String
    ): Flow<AppResult<List<GeocodingLocation>>>

    fun getAllCacheWeather(): Flow<List<Weather>>
}

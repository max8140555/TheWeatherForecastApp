package com.max.theweatherforecastapp.core.domain.model

import kotlinx.collections.immutable.ImmutableList

data class Weather(
    val name: String,
    val lat: Double,
    val lon: Double,
    val locationCountry: String,
    val locationState: String?,
    val timezone: String,
    val current: Current?,
    val hourly: ImmutableList<Hourly>,
    val daily: ImmutableList<Daily>
)

data class Current(
    val temp: Double,
    val weather: ImmutableList<WeatherDetail>
)

data class Hourly(
    val dt: Long,
    val temp: Double,
    val weather: ImmutableList<WeatherDetail>
)

data class Daily(
    val dt: Long,
    val temp: Temp,
    val weather: ImmutableList<WeatherDetail>
)

data class Temp(
    val min: Double,
    val max: Double
)

data class WeatherDetail(
    val main: String,
    val description: String,
    val icon: String
)

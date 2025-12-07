package com.max.theweatherforecastapp.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherResponse(
    @Json(name = "lat") val lat: Double,
    @Json(name = "lon") val lon: Double,
    @Json(name = "timezone") val timezone: String,
    @Json(name = "timezone_offset") val timezoneOffset: Int,
    @Json(name = "current") val current: CurrentWeather?,
    @Json(name = "hourly") val hourly: List<HourlyWeather>?,
    @Json(name = "daily") val daily: List<DailyWeather>?
)

@JsonClass(generateAdapter = true)
data class CurrentWeather(
    @Json(name = "dt") val dt: Long,
    @Json(name = "sunrise") val sunrise: Long?,
    @Json(name = "sunset") val sunset: Long?,
    @Json(name = "temp") val temp: Double,
    @Json(name = "feels_like") val feelsLike: Double,
    @Json(name = "pressure") val pressure: Int,
    @Json(name = "humidity") val humidity: Int,
    @Json(name = "dew_point") val dewPoint: Double,
    @Json(name = "uvi") val uvi: Double,
    @Json(name = "clouds") val clouds: Int,
    @Json(name = "visibility") val visibility: Int?,
    @Json(name = "wind_speed") val windSpeed: Double,
    @Json(name = "wind_deg") val windDeg: Int,
    @Json(name = "weather") val weather: List<WeatherDescription>
)

@JsonClass(generateAdapter = true)
data class HourlyWeather(
    @Json(name = "dt") val dt: Long,
    @Json(name = "temp") val temp: Double,
    @Json(name = "feels_like") val feelsLike: Double,
    @Json(name = "pressure") val pressure: Int,
    @Json(name = "humidity") val humidity: Int,
    @Json(name = "dew_point") val dewPoint: Double,
    @Json(name = "uvi") val uvi: Double,
    @Json(name = "clouds") val clouds: Int,
    @Json(name = "visibility") val visibility: Int?,
    @Json(name = "wind_speed") val windSpeed: Double,
    @Json(name = "wind_deg") val windDeg: Int,
    @Json(name = "weather") val weather: List<WeatherDescription>,
    @Json(name = "pop") val pop: Double
)

@JsonClass(generateAdapter = true)
data class DailyWeather(
    @Json(name = "dt") val dt: Long,
    @Json(name = "sunrise") val sunrise: Long,
    @Json(name = "sunset") val sunset: Long,
    @Json(name = "moonrise") val moonrise: Long,
    @Json(name = "moonset") val moonset: Long,
    @Json(name = "moon_phase") val moonPhase: Double,
    @Json(name = "summary") val summary: String,
    @Json(name = "temp") val temp: Temp,
    @Json(name = "feels_like") val feelsLike: FeelsLike,
    @Json(name = "pressure") val pressure: Int,
    @Json(name = "humidity") val humidity: Int,
    @Json(name = "dew_point") val dewPoint: Double,
    @Json(name = "wind_speed") val windSpeed: Double,
    @Json(name = "wind_deg") val windDeg: Int,
    @Json(name = "weather") val weather: List<WeatherDescription>,
    @Json(name = "clouds") val clouds: Int,
    @Json(name = "pop") val pop: Double,
    @Json(name = "uvi") val uvi: Double
)

@JsonClass(generateAdapter = true)
data class Temp(
    @Json(name = "day") val day: Double,
    @Json(name = "min") val min: Double,
    @Json(name = "max") val max: Double,
    @Json(name = "night") val night: Double,
    @Json(name = "eve") val eve: Double,
    @Json(name = "morn") val morn: Double
)

@JsonClass(generateAdapter = true)
data class FeelsLike(
    @Json(name = "day") val day: Double,
    @Json(name = "night") val night: Double,
    @Json(name = "eve") val eve: Double,
    @Json(name = "morn") val morn: Double
)

@JsonClass(generateAdapter = true)
data class WeatherDescription(
    @Json(name = "id") val id: Int,
    @Json(name = "main") val main: String,
    @Json(name = "description") val description: String,
    @Json(name = "icon") val icon: String
)

package com.max.theweatherforecastapp.core.data.mapper

import com.max.theweatherforecastapp.core.data.model.WeatherEntity
import com.max.theweatherforecastapp.core.domain.model.Current
import com.max.theweatherforecastapp.core.domain.model.Daily
import com.max.theweatherforecastapp.core.domain.model.Hourly
import com.max.theweatherforecastapp.core.domain.model.Weather
import com.max.theweatherforecastapp.core.domain.model.Temp
import com.max.theweatherforecastapp.core.domain.model.WeatherDetail
import com.max.theweatherforecastapp.core.network.model.WeatherResponse
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import com.max.theweatherforecastapp.core.data.model.CurrentWeather as CurrentWeatherEntity
import com.max.theweatherforecastapp.core.data.model.DailyWeather as DailyWeatherEntity
import com.max.theweatherforecastapp.core.data.model.FeelsLike as FeelsLikeEntity
import com.max.theweatherforecastapp.core.data.model.HourlyWeather as HourlyWeatherEntity
import com.max.theweatherforecastapp.core.data.model.Temp as TempEntity
import com.max.theweatherforecastapp.core.data.model.WeatherDescription as WeatherDescriptionEntity
import com.max.theweatherforecastapp.core.network.model.CurrentWeather as CurrentWeatherResponse
import com.max.theweatherforecastapp.core.network.model.DailyWeather as DailyWeatherResponse
import com.max.theweatherforecastapp.core.network.model.FeelsLike as FeelsLikeResponse
import com.max.theweatherforecastapp.core.network.model.HourlyWeather as HourlyWeatherResponse
import com.max.theweatherforecastapp.core.network.model.Temp as TempResponse
import com.max.theweatherforecastapp.core.network.model.WeatherDescription as WeatherDescriptionResponse

fun WeatherResponse.toEntity(
    name: String,
    locationCountry: String?,
    locationState: String?
): WeatherEntity =
    WeatherEntity(
        name = name,
        locationCountry = locationCountry ?: "",
        locationState = locationState ?: "",
        lat = lat,
        lon = lon,
        timezone = timezone,
        timezoneOffset = timezoneOffset,
        current = current?.toEntity(),
        hourly = hourly?.map { it.toEntity() },
        daily = daily?.map { it.toEntity() }
    )

private fun CurrentWeatherResponse.toEntity(): CurrentWeatherEntity =
    CurrentWeatherEntity(
        dt = dt,
        sunrise = sunrise,
        sunset = sunset,
        temp = temp,
        feelsLike = feelsLike,
        pressure = pressure,
        humidity = humidity,
        dewPoint = dewPoint,
        uvi = uvi,
        clouds = clouds,
        visibility = visibility,
        windSpeed = windSpeed,
        windDeg = windDeg,
        weather = weather.map { it.toEntity() }
    )

private fun HourlyWeatherResponse.toEntity(): HourlyWeatherEntity =
    HourlyWeatherEntity(
        dt = dt,
        temp = temp,
        feelsLike = feelsLike,
        pressure = pressure,
        humidity = humidity,
        dewPoint = dewPoint,
        uvi = uvi,
        clouds = clouds,
        visibility = visibility,
        windSpeed = windSpeed,
        windDeg = windDeg,
        weather = weather.map { it.toEntity() },
        pop = pop
    )

private fun DailyWeatherResponse.toEntity(): DailyWeatherEntity =
    DailyWeatherEntity(
        dt = dt,
        sunrise = sunrise,
        sunset = sunset,
        moonrise = moonrise,
        moonset = moonset,
        moonPhase = moonPhase,
        summary = summary,
        temp = temp.toEntity(),
        feelsLike = feelsLike.toEntity(),
        pressure = pressure,
        humidity = humidity,
        dewPoint = dewPoint,
        windSpeed = windSpeed,
        windDeg = windDeg,
        weather = weather.map { it.toEntity() },
        clouds = clouds,
        pop = pop,
        uvi = uvi
    )

private fun TempResponse.toEntity(): TempEntity =
    TempEntity(
        day = day,
        min = min,
        max = max,
        night = night,
        eve = eve,
        morn = morn
    )

private fun FeelsLikeResponse.toEntity(): FeelsLikeEntity =
    FeelsLikeEntity(
        day = day,
        night = night,
        eve = eve,
        morn = morn
    )

private fun WeatherDescriptionResponse.toEntity(): WeatherDescriptionEntity =
    WeatherDescriptionEntity(
        id = id,
        main = main,
        description = description,
        icon = icon
    )

fun WeatherEntity.toDomain(): Weather =
    Weather(
        name = name,
        lat = lat,
        lon = lon,
        locationCountry = locationCountry ?: "",
        locationState = locationState,
        timezone = timezone,
        current = current?.toDomain(),
        hourly = hourly?.map { it.toDomain() }?.toImmutableList() ?: persistentListOf(),
        daily = daily?.map { it.toDomain() }?.toImmutableList() ?: persistentListOf(),
    )

private fun CurrentWeatherEntity.toDomain(): Current =
    Current(
        temp = temp,
        weather = weather.map { it.toDomain() }.toImmutableList()
    )

private fun HourlyWeatherEntity.toDomain(): Hourly =
    Hourly(
        dt = dt,
        temp = temp,
        weather = weather.map { it.toDomain() }.toImmutableList()
    )

private fun DailyWeatherEntity.toDomain(): Daily =
    Daily(
        dt = dt,
        temp = temp.toDomain(),
        weather = weather.map { it.toDomain() }.toImmutableList()
    )

private fun TempEntity.toDomain(): Temp =
    Temp(
        min = min,
        max = max
    )

private fun WeatherDescriptionEntity.toDomain(): WeatherDetail =
    WeatherDetail(
        main = main,
        description = description,
        icon = icon
    )

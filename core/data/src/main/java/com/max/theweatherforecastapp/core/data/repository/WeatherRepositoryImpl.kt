package com.max.theweatherforecastapp.core.data.repository

import com.max.theweatherforecastapp.core.data.database.WeatherDao
import com.max.theweatherforecastapp.core.data.mapper.HttpErrorMapper
import com.max.theweatherforecastapp.core.data.mapper.toDomain
import com.max.theweatherforecastapp.core.data.mapper.toEntity
import com.max.theweatherforecastapp.core.data.util.safeApiCall
import com.max.theweatherforecastapp.core.domain.model.AppResult
import com.max.theweatherforecastapp.core.domain.model.GeocodingLocation
import com.max.theweatherforecastapp.core.domain.model.Weather
import com.max.theweatherforecastapp.core.domain.repository.WeatherRepository
import com.max.theweatherforecastapp.core.network.BuildConfig
import com.max.theweatherforecastapp.core.network.api.WeatherApiService
import com.max.theweatherforecastapp.core.data.mapper.toDomainError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherApiService: WeatherApiService,
    private val weatherDao: WeatherDao,
    private val coroutineDispatcher: CoroutineDispatcher,
    private val httpErrorMapper: HttpErrorMapper
) : WeatherRepository {

    override fun getWeather(
        location: GeocodingLocation,
        exclude: String?
    ): Flow<AppResult<Weather>> {
        return flow {
            val now = System.currentTimeMillis()
            val cachedWeather = weatherDao.getWeatherByName(location.name)

            if (cachedWeather != null && isCacheFresh(cachedWeather.fetchedAt, now)) {
                weatherDao.updateLastUsedTimestamp(location.name, now)
                emit(AppResult.Success(cachedWeather.toDomain()))
                return@flow
            }

            val result = safeApiCall(
                dispatcher = coroutineDispatcher,
                httpErrorMapper = httpErrorMapper,
                apiCall = {
                    weatherApiService.getWeather(
                        lat = location.lat,
                        lon = location.lon,
                        exclude = exclude,
                        apiKey = BuildConfig.OPENWEATHERMAP_API_KEY
                    )
                },
                mapBody = {
                    val entity = it.toEntity(
                        name = location.name,
                        locationCountry = location.country,
                        locationState = location.state,
                    )
                    weatherDao.insertWeather(entity)
                    entity.toDomain()
                }
            )
            emit(result)
        }.onStart {
            emit(AppResult.Loading)
        }.catch { e ->
            emit(AppResult.Failure(e.toDomainError()))
        }
    }

    override fun getAllCacheWeather(): Flow<List<Weather>> {
        return weatherDao.getAllCacheWeather().map { list -> list.map { it.toDomain() } }
    }

    override fun getLocationsByCityName(
        cityName: String
    ): Flow<AppResult<List<GeocodingLocation>>> {
        return flow {
           val result = safeApiCall(
                dispatcher = coroutineDispatcher,
                httpErrorMapper = httpErrorMapper,
                apiCall = {
                    weatherApiService.getLocationsByCityName(
                        cityName = cityName,
                        apiKey = BuildConfig.OPENWEATHERMAP_API_KEY
                    )
                },
                mapBody = { it.map { item -> item.toDomain() } }
            )
            emit(result)
        }
            .onStart { emit(AppResult.Loading) }
            .catch { e -> emit(AppResult.Failure(e.toDomainError())) }
    }

    private fun isCacheFresh(cacheTime: Long, currentTime: Long): Boolean {
        val cacheHour = TimeUnit.MILLISECONDS.toHours(cacheTime)
        val currentHour = TimeUnit.MILLISECONDS.toHours(currentTime)
        return cacheHour == currentHour
    }
}

package com.max.theweatherforecastapp.core.data.repository

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.max.theweatherforecastapp.core.data.database.WeatherDao
import com.max.theweatherforecastapp.core.data.mapper.HttpErrorMapper
import com.max.theweatherforecastapp.core.data.model.WeatherEntity
import com.max.theweatherforecastapp.core.domain.model.AppResult
import com.max.theweatherforecastapp.core.domain.model.DomainError
import com.max.theweatherforecastapp.core.domain.model.GeocodingLocation
import com.max.theweatherforecastapp.core.network.api.WeatherApiService
import com.max.theweatherforecastapp.core.network.model.GeocodingLocationResponseItem
import com.max.theweatherforecastapp.core.network.model.WeatherResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.util.concurrent.TimeUnit

class WeatherRepositoryImplTest {

    private lateinit var weatherApiService: WeatherApiService
    private lateinit var weatherDao: WeatherDao
    private lateinit var httpErrorMapper: HttpErrorMapper
    private lateinit var repository: WeatherRepositoryImpl
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Before
    fun setUp() {
        weatherApiService = mockk()
        weatherDao = mockk(relaxed = true)
        httpErrorMapper = HttpErrorMapper(moshi)
        repository = WeatherRepositoryImpl(
            weatherApiService = weatherApiService,
            weatherDao = weatherDao,
            coroutineDispatcher = Dispatchers.Unconfined,
            httpErrorMapper = httpErrorMapper
        )
    }

    @Test
    fun `getWeather returns weather from cache if cache is fresh`() = runTest {
        val location = GeocodingLocation(
            name = "Taipei",
            lat = 25.0,
            lon = 121.0,
            country = "TW",
            state = "TP"
        )
        val now = System.currentTimeMillis()
        val cachedWeather = WeatherEntity(
            name = "Taipei",
            locationCountry = "TW",
            locationState = "TP",
            lat = 25.0,
            lon = 121.0,
            fetchedAt = now,
            lastUsedAt = now,
            timezone = "",
            timezoneOffset = 0,
            current = mockk(relaxed = true),
            hourly = emptyList(),
            daily = emptyList()
        )
        coEvery { weatherDao.getWeatherByName("Taipei") } returns cachedWeather

        repository.getWeather(location).test {
            val loading = awaitItem()
            assertThat(loading).isInstanceOf(AppResult.Loading::class.java)

            val success = awaitItem()
            assertThat(success).isInstanceOf(AppResult.Success::class.java)
            assertThat((success as AppResult.Success).data.name).isEqualTo("Taipei")
            awaitComplete()
        }

        coVerify(exactly = 1) { weatherDao.getWeatherByName("Taipei") }
        coVerify(exactly = 0) { weatherApiService.getWeather(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `getWeather fetches from network if cache is stale`() = runTest {
        val location = GeocodingLocation(
            name = "Taipei",
            lat = 25.0,
            lon = 121.0,
            country = "TW",
            state = "TP"
        )
        val staleTime = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(2)
        val cachedWeather = WeatherEntity(
            name = "Taipei",
            fetchedAt = staleTime,
            lat = 25.0,
            lon = 121.0,
            locationCountry = "TW",
            locationState = "TP",
            lastUsedAt = staleTime,
            timezone = "",
            timezoneOffset = 0,
            current = mockk(relaxed = true),
            hourly = emptyList(),
            daily = emptyList()
        )
        val networkWeather = WeatherResponse(
            lat = 25.0,
            lon = 121.0,
            timezone = "Asia/Taipei",
            timezoneOffset = 28800,
            current = mockk(relaxed = true),
            hourly = null,
            daily = null
        )

        coEvery { weatherDao.getWeatherByName("Taipei") } returns cachedWeather
        coEvery {
            weatherApiService.getWeather(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns Response.success(
            networkWeather
        )
        coEvery { weatherDao.insertWeather(any()) } just Runs

        repository.getWeather(location).test {
            assertThat(awaitItem()).isInstanceOf(AppResult.Loading::class.java)
            val result = awaitItem()
            assertThat(result).isInstanceOf(AppResult.Success::class.java)
            awaitComplete()
        }

        coVerify(exactly = 1) { weatherApiService.getWeather(any(), any(), any(), any(), any()) }
        coVerify(exactly = 1) { weatherDao.insertWeather(any()) }
    }

    @Test
    fun `getWeather fetches from network if cache is empty`() = runTest {
        val location = GeocodingLocation(
            name = "Taipei",
            lat = 25.0,
            lon = 121.0,
            country = "TW",
            state = "TP"
        )
        val networkWeather = WeatherResponse(
            lat = 25.0,
            lon = 121.0,
            timezone = "Asia/Taipei",
            timezoneOffset = 28800,
            current = mockk(relaxed = true),
            hourly = null,
            daily = null
        )

        coEvery { weatherDao.getWeatherByName("Taipei") } returns null
        coEvery {
            weatherApiService.getWeather(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns Response.success(
            networkWeather
        )
        coEvery { weatherDao.insertWeather(any()) } just Runs

        repository.getWeather(location).test {
            assertThat(awaitItem()).isInstanceOf(AppResult.Loading::class.java)
            val result = awaitItem()
            assertThat(result).isInstanceOf(AppResult.Success::class.java)
            awaitComplete()
        }

        coVerify(exactly = 1) { weatherApiService.getWeather(any(), any(), any(), any(), any()) }
        coVerify(exactly = 1) { weatherDao.insertWeather(any()) }
    }

    @Test
    fun `getWeather returns failure when network call fails`() = runTest {
        val location = GeocodingLocation(
            name = "Taipei",
            lat = 25.0,
            lon = 121.0,
            country = "TW",
            state = "TP"
        )
        val errorBody =
            "{\"error\":\"not found\"}".toResponseBody("application/json".toMediaTypeOrNull())
        val exception = HttpException(Response.error<Any>(404, errorBody))

        coEvery { weatherDao.getWeatherByName("Taipei") } returns null
        coEvery { weatherApiService.getWeather(any(), any(), any(), any(), any()) } throws exception

        repository.getWeather(location).test {
            assertThat(awaitItem()).isInstanceOf(AppResult.Loading::class.java)
            val result = awaitItem()
            assertThat(result).isInstanceOf(AppResult.Failure::class.java)
            val failure = result as AppResult.Failure
            assertThat(failure.error).isInstanceOf(DomainError.NotFound::class.java)
            awaitComplete()
        }
    }

    @Test
    fun `getWeather uses exclude parameter in api call`() = runTest {
        val location = GeocodingLocation(
            name = "Taipei",
            lat = 25.0,
            lon = 121.0,
            country = "TW",
            state = "TP"
        )
        val exclude = "minutely,hourly"
        val networkWeather = WeatherResponse(
            lat = 25.0,
            lon = 121.0,
            timezone = "Asia/Taipei",
            timezoneOffset = 28800,
            current = mockk(relaxed = true),
            hourly = null,
            daily = null
        )


        coEvery { weatherDao.getWeatherByName("Taipei") } returns null
        coEvery {
            weatherApiService.getWeather(
                lat = location.lat,
                lon = location.lon,
                exclude = exclude,
                apiKey = ""
            )
        } returns Response.success(networkWeather)

        repository.getWeather(location, exclude).test {
            awaitItem() // Loading
            awaitItem() // Success
            awaitComplete()
        }

        coVerify {
            weatherApiService.getWeather(
                lat = location.lat,
                lon = location.lon,
                exclude = exclude,
                apiKey = any()
            )
        }
    }

    @Test
    fun `getAllCacheWeather returns all cached weather`() = runTest {
        val cachedWeathers = listOf(
            WeatherEntity(
                name = "Taipei",
                fetchedAt = 0,
                lat = 25.0,
                lon = 121.0,
                locationCountry = "TW",
                locationState = "TP",
                lastUsedAt = 0,
                timezone = "",
                timezoneOffset = 0,
                current = mockk(relaxed = true),
                hourly = emptyList(),
                daily = emptyList()
            ),
            WeatherEntity(
                name = "London",
                fetchedAt = 0,
                lat = 51.5,
                lon = -0.12,
                locationCountry = "GB",
                locationState = null,
                lastUsedAt = 0,
                timezone = "",
                timezoneOffset = 0,
                current = mockk(relaxed = true),
                hourly = emptyList(),
                daily = emptyList()
            )
        )
        coEvery { weatherDao.getAllCacheWeather() } returns flowOf(cachedWeathers)

        val result = repository.getAllCacheWeather().first()

        assertThat(result.size).isEqualTo(2)
        assertThat(result.first().name).isEqualTo("Taipei")
    }

    @Test
    fun `getLocationsByCityName returns locations from network`() = runTest {
        val cityName = "London"
        val networkLocations = listOf(
            GeocodingLocationResponseItem(
                name = "London",
                lat = 51.5,
                lon = -0.12,
                country = "GB",
                state = null
            )
        )
        coEvery {
            weatherApiService.getLocationsByCityName(
                cityName = cityName,
                limit = 5,
                apiKey = any()
            )
        } returns Response.success(networkLocations)

        repository.getLocationsByCityName(cityName).test {
            assertThat(awaitItem()).isInstanceOf(AppResult.Loading::class.java)

            val success = awaitItem()
            assertThat(success).isInstanceOf(AppResult.Success::class.java)
            val locations = (success as AppResult.Success).data
            assertThat(locations).isNotEmpty()
            assertThat(locations.first().name).isEqualTo("London")
            awaitComplete()
        }
    }

    @Test
    fun `getLocationsByCityName returns failure when network call fails`() = runTest {
        val cityName = "London"
        val errorBody =
            "{\"error\":\"not found\"}".toResponseBody("application/json".toMediaTypeOrNull())
        val exception = HttpException(Response.error<Any>(404, errorBody))

        coEvery { weatherApiService.getLocationsByCityName(cityName, 5, any()) } throws exception

        repository.getLocationsByCityName(cityName).test {
            assertThat(awaitItem()).isInstanceOf(AppResult.Loading::class.java)
            val result = awaitItem()
            assertThat(result).isInstanceOf(AppResult.Failure::class.java)
            val failure = result as AppResult.Failure
            assertThat(failure.error).isInstanceOf(DomainError.NotFound::class.java)
            awaitComplete()
        }
    }
}

package com.max.theweatherforecastapp.feature.home

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.max.theweatherforecastapp.core.domain.model.AppResult
import com.max.theweatherforecastapp.core.domain.model.DomainError
import com.max.theweatherforecastapp.core.domain.model.GeocodingLocation
import com.max.theweatherforecastapp.core.domain.model.Weather
import com.max.theweatherforecastapp.core.domain.usecase.GetAllCacheWeatherUseCase
import com.max.theweatherforecastapp.core.domain.usecase.GetLocationsByCityNameUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi @Suppress("UnusedFlow")
class HomeViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private  var getLocationsByCityNameUseCase: GetLocationsByCityNameUseCase =  mockk(relaxed = true)
    private val getAllCacheWeatherUseCase: GetAllCacheWeatherUseCase = mockk(relaxed = true)
    private lateinit var viewModel: HomeViewModel

    @Test
    fun `loadAllWeather success updates history ui state to success`() = runTest {
        val mockWeather = Weather(
            name = "Taipei",
            lat = 25.0,
            lon = 121.0,
            locationState = "TP",
            locationCountry = "TW",
            timezone = "",
            current = mockk(),
            hourly = emptyList(),
            daily = emptyList()
        )
        coEvery { getAllCacheWeatherUseCase.invoke() } returns flowOf(listOf(mockWeather))

        viewModel = HomeViewModel(
            getLocationsByCityNameUseCase = getLocationsByCityNameUseCase,
            getAllCacheWeatherUseCase = getAllCacheWeatherUseCase
        )

        viewModel.historyLocationUiState.test {
            val successState = awaitItem() as HistoryLocationUiState.Success
            assertThat(successState.historyLocation).hasSize(1)
            assertThat(successState.historyLocation.first().name).isEqualTo("Taipei")
        }
    }

    @Test
    fun `loadAllWeather failure updates history ui state to error`() = runTest {
        val exception = RuntimeException("Database error")
        coEvery { getAllCacheWeatherUseCase.invoke() } returns flow { throw exception }

        viewModel = HomeViewModel(
            getLocationsByCityNameUseCase = getLocationsByCityNameUseCase,
            getAllCacheWeatherUseCase = getAllCacheWeatherUseCase
        )

        viewModel.historyLocationUiState.test {
            val errorState = awaitItem() as HistoryLocationUiState.Error
            assertThat(errorState.error).isInstanceOf(DomainError.Unknown::class.java)
        }
    }

    @Test
    fun `search query change triggers location search`() = runTest {
        val query = "London"
        val mockLocations = listOf(GeocodingLocation("London", 51.5, -0.12, "GB", "England"))
        coEvery { getLocationsByCityNameUseCase.invoke(query) } returns flowOf(AppResult.Success(mockLocations))

        viewModel = HomeViewModel(
            getLocationsByCityNameUseCase = getLocationsByCityNameUseCase,
            getAllCacheWeatherUseCase = getAllCacheWeatherUseCase
        )

        viewModel.searchUiState.test {
            viewModel.onSearchQueryChanged(query)
            advanceTimeBy(501L) // Debounce delay is 500ms

            coVerify { getLocationsByCityNameUseCase.invoke(query) }

            assertThat(awaitItem()).isInstanceOf(SearchUiState.Idle::class.java)
            val successState = awaitItem() as SearchUiState.Success
            assertThat(successState.locations).isNotEmpty()
            assertThat(successState.locations.first().name).isEqualTo("London")
        }
    }

    @Test
    fun `search result loading updates search ui state to loading`() = runTest {
        val query = "Paris"
        coEvery { getLocationsByCityNameUseCase.invoke(query) } returns flowOf(AppResult.Loading)
        viewModel = HomeViewModel(
            getLocationsByCityNameUseCase = getLocationsByCityNameUseCase,
            getAllCacheWeatherUseCase = getAllCacheWeatherUseCase
        )

        viewModel.searchUiState.test {
            viewModel.onSearchQueryChanged(query)
            advanceTimeBy(501L) // Debounce delay is 500ms

            assertThat(awaitItem()).isInstanceOf(SearchUiState.Idle::class.java)
            assertThat(awaitItem()).isInstanceOf(SearchUiState.Loading::class.java)
        }
    }

    @Test
    fun `search result error updates search ui state to error`() = runTest {
        val query = "UnknownCity"
        coEvery { getLocationsByCityNameUseCase.invoke(query) } returns flowOf(AppResult.Failure(DomainError.NotFound))
        viewModel = HomeViewModel(
            getLocationsByCityNameUseCase = getLocationsByCityNameUseCase,
            getAllCacheWeatherUseCase = getAllCacheWeatherUseCase
        )

        viewModel.searchUiState.test {
            viewModel.onSearchQueryChanged(query)
            advanceTimeBy(501L) // Debounce delay is 500ms

            assertThat(awaitItem()).isInstanceOf(SearchUiState.Idle::class.java)
            val errorState = awaitItem() as SearchUiState.Error
            assertThat(errorState.error).isInstanceOf(DomainError.NotFound::class.java)
        }
    }

    @Test
    fun `blank search query resets search ui state to idle`() = runTest {
        viewModel = HomeViewModel(
            getLocationsByCityNameUseCase = getLocationsByCityNameUseCase,
            getAllCacheWeatherUseCase = getAllCacheWeatherUseCase
        )
        viewModel.onSearchQueryChanged("some query")
        advanceTimeBy(501L) // Debounce delay is 500ms

        viewModel.onSearchQueryChanged("")

        viewModel.searchUiState.test {
            assertThat(awaitItem()).isInstanceOf(SearchUiState.Idle::class.java)
        }
    }
}

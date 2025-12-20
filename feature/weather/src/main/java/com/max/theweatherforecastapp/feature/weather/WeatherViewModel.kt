package com.max.theweatherforecastapp.feature.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.max.theweatherforecastapp.core.domain.model.GeocodingLocation
import com.max.theweatherforecastapp.core.domain.model.Weather
import com.max.theweatherforecastapp.core.domain.model.AppResult
import com.max.theweatherforecastapp.core.domain.model.DomainError
import com.max.theweatherforecastapp.core.domain.usecase.GetWeatherUseCase
import com.max.theweatherforecastapp.core.domain.usecase.UserPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val userPreferencesUseCase: UserPreferencesUseCase
) : ViewModel() {

    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Idle)
    val weatherState: StateFlow<WeatherState> = _weatherState

    init {
        viewModelScope.launch {
            userPreferencesUseCase.lastSelectedCity.collect { location ->
                location?.let {
                    fetchWeather(it)
                }
            }
        }
    }

    private fun fetchWeather(location: GeocodingLocation) {
        viewModelScope.launch {
            getWeatherUseCase(location).collect { result ->
                _weatherState.value = when (result) {
                    is AppResult.Loading -> WeatherState.Loading
                    is AppResult.Success -> WeatherState.Success(result.data)
                    is AppResult.Failure -> WeatherState.Error(result.error)
                }
            }
        }
    }
}

sealed interface WeatherState {
    object Idle : WeatherState
    object Loading : WeatherState
    data class Success(val data: Weather) : WeatherState
    data class Error(val error: DomainError) : WeatherState
}

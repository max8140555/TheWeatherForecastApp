package com.max.theweatherforecastapp.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.max.theweatherforecastapp.core.domain.model.GeocodingLocation
import com.max.theweatherforecastapp.core.domain.model.AppResult
import com.max.theweatherforecastapp.core.domain.model.DomainError
import com.max.theweatherforecastapp.core.domain.usecase.GetAllCacheWeatherUseCase
import com.max.theweatherforecastapp.core.domain.usecase.GetLocationsByCityNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

sealed interface SearchUiState {
    object Idle : SearchUiState
    object Loading : SearchUiState
    data class Success(val locations: List<GeocodingLocation>) : SearchUiState
    data class Error(val error: DomainError) : SearchUiState
}

sealed interface HistoryLocationUiState {
    object Loading : HistoryLocationUiState
    data class Success(val historyLocation: List<GeocodingLocation>) : HistoryLocationUiState
    data class Error(val error: DomainError) : HistoryLocationUiState
}

@OptIn(FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getLocationsByCityNameUseCase: GetLocationsByCityNameUseCase,
    private val getAllCacheWeatherUseCase: GetAllCacheWeatherUseCase,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchUiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val searchUiState = _searchUiState.asStateFlow()

    private val _historyLocationUiState =
        MutableStateFlow<HistoryLocationUiState>(HistoryLocationUiState.Loading)
    val historyLocationUiState = _historyLocationUiState.asStateFlow()

    init {
        getHistoryLocations()

        _searchQuery
            .debounce(500L)
            .filter { it.isNotBlank() }
            .distinctUntilChanged()
            .flatMapLatest { query ->
                getLocationsByCityNameUseCase.invoke(query)
            }
            .onEach { result ->
                _searchUiState.value = when (result) {
                    is AppResult.Loading -> SearchUiState.Loading
                    is AppResult.Success -> SearchUiState.Success(result.data)
                    is AppResult.Failure -> SearchUiState.Error(result.error)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun getHistoryLocations() {
        getAllCacheWeatherUseCase.invoke()
            .onEach { weatherList ->
                val historyCity = weatherList.map {
                    GeocodingLocation(
                        name = it.name,
                        lat = it.lat,
                        lon = it.lon,
                        state = it.locationState,
                        country = it.locationCountry ?: ""
                    )
                }
                _historyLocationUiState.value = HistoryLocationUiState.Success(historyCity)
            }
            .catch { 
                _historyLocationUiState.value =
                    HistoryLocationUiState.Error(DomainError.Unknown)
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchUiState.value = SearchUiState.Idle
        }
    }
}

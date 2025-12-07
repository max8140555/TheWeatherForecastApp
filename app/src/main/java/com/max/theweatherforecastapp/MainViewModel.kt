package com.max.theweatherforecastapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.max.theweatherforecastapp.core.domain.model.GeocodingLocation
import com.max.theweatherforecastapp.core.domain.usecase.UserPreferencesUseCase
import com.max.theweatherforecastapp.navigation.AppScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userPreferencesUseCase: UserPreferencesUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _startDestination = MutableStateFlow<AppScreen?>(null)
    val startDestination = _startDestination.asStateFlow()

    private val _selectedLocation = MutableStateFlow<GeocodingLocation?>(null)
    val selectedLocation = _selectedLocation.asStateFlow()

    init {
        viewModelScope.launch {
            val city = userPreferencesUseCase.lastSelectedCity.first()
            _selectedLocation.value = city
            _startDestination.value = if (city == null) AppScreen.Home else AppScreen.Weather
            _isLoading.value = false
        }
    }

    fun onLocationSelected(location: GeocodingLocation) {
        viewModelScope.launch {
            _selectedLocation.value = location
            userPreferencesUseCase.saveLastSelectedLocation(location)
        }
    }
}

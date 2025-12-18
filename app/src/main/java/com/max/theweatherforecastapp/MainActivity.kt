package com.max.theweatherforecastapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.max.theweatherforecastapp.core.ui.theme.TheWeatherForecastAppTheme
import com.max.theweatherforecastapp.feature.home.HomeRoute
import com.max.theweatherforecastapp.feature.weather.WeatherRoute
import com.max.theweatherforecastapp.navigation.AppScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TheWeatherForecastAppTheme {
                AppNavigation(mainViewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(mainViewModel: MainViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppScreen.Launch.name) {
        composable(AppScreen.Launch.name) {
            val isLoading by mainViewModel.isLoading.collectAsState()
            val startDestination by mainViewModel.startDestination.collectAsState()

            if (!isLoading && startDestination != null) {
                LaunchedEffect(Unit) {
                    navController.navigate(startDestination!!.name) {
                        popUpTo(AppScreen.Launch.name) {
                            inclusive = true
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }

        composable(AppScreen.Home.name) {
            HomeRoute(
                onNavigateToWeather = { city ->
                    mainViewModel.onLocationSelected(city)
                    navController.navigate(AppScreen.Weather.name) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(AppScreen.Weather.name) {
            val selectedCity by mainViewModel.selectedLocation.collectAsState()
            WeatherRoute(
                selectedLocation = selectedCity,
                onNavigateToHome = {
                    if (!navController.popBackStack()) {
                        navController.navigate(AppScreen.Home.name) {
                            popUpTo(AppScreen.Home.name) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}
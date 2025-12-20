package com.max.theweatherforecastapp.feature.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.max.theweatherforecastapp.core.domain.model.Current
import com.max.theweatherforecastapp.core.domain.model.Daily
import com.max.theweatherforecastapp.core.domain.model.DomainError
import com.max.theweatherforecastapp.core.domain.model.Hourly
import com.max.theweatherforecastapp.core.domain.model.Weather
import com.max.theweatherforecastapp.core.domain.model.Temp
import com.max.theweatherforecastapp.core.domain.model.WeatherDetail
import com.max.theweatherforecastapp.core.ui.mapper.getResId
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun WeatherRoute(
    weatherViewModel: WeatherViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit
) {
    val weatherState by weatherViewModel.weatherState.collectAsStateWithLifecycle()

    WeatherScreen(
        state = weatherState,
        onNavigateToHome = onNavigateToHome
    )
}

@Composable
fun WeatherScreen(
    state: WeatherState,
    onNavigateToHome: () -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
    ) { paddingValues ->
        val gradientBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFF2F80ED), Color(0xFF56CCF2))
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
        ) {
            when (state) {
                is WeatherState.Loading,
                is WeatherState.Idle -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }

                is WeatherState.Success -> {
                    WeatherContent(weather = state.data)
                }

                is WeatherState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = state.error.getResId()),
                            color = Color.White
                        )
                    }
                }
            }

            FloatingActionButton(
                onClick = onNavigateToHome,
                containerColor = Color.White.copy(alpha = 0.3f),
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp,
                    focusedElevation = 0.dp,
                    hoveredElevation = 0.dp,
                ),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = paddingValues.calculateTopPadding() + 16.dp, end = 16.dp),
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = stringResource(id = R.string.search_location)
                )
            }
        }
    }
}

@Composable
private fun WeatherContent(weather: Weather) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(60.dp))
            CurrentWeatherInfo(weather)
            Spacer(modifier = Modifier.height(32.dp))
        }

        item {
            HourlyForecast(weather.hourly)
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            WeeklyForecast(weather.daily)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CurrentWeatherInfo(weather: Weather) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = weather.name,
            style = MaterialTheme.typography.headlineMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Normal
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${weather.current?.temp?.toInt()}${stringResource(id = R.string.celsius_symbol)}",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 90.sp,
                fontWeight = FontWeight.Thin,
                color = Color.White
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        val description = weather.current
            ?.weather
            ?.firstOrNull()
            ?.description
            ?.replaceFirstChar { it.uppercase() }
            .orEmpty()

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        val today = weather.daily.firstOrNull()
        val maxTemp = today?.temp?.max?.toInt()
        val minTemp = today?.temp?.min?.toInt()
        val degree = stringResource(R.string.degree_symbol)
        val highPrefix = stringResource(R.string.high_temp_prefix)
        val lowPrefix = stringResource(R.string.low_temp_prefix)

        Text(
            text = "$highPrefix$maxTemp$degree  $lowPrefix$minTemp$degree",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        )
    }
}

@Composable
private fun HourlyForecast(hourly: List<Hourly>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(id = R.string.hourly_forecast_title),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Color.White.copy(alpha = 0.7f)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow {
                itemsIndexed(hourly) { index, hour ->
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (index == 0) {
                                stringResource(id = R.string.now)
                            } else {
                                formatUnixTime(hour.dt, "ha")
                            },
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(
                                    "https://openweathermap.org/img/wn/${
                                        hour.weather.firstOrNull()?.icon
                                    }@2x.png"
                                )
                                .crossfade(true)
                                .build(),
                            contentDescription = hour.weather.firstOrNull()?.description,
                            placeholder = painterResource(R.drawable.ic_weather_placeholder),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${hour.temp.toInt()}${stringResource(id = R.string.celsius_symbol)}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeeklyForecast(daily: List<Daily>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(id = R.string.seven_day_forecast_title),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Color.White.copy(alpha = 0.7f)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            daily.forEachIndexed { index, day ->
                DailyForecastRow(day)
                if (index < daily.size - 1) {
                    HorizontalDivider(color = Color.White.copy(alpha = 0.3f))
                }
            }
        }
    }
}

@Composable
private fun DailyForecastRow(day: Daily) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val iconString = day.weather.firstOrNull()?.icon
        val lowTempPrefix = stringResource(id = R.string.low_temp_prefix)
        val highTempPrefix = stringResource(id = R.string.high_temp_prefix)
        val degreeSymbol = stringResource(id = R.string.degree_symbol)

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(
                    "https://openweathermap.org/img/wn/$iconString@2x.png"
                )
                .crossfade(true)
                .build(),
            contentDescription = day.weather.firstOrNull()?.description,
            placeholder = painterResource(R.drawable.ic_weather_placeholder),
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = formatUnixTime(day.dt, "EEE"),
            modifier = Modifier.weight(1f),
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "$lowTempPrefix ${day.temp.min.toInt()}$degreeSymbol",
            color = Color.White.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "$highTempPrefix ${day.temp.max.toInt()}$degreeSymbol",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun formatUnixTime(unixTime: Long, format: String): String {
    val date = Date(unixTime * 1000L)
    val sdf = SimpleDateFormat(format, Locale.US)
    sdf.timeZone = TimeZone.getDefault()
    return sdf.format(date)
}


/** ---------- Preview 用假資料 ---------- */

private fun mockWeather(): Weather {
    val mockWeatherDetail = WeatherDetail("Clouds", "overcast clouds", "04d")
    val mockTemp = Temp(min = 15.0, max = 22.0)
    val mockCurrent = Current(temp = 18.0, weather = persistentListOf(mockWeatherDetail))
    val mockHourly = (1..10).map {
        Hourly(
            dt = System.currentTimeMillis() / 1000 + (it * 3600),
            temp = 18.0 + it,
            weather = persistentListOf(mockWeatherDetail)
        )
    }.toImmutableList()

    val mockDaily = (1..7).map {
        Daily(
            dt = System.currentTimeMillis() / 1000 + (it * 86400),
            temp = mockTemp,
            weather = persistentListOf(mockWeatherDetail)
        )
    }.toImmutableList()

    return Weather(
        name = "Taipei",
        lat = 25.03,
        lon = 121.56,
        timezone = "Asia/Taipei",
        current = mockCurrent,
        hourly = mockHourly,
        daily = mockDaily,
        locationCountry = "TW",
        locationState = "Taipei"
    )
}

/** ---------- Screen Previews ---------- */

@Preview(name = "Weather - Success", showBackground = true)
@Composable
private fun WeatherScreenSuccessPreview() {
    MaterialTheme {
        WeatherScreen(
            state = WeatherState.Success(mockWeather()),
            onNavigateToHome = {}
        )
    }
}

@Preview(name = "Weather - Loading", showBackground = true)
@Composable
private fun WeatherScreenLoadingPreview() {
    MaterialTheme {
        WeatherScreen(
            state = WeatherState.Loading,
            onNavigateToHome = {}
        )
    }
}

@Preview(name = "Weather - Error", showBackground = true)
@Composable
private fun WeatherScreenErrorPreview() {
    MaterialTheme {
        WeatherScreen(
            state = WeatherState.Error(DomainError.Network),
            onNavigateToHome = {}
        )
    }
}

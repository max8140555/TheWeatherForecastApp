package com.max.theweatherforecastapp.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.max.theweatherforecastapp.core.domain.model.DomainError
import com.max.theweatherforecastapp.core.domain.model.GeocodingLocation
import com.max.theweatherforecastapp.core.ui.mapper.getResId
import kotlinx.collections.immutable.persistentListOf

@Composable
fun HomeRoute(
    homeViewModel: HomeViewModel = hiltViewModel(),
    onNavigateToWeather: (GeocodingLocation) -> Unit
) {
    val searchQuery by homeViewModel.searchQuery.collectAsStateWithLifecycle()
    val searchUiState by homeViewModel.searchUiState.collectAsStateWithLifecycle()
    val historyLocationUiState by homeViewModel.historyLocationUiState.collectAsStateWithLifecycle()

    HomeScreen(
        searchQuery = searchQuery,
        searchUiState = searchUiState,
        historyLocationUiState = historyLocationUiState,
        onSearchQueryChanged = homeViewModel::onSearchQueryChanged,
        onNavigateToWeather = onNavigateToWeather
    )
}

@Composable
fun HomeScreen(
    searchQuery: String,
    searchUiState: SearchUiState,
    historyLocationUiState: HistoryLocationUiState,
    onSearchQueryChanged: (String) -> Unit,
    onNavigateToWeather: (GeocodingLocation) -> Unit,
) {
    Scaffold(
        containerColor = Color.Black
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.weather_title),
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            SearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChanged,
            )

            Spacer(modifier = Modifier.height(16.dp))

            SearchContent(
                searchUiState = searchUiState,
                historyLocationUiState = historyLocationUiState,
                onNavigateToWeather = {
                    onNavigateToWeather.invoke(it)
                    onSearchQueryChanged.invoke("")
                }
            )
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = stringResource(id = R.string.search_placeholder),
                color = Color.Gray
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(id = R.string.search),
                tint = Color.Gray
            )
        },
        shape = RoundedCornerShape(10.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.DarkGray,
            unfocusedContainerColor = Color.DarkGray,
            disabledContainerColor = Color.DarkGray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        )
    )
}

@Composable
private fun SearchContent(
    searchUiState: SearchUiState,
    historyLocationUiState: HistoryLocationUiState,
    onNavigateToWeather: (GeocodingLocation) -> Unit
) {
    when (val state = searchUiState) {
        is SearchUiState.Idle -> {
            when (val historyState = historyLocationUiState) {
                is HistoryLocationUiState.Success -> {
                    if (historyState.historyLocation.isNotEmpty()) {
                        Text(
                            text = stringResource(id = R.string.history_title),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        LocationList(
                            locations = historyState.historyLocation,
                            onItemClick = onNavigateToWeather
                        )
                    }
                }

                else -> Unit
            }
        }

        is SearchUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is SearchUiState.Success -> {
            LocationList(
                locations = state.locations,
                onItemClick = onNavigateToWeather
            )
        }

        is SearchUiState.Error -> {
            Text(
                text = stringResource(id = state.error.getResId()),
                color = Color.Red
            )
        }
    }
}

@Composable
fun LocationList(
    locations: List<GeocodingLocation>,
    onItemClick: (GeocodingLocation) -> Unit
) {
    LazyColumn {
        items(items = locations) { location ->
            LocationListItem(
                location = location,
                onClick = { onItemClick(location) }
            )
        }
    }
}

@Composable
private fun LocationListItem(
    location: GeocodingLocation,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = location.name,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
        )

        Text(
            text = listOfNotNull(
                location.state?.takeIf { it.isNotBlank() },
                location.country
            ).joinToString(", "),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

/** Preview data */
private val previewLocations = persistentListOf(
    GeocodingLocation(
        name = "Taipei",
        lat = 25.03,
        lon = 121.56,
        country = "TW",
        state = "Taipei"
    ),
    GeocodingLocation(
        name = "Tokyo",
        lat = 35.68,
        lon = 139.69,
        country = "JP",
        state = null
    )
)

/** Previews */

@Preview(name = "Home - Idle with history", showBackground = true)
@Composable
fun HomeScreenIdlePreview() {
    MaterialTheme {
        HomeScreen(
            searchQuery = "",
            searchUiState = SearchUiState.Idle,
            historyLocationUiState = HistoryLocationUiState.Success(previewLocations),
            onSearchQueryChanged = {},
            onNavigateToWeather = {}
        )
    }
}

@Preview(name = "Home - Search Success", showBackground = true)
@Composable
fun HomeScreenSearchSuccessPreview() {
    MaterialTheme {
        HomeScreen(
            searchQuery = "Ta",
            searchUiState = SearchUiState.Success(previewLocations),
            historyLocationUiState = HistoryLocationUiState.Success(persistentListOf()),
            onSearchQueryChanged = {},
            onNavigateToWeather = {}
        )
    }
}

@Preview(name = "Home - Loading", showBackground = true)
@Composable
fun HomeScreenLoadingPreview() {
    MaterialTheme {
        HomeScreen(
            searchQuery = "Ta",
            searchUiState = SearchUiState.Loading,
            historyLocationUiState = HistoryLocationUiState.Success(persistentListOf()),
            onSearchQueryChanged = {},
            onNavigateToWeather = {}
        )
    }
}

@Preview(name = "Home - Error", showBackground = true)
@Composable
fun HomeScreenErrorPreview() {
    MaterialTheme {
        HomeScreen(
            searchQuery = "Lond",
            searchUiState = SearchUiState.Error(DomainError.Network),
            historyLocationUiState = HistoryLocationUiState.Success(previewLocations),
            onSearchQueryChanged = {},
            onNavigateToWeather = {}
        )
    }
}

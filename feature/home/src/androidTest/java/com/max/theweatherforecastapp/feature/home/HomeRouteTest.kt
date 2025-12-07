package com.max.theweatherforecastapp.feature.home

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.max.theweatherforecastapp.core.domain.model.DomainError
import com.max.theweatherforecastapp.core.domain.model.GeocodingLocation
import com.max.theweatherforecastapp.core.ui.mapper.getResId
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeRouteTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    // Idle + 有歷史紀錄 → 顯示「歷史標題」與地點列表
    @Test
    fun idleState_withHistoryLocations_showsHistorySectionAndItems() {
        val historyLocations = listOf(
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

        composeTestRule.setContent {
            MaterialTheme {
                HomeScreen(
                    searchQuery = "",
                    searchUiState = SearchUiState.Idle,
                    historyLocationUiState = HistoryLocationUiState.Success(historyLocations),
                    onSearchQueryChanged = {},
                    onNavigateToWeather = {}
                )
            }
        }

        val historyTitle =
            composeTestRule.activity.getString(R.string.history_title)

        composeTestRule.onNodeWithText(historyTitle).assertIsDisplayed()
        composeTestRule.onNodeWithText("Taipei").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tokyo").assertIsDisplayed()
    }

    // Search 成功 → 顯示搜尋結果列表
    @Test
    fun searchSuccess_showsSearchResultLocations() {
        val locations = listOf(
            GeocodingLocation(
                name = "London",
                lat = 51.50,
                lon = -0.12,
                country = "GB",
                state = "England"
            )
        )

        composeTestRule.setContent {
            MaterialTheme {
                HomeScreen(
                    searchQuery = "Lo",
                    searchUiState = SearchUiState.Success(locations),
                    historyLocationUiState = HistoryLocationUiState.Success(emptyList()),
                    onSearchQueryChanged = {},
                    onNavigateToWeather = {}
                )
            }
        }

        composeTestRule.onNodeWithText("London").assertIsDisplayed()
    }

    // 點擊某個地點 → 呼叫 onNavigateToWeather 並帶入正確的 location
    @Test
    fun clickLocation_callsOnNavigateToWeatherWithCorrectLocation() {
        val location = GeocodingLocation(
            name = "Paris",
            lat = 48.85,
            lon = 2.35,
            country = "FR",
            state = "Île-de-France"
        )

        var clickedLocation: GeocodingLocation? = null

        composeTestRule.setContent {
            MaterialTheme {
                HomeScreen(
                    searchQuery = "Par",
                    searchUiState = SearchUiState.Success(listOf(location)),
                    historyLocationUiState = HistoryLocationUiState.Success(emptyList()),
                    onSearchQueryChanged = {},
                    onNavigateToWeather = { clickedLocation = it }
                )
            }
        }

        composeTestRule.onNodeWithText("Paris").performClick()

        assertEquals(location, clickedLocation)
    }

    // Error 狀態 → 顯示錯誤訊息字串
    @Test
    fun errorState_showsErrorMessage() {
        val error = DomainError.Network

        composeTestRule.setContent {
            MaterialTheme {
                HomeScreen(
                    searchQuery = "Lond",
                    searchUiState = SearchUiState.Error(error),
                    historyLocationUiState = HistoryLocationUiState.Success(emptyList()),
                    onSearchQueryChanged = {},
                    onNavigateToWeather = {}
                )
            }
        }

        val errorText = composeTestRule.activity.getString(error.getResId())
        composeTestRule.onNodeWithText(errorText).assertIsDisplayed()
    }
}

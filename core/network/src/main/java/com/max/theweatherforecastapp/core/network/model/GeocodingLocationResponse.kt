package com.max.theweatherforecastapp.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

typealias GeocodingLocationResponse = List<GeocodingLocationResponseItem>

@JsonClass(generateAdapter = true)
data class GeocodingLocationResponseItem(
    @Json(name = "name") val name: String,
    @Json(name = "lat") val lat: Double,
    @Json(name = "lon") val lon: Double,
    @Json(name = "country") val country: String,
    @Json(name = "state") val state: String? = null
)

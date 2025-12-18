package com.max.theweatherforecastapp.core.domain.model
/**
 * A clean domain model representing a location from the geocoding search.
 * It is free from any framework or library annotations.
 */
data class GeocodingLocation(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String? = null
)

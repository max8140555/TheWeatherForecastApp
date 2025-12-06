package com.max.theweatherforecastapp.core.data.mapper

import com.max.theweatherforecastapp.core.domain.model.GeocodingLocation
import com.max.theweatherforecastapp.core.network.model.GeocodingLocationResponseItem

fun GeocodingLocationResponseItem.toDomain(): GeocodingLocation {
    return GeocodingLocation(
        name = name,
        lat = lat,
        lon = lon,
        country = country,
        state = state
    )
}

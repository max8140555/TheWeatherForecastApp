package com.max.theweatherforecastapp.core.network.api

import com.max.theweatherforecastapp.core.network.model.GeocodingLocationResponse
import com.max.theweatherforecastapp.core.network.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("data/3.0/onecall")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") exclude: String? = null,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String
    ): Response<WeatherResponse>

    @GET("geo/1.0/direct")
    suspend fun getLocationsByCityName(
        @Query("q") cityName: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String
    ): Response<GeocodingLocationResponse>
}

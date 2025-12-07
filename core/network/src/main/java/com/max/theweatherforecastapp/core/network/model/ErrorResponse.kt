package com.max.theweatherforecastapp.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    @Json(name = "cod") val code: Int,
    @Json(name = "message") val message: String
)

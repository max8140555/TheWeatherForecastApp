package com.max.theweatherforecastapp.core.data.mapper

import com.max.theweatherforecastapp.core.domain.model.DomainError
import com.max.theweatherforecastapp.core.network.model.ErrorResponse
import com.squareup.moshi.Moshi
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HttpErrorMapper @Inject constructor(moshi: Moshi) {

    private val adapter = moshi.adapter(ErrorResponse::class.java)

    fun <T> map(response: Response<T>): DomainError {
        val errorJson = response.errorBody()?.string()

        val parsed = try {
            errorJson?.let { adapter.fromJson(it) }
        } catch (_: Exception) {
            null
        }

        parsed?.let { apiError ->
            return when (apiError.code) {
                401 -> DomainError.Unauthorized
                404 -> DomainError.NotFound
                in 500..599 -> DomainError.ServerError
                else -> DomainError.HttpError(apiError.code, apiError.message)
            }
        }

        return DomainError.HttpError(response.code(), response.message())
    }
}

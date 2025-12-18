package com.max.theweatherforecastapp.core.data.util

import com.max.theweatherforecastapp.core.data.mapper.HttpErrorMapper
import com.max.theweatherforecastapp.core.data.mapper.toDomainError
import com.max.theweatherforecastapp.core.domain.model.AppResult
import com.max.theweatherforecastapp.core.domain.model.DomainError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Response

suspend inline fun <T, R> safeApiCall(
    dispatcher: CoroutineDispatcher,
    httpErrorMapper: HttpErrorMapper,
    crossinline apiCall: suspend () -> Response<T>,
    crossinline mapBody: suspend (T) -> R
): AppResult<R> {
    return withContext(dispatcher) {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    AppResult.Success(mapBody(body))
                } ?: AppResult.Failure(DomainError.EmptyBody)
            } else {
                AppResult.Failure(httpErrorMapper.map(response))
            }
        } catch (e: Throwable) {
            AppResult.Failure(e.toDomainError())
        }
    }
}
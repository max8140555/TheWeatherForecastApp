package com.max.theweatherforecastapp.core.data.mapper

import com.max.theweatherforecastapp.core.domain.model.DomainError
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

fun Throwable.toDomainError(): DomainError = when (this) {
    is SocketTimeoutException -> DomainError.Timeout
    is IOException -> DomainError.Network
    is HttpException -> {
        when (code()) {
            401 -> DomainError.Unauthorized
            404 -> DomainError.NotFound
            in 500..599 -> DomainError.ServerError
            else -> DomainError.HttpError(code(), message())
        }
    }
    else -> DomainError.Unknown
}

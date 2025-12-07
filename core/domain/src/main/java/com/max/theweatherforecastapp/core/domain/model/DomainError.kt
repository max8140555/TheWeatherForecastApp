package com.max.theweatherforecastapp.core.domain.model

sealed class DomainError {
    object Network : DomainError()
    object Timeout : DomainError()

    data class HttpError(val code: Int, val msg: String?) : DomainError()
    object Unauthorized : DomainError()
    object NotFound : DomainError()
    object ServerError : DomainError()

    object EmptyBody : DomainError()
    object Parsing : DomainError()

    object Unknown : DomainError()
}
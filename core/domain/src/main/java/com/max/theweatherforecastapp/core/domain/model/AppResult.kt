package com.max.theweatherforecastapp.core.domain.model

sealed class AppResult<out T> {

    data class Success<out T>(val data: T) : AppResult<T>()

    data class Failure(val error: DomainError) : AppResult<Nothing>()

    object Loading : AppResult<Nothing>()
}
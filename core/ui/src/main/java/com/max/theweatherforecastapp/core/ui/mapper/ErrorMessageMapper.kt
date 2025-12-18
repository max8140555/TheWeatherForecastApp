package com.max.theweatherforecastapp.core.ui.mapper

import com.max.theweatherforecastapp.core.domain.model.DomainError
import com.max.theweatherforecastapp.core.ui.R

fun DomainError.getResId(): Int {
    return when (this) {
        DomainError.Network -> R.string.error_network
        DomainError.Timeout -> R.string.error_timeout
        DomainError.Unauthorized -> R.string.error_unauthorized
        DomainError.NotFound -> R.string.error_not_found
        DomainError.ServerError -> R.string.error_server
        is DomainError.HttpError -> R.string.error_http
        DomainError.EmptyBody -> R.string.error_empty_body
        is DomainError.Parsing -> R.string.error_parsing
        is DomainError.Unknown ->  R.string.error_unknown
    }
}
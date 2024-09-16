package io.github.katarem.domain.response

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class ApiResponse<T>(
    val errorMessage: String? = null,
    val body: T? = null,
    val cardAvailableAt: LocalDateTime? = null,
    val responseDate: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
)
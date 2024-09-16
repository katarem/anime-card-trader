package io.github.katarem.service.utils

import io.github.katarem.domain.response.ApiResponse
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.hours

fun checkIfCardAvailable(lastChecked: LocalDateTime, now: LocalDateTime): ApiResponse<String> {
    val instant1 = lastChecked.toInstant(TimeZone.currentSystemDefault())
    val instant2 = now.toInstant(TimeZone.currentSystemDefault())
    val duration = instant2 - instant1
    return if (duration > 8.hours) {
        ApiResponse(
            body = "You can check your card now",
            errorMessage = null,
            cardAvailableAt = (now.toInstant(TimeZone.currentSystemDefault()) + 8.hours).toLocalDateTime(
                TimeZone.currentSystemDefault()
            )
        )
    } else {
        val availableDate = (instant1 + 8.hours).toLocalDateTime(TimeZone.currentSystemDefault())
        ApiResponse(
            body = null,
            errorMessage = "You can't get the card yet! Wait until this date",
            cardAvailableAt = availableDate
        )
    }
}

package io.github.katarem.presentation.dto

import kotlinx.datetime.LocalDateTime

data class UserDTO(
    val id: Long?,
    val username: String,
    val email: String?,
    val password: String?,
    val cards: List<CardDTO>? = emptyList(),
    val lastTimeChecked: LocalDateTime?
)
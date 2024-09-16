package io.github.katarem.presentation.dto

import kotlinx.datetime.LocalDateTime

data class CardDTO (
    val id: Long?,
    val uuid: String,
    val character: CharacterDTO,
    val obtainedDate: LocalDateTime?,
    val attachedUser: UserDTO?
)
package io.github.katarem.domain.model

import kotlinx.datetime.LocalDateTime

data class Card(
    val id: Long,
    val uuid: String,
    val characterId: Long,
    val obtainedDate: LocalDateTime?,
    val attachedUser: User?
)

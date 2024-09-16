package io.github.katarem.domain.model

import kotlinx.datetime.LocalDateTime

data class User(
    val id: Long,
    val username: String,
    val email: String,
    val password: String,
    val cards: List<Card>,
    val lastTimeChecked: LocalDateTime?,
    val role: Roles = Roles.USER
)

enum class Roles{
    ADMIN,USER
}
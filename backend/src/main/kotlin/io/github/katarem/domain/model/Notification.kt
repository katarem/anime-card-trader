package io.github.katarem.domain.model

import kotlinx.datetime.LocalDateTime

data class Notification(
    val id: Long,
    val userId: Long,
    val title: String,
    val content: String,
    val createdAt: LocalDateTime,
    val read: Boolean
)

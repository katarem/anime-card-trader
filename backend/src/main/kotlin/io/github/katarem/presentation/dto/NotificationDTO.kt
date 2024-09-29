package io.github.katarem.presentation.dto

import kotlinx.datetime.LocalDateTime

data class NotificationDTO(
    val id: Long = 0,
    val userId: Long,
    val title: String,
    val createdAt: LocalDateTime? = null,
    val content: String,
    val read: Boolean
)

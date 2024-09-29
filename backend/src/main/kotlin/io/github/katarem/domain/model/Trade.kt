package io.github.katarem.domain.model

import kotlinx.datetime.LocalDateTime

data class Trade(
    val id: Long,
    val offeringUsername: String,
    val offeredUsername: String,
    val offeringUserCards: List<Card>,
    val offeredUserCards: List<Card>,
    val createdAt: LocalDateTime,
    val accepted: Boolean?
)

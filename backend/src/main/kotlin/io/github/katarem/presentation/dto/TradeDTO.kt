package io.github.katarem.presentation.dto

import kotlinx.datetime.LocalDateTime

data class TradeDTO(
    val id: Long = 0,
    val offeringUserId: Long,
    val offeredUserId: Long,
    val offeringUserCards: List<CardDTO>,
    val offeredUserCards: List<CardDTO>,
    val createdAt: LocalDateTime? = null,
    val accepted: Boolean?
)
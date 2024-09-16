package io.github.katarem.domain.repository

import io.github.katarem.domain.model.Card

interface AnimeCardRepository {
    suspend fun getAllCards(): List<Card>
    suspend fun getCardById(uuid: String): Card?
    suspend fun insertCard(card: Card): Card?
    suspend fun updateCard(uuid: String, card: Card): Card?
    suspend fun deleteCard(uuid: String): Int?
}
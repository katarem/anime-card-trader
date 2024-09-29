package io.github.katarem.service.services.cards

import io.github.katarem.presentation.dto.CardDTO

interface CardService {
    suspend fun getAll(): List<CardDTO>
    suspend fun getById(uuid: String): CardDTO?
    suspend fun update(uuid: String, card: CardDTO): CardDTO?
    suspend fun delete(uuid: String): Int?
}
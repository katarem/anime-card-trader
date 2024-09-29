package io.github.katarem.service.services.trade

import io.github.katarem.presentation.dto.TradeDTO

interface TradeService {
    suspend fun getAll(): List<TradeDTO>
    suspend fun getById(id: Long): TradeDTO?
    suspend fun insert(tradeDTO: TradeDTO): TradeDTO?
    suspend fun update(id: Long, tradeDTO: TradeDTO): TradeDTO?
    suspend fun delete(id: Long): Int?
}
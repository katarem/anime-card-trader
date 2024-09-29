package io.github.katarem.service.services.trade

import io.github.katarem.presentation.dto.TradeDTO

interface TradeService {
    fun getAll(): List<TradeDTO>
    fun getById(id: Long): TradeDTO?
    fun insert(tradeDTO: TradeDTO): TradeDTO?
    fun update(id: Long, tradeDTO: TradeDTO): TradeDTO?
    fun delete(id: Long): Int?
}
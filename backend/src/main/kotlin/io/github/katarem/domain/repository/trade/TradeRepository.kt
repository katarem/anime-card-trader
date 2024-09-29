package io.github.katarem.domain.repository.trade

import io.github.katarem.domain.model.Trade

interface TradeRepository {
    fun getAll(): List<Trade>
    fun getById(id: Long): Trade?
    fun insert(trade: Trade): Trade?
    fun update(id: Long, trade: Trade): Trade?
    fun delete(id: Long): Int?
}
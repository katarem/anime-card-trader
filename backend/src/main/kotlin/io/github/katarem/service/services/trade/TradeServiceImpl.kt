package io.github.katarem.service.services.trade

import io.github.katarem.domain.mapper.Mapper
import io.github.katarem.domain.repository.trade.TradeRepositoryImpl
import io.github.katarem.presentation.dto.TradeDTO

class TradeServiceImpl(
    private val repository: TradeRepositoryImpl
) : TradeService {

    override fun getAll(): List<TradeDTO> {
        return repository.getAll().map(Mapper::toDto)
    }

    override fun getById(id: Long): TradeDTO? {
        return repository.getById(id)?.let(Mapper::toDto)
    }

    override fun insert(tradeDTO: TradeDTO): TradeDTO? {
        val mapped = Mapper.toEntity(tradeDTO)
        return repository.insert(mapped)?.let(Mapper::toDto)
    }

    override fun update(id: Long, tradeDTO: TradeDTO): TradeDTO? {
        val mapped = Mapper.toEntity(tradeDTO)
        return repository.update(id, mapped)?.let(Mapper::toDto)
    }

    override fun delete(id: Long): Int? {
        return repository.delete(id)
    }
}
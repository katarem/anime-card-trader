package io.github.katarem.service.services.trade

import io.github.katarem.domain.mapper.Mapper
import io.github.katarem.domain.repository.trade.TradeRepositoryImpl
import io.github.katarem.presentation.dto.CardDTO
import io.github.katarem.presentation.dto.TradeDTO
import io.github.katarem.service.services.cards.CardServiceImpl
import io.github.katarem.service.services.user.UserServiceImpl

class TradeServiceImpl(
    private val repository: TradeRepositoryImpl,
    private val cardService: CardServiceImpl,
    private val userService: UserServiceImpl
) : TradeService {

    override suspend fun getAll(): List<TradeDTO> {
        return repository.getAll().map(Mapper::toDto)
    }

    override suspend fun getById(id: Long): TradeDTO? {
        return repository.getById(id)?.let(Mapper::toDto)
    }

    override suspend fun insert(tradeDTO: TradeDTO): TradeDTO? {
        val mapped = Mapper.toEntity(tradeDTO)
        if(!validUserCards(tradeDTO)) return null
        return repository.insert(mapped)?.let(Mapper::toDto)
    }

    override suspend fun update(id: Long, tradeDTO: TradeDTO): TradeDTO? {
        val mapped = Mapper.toEntity(tradeDTO)
        if(!validUserCards(tradeDTO)) return null
        return repository.update(id, mapped)?.let(Mapper::toDto)
    }

    override suspend fun delete(id: Long): Int? {
        return repository.delete(id)
    }

    private suspend fun trade(tradeDTO: TradeDTO): TradeDTO? {
        getById(tradeDTO.id)?.let { databaseTrade ->
            cardService.update()
            update(databaseTrade.id, tradeDTO) // has to be accepted first, if not we won't be here
        }
    }

    private suspend fun validUserCards(tradeDTO: TradeDTO): Boolean {
        val offeringUser = tradeDTO.offeringUsername
        val offeredUser = tradeDTO.offeredUsername
        val offeringCardIds = tradeDTO.offeringUserCards.map(CardDTO::uuid)
        val offeredCardIds = tradeDTO.offeredUserCards.map(CardDTO::uuid)
        val offeringUserLegalCards =
                cardService.getByUsername(offeringUser)?.map(CardDTO::uuid)?.containsAll(offeringCardIds) ?: false
        val offeredUserLegalCards =
            cardService.getByUsername(offeredUser)?.map(CardDTO::uuid)?.containsAll(offeredCardIds) ?: false
        return offeringUserLegalCards && offeredUserLegalCards
    }

}
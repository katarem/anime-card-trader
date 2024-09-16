package io.github.katarem.service

import io.github.katarem.domain.mapper.Mapper
import io.github.katarem.domain.model.Card
import io.github.katarem.domain.repository.AnimeCardRepositoryImpl
import io.github.katarem.domain.repository.CharacterRepositoryImpl
import io.github.katarem.domain.repository.UserRepositoryImpl
import io.github.katarem.presentation.dto.CardDTO

class CardServiceImpl: CardService {

    private val cardRepository = AnimeCardRepositoryImpl()
    private val characterService = CharacterServiceImpl()
    private val userRepository = UserRepositoryImpl()

    private suspend fun map(card: Card): CardDTO{
        val user = userRepository.getUserById(card.attachedUser?.id ?: 0)?.let(Mapper::toDto)
        val character = characterService.getById(card.characterId)!! // for each card HAS a character
        return Mapper.toDto(card).copy(attachedUser = user, character = character)
    }

    override suspend fun getAll(): List<CardDTO> {
        return cardRepository.getAllCards().map{ map(it) }
    }

    override suspend fun getById(uuid: String): CardDTO? {
        return cardRepository.getCardById(uuid)?.let { map(it) }
    }

    override suspend fun update(uuid: String, card: CardDTO): CardDTO? {
       val cardEntity = Mapper.toEntity(card)
        return cardRepository.updateCard(uuid, cardEntity)?.let{ map(it) }
    }

    override suspend fun delete(uuid: String): Int? {
        return cardRepository.deleteCard(uuid)
    }
}
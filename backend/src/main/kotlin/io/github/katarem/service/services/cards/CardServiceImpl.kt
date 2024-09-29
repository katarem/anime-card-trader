package io.github.katarem.service.services.cards

import io.github.katarem.domain.mapper.Mapper
import io.github.katarem.domain.model.Card
import io.github.katarem.domain.repository.card.CardRepositoryImpl
import io.github.katarem.presentation.dto.CardDTO
import io.github.katarem.presentation.dto.UserDTO
import io.github.katarem.presentation.obtainNextCardDate
import io.github.katarem.service.services.character.CharacterServiceImpl
import io.github.katarem.service.services.user.UserServiceImpl
import kotlinx.datetime.LocalDateTime

class CardServiceImpl(
    private val cardRepository: CardRepositoryImpl,
    private val characterService: CharacterServiceImpl,
    private val userService: UserServiceImpl,
): CardService {

    private suspend fun map(card: Card): CardDTO{
        val user = userService.getById(card.attachedUser?.id ?: 0)
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
        return cardRepository.updateCard(uuid, cardEntity)?.let{ updated ->
            card.attachedUser?.let { newUser ->
                val userCards = newUser.cards!!.toMutableList()
                val existentCard = userCards.firstOrNull { it.uuid == uuid }
                existentCard?.let {
                    val index = userCards.indexOf(it)
                    userCards.removeAt(index)
                }
                userCards.add(card)
                userService.update(newUser.id!!, newUser.copy(cards = userCards.toList()))
            }
            map(updated) }
    }

    override suspend fun delete(uuid: String): Int? {
        return cardRepository.deleteCard(uuid)
    }

    fun nextCardDate(userDTO: UserDTO): LocalDateTime? {
        return userService.getByUsername(userDTO.username)?.nextCard
    }

    suspend fun getByUsername(username: String): List<CardDTO>? {
        return userService.getByUsername(username)?.let { dto ->
            val user = Mapper.toEntity(dto)
          return@let cardRepository.getCardsByUser(user)
              .map{
                  val character = characterService.getById(it.characterId)!!
                  Mapper.toDto(it).copy(character = character, attachedUser = dto)
              }
        }
    }

    suspend fun generateCard(userDTO: UserDTO): CardDTO? {
        return userService.internalGetByUsername(userDTO.username)?.let { databaseUser ->
            val user = Mapper.toEntity(databaseUser)
            val obtainedCard = cardRepository.obtainCard()
            val cardDto = cardRepository.updateCard(obtainedCard.uuid, obtainedCard.copy(attachedUser = user))?.let(Mapper::toDto)
            return@let cardDto?.let { dto ->
                val associatedCharacter = characterService.getById(dto.character.id)!!
                val dtoWithCharacter = dto.copy(character = associatedCharacter, attachedUser = databaseUser)
                val userNewCards = userDTO.cards?.let { it + dtoWithCharacter } ?: listOf(dtoWithCharacter)
                val nextCardDate = obtainNextCardDate()
                val updatedUser = Mapper.toDto(user, true)
                userService.update(user.id, updatedUser.copy(nextCard = nextCardDate, cards = userNewCards))
                dtoWithCharacter
            }
        }
    }
}
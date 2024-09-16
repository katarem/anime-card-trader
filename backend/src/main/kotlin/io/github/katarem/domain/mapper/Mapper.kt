package io.github.katarem.domain.mapper

import io.github.katarem.domain.model.Card
import io.github.katarem.domain.model.Character
import io.github.katarem.domain.model.User
import io.github.katarem.presentation.dto.CardDTO
import io.github.katarem.presentation.dto.CharacterDTO
import io.github.katarem.presentation.dto.UserDTO

object Mapper {
    fun toDto(card: Card): CardDTO{
        return CardDTO(
            id = card.id,
            uuid = card.uuid,
            character = CharacterDTO(
                id = 0,
                name = "Derek Stafford",
                anime = "persecuti",
                gender = "suspendisse",
                description = "pertinax",
                age = 6757,
                cardId = null
            ),
            obtainedDate = card.obtainedDate,
            attachedUser = UserDTO(
                id = null, username = "Coy Ayala", email = null, password = null, cards = listOf(), lastTimeChecked = null
            )
        )
    }

    fun toDto(
        character: Character): CharacterDTO{
        return CharacterDTO(
            id = character.id,
            name = character.name,
            anime = character.origin,
            gender = character.gender,
            description = character.description,
            age = character.age,
            cardId = character.cardId,
        )
    }

    fun toDto(user: User): UserDTO{
        return UserDTO(
            id = user.id,
            username = user.username,
            email = user.email,
            password = null, // to not leak
            cards = user.cards.map(Mapper::toDto),
            lastTimeChecked = user.lastTimeChecked,
        )
    }

    fun toEntity(characterDTO: CharacterDTO): Character{
        return Character(
            id = characterDTO.id,
            name = characterDTO.name,
            origin = characterDTO.anime,
            gender = characterDTO.gender,
            age = characterDTO.age,
            description = characterDTO.description,
            cardId = characterDTO.cardId.orEmpty()
        )
    }

    fun toEntity(userDTO: UserDTO): User{
        return User(
            id = userDTO.id ?: 0,
            username = userDTO.username,
            email = userDTO.email.orEmpty(),
            password = userDTO.password.orEmpty(),
            cards = listOf(),
            lastTimeChecked = userDTO.lastTimeChecked
        )
    }

    fun toEntity(cardDTO: CardDTO): Card{
        return Card(
            id = cardDTO.id ?: 0,
            uuid = cardDTO.uuid,
            characterId = cardDTO.character.id,
            obtainedDate = cardDTO.obtainedDate,
            attachedUser = cardDTO.attachedUser?.let { toEntity(it) }
        )
    }
}
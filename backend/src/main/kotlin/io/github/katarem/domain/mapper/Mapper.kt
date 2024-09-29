package io.github.katarem.domain.mapper

import io.github.katarem.domain.model.*
import io.github.katarem.presentation.dto.*
import io.github.katarem.presentation.now

object Mapper {
    fun toDto(card: Card): CardDTO{
        return CardDTO(
            id = card.id,
            uuid = card.uuid,
            character = CharacterDTO(
                id = card.characterId,
                name = "Derek Stafford",
                anime = "persecuti",
                gender = "suspendisse",
                description = "pertinax",
                age = 6757,
                cardId = null
            ),
            obtainedDate = card.obtainedDate,
            attachedUser = UserDTO(
                id = null, username = "Coy Ayala", email = null, password = null, cards = listOf(), nextCard = null
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

    fun toDto(user: User, includeSensible: Boolean = false): UserDTO{
        return UserDTO(
            id = user.id,
            username = user.username,
            email = if(includeSensible) user.email else null,
            password = if(includeSensible) user.password else null,
            cards = user.cards.map(Mapper::toDto),
            nextCard = user.nextCard,
        )
    }

    fun toDto(notification: Notification): NotificationDTO {
        return NotificationDTO(
            id = notification.id,
            userId = notification.userId,
            title = notification.title,
            createdAt = notification.createdAt,
            content = notification.content,
            read = notification.read
        )
    }

    fun toDto(trade: Trade): TradeDTO {
        return TradeDTO(
            id = trade.id,
            offeringUsername = trade.offeringUsername,
            offeredUsername = trade.offeredUsername,
            offeringUserCards = trade.offeringUserCards.map(::toDto),
            offeredUserCards = trade.offeredUserCards.map(::toDto),
            createdAt = trade.createdAt,
            accepted = trade.accepted

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
            nextCard = userDTO.nextCard
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

    fun toEntity(notificationDTO: NotificationDTO): Notification{
        return Notification(
            id = notificationDTO.id,
            userId = notificationDTO.userId,
            title = notificationDTO.title,
            content = notificationDTO.content,
            createdAt = notificationDTO.createdAt?: now(),
            read = notificationDTO.read
        )
    }

    fun toEntity(tradeDTO: TradeDTO): Trade {
        return Trade(
            id = tradeDTO.id,
            offeringUsername = tradeDTO.offeringUsername,
            offeredUsername = tradeDTO.offeredUsername,
            offeringUserCards = tradeDTO.offeringUserCards.map(::toEntity),
            offeredUserCards = tradeDTO.offeredUserCards.map(::toEntity),
            createdAt = tradeDTO.createdAt ?: now(),
            accepted = null
        )
    }
}
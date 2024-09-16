package io.github.katarem.domain.repository

import io.github.katarem.domain.database.CardTable
import io.github.katarem.domain.database.CharacterTable
import io.github.katarem.domain.database.toCard
import io.github.katarem.domain.model.Card
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class AnimeCardRepositoryImpl : AnimeCardRepository {

    override suspend fun getAllCards(): List<Card> {
        return transaction {
            CardTable.innerJoin(CharacterTable)
                .selectAll()
                .map(::toCard)
        }
    }

    override suspend fun getCardById(uuid: String): Card? {
        return transaction {
            CardTable.innerJoin(CharacterTable).selectAll()
                .where { CardTable.uuid eq uuid }
                .map(::toCard)
                .singleOrNull()
        }
    }

    override suspend fun insertCard(card: Card): Card? {
        return transaction {
            if(exists(card)) return@transaction null
            val id = UUID.randomUUID().toString()
            CardTable.insert {
                it[uuid] = id
                it[character] = card.characterId
                it[obtainedDate] = card.obtainedDate
                it[attachedUser] = card.attachedUser?.id
            }
            return@transaction card.copy(uuid = id)
        }
    }

    override suspend fun updateCard(uuid: String, card: Card): Card? {
        return transaction {
            if(!exists(card)) return@transaction null
            CardTable.update({ CardTable.uuid eq uuid }){
                it[CardTable.uuid] = uuid
                it[character] = card.characterId
                it[obtainedDate] = Clock.System.now().toLocalDateTime(timeZone = TimeZone.UTC)
                it[attachedUser] = card.attachedUser?.id
            }
            return@transaction card
        }
    }

    override suspend fun deleteCard(uuid: String): Int? {
        return transaction {
            if(!exists(uuid)) return@transaction null
            CardTable.deleteWhere { CardTable.uuid eq uuid }
        }
    }

    private fun exists(card: Card): Boolean{
        return !CardTable.selectAll()
            .where{ CardTable.character eq card.characterId }.empty()
    }

    private fun exists(uuid: String): Boolean{
        return !CardTable.selectAll()
            .where{ CardTable.uuid eq uuid }.empty()
    }

}
package io.github.katarem.domain.repository.character

import io.github.katarem.domain.database.CardTable
import io.github.katarem.domain.database.CharacterTable
import io.github.katarem.domain.database.toCard
import io.github.katarem.domain.database.toCharacter
import io.github.katarem.domain.model.Character
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class CharacterRepositoryImpl : CharacterRepository {
    override suspend fun getAll(): List<Character> {
        return transaction {
            CharacterTable.selectAll().map(::toCharacter)
        }
    }

    override suspend fun insertAll(characters: List<Character>): List<Character> {
        val hasRepeated = transaction { characters.any { exists(it) } }
        if(hasRepeated) return emptyList()
        return newSuspendedTransaction {
            val inserted = CharacterTable.batchInsert(characters, shouldReturnGeneratedValues = true){ character ->
                this[CharacterTable.name] = character.name
                this[CharacterTable.description] = character.description
                this[CharacterTable.origin] = character.origin
                this[CharacterTable.age] = character.age
                this[CharacterTable.gender] = character.gender
                this[CharacterTable.cardId] = ""
            }.map { toCharacter(it) }
            val cards = CardTable.batchInsert(inserted, shouldReturnGeneratedValues = true){ char ->
                val uuid = UUID.randomUUID().toString()
                this[CardTable.uuid] = uuid
                this[CardTable.character] = char.id
                this[CardTable.obtainedDate] = null
            }.map { toCard(it) }
            val finalCharacters = cards.map { card ->
                val existent = getCharacterById(card.characterId)!!
                val updated = updateCharacter(card.characterId, existent.copy(cardId = card.uuid))!!
                return@map updated
            }
            return@newSuspendedTransaction finalCharacters
        }
    }

    override suspend fun getCharacterById(id: Long): Character? {
        return transaction {
            CharacterTable.selectAll()
                .where{ CharacterTable.id eq id}
                .map(::toCharacter)
                .singleOrNull()
        }
    }

    override suspend fun insertCharacter(character: Character): Character? {
        return newSuspendedTransaction {
            if(exists(character)) return@newSuspendedTransaction null
            val id = CharacterTable.insertAndGetId {
                it[name] = character.name
                it[origin] = character.origin
                it[gender] = character.gender
                it[age] = character.age
                it[description] = character.description
            }
            // when we create a character we want it to be associated with a card
            val uuid = UUID.randomUUID().toString()
            CardTable.insert {
                it[CardTable.uuid] = uuid
                it[CardTable.character] = id
            }
            return@newSuspendedTransaction character.copy(id = id.value)
        }
    }

    override suspend fun updateCharacter(id: Long, character: Character): Character? {
        return transaction {
            if(!exists(id)) return@transaction null
            CharacterTable.update({ CharacterTable.id eq id }) {
                it[name] = character.name
                it[origin] = character.origin
                it[gender] = character.gender
                it[age] = character.age
                it[description] = character.description
                it[cardId] = character.cardId
            }
            return@transaction character
        }
    }

    override suspend fun deleteCharacter(id: Long): Int? {
        return transaction {
            if(!exists(id)) return@transaction null
            CharacterTable.deleteWhere { CharacterTable.id eq id }
        }
    }

    private fun exists(character: Character): Boolean{
        return !CharacterTable.selectAll().where {
            (CharacterTable.name eq character.name) and
                    (CharacterTable.origin eq character.origin)
        }.empty()
    }

    private fun exists(id: Long): Boolean{
        return !CharacterTable.selectAll().where {
            CharacterTable.id eq id
        }.empty()
    }

    suspend fun searchByFilters(name: String?, origin: String?, gender: String?): List<Character>{
        return transaction {
            var query =  CharacterTable.selectAll()
            name?.let {
                query = query.andWhere { CharacterTable.name like "%$it%" }
            }
            origin?.let {
                query = query.andWhere { CharacterTable.origin like "%$it%" }
            }
            gender?.let {
                query = query.andWhere { CharacterTable.gender eq it }
            }
            return@transaction query.map(::toCharacter)
        }
    }

}
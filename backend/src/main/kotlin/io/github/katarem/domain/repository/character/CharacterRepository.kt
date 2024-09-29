package io.github.katarem.domain.repository.character

import io.github.katarem.domain.model.Character

interface CharacterRepository {
    suspend fun getAll(): List<Character>
    suspend fun insertAll(characters: List<Character>): List<Character>
    suspend fun getCharacterById(id: Long): Character?
    suspend fun insertCharacter(character: Character): Character?
    suspend fun updateCharacter(id: Long,character: Character): Character?
    suspend fun deleteCharacter(id: Long): Int?
}
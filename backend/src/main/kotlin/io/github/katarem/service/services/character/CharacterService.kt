package io.github.katarem.service.services.character

import io.github.katarem.presentation.dto.CharacterDTO
import io.github.katarem.presentation.dto.CharacterFilters

interface CharacterService {
    suspend fun getAll(): List<CharacterDTO>
    suspend fun insertAll(characters: List<CharacterDTO>): List<CharacterDTO>
    suspend fun search(params: CharacterFilters): List<CharacterDTO>
    suspend fun getById(id: Long): CharacterDTO?
    suspend fun insert(characterDTO: CharacterDTO): CharacterDTO?
    suspend fun update(id: Long, characterDTO: CharacterDTO): CharacterDTO?
    suspend fun delete(id: Long): Int?
}
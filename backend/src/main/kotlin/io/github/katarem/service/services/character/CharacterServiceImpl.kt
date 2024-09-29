package io.github.katarem.service.services.character

import io.github.katarem.domain.mapper.Mapper
import io.github.katarem.domain.repository.character.CharacterRepositoryImpl
import io.github.katarem.presentation.dto.CharacterDTO
import io.github.katarem.presentation.dto.CharacterFilters

class CharacterServiceImpl(
    private val repository: CharacterRepositoryImpl
): CharacterService {

    override suspend fun getAll(): List<CharacterDTO> {
        return repository.getAll().map(Mapper::toDto)
    }

    override suspend fun insertAll(characters: List<CharacterDTO>): List<CharacterDTO> {
        val toInsert = characters.map(Mapper::toEntity)
        return repository.insertAll(toInsert).map(Mapper::toDto)
    }

    override suspend fun search(params: CharacterFilters): List<CharacterDTO> {
        return repository.searchByFilters(
            params.name,
            params.origin,
            params.gender
        ).map(Mapper::toDto)
    }

    override suspend fun getById(id: Long): CharacterDTO? {
        return repository.getCharacterById(id)?.let(Mapper::toDto)
    }

    override suspend fun insert(characterDTO: CharacterDTO): CharacterDTO? {
        val toInsert = Mapper.toEntity(characterDTO)
        return repository.insertCharacter(toInsert)?.let(Mapper::toDto)
    }

    override suspend fun update(id: Long, characterDTO: CharacterDTO): CharacterDTO? {
        val toInsert = Mapper.toEntity(characterDTO)
        return repository.updateCharacter(id, toInsert)?.let(Mapper::toDto)
    }

    override suspend fun delete(id: Long): Int? {
        return repository.deleteCharacter(id)
    }
}
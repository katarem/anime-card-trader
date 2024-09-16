package io.github.katarem.service

import io.github.katarem.domain.mapper.Mapper
import io.github.katarem.domain.repository.UserRepositoryImpl
import io.github.katarem.domain.utils.encryptor
import io.github.katarem.presentation.dto.UserDTO

class UserServiceImpl : UserService {

    private val repository = UserRepositoryImpl()

    override suspend fun getAll(): List<UserDTO> {
        return repository.allUsers().map(Mapper::toDto)
    }

    override suspend fun getById(id: Long): UserDTO? {
        return repository.getUserById(id)?.let(Mapper::toDto)
    }

    override suspend fun insert(userDTO: UserDTO): UserDTO? {
        val toInsert = Mapper.toEntity(userDTO)
        if(toInsert.email.isBlank() || toInsert.password.isBlank())
            return null
        return repository.insertUser(toInsert)?.let(Mapper::toDto)
    }

    override suspend fun update(id: Long, userDTO: UserDTO): UserDTO? {
        val updated = Mapper.toEntity(userDTO)
        return repository.updateUser(id, updated)?.let(Mapper::toDto)
    }

    override suspend fun delete(id: Long): Int? {
        return repository.deleteUser(id)
    }

    fun authenticate(username: String, password: String): UserDTO? {
        return repository.authenticate(username, password)?.let(Mapper::toDto)
    }

}
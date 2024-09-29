package io.github.katarem.service.services.user

import io.github.katarem.domain.mapper.Mapper
import io.github.katarem.domain.repository.user.UserRepositoryImpl
import io.github.katarem.presentation.checkValidApiKey
import io.github.katarem.presentation.dto.UserDTO
import java.util.*

class UserServiceImpl(
    private val repository: UserRepositoryImpl
) : UserService {


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

    fun exists(username: String): Boolean {
        return repository.getByUsername(username)?.let(Mapper::toDto) != null
    }

    fun getByUsername(username: String): UserDTO? {
        return repository.getByUsername(username)?.let(Mapper::toDto)
    }

    fun internalGetByUsername(username: String): UserDTO? {
        return repository.getByUsername(username)?.let{ Mapper.toDto(it, true) }
    }

    suspend fun changePassword(userDTO: UserDTO, newPassword: String): UserDTO? {
        return userDTO.password?.let {
            return@let authenticate(userDTO.username, it)?.let { user ->
                val entity = Mapper.toEntity(user)
                val updated = repository.updateUser(entity.id, entity.copy(password = newPassword))
                return@let updated?.let(Mapper::toDto)
            }
        }
    }
    suspend fun changeEmail(userDTO: UserDTO, newEmail: String): UserDTO? {
        return userDTO.password?.let {
            return@let authenticate(userDTO.username, it)?.let { user ->
                val entity = Mapper.toEntity(user)
                val updated = repository.updateUser(entity.id, entity.copy(email = newEmail))
                return@let updated?.let(Mapper::toDto)
            }
        }
    }

    fun checkApiKey(key: String): Boolean {
        return checkValidApiKey(key) &&
        repository.apiKeyInDatabase(key)
    }

    fun authenticate(username: String, password: String): UserDTO? {
        return repository.authenticate(username, password)?.let(Mapper::toDto)
    }

    fun generateApiKey(username: String, password: String): String? {
        return repository.authenticate(username,password)?.let { auth ->
            if(repository.isAdmin(auth)){
                val uuid = UUID.randomUUID().toString()
                val key = "ACAPI-$uuid"
                repository.storeApiKey(username, key)
                return@let key
            } else null
        }
    }

}
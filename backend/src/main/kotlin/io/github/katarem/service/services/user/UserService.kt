package io.github.katarem.service.services.user

import io.github.katarem.presentation.dto.UserDTO

interface UserService {
    suspend fun getAll(): List<UserDTO>
    suspend fun getById(id: Long): UserDTO?
    suspend fun insert(userDTO: UserDTO): UserDTO?
    suspend fun update(id: Long, userDTO: UserDTO): UserDTO?
    suspend fun delete(id: Long): Int?
}
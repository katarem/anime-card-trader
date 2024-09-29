package io.github.katarem.domain.repository.user

import io.github.katarem.domain.model.User

interface UserRepository {
    suspend fun allUsers(): List<User>
    suspend fun insertUser(user: User): User?
    suspend fun getUserById(id: Long): User?
    suspend fun updateUser(id: Long, user: User): User?
    suspend fun deleteUser(id: Long): Int?
}
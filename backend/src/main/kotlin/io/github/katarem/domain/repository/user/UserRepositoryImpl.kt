package io.github.katarem.domain.repository.user

import io.github.katarem.domain.database.UserTable
import io.github.katarem.domain.database.toUser
import io.github.katarem.domain.model.Roles
import io.github.katarem.domain.model.User
import io.github.katarem.service.security.Encryptor
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepositoryImpl : UserRepository {

    override suspend fun allUsers(): List<User> {
        return transaction {
            UserTable.selectAll().map(::toUser)
        }
    }

    override suspend fun insertUser(user: User): User? {
        return transaction {
            if(exists(user)) return@transaction null
            val id = UserTable.insertAndGetId {
                it[username] = user.username
                it[email] = user.email
                it[password] = Encryptor.encryptPassword(user.password)
            }
            return@transaction user.copy(id = id.value)
        }
    }

    override suspend fun getUserById(id: Long): User? {
        return transaction {
            UserTable.selectAll()
                .where{ UserTable.id eq id }
                .map(::toUser)
                .singleOrNull()
        }
    }

    override suspend fun updateUser(id: Long, user: User): User? {
        return transaction {
            if(!exists(id)) return@transaction null
            UserTable.update({ UserTable.id eq id }) {
                it[username] = user.username
                it[email] = user.email
                it[password] = Encryptor.encryptPassword(user.password)
                it[nextCard] = user.nextCard
            }
            return@transaction user
        }
    }

    override suspend fun deleteUser(id: Long): Int? {
        return transaction {
            if(!exists(id)) return@transaction null
            return@transaction UserTable.deleteWhere { UserTable.id eq id }
        }
    }

    fun getByUsername(userName: String): User? {
        return transaction {
            UserTable.selectAll().where {
                UserTable.username eq userName
            }.map(::toUser).singleOrNull()
        }
    }

    fun isAdmin(user: User): Boolean {
        return user.role == Roles.ADMIN
    }

    fun authenticate(userName: String, password: String): User?{
        return transaction {
            val user = UserTable.selectAll().where { UserTable.username eq userName }.map(::toUser).singleOrNull()
            user?.let {
                val correctPassword = Encryptor.checkPassword(password, user.password)
                if(correctPassword) return@transaction it
                else return@transaction null
            }
        }
    }

    fun apiKeyInDatabase(key: String): Boolean{
        return transaction {
            !UserTable.selectAll().where {
                UserTable.apikey eq key
            }.empty()
        }
    }

    fun storeApiKey(userName: String, key: String){
        return transaction {
            UserTable.update({ UserTable.username eq userName }){
                it[apikey] = key
            }
        }
    }

    private fun exists(id: Long): Boolean {
        return transaction {
            !UserTable.selectAll()
                .where { UserTable.id eq id }.empty()
        }
    }

    fun exists(user: User): Boolean{
        return transaction {
            !UserTable.selectAll()
                .where { UserTable.username eq user.username }.empty()
        }
    }

}
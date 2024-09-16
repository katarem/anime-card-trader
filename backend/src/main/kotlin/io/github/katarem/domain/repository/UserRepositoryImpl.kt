package io.github.katarem.domain.repository

import io.github.katarem.domain.database.UserTable
import io.github.katarem.domain.database.toUser
import io.github.katarem.domain.model.User
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import javax.print.attribute.standard.RequestingUserName

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
                it[password] = user.password
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
                it[password] = user.password
                it[lastTimeChecked] = Clock.System.now().toLocalDateTime(timeZone = TimeZone.currentSystemDefault())
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

    fun authenticate(userName: String, password: String): User?{
        return transaction {
            UserTable.selectAll().where {
                UserTable.username eq userName
            }.map { toUser(it) }.singleOrNull()?.let {
                return@let if(password == it.password)
                    it
                else
                    null
            }
        }
    }


    private fun exists(id: Long): Boolean {
        return transaction {
            !UserTable.selectAll()
                .where { UserTable.id eq id }.empty()
        }
    }

    private fun exists(user: User): Boolean{
        return transaction {
            !UserTable.selectAll()
                .where { UserTable.username eq user.username }.empty()
        }
    }

}
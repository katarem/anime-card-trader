package io.github.katarem.domain.repository.notification

import io.github.katarem.domain.database.NotificationTable
import io.github.katarem.domain.database.toNotification
import io.github.katarem.domain.model.Notification
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class NotificationRepositoryImpl: NotificationRepository {
    override fun getAll(): List<Notification> {
        return transaction {
            NotificationTable.selectAll().map(::toNotification)
        }
    }

    override fun getById(id: Long): Notification? {
        return transaction {
            NotificationTable.selectAll().where {
                NotificationTable.id eq id
            }.map(::toNotification).singleOrNull()
        }
    }

    override fun insert(notification: Notification): Notification? {
        if(exists(notification)) return null
        return transaction {
            val id = NotificationTable.insertAndGetId {
                it[userId] = notification.userId
                it[title] = notification.title
                it[content] = notification.content
                it[createDate] = notification.createdAt
                it[read] = notification.read
            }
            return@transaction notification.copy(id = id.value)
        }
    }

    override fun update(id: Long, notification: Notification): Notification? {
        if(!exists(id)) return null
        return transaction {
            NotificationTable.update({ NotificationTable.id eq id }){
                it[userId] = notification.userId
                it[title] = notification.title
                it[content] = notification.content
                it[read] = notification.read
            }
            return@transaction notification
        }
    }

    override fun delete(id: Long): Int? {
        if (!exists(id)) return null
        return transaction {
            NotificationTable.deleteWhere { NotificationTable.id eq id }
        }
    }

    fun getByUserId(id: Long): List<Notification>{
        return transaction {
            NotificationTable.selectAll().where {
                NotificationTable.userId eq id
            }.map(::toNotification)
        }
    }

    private fun exists(notification: Notification): Boolean {
        return transaction {
            !NotificationTable.selectAll().where {
                (NotificationTable.userId eq notification.userId) and
                        (NotificationTable.title eq notification.title)
            }.empty()
        }
    }
    private fun exists(id: Long): Boolean {
        return transaction {
            !NotificationTable.selectAll().where {
                (NotificationTable.id eq id)
            }.empty()
        }
    }
}
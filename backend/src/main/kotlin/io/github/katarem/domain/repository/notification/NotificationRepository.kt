package io.github.katarem.domain.repository.notification

import io.github.katarem.domain.model.Notification

interface NotificationRepository {
    fun getAll(): List<Notification>
    fun getById(id: Long): Notification?
    fun insert(notification: Notification): Notification?
    fun update(id: Long, notification: Notification): Notification?
    fun delete(id: Long): Int?
}
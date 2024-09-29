package io.github.katarem.service.notification

import io.github.katarem.presentation.dto.NotificationDTO

interface NotificationService {
    fun getAll(): List<NotificationDTO>
    fun getById(id: Long): NotificationDTO?
    fun insert(notificationDTO: NotificationDTO): NotificationDTO?
    fun update(id: Long, notificationDTO: NotificationDTO): NotificationDTO?
    fun delete(id: Long): Int?
    fun read(id: Long): Boolean
}
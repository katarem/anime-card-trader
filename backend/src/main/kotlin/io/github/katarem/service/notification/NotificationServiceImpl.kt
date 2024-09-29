package io.github.katarem.service.notification

import io.github.katarem.domain.mapper.Mapper
import io.github.katarem.domain.repository.notification.NotificationRepositoryImpl
import io.github.katarem.presentation.dto.NotificationDTO
import io.github.katarem.presentation.dto.UserDTO

class NotificationServiceImpl(
    val repository: NotificationRepositoryImpl
): NotificationService {

    override fun getAll(): List<NotificationDTO> {
        return repository.getAll().map(Mapper::toDto)
    }

    override fun getById(id: Long): NotificationDTO? {
        return repository.getById(id)?.let(Mapper::toDto)
    }

    override fun insert(notificationDTO: NotificationDTO): NotificationDTO? {
        val mapped = Mapper.toEntity(notificationDTO)
        return repository.insert(mapped)?.let(Mapper::toDto)
    }

    override fun update(id: Long, notificationDTO: NotificationDTO): NotificationDTO? {
        val mapped = Mapper.toEntity(notificationDTO)
        return repository.update(id, mapped)?.let(Mapper::toDto)
    }

    override fun delete(id: Long): Int? {
        return repository.delete(id)
    }

    override fun read(id: Long): Boolean {
        val notification = repository.getById(id) ?: return false
        val updated = repository.update(notification.id, notification.copy(read = true))
        return updated != null
    }

    fun broadcast(notificationDTO: NotificationDTO, users: List<UserDTO>): List<NotificationDTO> {
        val insertedNotifications = users.map { user ->
            val not = Mapper.toEntity(notificationDTO.copy(userId = user.id!!))
            val inserted = repository.insert(not)
            return@map inserted?.let { Mapper.toDto(it) }
        }
        val failedNotifications = insertedNotifications.any { it == null }
        return if(failedNotifications) emptyList()
        else insertedNotifications.mapNotNull { it!! }
    }

    fun getUserNotifications(userId: Long): List<NotificationDTO> {
        return repository.getByUserId(userId).map(Mapper::toDto)
    }
}
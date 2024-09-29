package io.github.katarem.presentation.routing

import io.github.katarem.domain.utils.extractId
import io.github.katarem.presentation.dto.NotificationDTO
import io.github.katarem.presentation.response.ApiResponse
import io.github.katarem.service.services.auth.AuthService
import io.github.katarem.service.services.user.UserServiceImpl
import io.github.katarem.service.notification.NotificationServiceImpl
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.notificationRoutes(
    notificationService: NotificationServiceImpl,
    userServiceImpl: UserServiceImpl,
    authService: AuthService
){
    routing{
        route("api"){
            get("/notification") {
                authService.adminAccessEndpoint(call)?.run {
                    val body = notificationService.getAll()
                    call.respond(HttpStatusCode.OK, ApiResponse(
                        body = body
                    ))
                }
            }
            get("/notification/user/{id}"){
                extractId(call)?.let { id ->
                    notificationService.getUserNotifications(id).let { notification ->
                        call.respond(
                            HttpStatusCode.OK, ApiResponse(
                                body = notification
                            )
                        )
                    }
                }
            }
            get("/notification/{id}"){
                extractId(call)?.let { id ->
                    notificationService.getById(id)?.let { notification ->
                        call.respond(HttpStatusCode.OK, ApiResponse(
                            body = notification
                        ))
                    } ?: call.respond(HttpStatusCode.BadRequest, ApiResponse<String>(
                            errorMessage = "Notification wasn't found with id=$id"
                    ))
                } ?: call.respond(HttpStatusCode.BadRequest, ApiResponse<String>(
                        errorMessage = "Invalid id"
                ))
            }
            post<NotificationDTO>("/notification") { dto ->
                authService.adminAccessEndpoint(call)?.run {
                    val inserted = notificationService.insert(dto)
                    inserted?.let {
                        call.respond(HttpStatusCode.OK, ApiResponse<NotificationDTO>(
                            body = it
                        ))
                    } ?: call.respond(HttpStatusCode.BadRequest, ApiResponse<String>(
                        errorMessage = "Notification already exists"
                    ))
                }
            }
            put<NotificationDTO>("/notification/{id}") { dto ->
                authService.adminAccessEndpoint(call)?.run {
                    extractId(call)?.let { id ->
                        notificationService.update(id, dto)?.let { updated ->
                            call.respond(HttpStatusCode.OK, ApiResponse(
                                body = updated
                            ))
                        } ?: call.respond(HttpStatusCode.BadRequest, ApiResponse<String>(
                                errorMessage = "Notification with id=$id doesn't exist"
                        ))
                    }
                }
            }
            delete("/notification/{id}") {
                authService.adminAccessEndpoint(call)?.run {
                    extractId(call)?.let { id ->
                        notificationService.delete(id)?.let { deleted ->
                            call.respond(HttpStatusCode.OK, ApiResponse(
                                body = deleted
                            ))
                        } ?: call.respond(HttpStatusCode.NotFound, ApiResponse<String>(
                            errorMessage = "Notification with id=$id doesn't exist"
                        ))
                    }
                }
            }
            // read notification
            patch("/notification/{id}") {
                extractId(call)?.let { id ->
                    val executed = notificationService.read(id)
                    if(executed){
                        call.respond(HttpStatusCode.OK, ApiResponse(
                            body = "Notification read successfully"
                        ))
                    } else {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<String>(
                                errorMessage = "Notification with id=$id doesn't exist"
                        ))
                    }
                }
            }
            post<NotificationDTO>("/notification/broadcast") { dto ->
                authService.adminAccessEndpoint(call)?.run {
                    val users = userServiceImpl.getAll()
                    val createdNotifications = notificationService.broadcast(dto, users)
                    if(createdNotifications.isNotEmpty()){
                        call.respond(HttpStatusCode.OK, ApiResponse(
                            body = "Notification broadcasted successfully"
                        ))
                    } else {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<String>(
                            errorMessage = "There was an error creating 1 or more notifications"
                        ))
                    }
                }
            }
        }
    }
}
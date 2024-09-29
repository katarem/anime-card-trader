package io.github.katarem.presentation.routing

import io.github.katarem.presentation.response.ApiResponse
import io.github.katarem.domain.utils.extractId
import io.github.katarem.presentation.dto.UserDTO
import io.github.katarem.service.services.auth.AuthService
import io.github.katarem.service.services.user.UserServiceImpl
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.userRoutes(
    service: UserServiceImpl,
    authService: AuthService
) {
    routing {
        route("api") {
            get("/users") {
                authService.adminAccessEndpoint(call)?.run {
                    call.respond(
                        HttpStatusCode.OK, ApiResponse(
                            body = service.getAll()
                        )
                    )
                }
            }
            post<UserDTO>("/users/login") { user ->
                if (user.password.isNullOrEmpty()) {
                    call.respond(
                        HttpStatusCode.BadRequest, ApiResponse<String>(
                            errorMessage = "You must provide a password to login!"
                        )
                    )
                }
                service.authenticate(user.username, user.password!!)?.let { auth ->
                    call.respond(
                        HttpStatusCode.OK, ApiResponse(
                            body = auth
                        )
                    )
                } ?: call.respond(
                    HttpStatusCode.BadRequest, ApiResponse<String>(
                        errorMessage = "Incorrect username or password"
                    )
                )
            }

            post<UserDTO>("/users/register") {
                service.insert(it)?.let { inserted ->
                    call.respond(
                        HttpStatusCode.OK, ApiResponse(
                            body = inserted
                        )
                    )
                } ?: call.respond(
                    HttpStatusCode.BadRequest, ApiResponse<String>(
                        errorMessage = "User was already created OR missing email/password"
                    )
                )
            }
            get("/users/{id}") {
                authService.adminAccessEndpoint(call)?.run {
                    extractId(call)?.let { id ->
                        service.getById(id)?.let { obtained ->
                            call.respond(
                                HttpStatusCode.OK, ApiResponse(
                                    body = obtained
                                )
                            )
                        } ?: call.respond(
                            HttpStatusCode.NotFound, ApiResponse<String>(
                                errorMessage = "User with id=$id wasn't found"
                            )
                        )
                    }
                }
            }
            put<UserDTO>("/users/{id}") { user ->
                extractId(call)?.let { id ->
                    service.update(id, user)?.let { updated ->
                        call.respond(
                            HttpStatusCode.OK, ApiResponse(
                                body = updated
                            )
                        )
                    } ?: call.respond(
                        HttpStatusCode.NotFound, ApiResponse<String>(
                            errorMessage = "User with id=$id wasn't found"
                        )
                    )
                }
            }
            patch<UserDTO>("/users/change-password") {
                val pwd = call.request.headers["X-NEW-USER-PASSWORD"]
                if (pwd.isNullOrEmpty())
                    call.respond(
                        HttpStatusCode.BadRequest, ApiResponse<String>(
                            errorMessage = "You must provide a new password on the headers"
                        )
                    )
                service.changePassword(it, pwd!!)?.let {
                    call.respond(
                        HttpStatusCode.OK, ApiResponse<String>(
                            body = "Password changed succesfully"
                        )
                    )
                } ?: call.respond(
                    HttpStatusCode.BadRequest, ApiResponse<String>(
                        errorMessage = "Old credentials were invalid."
                    )
                )
            }

            patch<UserDTO>("/users/change-email") {
                val newEmail = call.request.headers["X-NEW-USER-EMAIL"]
                if (newEmail.isNullOrEmpty())
                    call.respond(
                        HttpStatusCode.BadRequest, ApiResponse<String>(
                            errorMessage = "You must provide a new email on the headers"
                        )
                    )
                service.changeEmail(it, newEmail!!)?.let {
                    call.respond(
                        HttpStatusCode.OK, ApiResponse<String>(
                            body = "Email changed succesfully"
                        )
                    )
                } ?: call.respond(
                    HttpStatusCode.BadRequest, ApiResponse<String>(
                        errorMessage = "Old credentials were invalid."
                    )
                )
            }

            delete("/users/{id}") {
                authService.adminAccessEndpoint(call)?.run {
                    extractId(call)?.let { id ->
                        service.delete(id)?.let {
                            call.respond(
                                HttpStatusCode.Accepted, ApiResponse(
                                    body = "User $id deleted successfully"
                                )
                            )
                        } ?: call.respond(
                            HttpStatusCode.NotFound, ApiResponse<String>(
                                errorMessage = "User with id=$id wasn't found"
                            )
                        )
                    }
                }
            }
        }
    }
}
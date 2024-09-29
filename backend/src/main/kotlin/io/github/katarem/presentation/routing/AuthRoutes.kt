package io.github.katarem.presentation.routing

import io.github.katarem.presentation.response.ApiResponse
import io.github.katarem.presentation.dto.UserDTO
import io.github.katarem.service.services.user.UserServiceImpl
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.authRoutes(
    service: UserServiceImpl
){
    routing {
        post<UserDTO>("/auth/gen-key"){ user ->
            user.password?.let { pwd ->
                service.generateApiKey(user.username,pwd)?.let { apiKey ->
                    call.respond(HttpStatusCode.OK, ApiResponse(
                        body = apiKey
                    )
                    )
                } ?: call.respond(HttpStatusCode.Unauthorized, ApiResponse<String>(
                    errorMessage = "This endpoint is just for Admins."
                )
                )
            }?: call.respond(HttpStatusCode.BadRequest, ApiResponse<String>(
                errorMessage = "You must provide a password"
            )
            )
        }
    }
}
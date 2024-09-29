package io.github.katarem.service.services.auth

import io.github.katarem.domain.utils.extractQueryUsername
import io.github.katarem.presentation.dto.UserDTO
import io.github.katarem.presentation.response.ApiResponse
import io.github.katarem.service.services.user.UserServiceImpl
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.response.*

class AuthService(
    private val userService: UserServiceImpl
) {

    suspend fun adminAccessEndpoint(call: RoutingCall): Unit? {
        val apiKey = call.request.headers["X-API-KEY"]
        if (apiKey.isNullOrEmpty()) {
            call.respond(
                HttpStatusCode.BadRequest, ApiResponse<String>(
                    errorMessage = "You must provide a valid api key"
                )
            )
            return null
        }
        if (!userService.checkApiKey(apiKey)){
            call.respond(
                HttpStatusCode.Unauthorized, ApiResponse(
                    body = "Invalid key: You MUST be an admin to use this endpoint"
                )
            )
            return null
        } else return Unit
}

suspend fun checkIfUserExists(call: RoutingCall): UserDTO? {
    return extractQueryUsername(call)?.let { username ->
        val exists = userService.exists(username)
        return if (!exists) {
            call.respond(
                HttpStatusCode.BadRequest, ApiResponse<String>(
                    errorMessage = "User with username=$username doesn't exist"
                )
            )
            null
        } else userService.getByUsername(username)!!
    }
}

}
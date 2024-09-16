package io.github.katarem.presentation.routing

import io.github.katarem.domain.response.ApiResponse
import io.github.katarem.domain.utils.extractId
import io.github.katarem.presentation.dto.UserDTO
import io.github.katarem.service.UserServiceImpl
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.userRoutes(){

    val service = UserServiceImpl()

    routing {
        get("/users") {
            call.respond(HttpStatusCode.OK,ApiResponse(
                body = service.getAll()
            ))
        }
        post<UserDTO>("/users/login"){ user ->
            if(user.password.isNullOrEmpty()){
                call.respond(HttpStatusCode.BadRequest, ApiResponse<String>(
                    errorMessage = "You must provide a password to login!"
                ))
            }
            service.authenticate(user.username,user.password!!)?.let { auth ->
                call.respond(HttpStatusCode.OK, ApiResponse(
                    body = auth
                ))
            } ?: call.respond(HttpStatusCode.BadRequest, ApiResponse<String>(
                errorMessage = "Incorrect username or password"
            ))
        }

        post<UserDTO>("/users"){
            service.insert(it)?.let { inserted ->
                call.respond(HttpStatusCode.OK, ApiResponse(
                    body = inserted
                ))
            } ?: call.respond(HttpStatusCode.BadRequest, ApiResponse<String>(
                errorMessage = "User was already created OR missing email/password"
            ))
        }
        get("/users/{id}"){
            extractId(call)?.let { id ->
                service.getById(id)?.let { obtained ->
                    call.respond(HttpStatusCode.OK, ApiResponse(
                        body = obtained
                    ))
                } ?: call.respond(HttpStatusCode.NotFound, ApiResponse<String>(
                    errorMessage = "User with id=$id wasn't found"
                ))
            }
        }
        put<UserDTO>("/users/{id}") { user ->
            extractId(call)?.let { id ->
                service.update(id, user)?.let { updated ->
                    call.respond(HttpStatusCode.OK, ApiResponse(
                        body = updated
                    ))
                } ?: call.respond(HttpStatusCode.NotFound, ApiResponse<String>(
                    errorMessage = "User with id=$id wasn't found"
                ))
            }
        }
        delete("/users/{id}"){
            extractId(call)?.let { id ->
                service.delete(id)?.let {
                    call.respond(HttpStatusCode.Accepted, ApiResponse(
                        body = "User $id deleted successfully"
                    ))
                } ?: call.respond(HttpStatusCode.NotFound, ApiResponse<String>(
                        errorMessage = "User with id=$id wasn't found"
                    ))
            }
        }
    }
}
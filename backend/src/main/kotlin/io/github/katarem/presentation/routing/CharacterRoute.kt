package io.github.katarem.presentation.routing

import io.github.katarem.presentation.response.ApiResponse
import io.github.katarem.domain.utils.extractFilters
import io.github.katarem.domain.utils.extractId
import io.github.katarem.presentation.dto.CharacterDTO
import io.github.katarem.service.services.auth.AuthService
import io.github.katarem.service.services.character.CharacterServiceImpl
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.characterRoute(
    service: CharacterServiceImpl,
    authService: AuthService
){
    routing {
        route("api"){
            post<List<CharacterDTO>>("/character/create") {
                authService.adminAccessEndpoint(call)?.run {
                    val createdCharacters = service.insertAll(it)
                    if(createdCharacters.isEmpty())
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<String>(
                            errorMessage = "One or more characters in your list already exist in the database.",
                        )
                        )
                    call.respond(HttpStatusCode.OK, ApiResponse(
                        body = createdCharacters,
                    )
                    )
                }
            }

            get("/character/search"){
                val filters = extractFilters(call)
                val result = service.search(filters)
                if(result.isEmpty())
                    call.respond(HttpStatusCode.NotFound, ApiResponse<String>(
                        errorMessage = "Characters weren't found with the specified filters: ${filters.name?.let {
                            "name=$it "
                        } ?: ""}${filters.origin?.let {
                            "origin=$it "
                        } ?: ""}${filters.gender?.let{
                            "gender=$it "
                        } ?: ""}".trimEnd()
                    ))
                else
                    call.respond(HttpStatusCode.OK, ApiResponse(
                        body = result
                    )
                    )
            }
            get("/character") {
                val all = service.getAll()
                call.respond(HttpStatusCode.OK, ApiResponse(
                    body = all
                )
                )
            }
            get("/character/{id}") {
                extractId(call)?.let { id ->
                    service.getById(id)?.let {
                        call.respond(HttpStatusCode.OK, it)
                    } ?: call.respond(HttpStatusCode.NotFound, ApiResponse<String>(
                        errorMessage = "Character with id=$id doesn't exist"
                    ))
                }

            }
            post<CharacterDTO>("/character") {
                authService.adminAccessEndpoint(call)?.run {
                    service.insert(it)?.let { inserted ->
                        call.respond(HttpStatusCode.Accepted, ApiResponse(
                            body = inserted
                        ))
                    } ?: call.respond(HttpStatusCode.Conflict, ApiResponse<String>(
                        errorMessage = "Character already exists"
                    ))
                }
            }
            put<CharacterDTO>("/character/{id}") {
                authService.adminAccessEndpoint(call)?.run {
                    extractId(call)?.let { id ->
                        service.update(id, it)?.let { updated ->
                            call.respond(HttpStatusCode.OK, updated)
                        } ?: call.respond(HttpStatusCode.NotFound, "Character with id=$id doesn't exist")
                    }
                }
            }
            delete("/character/{id}") {
                authService.adminAccessEndpoint(call)?.run {
                    extractId(call)?.let { id ->
                        service.delete(id)?.let {
                            call.respond(HttpStatusCode.Accepted, ApiResponse(
                                body = "Character with id=$id deleted succesfully"
                            )
                            )
                        } ?: call.respond(HttpStatusCode.NotFound, ApiResponse<String>(
                            errorMessage = "Character with id=$id"
                        )
                        )
                    }
                }
            }
        }
    }
}
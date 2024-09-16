package io.github.katarem.presentation.routing

import io.github.katarem.domain.response.ApiResponse
import io.github.katarem.domain.utils.extractFilters
import io.github.katarem.domain.utils.extractId
import io.github.katarem.presentation.dto.CharacterDTO
import io.github.katarem.service.CharacterServiceImpl
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.characterRoute(){

    val service = CharacterServiceImpl()

    routing {

        post<List<CharacterDTO>>("/character/create") {
            val createdCharacters = service.insertAll(it)
            if(createdCharacters.isEmpty())
                call.respond(HttpStatusCode.BadRequest, ApiResponse<String>(
                    errorMessage = "One or more characters in your list already exist in the database.",
                ))
            call.respond(HttpStatusCode.OK, ApiResponse(
                body = createdCharacters,
            )
            )
        }

        get("/character/search"){
            val filters = extractFilters(call)
            val result = service.search(filters)
            if(result.isEmpty())
                call.respond(HttpStatusCode.NotFound, ApiResponse(
                    body = "Characters weren't found with the specified filters: ${filters.name?.let { 
                        "name=$it "
                    } ?: ""}${filters.origin?.let { 
                        "origin=$it "
                    } ?: ""}${filters.gender?.let{
                        "gender=$it "
                    } ?: ""}"
                ))
            else
                call.respond(HttpStatusCode.OK, ApiResponse(
                    body = result
                ))
        }
        get("/character") {
            val all = service.getAll()
            call.respond(HttpStatusCode.OK, ApiResponse(
                body = all
            ))
        }
        get("/character/{id}") {
            extractId(call)?.let { id ->
                service.getById(id)?.let {
                    call.respond(HttpStatusCode.OK, it)
                } ?: call.respond(HttpStatusCode.NotFound, "Character with id=$id doesn't exist")
            }

        }
        post<CharacterDTO>("/character") {
            service.insert(it)?.let { inserted ->
                call.respond(HttpStatusCode.Accepted, inserted)
            } ?: call.respond(HttpStatusCode.Conflict, "Character with id=${it.id} already exists")
        }
        put<CharacterDTO>("/character/{id}") {
            extractId(call)?.let { id ->
                service.update(id, it)?.let { updated ->
                    call.respond(HttpStatusCode.OK, updated)
                } ?: call.respond(HttpStatusCode.NotFound, "Character with id=$id doesn't exist")
            }
        }
        delete("/character/{id}") {
            extractId(call)?.let { id ->
                service.delete(id)?.let {
                    call.respond(HttpStatusCode.Accepted,ApiResponse(
                        body = "Character with id=$id deleted succesfully"
                    ))
                } ?: call.respond(HttpStatusCode.NotFound, ApiResponse<String>(
                    errorMessage = "Character with id=$id"
                ))
            }
        }
    }
}
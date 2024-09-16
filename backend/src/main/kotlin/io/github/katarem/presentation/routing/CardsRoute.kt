package io.github.katarem.presentation.routing

import io.github.katarem.domain.response.ApiResponse
import io.github.katarem.domain.utils.extractUUID
import io.github.katarem.presentation.dto.CardDTO
import io.github.katarem.presentation.dto.UserDTO
import io.github.katarem.service.CardServiceImpl
import io.github.katarem.service.utils.checkIfCardAvailable
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.*
import kotlin.time.Duration.Companion.hours

fun Application.cardsRoutes() {

    val service = CardServiceImpl()

    routing {

        // DEBUG ONLY
        get("/cards/debug") {
            val hours = call.queryParameters["hours"]?.toIntOrNull()
            hours?.let {
                val now = Clock.System.now().toLocalDateTime(timeZone = TimeZone.currentSystemDefault())
                val instant = now.toInstant(TimeZone.currentSystemDefault())
                val time = (instant - it.hours).toLocalDateTime(TimeZone.currentSystemDefault())
                call.respond(HttpStatusCode.OK, time)
            } ?: call.respond(HttpStatusCode.BadRequest)
        }

        post<UserDTO>("/cards/check") {
            val now = Clock.System.now().toLocalDateTime(timeZone = TimeZone.currentSystemDefault())
            it.lastTimeChecked?.let { last ->
                val response = checkIfCardAvailable(last, now)
                call.respond(HttpStatusCode.OK, response)
            } ?: call.respond(
                HttpStatusCode.OK, ApiResponse<String>(
                    body = "You can check your card now",
                    cardAvailableAt = (now.toInstant(TimeZone.currentSystemDefault()) + 8.hours).toLocalDateTime(
                        TimeZone.currentSystemDefault()
                    )
                )
            )
        }
        get("/cards") {
            val all = service.getAll()
            call.respond(
                HttpStatusCode.OK, ApiResponse(
                    body = all
                )
            )
        }
        get("/cards/{id}") {
            extractUUID(call)?.let { uuid ->
                service.getById(uuid)?.let { card ->
                    call.respond(
                        HttpStatusCode.OK, ApiResponse(
                            body = card
                        )
                    )
                } ?: call.respond(
                    HttpStatusCode.NotFound, ApiResponse<String>(
                        errorMessage = "Card with uuid=$uuid wasn't found"
                    )
                )
            }
        }

        put<CardDTO>("/cards/{id}") {
            extractUUID(call)?.let { uuid ->
                service.update(uuid, it)?.let { updated ->
                    call.respond(
                        HttpStatusCode.OK, ApiResponse(
                            body = updated
                        )
                    )
                } ?: call.respond(
                    HttpStatusCode.NotFound, ApiResponse<String>(
                        errorMessage = "Card with uuid=$uuid wasn't found"
                    )
                )
            }
        }
        delete("/cards/{id}") {
            extractUUID(call)?.let { uuid ->
                service.delete(uuid)?.let {
                    call.respond(
                        HttpStatusCode.Accepted, ApiResponse(
                            body = "Character with uuid=$uuid deleted"
                        )
                    )
                } ?: call.respond(
                    HttpStatusCode.NotFound, ApiResponse<String>(
                        errorMessage = "Character with uuid=$uuid wasn't found"
                    )
                )
            }
        }
    }


}
package io.github.katarem.presentation.routing

import io.github.katarem.presentation.response.ApiResponse
import io.github.katarem.domain.utils.extractUUID
import io.github.katarem.domain.utils.extractUsername
import io.github.katarem.presentation.cooldownFinished
import io.github.katarem.presentation.dto.CardDTO
import io.github.katarem.presentation.obtainNextCardDate
import io.github.katarem.service.services.auth.AuthService
import io.github.katarem.service.services.cards.CardServiceImpl
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.*

fun Application.cardsRoutes(
    service: CardServiceImpl,
    authService: AuthService
) {
    routing {
        route("api") {

            post("/cards/generate") {
                authService.checkIfUserExists(call)?.let { user ->
                    if (user.nextCard == null) {
                        val generated = service.generateCard(user)
                        call.respond(
                            HttpStatusCode.OK, ApiResponse(
                                body = generated,
                                cardAvailableAt = obtainNextCardDate()
                            )
                        )
                    } else {
                        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                        if(cooldownFinished(now, user.nextCard)){
                            val generated = service.generateCard(user)
                            call.respond(
                                HttpStatusCode.OK, ApiResponse(
                                    body = generated,
                                    cardAvailableAt = obtainNextCardDate()
                                )
                            )
                        } else {
                            call.respond(HttpStatusCode.Accepted, ApiResponse<String>(
                                errorMessage = "Card generation is still on cooldown!",
                                cardAvailableAt = user.nextCard
                            ))
                        }
                    }
                }
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
                authService.adminAccessEndpoint(call)?.run {
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
            get("/cards/from-user/{id}") {
                extractUsername(call)?.let { username ->
                    val cards = service.getByUsername(username)
                    cards?.let {
                        call.respond(
                            HttpStatusCode.OK, ApiResponse(
                                body = it
                            )
                        )
                    } ?: call.respond(HttpStatusCode.NotFound, ApiResponse<String>(
                        errorMessage = "No user with username=$username was found"
                    ))
                }
            }
        }
    }
}

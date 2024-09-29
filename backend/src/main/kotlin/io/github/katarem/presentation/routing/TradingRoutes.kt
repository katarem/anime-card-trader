package io.github.katarem.presentation.routing

import io.github.katarem.domain.utils.extractId
import io.github.katarem.presentation.dto.TradeDTO
import io.github.katarem.presentation.response.ApiResponse
import io.github.katarem.service.services.auth.AuthService
import io.github.katarem.service.services.trade.TradeServiceImpl
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.tradingRoutes(
    service: TradeServiceImpl,
    authService: AuthService
){
    routing {
        route("api"){
            get("/trades"){
                authService.adminAccessEndpoint(call)?.run {
                    val all = service.getAll()
                    call.respond(HttpStatusCode.OK, ApiResponse(
                        body = all
                    ))
                }
            }
            post<TradeDTO>("/trades") { trade ->
                service.insert(trade)?.let {
                    call.respond(HttpStatusCode.OK, ApiResponse(
                        body = it
                    ))
                } ?: call.respond(HttpStatusCode.BadRequest, ApiResponse<String>(
                    errorMessage = "Trade already exists"
                ))
            }
            put<TradeDTO>("/trades/{id}"){ trade ->
                extractId(call)?.let { id ->
                    service.update(id, trade)?.let {
                        call.respond(HttpStatusCode.OK, ApiResponse(
                            body = it
                        ))
                    } ?: call.respond(HttpStatusCode.BadRequest, ApiResponse<String>(
                        errorMessage = "There wasn't any trade with id=$id"
                    ))
                } ?: call.respond(HttpStatusCode.BadRequest, ApiResponse<String>(
                    errorMessage = "Invalid id"
                ))
            }
        }
    }
}
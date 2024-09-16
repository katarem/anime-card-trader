package io.github.katarem.domain.utils

import io.github.katarem.domain.response.ApiResponse
import io.github.katarem.presentation.dto.CharacterFilters
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.crypt.Algorithms

suspend fun extractId(call: RoutingCall): Long?{
    return try {
        call.pathParameters["id"]!!.toLong()
    } catch (ex: Exception){
        call.respond(
            HttpStatusCode.BadRequest, ApiResponse<String>(
            errorMessage = "Invalid id"
        ))
        null
    }
}

suspend fun extractUUID(call: RoutingCall): String?{
    return try {
        call.pathParameters["id"]!!
    } catch (ex: Exception){
        call.respond(
            HttpStatusCode.BadRequest, ApiResponse<String>(
                errorMessage = "Invalid uuid"
            ))
        null
    }
}

fun extractFilters(call: RoutingCall): CharacterFilters{
    return CharacterFilters(
        name = call.queryParameters["name"],
        origin = call.queryParameters["origin"],
        gender = call.queryParameters["gender"],
        older = call.queryParameters["older"]?.toIntOrNull(),
        younger = call.queryParameters["younger"]?.toIntOrNull()
    )
}
val encryptor = Algorithms.AES_256_PBE_GCM("7f6c74a58d9034f5e235bf4e162f89aa", "a0a1a2a3a4a5a6a7")
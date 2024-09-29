package io.github.katarem.domain.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.github.katarem.domain.model.Card
import io.github.katarem.presentation.dto.CardDTO
import io.github.katarem.presentation.response.ApiResponse
import io.github.katarem.presentation.dto.CharacterFilters
import io.github.katarem.presentation.dto.UserDTO
import io.github.katarem.service.utils.LocalDateTimeAdapter
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.LocalDateTime
import java.lang.reflect.Type
import java.security.SecureRandom

val GSON: Gson = GsonBuilder()
    .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
    .create()

val userListType: Type = object : TypeToken<List<UserDTO>>() {}.type
val cardsListType: Type = object : TypeToken<List<Card>>() {}.type

suspend fun extractId(call: RoutingCall): Long?{
    return try {
        call.pathParameters["id"]!!.toLong()
    } catch (ex: Exception){
        call.respond(
            HttpStatusCode.BadRequest, ApiResponse<String>(
            errorMessage = "Invalid id"
        )
        )
        null
    }
}
suspend fun extractQueryUsername(call: RoutingCall): String?{
    return try {
        call.queryParameters["user_name"]!!
    } catch (ex: Exception){
        call.respond(
            HttpStatusCode.BadRequest, ApiResponse<String>(
                errorMessage = "Invalid username"
            )
        )
        null
    }
}
suspend fun extractUsername(call: RoutingCall): String?{
    return try {
        call.pathParameters["id"]!!
    } catch (ex: Exception){
        call.respond(
            HttpStatusCode.BadRequest, ApiResponse<String>(
                errorMessage = "Invalid username"
            )
        )
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
            )
        )
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
fun getRandomNumber(maxNumber: Int): Int{
    val random = SecureRandom()
    random.generateSeed(10)
    return random.nextInt(0, maxNumber)
}
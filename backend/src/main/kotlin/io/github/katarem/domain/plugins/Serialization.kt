package io.github.katarem.domain.plugins

import io.github.katarem.service.utils.LocalDateTimeAdapter
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.LocalDateTime

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        gson {
            registerTypeAdapter(LocalDateTime::class.java,LocalDateTimeAdapter())
            setPrettyPrinting()
        }
    }
}

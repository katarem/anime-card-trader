package io.github.katarem

import io.github.katarem.domain.database.prepareDatabase
import io.github.katarem.domain.plugins.configureHTTP
import io.github.katarem.presentation.routing.configureRouting
import io.github.katarem.domain.plugins.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    prepareDatabase()
    configureSerialization()
    configureRouting()
    configureHTTP()
}

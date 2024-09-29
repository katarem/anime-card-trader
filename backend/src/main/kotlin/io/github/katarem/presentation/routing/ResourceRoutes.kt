package io.github.katarem.presentation.routing

import io.github.katarem.presentation.readImage
import io.github.katarem.presentation.response.ApiResponse
import io.github.katarem.service.services.resource.ResourceService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

private const val FILE_SIZE_LIMIT = (10 * 1024 * 1024).toLong() // 10 MB
fun Application.resourceRoutes(
    service: ResourceService
){
    routing {
        post("/files/upload/profile"){
            val multipart = call.receiveMultipart(FILE_SIZE_LIMIT)
            val image = readImage(multipart)
            image?.let { file ->
                val uploaded = service.changeProfilePhoto(file.content, file.filename)
                if(!uploaded)
                    call.respond(HttpStatusCode.InternalServerError, "Failed to save file.")
                call.respond(HttpStatusCode.OK, "File uploaded successfully!")
            } ?: call.respond(HttpStatusCode.BadRequest, ApiResponse<String>(
                errorMessage = "Your file couldn't be read"
            ))
        }

        post("/files/upload/cards"){
            val multipart = call.receiveMultipart(FILE_SIZE_LIMIT)
            val image = readImage(multipart)
            image?.let { file ->
                val uploaded = service.changeCardPhoto(file.content, file.filename)
                if(!uploaded)
                    call.respond(HttpStatusCode.InternalServerError, "Failed to save file.")
                call.respond(HttpStatusCode.OK, "File uploaded successfully!")
            } ?: call.respond(HttpStatusCode.BadRequest, ApiResponse<String>(
                errorMessage = "Your file couldn't be read"
            ))
        }

        staticFiles("/files/res/", File("./files"))
        staticResources("/static","static")
        get("/favicon.ico"){
            call.respondRedirect("/static/favicon.ico")
        }
    }
}


package io.github.katarem.presentation

import io.ktor.http.content.*
import io.ktor.utils.io.*
import kotlinx.datetime.*
import java.io.File
import kotlin.time.Duration.Companion.hours

fun checkValidApiKey(key: String): Boolean {
    if(key.isEmpty()) return false
    val splitted = key.split("-")
    if(splitted.size != 6)
        return false
    val header = splitted[0]
    return header == "ACAPI"
}

fun cooldownFinished(now: LocalDateTime, nextCard: LocalDateTime): Boolean {
    val difference = now.toInstant(TimeZone.currentSystemDefault()) - nextCard.toInstant(TimeZone.currentSystemDefault())
    return difference >= 8.hours
}

fun obtainNextCardDate(): LocalDateTime{
    val instant = Clock.System.now()
    val newInstant = instant + 8.hours
    return newInstant.toLocalDateTime(TimeZone.currentSystemDefault())
}

fun now(): LocalDateTime{
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
}

fun resolveFilesFolder(): File{
    val rootFolder = File("./files")
    val profilePhotos = File("./files/profile/")
    val cardsPhotos = File("./files/cards/")
    val folderExists = rootFolder.exists()
    if(!folderExists){
        rootFolder.mkdirs()
        profilePhotos.mkdirs()
        cardsPhotos.mkdirs()
    }
    return rootFolder
}

suspend fun readImage(multipart: MultiPartData): ResourceImage? {
    var filename = "unknown-${System.currentTimeMillis()}"
    var fileContent: ByteArray? = null
    multipart.forEachPart { part ->
        if(part is PartData.FileItem){
            filename = part.originalFileName ?: part.name ?: filename
            fileContent = part.provider().toByteArray()
        }
        part.dispose()
    }
    return fileContent?.let { ResourceImage(it, filename) }
}

data class ResourceImage(
    val content: ByteArray,
    val filename: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ResourceImage

        if (!content.contentEquals(other.content)) return false
        if (filename != other.filename) return false

        return true
    }

    override fun hashCode(): Int {
        var result = content.contentHashCode()
        result = 31 * result + filename.hashCode()
        return result
    }
}
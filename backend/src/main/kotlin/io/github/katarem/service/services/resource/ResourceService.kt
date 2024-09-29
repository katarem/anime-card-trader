package io.github.katarem.service.services.resource

import io.github.katarem.presentation.resolveFilesFolder
import java.io.File

class ResourceService {

    private val folder = resolveFilesFolder()
    private val profilesFolder = folder.resolve("profile")
    private val cardsFolder = folder.resolve("cards")

    fun changeProfilePhoto(image: ByteArray, name: String): Boolean {
        return uploadFile(image, name, profilesFolder)
    }

    fun changeCardPhoto(image: ByteArray, name: String): Boolean{
        return uploadFile(image, name, cardsFolder)
    }

    private fun uploadFile(image: ByteArray, name: String, folder: File): Boolean {
        try {
            val file = folder.resolve(name)
            file.writeBytes(image)
            return true
        } catch (e: Exception) {
            return false
        }
    }
}
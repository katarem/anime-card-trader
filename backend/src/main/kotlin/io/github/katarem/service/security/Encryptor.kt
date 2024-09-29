package io.github.katarem.service.security

import org.mindrot.jbcrypt.BCrypt

object Encryptor {

    fun encryptPassword(password: String): String {
        val hashed = BCrypt.hashpw(password, BCrypt.gensalt(5))
        return hashed
    }

    fun checkPassword(password: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(password, hashedPassword)
    }

}
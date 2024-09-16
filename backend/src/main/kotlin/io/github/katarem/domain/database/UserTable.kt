package io.github.katarem.domain.database

import io.github.katarem.domain.utils.encryptor
import org.jetbrains.exposed.crypt.Algorithms
import org.jetbrains.exposed.crypt.encryptedVarchar
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object UserTable: LongIdTable("users") {
    val username = varchar(name = "user_name", length = 16).uniqueIndex()
    val email = varchar(name = "user_email", length = 320).uniqueIndex()
    val password = encryptedVarchar(name = "user_password", 32, encryptor)
    val lastTimeChecked = datetime("last_check").nullable()
}
package io.github.katarem.domain.database

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object UserTable: LongIdTable("users") {
    val username = varchar(name = "user_name", length = 16).uniqueIndex()
    val email = varchar(name = "user_email", length = 320).uniqueIndex()
    val role = varchar(name = "role", length = 10).default("USER")
    val password = varchar(name = "user_password", 60)
    val apikey = varchar(name = "api_key", length = 42).nullable()
    val nextCard = datetime("next_card").nullable()
}
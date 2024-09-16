package io.github.katarem.domain.database

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object CardTable: LongIdTable("cards") {
    val uuid = varchar("uuid", length = 36).uniqueIndex()
    val character = reference("character_id",CharacterTable.id)
    val obtainedDate = datetime("obtained_date").nullable()
    val attachedUser = reference("attached_user",UserTable.id).nullable()
}
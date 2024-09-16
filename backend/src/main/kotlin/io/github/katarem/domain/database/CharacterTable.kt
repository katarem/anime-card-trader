package io.github.katarem.domain.database

import org.jetbrains.exposed.dao.id.LongIdTable

object CharacterTable: LongIdTable("characters") {
    val name = varchar(name = "name",length = 64)
    val origin = varchar(name = "origin", length = 128)
    val gender = varchar(name = "gender", length = 20)
    val age = integer("age")
    val description = varchar(name = "description", length = 256)
    val cardId = varchar(name = "card_id", length = 36)
}
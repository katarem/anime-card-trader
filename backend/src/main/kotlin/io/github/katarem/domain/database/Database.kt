package io.github.katarem.domain.database

import io.github.katarem.domain.model.Card
import io.github.katarem.domain.model.Character
import io.github.katarem.domain.model.User
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

fun Application.prepareDatabase(){
    Database.connect(url = "jdbc:sqlite:test.db")
    transaction {
        SchemaUtils.createMissingTablesAndColumns(
            CardTable,
            CharacterTable,
            UserTable
        )
    }
    //transaction {
    //    val isEmpty = CharacterTable.selectAll().empty()
    //    if(isEmpty){
    //        val sqlFile = File(ClassLoader.getSystemResource("import.sql").file)
    //        val sqlStatements = sqlFile.readText().split(";")
    //        sqlStatements.forEach { statement ->
    //            if(statement.trim().isNotEmpty()){
    //                exec(statement.trim())
    //            }
    //        }
    //    }
    //}
}

fun toUser(resultRow: ResultRow): User {
    return User(
        id = resultRow[UserTable.id].value,
        username = resultRow[UserTable.username],
        email = resultRow[UserTable.email],
        password = resultRow[UserTable.password],
        cards = listOf(),
        lastTimeChecked = resultRow[UserTable.lastTimeChecked]
    )
}

fun toCharacter(resultRow: ResultRow): Character {
    return Character(
        id = resultRow[CharacterTable.id].value,
        name = resultRow[CharacterTable.name],
        origin = resultRow[CharacterTable.origin],
        gender = resultRow[CharacterTable.gender],
        age = resultRow[CharacterTable.age],
        description = resultRow[CharacterTable.description],
        cardId = resultRow[CharacterTable.cardId]
    )
}

fun toCard(resultRow: ResultRow): Card {
    return Card(
        id = resultRow[CardTable.id].value,
        uuid = resultRow[CardTable.uuid],
        characterId = resultRow[CardTable.character].value,
        obtainedDate = resultRow[CardTable.obtainedDate],
        attachedUser = null
    )
}
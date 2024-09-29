package io.github.katarem.domain.database

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.github.katarem.domain.model.*
import io.github.katarem.domain.utils.GSON
import io.github.katarem.domain.utils.cardsListType
import io.github.katarem.presentation.dto.UserDTO
import io.github.katarem.service.security.Encryptor
import io.github.katarem.service.utils.LocalDateTimeAdapter
import io.ktor.server.application.*
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.FileInputStream
import java.io.FileReader
import java.io.InputStreamReader

fun Application.prepareDatabase() {
    Database.connect(url = "jdbc:sqlite:test.db")
    transaction {
        SchemaUtils.createMissingTablesAndColumns(
            CardTable,
            CharacterTable,
            UserTable,
            NotificationTable,
            TradeTable
        )
    }
    try{
        transaction {
            val isEmpty = UserTable.selectAll().empty()
            if(isEmpty){

                val path = ClassLoader.getSystemResourceAsStream("test-users.json")
                path?.let { InputStreamReader(it) }?.use { reader ->
                    val userListType = object : TypeToken<List<UserDTO>>() {}.type
                    val users = GSON.fromJson<List<UserDTO>>(reader, userListType)
                    users.forEach { user ->
                        UserTable.insert {
                            it[username] = user.username
                            it[email] = user.email?:""
                            it[password] = Encryptor.encryptPassword(user.password?:"hola")
                            it[role] = user.role?:Roles.USER.name
                        }
                    }
                }
            }
        }
    } catch (_: Exception){
        // ignored
    }
}

fun toUser(resultRow: ResultRow): User {
    return User(
        id = resultRow[UserTable.id].value,
        username = resultRow[UserTable.username],
        email = resultRow[UserTable.email],
        password = resultRow[UserTable.password],
        cards = listOf(),
        role = Roles.valueOf(resultRow[UserTable.role]),
        nextCard = resultRow[UserTable.nextCard]
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

fun toNotification(resultRow: ResultRow): Notification{
    return Notification(
        id = resultRow[NotificationTable.id].value,
        userId = resultRow[NotificationTable.userId].value,
        title = resultRow[NotificationTable.title],
        content = resultRow[NotificationTable.content],
        createdAt = resultRow[NotificationTable.createDate],
        read = resultRow[NotificationTable.read]
    )
}

fun toTrade(resultRow: ResultRow): Trade{
    return Trade(
        id = resultRow[TradeTable.id].value,
        offeringUserId = resultRow[TradeTable.offeringUserId].value,
        offeredUserId = resultRow[TradeTable.offeredUserId].value,
        offeringUserCards = GSON.fromJson(resultRow[TradeTable.offeringCards], cardsListType),
        offeredUserCards = GSON.fromJson(resultRow[TradeTable.offeredCards], cardsListType),
        createdAt = resultRow[TradeTable.createdAt],
        accepted = resultRow[TradeTable.accepted]
    )
}
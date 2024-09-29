package io.github.katarem.domain.database

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object TradeTable: LongIdTable("trades") {
    val offeringUsername = reference("offering_user", UserTable.username)
    val offeredUsername = reference("offered_user",UserTable.username)
    val offeringCards = text("offering_cards")
    val offeredCards = text("offered_cards")
    val accepted = bool("accepted").nullable()
    val createdAt = datetime("created_at")
}
package io.github.katarem.domain.database

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object TradeTable: LongIdTable("trades") {
    val offeringUserId = reference("offering_user", UserTable.id)
    val offeredUserId = reference("offered_user",UserTable.id)
    val offeringCards = text("offering_cards")
    val offeredCards = text("offered_cards")
    val accepted = bool("accepted").nullable()
    val createdAt = datetime("created_at")
}
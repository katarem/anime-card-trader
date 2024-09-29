package io.github.katarem.domain.repository.trade

import io.github.katarem.domain.database.TradeTable
import io.github.katarem.domain.database.toTrade
import io.github.katarem.domain.model.Trade
import io.github.katarem.domain.utils.GSON
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class TradeRepositoryImpl : TradeRepository {
    override fun getAll(): List<Trade> {
        return transaction {
            TradeTable.selectAll().map(::toTrade)
        }
    }

    override fun getById(id: Long): Trade? {
        return transaction {
            TradeTable.selectAll().where {
                TradeTable.id eq id
            }.map(::toTrade).singleOrNull()
        }
    }

    override fun insert(trade: Trade): Trade? {
        if (exists(trade)) return null
        return transaction {
            val id = TradeTable.insertAndGetId {
                it[offeredUsername] = trade.offeringUsername
                it[offeredUsername] = trade.offeredUsername
                it[offeringCards] = GSON.toJson(trade.offeringUserCards)
                it[offeredCards] = GSON.toJson(trade.offeredUserCards)
                it[createdAt] = trade.createdAt
                it[accepted] = trade.accepted
            }
            return@transaction trade.copy(id = id.value)
        }
    }

    override fun update(id: Long, trade: Trade): Trade? {
        if (!exists(id)) return null
        return transaction {
            TradeTable.update({ TradeTable.id eq id }) {
                it[offeredUsername] = trade.offeringUsername
                it[offeredUsername] = trade.offeredUsername
                it[offeringCards] = GSON.toJson(trade.offeringUserCards)
                it[offeredCards] = GSON.toJson(trade.offeredUserCards)
                it[createdAt] = trade.createdAt
                it[accepted] = trade.accepted
            }
            return@transaction trade.copy(id = id)
        }
    }

    override fun delete(id: Long): Int? {
        if (!exists(id)) return null
        return transaction {
            TradeTable.deleteWhere {
                TradeTable.id eq id
            }
        }
    }

    /**
     * We check that the trading users don't have an open trade
     * */
    fun exists(trade: Trade): Boolean {
        return transaction {
            !TradeTable.selectAll().where {
                (TradeTable.offeringUsername eq trade.offeringUsername) and
                        (TradeTable.offeredUsername eq trade.offeredUsername) and
                        (TradeTable.accepted.isNull())
            }.empty()
        }
    }

    fun exists(id: Long): Boolean {
        return transaction {
            !TradeTable.selectAll().where {
                TradeTable.id eq id
            }.empty()
        }
    }
}
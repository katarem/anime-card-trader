package io.github.katarem.domain.database

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object NotificationTable: LongIdTable("notifications") {
    val userId = reference("for_user",UserTable.id)
    val title = varchar("title", length = 300)
    val content = text("content")
    val createDate = datetime("created_at")
    val read = bool("read").default(false)
}
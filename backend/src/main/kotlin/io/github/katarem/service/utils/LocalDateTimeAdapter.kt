package io.github.katarem.service.utils

import com.google.gson.*
import kotlinx.datetime.LocalDateTime
import java.lang.reflect.Type

// Custom Gson Serializer/Deserializer for kotlinx.datetime.LocalDateTime
class LocalDateTimeAdapter : JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    override fun serialize(
        src: LocalDateTime?, typeOfSrc: Type?, context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.toString()) // ISO-8601 format
    }

    override fun deserialize(
        json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?
    ): LocalDateTime {
        return json?.asString?.let { LocalDateTime.parse(it) }!!
    }
}

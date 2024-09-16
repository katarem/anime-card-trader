package io.github.katarem.domain.model

data class Character(
    val id: Long,
    val name: String,
    val origin: String,
    val gender: String,
    val age: Int,
    val description: String,
    val cardId: String
)
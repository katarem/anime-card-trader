package io.github.katarem.presentation.dto

data class CharacterFilters(
    val name: String?,
    val origin: String?,
    val gender: String?,
    val older: Int?,
    val younger: Int?,
)

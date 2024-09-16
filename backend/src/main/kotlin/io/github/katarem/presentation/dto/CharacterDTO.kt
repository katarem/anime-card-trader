package io.github.katarem.presentation.dto

data class CharacterDTO(
    val id: Long,
    val name: String,
    val anime: String,
    val gender: String,
    val description: String,
    val age: Int,
    val cardId: String?
){
}
package com.app.appellas.data.models.dtos.body

data class CreateContactBody(
    val name: String,
    val number: Long,
    val id_user: Int,
    val email: String
)

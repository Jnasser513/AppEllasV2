package com.app.appellas.data.models.dtos.response

data class ContactResponse(
    val id_contacto: Int,
    val nombre: String,
    val telefono: Long,
    val user: UserResponse
)

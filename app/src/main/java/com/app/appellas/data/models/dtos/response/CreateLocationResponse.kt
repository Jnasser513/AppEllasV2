package com.app.appellas.data.models.dtos.response

data class CreateLocationResponse(
    val id: Int,
    val latitud: Double,
    val longitud: Double,
    val user: UserResponse
)

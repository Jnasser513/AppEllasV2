package com.app.appellas.data.models.dtos.response

data class LoginResponse(
    val id: Int,
    val username: String,
    val nombre: String,
    val email: String,
    val rol :String,
    val accessToken: String
)

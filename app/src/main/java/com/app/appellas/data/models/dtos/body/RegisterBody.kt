package com.app.appellas.data.models.dtos.body

data class RegisterBody(
    val nombre: String,
    val telefono: Int,
    val correo: String,
    val password: String,
    val id_rol: Int
)

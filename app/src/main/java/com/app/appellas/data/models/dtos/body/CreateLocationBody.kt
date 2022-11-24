package com.app.appellas.data.models.dtos.body

data class CreateLocationBody(
    val latitude: Double,
    val longitude: Double,
    val type: Int,
    val id_user: Int
)

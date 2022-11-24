package com.app.appellas.data.models.dtos.body

data class CreateBlogBody(
    val title: String,
    val subtitle: String,
    val description: String,
    val id_user: Int,
    val image: String
)

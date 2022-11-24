package com.app.appellas.data.models.dtos.response

data class CreateBlogResponse(
    val idBlog: Int,
    val titulo: String,
    val subtitulo: String,
    val descripcion: String
)
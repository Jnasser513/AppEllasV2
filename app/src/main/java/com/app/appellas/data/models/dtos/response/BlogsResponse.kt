package com.app.appellas.data.models.dtos.response

data class BlogsResponse(
    val idBlog: Int,
    val titulo: String,
    val subtitulo: String,
    val descripcion: String,
    val user: UserResponse,
    val imagenes: List<ImageDetail>
)

data class UserResponse(
    val id: Int,
    val nombre: String,
    val telefono: Int,
    val correo: String
)

data class ImageDetail(
    val id_imagen: Int,
    val src: String
)
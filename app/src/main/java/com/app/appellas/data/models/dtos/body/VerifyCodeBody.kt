package com.app.appellas.data.models.dtos.body

data class VerifyCodeBody(
    val email: String,
    val code: String
)
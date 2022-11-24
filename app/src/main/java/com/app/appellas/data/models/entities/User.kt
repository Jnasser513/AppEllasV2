package com.app.appellas.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey
    val id: Int,
    val username: String,
    val nombre: String,
    val mail: String,
    val rol: String,
    val accessToken: String
)

package com.app.appellas.data.network.services.admin

import com.app.appellas.data.models.dtos.response.GenericResponse
import com.app.appellas.data.models.dtos.response.UserResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface AdminUserService {

    @GET("user")
    suspend fun getUsers(@Header("Authorization") token: String): Response<GenericResponse<List<UserResponse>>>

    @DELETE("user/delete_user/{id}")
    suspend fun deleteUser(@Header("Authorization") token: String, @Path("id") id: Int): Response<GenericResponse<String>>

}
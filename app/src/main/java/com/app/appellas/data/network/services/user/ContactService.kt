package com.app.appellas.data.network.services.user

import com.app.appellas.data.models.dtos.body.CreateContactBody
import com.app.appellas.data.models.dtos.response.ContactResponse
import com.app.appellas.data.models.dtos.response.GenericResponse
import retrofit2.Response
import retrofit2.http.*

interface ContactService {

    @POST("contact")
    suspend fun createContact(@Header("Authorization") token: String, @Body createContactBody: CreateContactBody): Response<GenericResponse<ContactResponse>>

    @GET("contact/{id}")
    suspend fun getUserContacts(@Header("Authorization") token: String, @Path("id") id: Int): Response<GenericResponse<List<ContactResponse>>>

    @DELETE("contact/{id}")
    suspend fun deleteContact(@Header("Authorization") token: String, @Path("id") id: Int): Response<GenericResponse<String>>
}
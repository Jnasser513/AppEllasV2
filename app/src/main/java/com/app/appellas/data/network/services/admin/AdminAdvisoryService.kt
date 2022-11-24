package com.app.appellas.data.network.services.admin

import com.app.appellas.data.models.dtos.body.CreateAdvisorBody
import com.app.appellas.data.models.dtos.response.CreateAdvisorResponse
import com.app.appellas.data.models.dtos.response.GenericResponse
import retrofit2.Response
import retrofit2.http.*

interface AdminAdvisoryService {

    @POST("advisory")
    suspend fun createAdvisor(@Header("Authorization") token: String, @Body createAdvisorBody: CreateAdvisorBody): Response<GenericResponse<CreateAdvisorResponse>>

    @GET("advisory/{type}")
    suspend fun getAdvisorByCategory(@Header("Authorization") token: String, @Path("type") type: Int): Response<GenericResponse<List<CreateAdvisorResponse>>>

    @DELETE("advisory/delete_advisor/{id}")
    suspend fun deleteAdvisory(@Header("Authorization") token: String, @Path("id") id: Long): Response<GenericResponse<String>>
}
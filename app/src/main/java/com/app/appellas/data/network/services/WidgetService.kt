package com.app.appellas.data.network.services

import com.app.appellas.data.models.dtos.body.CreateLocationBody
import com.app.appellas.data.models.dtos.response.CreateLocationResponse
import com.app.appellas.data.models.dtos.response.GenericResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface WidgetService {

    @POST("location")
    suspend fun createLocation(@Header("Authorization") token: String, @Body locationBody: CreateLocationBody): GenericResponse<CreateLocationResponse>

}
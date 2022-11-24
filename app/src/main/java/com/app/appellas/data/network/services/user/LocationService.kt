package com.app.appellas.data.network.services.user

import com.app.appellas.data.models.dtos.body.CreateLocationBody
import com.app.appellas.data.models.dtos.body.UpdateLocationUserBody
import com.app.appellas.data.models.dtos.response.CreateLocationResponse
import com.app.appellas.data.models.dtos.response.GenericResponse
import com.app.appellas.data.models.dtos.response.LocationResponse
import retrofit2.Response
import retrofit2.http.*

interface LocationService {

    @POST("location")
    suspend fun createLocation(@Header("Authorization") token: String, @Body locationBody: CreateLocationBody): Response<GenericResponse<CreateLocationResponse>>

    @PUT("location/update")
    suspend fun updateLocation(@Header("Authorization") token: String, @Body updateLocationBody: UpdateLocationUserBody): Response<GenericResponse<String>>

    @GET("location/detail/{id}")
    suspend fun getAlertDetail(@Header("Authorization") token: String, @Path("id") id: Int): Response<GenericResponse<LocationResponse>>

}